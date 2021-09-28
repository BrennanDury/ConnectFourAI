package connectFour;

public class NeuralHeuristic extends Minimax {
    private final NeuralNetwork network;
    public NeuralHeuristic(int maxdepth, Object[] vector, String networkType) {
        super(maxdepth, vector);
        if (networkType.equals("MLP")) {
            network = new MLP(vector);
        } else {
            network = new Perceptron(vector);
        }
    }

    protected float getHeuristicScore(Grid g, int myPlayer) {
        return this.network.process(g);
    }

    @Override
    public Object[] getGenetics() {
        return this.vector;
    }

}
