package connectFour;

//---------------------------------------------------------------------------------
// DO NOT MODIFY!!!
//---------------------------------------------------------------------------------

import java.util.List;

/**
 * Represents the grid of a Connect Four game.
 * Note: The first position in a column has row index 0, which means that when you display the
 * Grid you'll show the rows in order of decreasing index, which is the opposite of TextExcel.
 * For example, consider a game that starts with these four moves:
 * Player 1 moves in column 2, which drops to row 0
 * Player 2 moves in column 3, which drops to row 0
 * Player 1 moves in column 3, which drops to row 1
 * Player 2 moves in column 3, which drops to row 2
 * The grid would look like this:
 * |   |   |   |   |   |   |   |
 * |   |   |   |   |   |   |   |
 * |   |   |   |   |   |   |   |
 * |   |   |   | 2 |   |   |   |
 * |   |   |   | 1 |   |   |   |
 * |   |   | 1 | 2 |   |   |   |
 *  --- --- --- --- --- --- ---
 *   0   1   2   3   4   5   6 
 *   
 */
public interface Grid
{
    // Note: interfaces can define constants, although it's not in the AP subset
    // For example, you can use Grid.PLAYEREMPTY rather than 0 in your code,
    // just like class constants
    public static final int PLAYEREMPTY = 0; // player number for empty cell
    public static final int PLAYER1 = 1; // player number for first player
    public static final int PLAYER2 = 2; // player number for second player
    public static final int DRAW = -1; // returned by getWinningPlayer() if the
                                       // game ended in a draw

    // Directions, used in getLengthAndSpaces
    public static final int RIGHT = 0;
    public static final int UP = 1;
    public static final int UPLEFT = 2;
    public static final int UPRIGHT = 3;

    public static final int ROWS = 6;
    public static final int COLS = 7;


    /**
     * Gets the player number (1 or 2) at the given row and column, or 0 if that
     * cell is empty.
     * 
     * @param row
     *            the row index (0 is bottom)
     * @param col
     *            the column index (0 is left)
     * @return the player number (1 or 2) at the given row and column, or 0 if
     *         empty
     */
    public int getPlayerAt(int row, int col);

    /**
     * Checks whether the given column is full.
     * 
     * @param col
     *            the column index
     * @return true if the column is full, otherwise false
     */
    public boolean isColumnFull(int col);

    /**
     * These return the number of rows & columns on the board
     */
    public int getRows();
    public int getCols();

    /**
     * Returns the player number whose move is next
     */
    public int getNextPlayer();

    /**
     * If this Grid is a finished game, this returns the winner (PLAYER1,
     * PLAYER2, or DRAW)
     * 
     * If this Grid is not yet a finished game, this returns PLAYEREMPTY
     */
    public int getWinningPlayer();
    
    // -----------------------------------------------------------------------
    // The methods below are generally only useful when you implement lookahead
    // For simpler Player algorithms, the methods below are not useful
    // -----------------------------------------------------------------------

    /**
     * NOTE!!! Your Player should NOT call this. Your Player should simply
     * return the move it chooses to make from getMoveColumn(), and the Runner
     * will take care of calling this
     * 
     * -----------------------------------------------------------------------
     * 
     * Except... Your Player may want to call this to help implement look-ahead.
     * But be very careful about resetting the Grid to its original state before
     * you return from getMoveColumn(). You may use the undo() method below to
     * help you
     * 
     * -----------------------------------------------------------------------
     * 
     * Adds a piece in the specified column at the lowest available row, with
     * the color of the next player (see getNextPlayer()). Returns false if you
     * try to make an illegal move.
     * 
     * @param col
     *            The column to move in. The Grid determines which row the piece
     *            will end up in.
     * @return Returns true if the move is legal. Otherwise returns false and
     *         does not make the move.
     */
    public void makeMove(int col);

    /**
     * All moves are recorded in an "undo" list, so that any number of moves may
     * be undone. This undoes the most recent move. This has no effect if no one
     * has moved yet.
     * 
     */
    public void undo();

    public int getMyPlayer();

    public Grid makeCopy();

    public long getBoardState();

    public List<Integer> get1DBoard();
}
