package connectFour;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrainingGround {

    public static void main(String[] args) {
        AlphaZero alphaZero = new AlphaZero();
        ExecutorService service = Executors.newSingleThreadExecutor();
        for (int iteration = 0; iteration < 10000; iteration++) {
            for (int game = 0; game < 1000; game++) {
                Runner.playGame(alphaZero, alphaZero);
            }
            service.submit(alphaZero.retrain());
        }

    }
}