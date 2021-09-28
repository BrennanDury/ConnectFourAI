package connectFour;

import java.util.Arrays;

public class Perceptron implements NeuralNetwork {
    private final Neuron neuron;

    public Perceptron(Object[] weights) {
        this.neuron = new Neuron(weights, new Linear());
    }

    @Override
    public float process(Grid g) {
        Object[] features = getFeatures(g);
        return neuron.process(features);
    }

    private Object[] getFeatures(Grid g) {
        Object[] features = new Object[5590];
        Arrays.fill(features, 0);
        int myPlayer = g.getMyPlayer();
        int validFours = 0;
        for (int row = 0; row < Grid.ROWS; row++) {
            for (int col = 0; col < Grid.COLS; col++) {
                for (int direction = 0; direction < 4; direction++) {
                    float[] four = getFour(g, row, col, direction, myPlayer);
                    int encoding = encoding(four);
                    if (encoding != -1) {
                        features[validFours * 81 + encoding] = 1;
                        validFours++;
                    }
                }
            }
        }
        features[features.length - 1] = 1;
        return features;
    }

    private float[] getFour(Grid g, int row, int col, int dir, int myPlayer) {
        float[] four = new float[4];
        int dx;
        int dy;
        if (dir == Grid.RIGHT) {
            dx = 1;
            dy = 0;
        } else if (dir == Grid.UP) {
            dx = 0;
            dy = 1;
        } else if (dir == Grid.UPLEFT) {
            dx = -1;
            dy = 1;
        } else {
            dx = 1;
            dy = 1;
        }
        for (int i = 0; i < 4; i++) {
            int x = row + dx * i;
            int y = col + dy * i;
            if (x >= 0 && x < Grid.COLS && y >= 0 && y < Grid.ROWS) {
                int playerAt = g.getPlayerAt(y, x);
                if (myPlayer == playerAt) {
                    playerAt = 1;
                } else if (playerAt != Grid.PLAYEREMPTY) {
                    playerAt = 2;
                }
                four[i] = playerAt;
            } else {
                four[i] = -1;
            }
        }
        return four;
    }

    private int encoding(float[] four) {
        if (four[0] != -1 && four[1] != -1 && four[2] != -1 && four[3] != -1) {
            return (int) (four[0] * Math.pow(3, 0) +
                    four[1] * Math.pow(3, 1) +
                    four[2] * Math.pow(3, 2) +
                    four[3] * Math.pow(3, 3));
        } else {
            return -1;
        }
    }
}
