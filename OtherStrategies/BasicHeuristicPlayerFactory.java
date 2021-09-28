package connectFour;
public class BasicHeuristicPlayerFactory implements PlayerFactory {

    @Override
    public float initializeParameter(int position) {
        return (float) Math.pow(10, 2 * (Math.random() - 0.5));
    }

    @Override
    public float mutateParameter(float val, int position) {
        return (float) (val * Math.pow(10, (Math.random() - 0.5) * 0.4));
    }

    @Override
    public int geneticLength() {
        return 4;
    }

    @Override
    public Player createPlayer(Object[] genetics) {
        return new HeuristicPlayer(genetics, new BasicHeuristic(7, genetics));
    }
}
