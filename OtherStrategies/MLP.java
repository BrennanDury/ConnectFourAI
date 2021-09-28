package connectFour;

public class MLP implements NeuralNetwork {
    private final Neuron[] hidden;
    private final Neuron outputNeuron;
    private int[][] variableConnections;
    public static final int INPUTNEURONS = 5589;
    public static final int HIDDENNEURONS = 500000;

    public MLP(Object[] parameters) {
        this.hidden = new Neuron[HIDDENNEURONS];
        int weightsDone = 0;
        for (int neuronIdx = 0; neuronIdx < this.hidden.length; neuronIdx++) {
            Object[] neuronWeights = new Object[3]; // each hidden neuron connects to 2 features and the bias
            for (int i = 0; i < neuronWeights.length; i++) {
                neuronWeights[i] = parameters[weightsDone];
                weightsDone++;
            }
            this.hidden[neuronIdx] = new Neuron(neuronWeights, new Tanh());
        }
        Object[] outputWeights = new Object[HIDDENNEURONS + INPUTNEURONS + 1];
        for (int i = 0; i < outputWeights.length; i++) {
            outputWeights[i] = parameters[weightsDone];
            weightsDone++;
        }
        this.outputNeuron = new Neuron(outputWeights, new Linear());

        this.variableConnections = new int[HIDDENNEURONS][2];
        for (int i = 0; i < parameters.length - weightsDone; i++) {
            this.variableConnections[i / 2][i % 2] = (int) parameters[i + weightsDone];
        }
    }

    public float process(Grid g) {
        Object[] features = getFeatures(g);
        Object[] featuresOutput = new Object[features.length + 1];
        for (int i = 0; i < features.length; i++) {
            featuresOutput[i] = features[i];
        }
        featuresOutput[features.length] = 1; // Bias

        Object[] hiddenOutput = new Object[this.hidden.length]; // Bias already accounted for
        for (int i = 0; i < this.hidden.length; i++) {
            Object[] featuresSelection = new Object[3];
            int feature1 = this.variableConnections[i][0];
            int feature2 = this.variableConnections[i][0];
            featuresSelection[0] = 1;
            featuresSelection[1] = featuresOutput[feature1];
            featuresSelection[2] = featuresOutput[feature2];
            hiddenOutput[i] = this.hidden[i].process(featuresSelection);
        }

        Object[] combinedOutput = new Object[featuresOutput.length + hiddenOutput.length];
        for (int i = 0; i < featuresOutput.length; i++) {
            combinedOutput[i] = featuresOutput[i];
        }
        for (int i = featuresOutput.length; i < combinedOutput.length; i++) {
            combinedOutput[i] = hiddenOutput[i - featuresOutput.length];
        }
        return this.outputNeuron.process(combinedOutput);
    }

    private Object[] getFeatures(Grid g) {
        Object[] features = new Object[5589];
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
