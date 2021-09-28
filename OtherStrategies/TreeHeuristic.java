package connectFour;


import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TreeHeuristic extends Minimax {

    private Node root;

    protected TreeHeuristic(int maxdepth, Object[] vector) {
        super(maxdepth, vector);
        try {
            String leftString = Files.readString(Path.of
                    ("/Users/brennandury/IdeaProjects/ConnectFourAI/Model/src/left.txt"));
            String rightString = Files.readString(Path.of
                    ("/Users/brennandury/IdeaProjects/ConnectFourAI/Model/src/right.txt"));
            String featureString = Files.readString(Path.of
                    ("/Users/brennandury/IdeaProjects/ConnectFourAI/Model/src/feature.txt"));
            String valueString = Files.readString(Path.of
                    ("/Users/brennandury/IdeaProjects/ConnectFourAI/Model/src/value.txt"));
            String thresholdString = Files.readString(Path.of
                    ("/Users/brennandury/IdeaProjects/ConnectFourAI/Model/src/value.txt"));
            Gson gson = new Gson();
            int[] left = gson.fromJson(leftString, int[].class);
            int[] right = gson.fromJson(rightString, int[].class);
            int[] feature = gson.fromJson(featureString, int[].class);
            float[] value = gson.fromJson(valueString, float[].class);
            float[] threshold = gson.fromJson(thresholdString, float[].class);
            this.root = initializeNode(left, right, feature, value, threshold, 0);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(10);
        }
    }

    private Node initializeNode(int[] left, int[] right, int[] feature, float[] value, float[] threshold, int i) {
        if (i == -1) {
            return null;
        }
        return new Node(feature[i], value[i], threshold[i],
                initializeNode(left, right, feature, value, threshold, left[i]),
                initializeNode(left, right, feature, value, threshold, right[i]));
    }

    @Override
    protected float getHeuristicScore(Grid g, int myPlayer) {
        if (myPlayer == 1) {
            return root.evaluate(getFeatures(g));
        }
        return 1 - root.evaluate(getFeatures(g));
    }

    @Override
    public Object[] getGenetics() {
        return new Object[0];
    }

    private float[] getFeatures(Grid g) {
        float[] features = new float[5632];
        int myPlayer = g.getMyPlayer();
        int validFours = 0;
        for (int row = 0; row < Grid.ROWS; row++) {
            for (int col = 0; col < Grid.COLS; col++) {
                for (int direction = 0; direction < 4; direction++) {
                    float[] four = getFour(g, row, col, direction);
                    int encoding = encoding(four);
                    if (encoding != -1) {
                        features[validFours * 81 + encoding + 42] = 1;
                        validFours++;
                    }
                }
                if (g.getPlayerAt(row, col) == 1) {
                    features[row * 7 + col] = 1;
                } else if (g.getPlayerAt(row, col) == 0) {
                    features[row * 7 + col] = 0;
                } else {
                    features[row * 7 + col] = -1;
                }
            }
        }
        features[features.length - 1] = g.getNextPlayer();
        return features;
    }

    private float[] getFour(Grid g, int row, int col, int dir) {
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
                if (playerAt == 1) {
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

    private class Node {
        private Node left;
        private Node right;
        private int feature;
        private float value;
        private float threshold;

        public Node(int feature, float value, float threshold, Node left, Node right) {
            this.left = left;
            this.right = right;
            this.feature = feature;
            this.value = value;
            this.threshold = threshold;
        }

        public float evaluate(float[] features) {
            if (left == null && right == null) {
                return value;
            }
            if (features[feature] < threshold) {
                if (left == null) {
                    return value;
                }
                return left.evaluate(features);
            } else {
                if (right == null) {
                    return value;
                }
                return right.evaluate(features);
            }
        }
    }
}