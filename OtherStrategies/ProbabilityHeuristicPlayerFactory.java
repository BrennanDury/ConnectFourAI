package connectFour;

public class ProbabilityHeuristicPlayerFactory implements PlayerFactory {

    @Override
    public float initializeParameter(int position) {
        return (float) Math.pow(10, (2 * position) - 2);
    }

    @Override
    public float mutateParameter(float val, int position) {
        return 0;
    }

    @Override
    public int geneticLength() {
        return 0;
    }

    @Override
    public Player createPlayer(Object[] genetics) {
        return new HeuristicPlayer(genetics, new ProbabilityHeuristic(3, genetics));
    }
}
