import java.util.Scanner;

public class Interact {
    public static void main(String[] args) {
        AlphaZero ai = new AlphaZero();
        Board board = new Board();
        int winningPlayer = 0;
        Scanner sc = new Scanner(System.in);
        int move;
        if (Math.random() > 0.5) {
            System.out.println("Enter a move");
            move = Integer.parseInt(sc.next());
            board.makeMove(move);
            winningPlayer = board.getWinningPlayer();
        }
        while (winningPlayer == 0) {
            move = ai.getMoveColumn(board);
            board.makeMove(move);
            winningPlayer = board.getWinningPlayer();
            if (winningPlayer == 0) {
                System.out.println("Enter a move");
                move = Integer.parseInt(sc.next());
                board.makeMove(move);
                winningPlayer = board.getWinningPlayer();
            }
        }
    }
}
