package connectFour;

public class MLPHeuristicPlayerFactory implements PlayerFactory {
    @Override
    public float initializeParameter(int position) {
        if (position < (MLP.HIDDENNEURONS * 4) + MLP.INPUTNEURONS + 1) {
            float val = (float) Math.pow(10, 2 * (Math.random() - 0.5));
            if (Math.random() > 0.5) {
                val = val * -1;
            }
            return val;
        } else {
            return (float) (int) (Math.random() * 5589);
        }
    }

    @Override
    public float mutateParameter(float val, int position) {
        if (position < (MLP.HIDDENNEURONS * 4) + MLP.INPUTNEURONS + 1) {
            val = val * (float) Math.pow(10, (Math.random() - 0.5) * 0.4);
            if (Math.random() > 0.5) {
                val = val * -1;
            }
            return val;
        } else {
            return (float) (int) (Math.random() * MLP.INPUTNEURONS);
        }
    }

    @Override
    public int geneticLength() {
        return (MLP.HIDDENNEURONS * 6) + MLP.INPUTNEURONS + 1;
    }

    @Override
    public Player createPlayer(Object[] genetics) {
        return new HeuristicPlayer(genetics, new NeuralHeuristic(2, genetics, "MLP"));
    }
}
