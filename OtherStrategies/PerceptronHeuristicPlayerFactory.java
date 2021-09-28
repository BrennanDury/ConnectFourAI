package connectFour;

public class PerceptronHeuristicPlayerFactory implements PlayerFactory{

    @Override
    public float initializeParameter(int position) {
        return (float)(int)((Math.random() - 0.5) * 1000);
    }

    @Override
    public float mutateParameter(float val, int position) {
        long deviationRange =  ((long) (val) / 10) + 1;
        long deviation =  ((long) (Math.random() - 0.5)) * deviationRange * 2;
        long mutation = ((long) (val)) + deviation;
        if (mutation > Integer.MAX_VALUE) {
            mutation = Integer.MAX_VALUE;
        } else if (mutation < Integer.MIN_VALUE) {
            mutation = Integer.MIN_VALUE;
        }
        return (float) mutation;
    }

    @Override
    public int geneticLength() {
        return 5590;
    }

    @Override
    public Player createPlayer(Object[] genetics) {
        return new HeuristicPlayer(genetics, new NeuralHeuristic(8, genetics, "Perceptron"));
    }
}
