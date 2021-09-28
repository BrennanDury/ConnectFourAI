package connectFour;

import java.util.*;

public class AlphaZero implements Player {
    private static final int SIMULATIONS = 100;
    private static final float C = 4;

    private Map<List<Integer>, float[]> qnp;
    private List<List<Integer>> data1;
    private List<List<Integer>> data2;
    private TensorModel network;
    private boolean training;
    private int sinceLast;

    public AlphaZero() {
        this.qnp = new HashMap<>();
        this.data1 = new ArrayList<>();
        this.data2 = new ArrayList<>();
        this.network = new TensorModel();
        this.training = true;
        this.sinceLast = 0;
    }

    public int getMoveColumn(Grid g) {
        float[] qualities = new float[7];
        initializeNode(g);
        for (int i = 0; i < SIMULATIONS; i++) {
            search(g);
        }
        for (int i = 0; i < 7; i++) {
            g.makeMove(i);
            List<Integer> board = g.get1DBoard();
            if (qnp.containsKey(board)) {
                qualities[i] = qnp.get(board)[1];
            }
            g.undo();
        }
        this.qnp = new HashMap<>();
        int next = g.getNextPlayer();
        return best(qualities, next);
    }

    private float search(Grid g) {
        int result = g.getWinningPlayer();
        if (result != 0) {
            return result;
        }
        List<Integer> board = g.get1DBoard();
        int next = g.getNextPlayer();
        if (this.qnp.get(board)[1] == 0) {
            for (int i = 0; i < 7; i++) {
                g.makeMove(i);
                initializeNode(g);
                g.undo();
            }
            this.qnp.get(board)[1] = 1;
            return this.qnp.get(board)[2];
        } else {
            int move = chooseMove(g);
            g.makeMove(move);
            float v = search(g);
            if (v % 1 == 0 && this.training) {
                record(g, (int) v);
            }
            g.undo();
            update(g.get1DBoard(), v);
            return v;
        }
    }

    private void initializeNode(Grid g) {
        List<Integer> board = g.get1DBoard();
        int player = 3 - g.getNextPlayer();
        if (!this.qnp.containsKey(board)) {
            List<Integer> flip = flipBoard(g);
            float p = this.network.process(g, player);
            this.qnp.put(board, new float[]{p, 0f, p});
            this.qnp.put(flip, new float[]{p, 0f, p});
        }
    }

    private int chooseMove(Grid g) {
        List<Integer> parentBoard = g.get1DBoard();
        int parentN = (int) this.qnp.get(parentBoard)[1];
        float[] bounds = new float[7];
        for (int i = 0; i < 7; i++) {
            g.makeMove(i);
            List<Integer> board = g.get1DBoard();
            float[] stats = this.qnp.get(board);
            float q = stats[0];
            float n = stats[0];
            float p = stats[0];
            float bound = q + C * p * (float) (Math.sqrt(parentN)) / (1 + n);
            bounds[i] = bound;
            g.undo();
        }
        int parentNext = g.getNextPlayer();
        return best(bounds, parentNext);
    }

    private int best(float[] arr, int next) {
        if (next == 1) {
            int maxI = -1;
            float maxVal = -Float.MAX_VALUE;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] > maxVal) {
                    maxI = i;
                    maxVal = arr[i];
                }
            }
            return maxI;
        } else {
            int minI = -1;
            float minVal = Float.MAX_VALUE;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] < minVal) {
                    minI = i;
                    minVal = arr[i];
                }
            }
            return minI;
        }
    }

    private List<Integer> flipBoard(Grid g) {
        List<Integer> board = g.get1DBoard();
        List<Integer> flip = new ArrayList<Integer>(42);
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                flip.add(row * 7 + col, board.get(row * 7 + (6 - col)));
            }
        }
        return flip;
    }

    private void update(List<Integer> board, float v) {
        float[] stats = this.qnp.get(board);
        float q = stats[0];
        float n = stats[0];
        float p = stats[0];
        float newQ = (q * n + v) / (n + 1);
        float newN = n + 1;
        this.qnp.put(board, new float[]{newQ, newN, p});
    }

    private void record(Grid g, int result) {
        if (result == 2) {
            result = -1;
        }
        List<Integer> board = g.get1DBoard();
        board.add(result);
        List<Integer> flip = flipBoard(g);
        flip.add(result);
        int player = 3 - g.getNextPlayer();
        if (player == 1) {
            this.data1.add(board);
            this.data1.add(flip);
        } else {
            this.data2.add(board);
            this.data2.add(flip);
        }
        this.sinceLast += 2;
    }

    public Runnable retrain() {
        return new Retrain(this.data1, this.data2, this.sinceLast, this.network);
    }

    @Override
    public String getPlayerName() {
        return "AlphaZero";
    }

    @Override
    public Object[] getGenetics() {
        return new Object[0];
    }
}
