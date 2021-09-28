package connectFour;

public class BasicHeuristic extends Minimax {

    public BasicHeuristic(int maxdepth, Object[] vector) {
        super(maxdepth, vector);
    }

    protected float getHeuristicScore(Grid g, int myPlayer) {
        float score = 0;
        score += evalChains(g, myPlayer);
        return score;
    }

    @Override
    public Object[] getGenetics() {
        return this.vector;
    }

    private float evalChains(Grid g, int myPlayer) {
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
        return score;
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

}
