//---------------------------------------------------------------------------------
// DO NOT MODIFY!!!
//---------------------------------------------------------------------------------
import java.util.ArrayList;
import java.util.List;

public class Board
{
    public final static int PLAYER1 = 1;
    public final static int PLAYER2 = -1;
    public final static int PLAYERNONE = 0;
    public final static int DRAW = 0;
    public final static int GAMENOTOVER = -2;
    public final static int ROWS = 6;
    public final static int COLS = 7;
    public static final int RIGHT = 0;
    public static final int UP = 1;
    public static final int UPLEFT = 2;

    private int nextPlayer;
    private int[][] playerAtPosition;
    private ArrayList<Integer> undoList;

    public Board()
    {
        undoList = new ArrayList<Integer>();
        nextPlayer = PLAYER1;
        playerAtPosition = new int[ROWS][COLS];
    }

    private Board(int nextPlayer, int[][] playerAtPosition, ArrayList<Integer> undoList) {
        this.nextPlayer = nextPlayer;
        this.playerAtPosition = playerAtPosition;
        this.undoList = undoList;
    }
    
    public int getPlayerAt(int row, int col)
    {
        return playerAtPosition[row][col];
    }

    public boolean isColumnFull(int col) {
        return (playerAtPosition[playerAtPosition.length - 1][col] != PLAYERNONE);
    }

    public void makeMove(int col) {
        for (int iRow = 0; iRow < playerAtPosition.length; iRow++)
        {
            if (playerAtPosition[iRow][col] == 0)
            {
                undoList.add(col);
                playerAtPosition[iRow][col] = nextPlayer;
                nextPlayer *= -1;
                return;
            }
        }
        for (col = 0; col < 7; col++) {
            for (int iRow = 0; iRow < playerAtPosition.length; iRow++)
            {
                if (playerAtPosition[iRow][col] == 0) {
                    undoList.add(col);
                    playerAtPosition[iRow][col] = nextPlayer;
                    nextPlayer *= -1;
                    return;
                }
            }
        }
    }
    
    public void undo() {
        if (undoList.size() == 0)
            return;
        int col = undoList.remove(undoList.size() - 1);
        for (int row = ROWS - 1; row >= 0; row--)
        {
            if (playerAtPosition[row][col] != PLAYERNONE)
            {
                playerAtPosition[row][col] = PLAYERNONE;
                nextPlayer *= -1;
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
        for (int iRow = 0; iRow < ROWS; iRow++)
        {
            for (int iCol = 0; iCol < COLS; iCol++)
            {
                int player = playerAtPosition[iRow][iCol];
                if (player != 0)
                {
                    if (doesHorizontal4StartHere(iRow, iCol) ||
                    		doesVertical4StartHere(iRow, iCol) ||
                    		doesDiagonalLeft4StartHere(iRow, iCol) ||
                    		doesDiagonalRight4StartHere(iRow, iCol))
                    {
                        return player;
                    }
                }
            }
        }

        boolean allColumnsFull = true;
        for (int iCol = 0; iCol < COLS; iCol++)
        {
            if (!isColumnFull(iCol))
            {
                allColumnsFull = false;
            }
        }
        if (allColumnsFull)
        {
            // No one has won, but the game is over.  A draw!
            return DRAW;
        }

        // No winner yet
        return GAMENOTOVER;
    }

    public int getMyPlayer() {
        int nonPLAYERNONE = 0;
        int myPlayer;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (getPlayerAt(row, col) != 0) {
                    nonPLAYERNONE += 1;
                }
            }
        }
        if (nonPLAYERNONE % 2 == 0) {
            myPlayer = PLAYER1;
        } else {
            myPlayer = PLAYER2;
        }
        return myPlayer;
    }

    public boolean doesHorizontal4StartHere(int iRow, int iCol)
    {
        if (iCol + 3 >= COLS)
        {
            return false;
        }

        return (getPlayerAt(iRow, iCol) == getPlayerAt(iRow, iCol + 1) &&
                getPlayerAt(iRow, iCol) == getPlayerAt(iRow, iCol + 2) &&
                getPlayerAt(iRow, iCol) == getPlayerAt(iRow, iCol + 3));
    }

    public boolean doesVertical4StartHere(int iRow, int iCol)
    {
        if (iRow + 3 >= ROWS)
        {
            return false;
        }

        return (getPlayerAt(iRow, iCol) == getPlayerAt(iRow + 1, iCol) &&
                getPlayerAt(iRow, iCol) == getPlayerAt(iRow + 2, iCol) &&
                getPlayerAt(iRow, iCol) == getPlayerAt(iRow + 3, iCol));
    }

    public boolean doesDiagonalLeft4StartHere(int iRow, int iCol)
    {
        if (iRow + 3 >= ROWS || iCol - 3 < 0)
        {
            return false;
        }

        return (getPlayerAt(iRow, iCol) == getPlayerAt(iRow + 1, iCol - 1) &&
                getPlayerAt(iRow, iCol) == getPlayerAt(iRow + 2, iCol - 2) &&
                getPlayerAt(iRow, iCol) == getPlayerAt(iRow + 3, iCol - 3));
    }

    public boolean doesDiagonalRight4StartHere(int iRow, int iCol)
    {
        if (iRow + 3 >= ROWS || iCol + 3 >= COLS)
        {
            return false;
        }

        return (getPlayerAt(iRow, iCol) == getPlayerAt(iRow + 1, iCol + 1) &&
                getPlayerAt(iRow, iCol) == getPlayerAt(iRow + 2, iCol + 2) &&
                getPlayerAt(iRow, iCol) == getPlayerAt(iRow + 3, iCol + 3));
    }
    
    public Board makeCopy() {
        int[][] position = new int[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int player = getPlayerAt(row, col);
                position[row][col] = player;
            }
        }
        ArrayList<Integer> undos = new ArrayList<Integer>(undoList);
        return new Board(nextPlayer, position, undos);
    }
    
    public List<Short> get1DBoard() {
        List<Short> position = new ArrayList<>(ROWS * COLS);
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int player = getPlayerAt(row, col);
                position.add(row * COLS + col, (short) player);
            }
        }
        return position;
    }

    public List<Short> getFlippedBoard() {
        List<Short> position = new ArrayList<>(ROWS * COLS);
        for (int row = 0; row < ROWS; row++) {
            for (int col = COLS - 1; col >= 0; col--) {
                int player = getPlayerAt(row, col);
                position.add((short) player);
            }
        }
        return position;
    }
}
