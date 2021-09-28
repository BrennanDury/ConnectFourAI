package connectFour;

//---------------------------------------------------------------------------------
// DO NOT MODIFY!!!
//
// This file contains methods you MAY find useful when writing your tournament
// player, but you don't have to use any of these if you don't want to.
//
//---------------------------------------------------------------------------------

public class GridUtilities 
{
	private Grid grid;
	
	public GridUtilities(Grid gridP)
	{
		grid = gridP;
	}
	
	
    public boolean doesHorizontal4StartHere(int iRow, int iCol)
    {
        if (iCol + 3 >= grid.getCols())
        {
            return false;
        }

        return (grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow, iCol + 1) &&
                grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow, iCol + 2) &&
                grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow, iCol + 3));
    }

    public boolean doesVertical4StartHere(int iRow, int iCol)
    {
        if (iRow + 3 >= grid.getRows())
        {
            return false;
        }

        return (grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow + 1, iCol) &&
                grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow + 2, iCol) &&
                grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow + 3, iCol));
    }

    public boolean doesDiagonalLeft4StartHere(int iRow, int iCol)
    {
        if (iRow + 3 >= grid.getRows() || iCol - 3 < 0)
        {
            return false;
        }

        return (grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow + 1, iCol - 1) &&
                grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow + 2, iCol - 2) &&
                grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow + 3, iCol - 3));
    }

    public boolean doesDiagonalRight4StartHere(int iRow, int iCol)
    {
        if (iRow + 3 >= grid.getRows() || iCol + 3 >= grid.getCols())
        {
            return false;
        }

        return (grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow + 1, iCol + 1) &&
                grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow + 2, iCol + 2) &&
                grid.getPlayerAt(iRow, iCol) == grid.getPlayerAt(iRow + 3, iCol + 3));
    }

    /**
     * You may find this useful when implementing your heuristic board score
     * method. This searches for chains of 1, 2, 3, or 4-in-a-row, starting at
     * the specified row and column, and moving in the specified direction. It
     * returns the length of the chain, as well as whether the chain is
     * surrounded by one or two spaces
     * 
     * @param row
     *            The row of the space to start scanning
     * @param col
     *            The column of the space to start scanning
     * @param direction
     *            The direction to scan: RIGHT, UP, UPLEFT (diagonal), or
     *            UPRIGHT (diagonal)
     * 
     * @return This method returns several values, all in a single int array.
     *         The array always has 4 elements as follows:
     *         [0] = length of chain found
     *         [1] = number of spaces surrounding the chain (either 0, 1, or 2)
     *         ([2], [3]) = the row and column of one of the spaces surrounding the chain.
     *         If no spaces surround the chain, these are just (-1, -1)
     */
    public int[] getLengthAndSpaces(int iRow, int iCol, int direction)
    {
        int player = grid.getPlayerAt(iRow, iCol);
        if (direction == Grid.RIGHT)
        {
            int[] locs = { iRow, iCol - 1, iRow, iCol, iRow, iCol + 1, iRow, iCol + 2, iRow, iCol + 3, iRow, iCol + 4 };
            return getLengthAndSpacesHelper(player, locs);
        }
        if (direction == Grid.UP)
        {
            int[] locs = { iRow - 1, iCol, iRow, iCol, iRow + 1, iCol, iRow + 2, iCol, iRow + 3, iCol, iRow + 4, iCol };
            return getLengthAndSpacesHelper(player, locs);
        }
        if (direction == Grid.UPLEFT)
        {
            int[] locs = { iRow - 1, iCol + 1, iRow, iCol, iRow + 1, iCol - 1, iRow + 2, iCol - 2, iRow + 3, iCol - 3, iRow + 4, iCol - 4 };
            return getLengthAndSpacesHelper(player, locs);
        }
        
        // UPRIGHT is the only direction remaining
        {
            int[] locs = { iRow - 1, iCol - 1, iRow, iCol, iRow + 1, iCol + 1, iRow + 2, iCol + 2, iRow + 3, iCol + 3, iRow + 4, iCol + 4 };
            return getLengthAndSpacesHelper(player, locs);
        }
    }
    
    // Private helper function to avoid duplicated code in getLengthAndSpaces
    private int[] getLengthAndSpacesHelper(int player, int[] locs)
    {
        // Special case: Check first for 4-in-a-row except one space plugs a hole in the middle
        // Treat it like 3-in-a-row plus one empty space
        if (safeGetPlayerAt(locs[2], locs[3]) == player &&
                safeGetPlayerAt(locs[8], locs[9]) == player)
        {
            if (safeGetPlayerAt(locs[4], locs[5]) == player &&
                    safeGetPlayerAt(locs[6], locs[7]) == Grid.PLAYEREMPTY)
            {
                return new int[] { 3, 1, locs[6], locs[7] };
            }
            if (safeGetPlayerAt(locs[4], locs[5]) == Grid.PLAYEREMPTY &&
                    safeGetPlayerAt(locs[6], locs[7]) == player )
            {
                return new int[] { 3, 1, locs[4], locs[5] };
            }
        }

        int length = 0;
        int spaces = 0;
        int spaceRow = -1, spaceCol = -1;
        int beforeChain = safeGetPlayerAt(locs[0], locs[1]);
        if (beforeChain == player)
        {
            // We already encountered this chain, so don't score it
            return new int[] { 0, 0, spaceRow, spaceCol };
        }

        if (beforeChain == Grid.PLAYEREMPTY)
        {
            spaces++;
            spaceRow = locs[0];
            spaceCol = locs[1];
        }

        int i;
        for (i = 2; i < locs.length - 3; i += 2)
        {
            int playerCur =  safeGetPlayerAt(locs[i], locs[i + 1]);
            if (playerCur != player)
            {
                break;
            }
            length++;
        }

        if (safeGetPlayerAt(locs[i], locs[i + 1]) == Grid.PLAYEREMPTY)
        {
            spaces++;
            spaceRow = locs[i];
            spaceCol = locs[i + 1];
        }

        return new int[] { length, spaces, spaceRow, spaceCol };
    }

    private int safeGetPlayerAt(int row, int col)
    {
        if (row < 0 || row >= grid.getRows() ||
                col < 0 || col >= grid.getCols())
        {
            return -1;
        }

        return grid.getPlayerAt(row, col);
    }
    public int getLengthCorrectly(int iRow, int iCol, int direction)
    {
    	int player = grid.getPlayerAt(iRow, iCol);
        if (direction == Grid.RIGHT)
        {
            int[] locs = { iRow, iCol - 1, iRow, iCol, iRow, iCol + 1, iRow, iCol + 2, iRow, iCol + 3, iRow, iCol + 4 };
            return getLengthCorrectlyHelper(player, locs);
        }
        if (direction == Grid.UP)
        {
            int[] locs = { iRow - 1, iCol, iRow, iCol, iRow + 1, iCol, iRow + 2, iCol, iRow + 3, iCol, iRow + 4, iCol };
            return getLengthCorrectlyHelper(player, locs);
        }
        if (direction == Grid.UPLEFT)
        {
            int[] locs = { iRow - 1, iCol + 1, iRow, iCol, iRow + 1, iCol - 1, iRow + 2, iCol - 2, iRow + 3, iCol - 3, iRow + 4, iCol - 4 };
            return getLengthCorrectlyHelper(player, locs);
        }
        
        // UPRIGHT is the only direction remaining
        {
            int[] locs = { iRow - 1, iCol - 1, iRow, iCol, iRow + 1, iCol + 1, iRow + 2, iCol + 2, iRow + 3, iCol + 3, iRow + 4, iCol + 4 };
            return getLengthCorrectlyHelper(player, locs);
        }
    }
    private int getLengthCorrectlyHelper(int player, int[] locs)
    {
    	int length = 0;
    	for (int i = 2; i<locs.length;i+=2)
    	{		
    		if (safeGetPlayerAt(locs[i], locs[i+1]) == player)
    		{
    			length++;
    		}
    		else if (safeGetPlayerAt(locs[i], locs[i+1]) != Grid.PLAYEREMPTY)
    		{
    			return 0;
    		}
    	}
    	return length;
    }
}
