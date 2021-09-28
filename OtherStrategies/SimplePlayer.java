package connectFour;

public class SimplePlayer implements Player 
{
	@Override
	public int getMoveColumn(Grid g) 
	{
		GlobalRandom random = GlobalRandom.getRandomInstance();
		int move = random.randInt(7);
		while (g.isColumnFull(move))
		{
			move = random.randInt(7);
		}
		return move;
	}

	@Override
	public String getPlayerName() 
	{
		return "My Simple Player";
	}

	@Override
	public Object[] getGenetics() {
		return null;
	}
}
