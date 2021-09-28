package connectFour;

import java.util.Random;

public class GlobalRandom {
    private static final GlobalRandom INSTANCE = new GlobalRandom();
    private Random rand;

    private GlobalRandom() {
        this.rand = new Random();
    }

    public static GlobalRandom getRandomInstance() {
        return INSTANCE;
    }

    public int randInt(int bound) {
        return this.rand.nextInt(bound);
    }
}
