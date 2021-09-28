package connectFour;

public class TreeHeuristicPlayerFactory implements PlayerFactory {
    @Override
    public float initializeParameter(int position) {
        return 0;
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
        return new HeuristicPlayer(genetics, new TreeHeuristic(1, genetics));
    }
}
