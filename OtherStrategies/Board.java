//---------------------------------------------------------------------------------
// DO NOT MODIFY!!!
//---------------------------------------------------------------------------------

package connectFour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Board implements Grid
{
    private int nextPlayer;
    private int[][] playerAtPosition;
    private ArrayList<Integer> undoList;

    public Board()
    {
        undoList = new ArrayList<Integer>();
        nextPlayer = connectFour.Grid.PLAYER1;
        playerAtPosition = new int[getRows()][getCols()];
    }

    private Board(int nextPlayer, int[][] playerAtPosition, ArrayList<Integer> undoList) {
        this.nextPlayer = nextPlayer;
        this.playerAtPosition = playerAtPosition;
        this.undoList = undoList;
    }

    public int getRows()
    {
        return connectFour.Grid.ROWS;
    }

    public int getCols()
    {
        return connectFour.Grid.COLS;
    }


    @Override
    public int getPlayerAt(int row, int col)
    {
        return playerAtPosition[row][col];
    }

    @Override
    public boolean isColumnFull(int col)
    {
        return (playerAtPosition[playerAtPosition.length - 1][col] != connectFour.Grid.PLAYEREMPTY);
    }

    public void makeMove(int col)
    {
        for (int iRow = 0; iRow < playerAtPosition.length; iRow++)
        {
            if (playerAtPosition[iRow][col] == 0)
            {
                undoList.add(col);
                playerAtPosition[iRow][col] = nextPlayer;
                nextPlayer = 3 - nextPlayer;
                return;
            }
        }
        for (col = 0; col < 7; col++) {
            for (int iRow = 0; iRow < playerAtPosition.length; iRow++)
            {
                if (playerAtPosition[iRow][col] == 0)
                {
                    undoList.add(col);
                    playerAtPosition[iRow][col] = nextPlayer;
                    nextPlayer = 3 - nextPlayer;
                    return;
                }
            }
        }
    }
    
    public void undo()
    {
        if (undoList.size() == 0)
            return;

        // Get most recently-played column
        int col = undoList.remove(undoList.size() - 1);
        
        // Get highest row played in that column, so we know which checker to remove
        for (int row = getRows() - 1; row >= 0; row--)
        {
            if (playerAtPosition[row][col] != connectFour.Grid.PLAYEREMPTY)
            {
                playerAtPosition[row][col] = connectFour.Grid.PLAYEREMPTY;
                nextPlayer = 3 - nextPlayer;
                return;
            }
        }
    }

    public int getNextPlayer()
    {
        return nextPlayer;
    }

    public int getWinningPlayer()
    {
    	GridUtilities utilities = new GridUtilities(this);
    	
        for (int iRow = 0; iRow < getRows(); iRow++)
        {
            for (int iCol = 0; iCol < getCols(); iCol++)
            {
                int player = playerAtPosition[iRow][iCol];
                if (player != 0)
                {
                    if (utilities.doesHorizontal4StartHere(iRow, iCol) ||
                    		utilities.doesVertical4StartHere(iRow, iCol) ||
                    		utilities.doesDiagonalLeft4StartHere(iRow, iCol) ||
                    		utilities.doesDiagonalRight4StartHere(iRow, iCol))
                    {
                        return player;
                    }
                }
            }
        }

        // Is board completely full, even though there's no winner?
        boolean allColumnsFull = true;
        for (int iCol = 0; iCol < getCols(); iCol++)
        {
            if (!isColumnFull(iCol))
            {
                allColumnsFull = false;
            }
        }
        if (allColumnsFull)
        {
            // No one has won, but the game is over.  A draw!
            return Grid.DRAW;
        }

        // No winner yet
        return Grid.PLAYEREMPTY;
    }

    public int getMyPlayer() {
        int nonEmpty = 0;
        int myPlayer;
        for (int row = 0; row < Grid.ROWS; row++) {
            for (int col = 0; col < Grid.COLS; col++) {
                if (getPlayerAt(row, col) != 0) {
                    nonEmpty += 1;
                }
            }
        }
        if (nonEmpty % 2 == 0) {
            myPlayer = 1;
        } else {
            myPlayer = 2;
        }
        return myPlayer;
    }

    @Override
    public Grid makeCopy() {
        int[][] position = new int[getRows()][getCols()];
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                position[row][col] = getPlayerAt(row, col);
            }
        }
        ArrayList<Integer> undos = new ArrayList<Integer>();
        for (int undo : undoList) {
            undos.add(undo);
        }

        return new Board(this.nextPlayer, position, undos);
    }

    @Override
    public long getBoardState() {
        StringBuilder bits = new StringBuilder(64);
        for (int col = 0; col < connectFour.Grid.COLS; col++) {
            int height = 0;
            for (int row = 0; row < connectFour.Grid.ROWS; row++) {
                int player = getPlayerAt(row, col);
                if (player != connectFour.Grid.PLAYEREMPTY) {
                    height++;
                }
                bits.append(player % 2);
            }
            for (int i = 4; i >= 1; i /= 2) {
                if (height >= i) {
                    bits.append(1);
                } else {
                    bits.append(0);
                }
            }
        }
        return Long.parseLong(bits.toString(), 2);
    }

    @Override
    public List<Integer> get1DBoard() {
        List<Integer> position = new ArrayList<>(42);
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                position.add(row * 7 + col, getPlayerAt(row, col));
            }
        }
        return position;
    }
}
