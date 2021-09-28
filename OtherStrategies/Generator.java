package connectFour;
import java.util.concurrent.Callable;

public class Generator implements Callable<String[]> {

    public String[] generate() {
        PlayerFactory factory = new ProbabilityHeuristicPlayerFactory();
        PlayerFactory factory2 = new ProbabilityHeuristicPlayerFactory();
        Player player1 = factory.createPlayer(new Object[]{});
        Player player2 = factory.createPlayer(new Object[]{});
        int winningPlayer = 0;
        Board board = new Board();
        for (int j = 0; j < 5; j++) {
            int move = (int) (Math.random() * 7);
            while ((board.isColumnFull(move))) {
                move = (int) (Math.random() * 7);
            }
            int p1move = player1.getMoveColumn(board);
            board.makeMove(move);

            move = (int) (Math.random() * 7);
            while ((board.isColumnFull(move))) {
                move = (int) (Math.random() * 7);
            }
            int p2move = player2.getMoveColumn(board);
            board.makeMove(move);
        }
        while (winningPlayer == 0) {
            int p1move = player1.getMoveColumn(board);
            board.makeMove(p1move);
            winningPlayer = board.getWinningPlayer();
            if (winningPlayer == 0) {
                int p2move = player2.getMoveColumn(board);
                board.makeMove(p2move);
                winningPlayer = board.getWinningPlayer();
            }
        }

        String json1 = player1.getGenetics()[0].toString();
        String json2 = player2.getGenetics()[0].toString();
        return new String[]{json1, json2};
    }

    @Override
    public String[] call() {
        return generate();
    }
}

