import java.util.*;

public class AlphaZero {
    private static final int SIMULATIONS = 400;
    private static final float C = 4;

    private List<Float> pi;
    private final TensorModel network;

    public AlphaZero() {
        this.pi = new ArrayList<>();
        this.network = new TensorModel();
    }

    public int getMoveColumn(Board g) {
        pi = new ArrayList<>();
        Node root = new Node(g);
        for (int i = 0; i < SIMULATIONS; i++) {
            search(root);
        }
        float[] visits = root.visits();
        for (int i = 0; i < Board.COLS; i++) {
            pi.add(visits[i] / (SIMULATIONS - 1));
        }
        return root.mostVisited();
    }

    public List<Float> getPi() {
        return new ArrayList<>(pi);
    }

    private float search(Node node) {
        Board g = node.getG();
        int result = g.getWinningPlayer();
        if (result != Board.GAMENOTOVER) {
            node.q++;
            node.n++;
            if (result == Board.PLAYERNONE) {
                return 0;
            } else {
                return -1;
            }
        } else if (node.n == 0) {
            node.visitFirstTime();
            return -node.q; // q is the value for the player who just played
        } else {
            Node move = node.bestMove();
            float v = search(move);
            node.update(v);
            return -v;
        }
    }

    private class Node {
        private float q;
        private int n;
        private float[] probabilityVector;
        private Node[] children;
        private Board g;

        public Node(Board g) {
            this.g = g.makeCopy();
            this.q = 0;
            this.probabilityVector = new float[Board.COLS];
            this.children = new Node[Board.COLS];
            this.n = 0;
        }

        public void visitFirstTime() {
            List<float[]> output = network.inference(g);
            q = output.get(0)[0];
            probabilityVector = output.get(1);
            for (int move = 0; move < Board.COLS; move++) {
                g.makeMove(move);
                children[move] = new Node(g);
                g.undo();
            }
            n++;
        }

        public Node bestMove() {
            return children[bestIndex()];
        }

        private int max(float[] arr) {
            int maxI = 0;
            float maxVal = -Float.MAX_VALUE;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] > maxVal) {
                    maxI = i;
                    maxVal = arr[i];
                }
            }
            return maxI;
        }

        private int bestIndex() {
            int nState = n;
            float[] bounds = new float[Board.COLS];
            for (int i = 0; i < Board.COLS; i++) {
                Node child = this.children[i];
                float qAction = child.q;
                float nAction = child.n;
                float pAction = probabilityVector[i];
                float bound = qAction + C * pAction * (float) (Math.sqrt(nState)) / (1 + nAction);
                bounds[i] = bound;
            }
            return max(bounds);
        }

        private float[] injectRandomness(float[] f) {
            float[] noiseVector = new float[]{1f/Board.COLS, 1f/Board.COLS, 1f/Board.COLS, 1f/Board.COLS,
                    1f/Board.COLS, 1f/Board.COLS, 1f/Board.COLS};
            float maxNoise = .2f;
            for (int i = 0; i < noiseVector.length - 1; i++) {
                float noise = (float) Math.random() * maxNoise;
                float noiseFraction = noise / (noiseVector.length - i - 1);
                noiseVector[i] += noise;
                for (int j = i + 1; j < noiseVector.length; j++) {
                    noiseVector[j] -= noiseFraction;
                }
                maxNoise = noise - noise / (noiseVector.length - i - 1);
            }
            List<Integer> order = new ArrayList<>();
            for (int i = 0; i < f.length; i++) {
                int maxidx = -1;
                float max = -1;
                for (int j = 0; j < f.length; j++) {
                    if (f[j] > max && !order.contains(j)) {
                        maxidx = j;
                        max = f[j];
                    }
                }
                order.add(maxidx);
            }
            float[] noisyProbabilities = new float[f.length];
            for (int i = 0; i < f.length; i++) {
                float value = (float) ((.75) * f[order.get(i)] + (.25) * noiseVector[i]);
                noisyProbabilities[order.get(i)] = value;
            }
            return noisyProbabilities;
        }

        public int mostVisited() {
            return max(visits());
        }

        public float[] visits() {
            float[] visits = new float[Board.COLS];
            for (int move = 0; move < Board.COLS; move++) {
                Node child = children[move];
                visits[move] = child.n;
            }
            return visits;
        }

        public void update(float v) {
            q = (q * n + v) / (n + 1);
            n++;
        }

        /*public void record(int result) {
            List<Short> board = g.get1DBoard();
            int playerJustPlayed = 3 - g.getNextPlayer();
            if (playerJustPlayed == 2) {
                for (int i = 0; i < board.size(); i++) {
                    board.set(i, (short) (board.get(i) * -1));
                }
            }
            List<Number> dataPoint = new ArrayList<>(board);
            if (myPlayer != playerJustPlayed) {
                    result *= -1;
            } // result is the result for the player who just played
            dataPoint.add(result);
            List<Number> policy = Arrays.asList(0, 0, 0, 0, 0, 0, 0);
            policy.set(bestIndex(), 1);
            dataPoint.addAll(policy);
            data.add(dataPoint);
        }*/

        public Board getG() {
            return g.makeCopy();
        }
    }
}
