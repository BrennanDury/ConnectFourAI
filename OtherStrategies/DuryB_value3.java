package connectFour;

public class DuryB_value3 implements connectFour.Player
	{
		
		@Override
		public int getMoveColumn(Grid g)
		{
			if (g.getCols()==1)
			{
				return 0;
			}
			// TODO Auto-generated method stub
			boolean empty = true;
			for ( int i = 0; i<g.getCols();i++)
			{
				if (i!=3&&g.getPlayerAt(0, i)!=0)
				{	
					empty=false;
				}	
			}
			if (empty&&g.getPlayerAt(g.getRows()-2, 3)==Grid.PLAYEREMPTY)
			{
				return (3);
			}
			return minimaxGetScore(g,6,1)[1];
		}

		@Override
		public String getPlayerName()
		{
			// TODO Auto-generated method stub
			return "DuryB_value3";
		}

	    // Returns an array of two integers: [0] is the score for this grid, and
	    // [1] is the recommended column to move in for this grid. 
	    private int[] minimaxGetScore(Grid g, int remainingDepth, int myPlayer)
	    {
	        // Did this move end the game?  If so, score it now based on whether we won.
	        if (g.getWinningPlayer() == myPlayer)
	        {
	            // We won!
	            return new int[] { 1000 * (remainingDepth + 1), -1 };
	        }
	        else if (g.getWinningPlayer() == (3 - myPlayer))
	        {
	            // They won
	            return new int[] { -1000 * (remainingDepth + 1), -1 };
	        }
	        else if (g.getWinningPlayer() == -1)
	        {
	            // Game ends in a draw.
	            return new int[] { 0, -1 };
	        }

	        int nextPlayer = g.getNextPlayer();

	        // We don't want to go any deeper, so just return the immediate heuristic score
	        // for this board
	        if (remainingDepth <= 0)
	        {
	            // TODO: FOR YOU TO DO!  WRITE THIS getHeuristicScore METHOD
	            // TO EXAMINE THE GRID AND COME UP WITH A NUMERIC SCORE FOR IT.
	            // THE SCORE SHOULD BE FROM THE POINT OF VIEW OF YOUR PLAYER
	            // (HIGH VALUES MEANS GOOD FOR YOU, LOW VALUES MEAN BAD FOR YOU).
	            // THEN REPLACE '= 1' WITH '= getHeuristicScore(g)'
	            int score = getHeuristicScore(g);

	            return new int[] { score, -1 };
	        }

	        // Call self recursively for next player's moves' scores
	        
	        // Is this nextPlayer trying to minimize or maximize the score?  If it's us,
	        // maximize.  If opponent, minimize.
	        boolean isMax = (nextPlayer == myPlayer);
	        int bestMove = -1;
	        int bestScore;
	        if (isMax)
	        {
	            bestScore = Integer.MIN_VALUE;
	        }
	        else
	        {
	            bestScore = Integer.MAX_VALUE;
	        }        

	        for (int nextCol = 0; nextCol < g.getCols(); nextCol++)
	        {
	            if (!g.isColumnFull(nextCol))
	            {
	                // Apply the move (temporarily) to the grid...
	                g.makeMove(nextCol);
	                
	                // ... and then call ourselves recursively to move down the decision tree
	                // and come up with a score                
	                int scoreCur = minimaxGetScore(g, remainingDepth - 1, myPlayer)[0];
	                
	                // ... and we must remember to UNDO that move now that the call is done.
	                g.undo();
	                
	                // Update bestScore with what the recursive call returned
	                if (isMax)
	                {
	                    if (scoreCur > bestScore)
	                    {
	                        bestScore = scoreCur;
	                        bestMove = nextCol;
	                    }
	                }
	                else
	                {
	                    // minimizing!
	                    if (scoreCur < bestScore)
	                    {
	                        bestScore = scoreCur;
	                        bestMove = nextCol;
	                    }
	                }
	            }
	        }

	        // Return the best score (and the recommended move)
	        return new int[] { bestScore, bestMove };        
	    }
	    public int getHeuristicScore(Grid g)
	    {
	    	int score = 0;
	    	boolean pos = true;
	    	int np = g.getNextPlayer();
	    	if (np==2)	
	    		pos = false;
	    	int twos = 0;
	    	int threes = 0;
	    	int fours = 0;
	    	int ones = 0;

	    	for (int i = 0; i<g.getRows();i++)
	    	{
	        	for (int j = 0; j<g.getCols();j++)
	        	{
	        		for (int direction = 0; direction<=3;direction++)
	        		{	
	        			if (getLengthCorrectly(g,i, j, direction,np)==1)
	        			{
							ones++;
	        			}
	        			if (getLengthCorrectly(g,i, j, direction,np)==2)
	        			{
	        				twos++;
						}
	        			if (getLengthCorrectly(g,i, j, direction,np)==3)
	        			{
							threes++;
	        			}
	        			if (getLengthCorrectly(g,i, j, direction,np)==4)
	        			{
	        				fours++;
	        			}
	        		}
				}
	    	}
	            		
	    	if (pos)
	    		score += (fours*1000000) + (threes*1000) + (twos*100) + (ones*10);
	    	else
	    	{
	    		score -= (fours*999999) + (threes*999)+ (twos*99) + (ones*9);
	    	}
	    	return score;
	    }
	    public int getLengthCorrectly(Grid g, int iRow, int iCol, int direction, int player)
	    {
	    	if (player == g.getPlayerAt(iRow, iCol))
	    	{
	        if (direction == Grid.RIGHT)
	        {
	            int[] locs = { iRow, iCol, iRow, iCol+1, iRow, iCol + 2, iRow, iCol + 3 };
	            return getLengthCorrectlyHelper(g,player, locs);
	        }
	        if (direction == Grid.UP)
	        {
	            int[] locs = { iRow, iCol, iRow+1, iCol, iRow + 2, iCol, iRow + 3, iCol };
	            return getLengthCorrectlyHelper(g,player, locs);
	        }
	        if (direction == Grid.UPLEFT)
	        {
	            int[] locs = { iRow , iCol , iRow+1, iCol-1, iRow + 2, iCol - 2, iRow + 3, iCol - 3, };
	            return getLengthCorrectlyHelper(g,player, locs);
	        }
	        
	        // UPRIGHT is the only direction remaining
	        {
	            int[] locs = { iRow, iCol, iRow+1, iCol+1, iRow + 2, iCol + 2, iRow + 3, iCol + 3 };
	            return getLengthCorrectlyHelper(g,player, locs);
	        }
	    	}
	    	return 0;
	    }
	    private int safeGetPlayerAt(Grid g, int row, int col)
	    {
	        if (row < 0 || row >= g.getRows() ||
	                col < 0 || col >= g.getCols())
	        {
	            return -1;
	        }

	        return g.getPlayerAt(row, col);
	    }
	    private int getLengthCorrectlyHelper(Grid g, int player, int[] locs)
	    {
	    	int length = 0;
	    	for (int i = 0; i<locs.length;i+=2)
	    	{		
	    		if (safeGetPlayerAt(g,locs[i], locs[i+1]) == player)
	    		{
	    			length++;
	    		}
	    		else if (safeGetPlayerAt(g,locs[i], locs[i+1]) != Grid.PLAYEREMPTY)
	    		{
	    			return 0;
	    		}
	    	}
	    	return length;
	    }
		@Override
		public Object[] getGenetics() {
			return null;
		}
	}
	

