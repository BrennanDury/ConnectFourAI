package connectFour;

import java.util.Arrays;

public class HeuristicPlayer implements Player {
    private final Object[] vector;
    private final Minimax minimax;

    public HeuristicPlayer(Object[] vector, Minimax minimax) {
        this.vector = vector;
        this.minimax = minimax;
    }

    @Override
    public int getMoveColumn(Grid g) {
        int myPlayer = g.getMyPlayer();
        return minimax.getBestMove(g, myPlayer);
    }

    @Override
    public String getPlayerName() {
        return Arrays.toString(this.vector);
    }

    @Override
    public Object[] getGenetics() {
        return this.minimax.getGenetics();
    }
}
