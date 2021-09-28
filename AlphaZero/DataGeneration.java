import java.util.ArrayList;
import java.util.List;

public class DataGeneration {

    public static void main(String[] args) {
        AlphaZero alphaZero = new AlphaZero();
        List<List<Number>> data = new ArrayList<>();
        Board board = new Board();
        new PlayGame(board, alphaZero, data).run();
        System.out.println("File Saved");
    }
}