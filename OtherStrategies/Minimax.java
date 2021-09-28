package connectFour;

import java.util.*;

public abstract class Minimax {
    private final int MAXDEPTH;
    protected final Object[] vector;
    public Map<Long, Float> boardsSeen = new HashMap<>();

    protected Minimax(int maxdepth, Object[] vector) {
        MAXDEPTH = maxdepth;
        if (vector.length == 0) {
            this.vector = new Object[]{0.001f, 0.1f, 0.1f, 0.7f, 1f};
        } else {
            this.vector = vector;
        }
    }

    public int getBestMove(Grid g, int myPlayer) {
        int move = 0;
        float alpha = -1;
        float beta = 1;
        for (int i = MAXDEPTH; i <= MAXDEPTH; i++) {
            Tuple tup = minimaxGetScore(g, i, myPlayer, alpha, beta, vector);
            move = tup.getMove();
        }
        return move;
    }

    private Tuple minimaxGetScore(Grid g, int remainingDepth, int myPlayer, float alpha,
                                  float beta, Object[] vector) {
        if (g.getWinningPlayer() == myPlayer) {
            return new Tuple( remainingDepth + 1, -1);
        }
        else if (g.getWinningPlayer() == (3 - myPlayer)) {
            return new Tuple( -remainingDepth - 1, -1);
        }
        else if (g.getWinningPlayer() == -1) {
            return new Tuple(0, -1);
        }
        int nextPlayer = g.getNextPlayer();
        if (remainingDepth <= 0) {
            float score = getHeuristicScore(g, myPlayer);
            return new Tuple(score, -1);
        }

        boolean isMax = (nextPlayer == myPlayer);
        int bestMove = -1;
        float bestScore;
        if (isMax) {
            bestScore = -Float.MAX_VALUE;
        }
        else {
            bestScore = Float.MAX_VALUE;
        }
        //int[] order = new int[]{3, 2, 4, 1, 5, 0, 6};
        List<Float> preScores = new LinkedList<>();
        List<Integer> order = new LinkedList<>();
        for (int i = 0; i < 7; i++) {
            if (!g.isColumnFull(i)) {
                g.makeMove(i);
                float preScore;
                long state = g.getBoardState();
                if (this.boardsSeen.containsKey(state)) {
                    preScore = boardsSeen.get(state);
                } else {
                    preScore = cheapHeuristic(g, myPlayer);
                    boardsSeen.put(state, preScore);
                }
                if (preScores.isEmpty()) {
                    preScores.add(preScore);
                    order.add(i);
                } else {
                    int j = 0;
                    if (isMax) {
                        while (j < preScores.size() && preScores.get(j) < preScore) {
                            j++;
                        }
                    } else {
                        while (j < preScores.size() && preScores.get(j) > preScore) {
                            j++;
                        }
                    }
                    preScores.add(j, preScore);
                    order.add(j, i);
                }
                g.undo();
            }
        }
        for (int move : order) {
            if (!g.isColumnFull(move)) {
                g.makeMove(move);
                float scoreCur = minimaxGetScore(g, remainingDepth - 1, myPlayer,
                        alpha, beta, vector).getScore();
                g.undo();
                if (isMax) {
                    if (scoreCur > bestScore) {
                        bestScore = scoreCur;
                        bestMove = move;
                    }
                    alpha = Math.max(alpha, bestScore);
                    if (bestScore >= beta) {
                        break;
                    }
                } else {
                    if (scoreCur < bestScore) {
                        bestScore = scoreCur;
                        bestMove = move;
                    }
                    beta = Math.min(beta, bestScore);
                    if (bestScore <= alpha) {
                        break;
                    }
                }
            }
        }
        if (remainingDepth == MAXDEPTH) {
            System.out.println(bestMove + ": " + bestScore + " odds");
        }
        boardsSeen.put(g.getBoardState(), bestScore);
        return new Tuple(bestScore, bestMove);
    }

    protected abstract float getHeuristicScore(Grid g, int myPlayer);

    public abstract Object[] getGenetics();

    private float cheapHeuristic(Grid g, int myPlayer) {
        float score = 0;
        int[] myChains = new int[]{0, 0, 0, 0, 0};
        int[] otherChains = new int[]{0, 0, 0, 0, 0};
        int otherPlayer = myPlayer % 2 + 1;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                for (int dir = 0; dir < 4; dir++) {
                    int myLen = getLength(g, row, col, dir, myPlayer);
                    myChains[myLen] = myChains[myLen] + 1;

                    int otherLen = getLength(g, row, col, dir, otherPlayer);
                    otherChains[otherLen] = otherChains[otherLen] + 1;
                }
            }
        }
        for (int i = 1; i < myChains.length; i++) {
            score += myChains[i] * (float) vector[i];
            score -= otherChains[i] * (float) vector[i];
        }
        return (float) Math.tanh(score);
    }

    public int getLength(Grid g, int iRow, int iCol, int direction, int player) {
        if (direction == Grid.RIGHT) {
            int[] locs = { iRow, iCol, iRow, iCol + 1, iRow, iCol + 2, iRow, iCol + 3 };
            return getLengthHelper(g, player, locs);
        } else if (direction == Grid.UP) {
            int[] locs = { iRow, iCol, iRow + 1, iCol, iRow + 2, iCol, iRow + 3, iCol };
            return getLengthHelper(g, player, locs);
        } else if (direction == Grid.UPLEFT) {
            int[] locs = { iRow , iCol , iRow + 1, iCol - 1, iRow + 2, iCol - 2, iRow + 3, iCol - 3 };
            return getLengthHelper(g, player, locs);
        } else { // UPRIGHT
            int[] locs = { iRow, iCol, iRow + 1, iCol + 1, iRow + 2, iCol + 2, iRow + 3, iCol + 3 };
            return getLengthHelper(g, player, locs);
        }
    }

    private int getLengthHelper(Grid g, int player, int[] locs) {
        int length = 0;
        for (int i = 0; i < locs.length; i += 2) {
            int playerAt = safeGetPlayerAt(g, locs[i], locs[i + 1]);
            if (playerAt == player) {
                length++;
            }
            else if (playerAt != Grid.PLAYEREMPTY) {
                return 0;
            }
        }
        return length;
    }

    private int safeGetPlayerAt(Grid g, int row, int col) {
        if (row < 0 || row >= g.getRows() ||
                col < 0 || col >= g.getCols()) {
            return -1;
        }
        return g.getPlayerAt(row, col);
    }

    public static class Tuple {
        private final float score;
        private final int move;

        public Tuple(float score, int move) {
            this.score = score;
            this.move = move;
        }

        public float getScore() {
            return this.score;
        }

        public int getMove() {
            return this.move;
        }
    }
}
