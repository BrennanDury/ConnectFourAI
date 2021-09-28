package connectFour;

import java.util.Map;
import java.util.concurrent.Callable;

public class Simulation implements Callable<Short> {
    private Grid board;
    private static final int MAX_BOARDS = 7000000;

    public Simulation(Grid g, Map<Long, Float> boardSeen) {
        this.board = g;
    }
    @Override
    public Short call() throws Exception {
        return simulate();
    }

    private short simulate() {
        SimplePlayer player1 = new SimplePlayer();
        SimplePlayer player2 = new SimplePlayer();
        int numMoves = 0;
        int winningPlayer = 0;
        while (winningPlayer == 0) {
            int p1move = player1.getMoveColumn(board);
            board.makeMove(p1move);
            numMoves++;
            winningPlayer = board.getWinningPlayer();
            if (winningPlayer == 0) {
                int p2move = player2.getMoveColumn(board);
                board.makeMove(p2move);
                numMoves++;
                winningPlayer = board.getWinningPlayer();
            }
        }
        for (int i = 0; i < numMoves; i++) {
            board.undo();
        }
        return (short) winningPlayer;
    }

}
