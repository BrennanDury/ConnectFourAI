package connectFour;

public interface PlayerFactory {
    public float initializeParameter(int position);

    public float mutateParameter(float val, int position);

    public int geneticLength();

    public Player createPlayer(Object[] genetics);
}
