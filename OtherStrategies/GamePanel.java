//---------------------------------------------------------------------------------
// DO NOT MODIFY!!!
//---------------------------------------------------------------------------------

package connectFour;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import connectFour.Grid;

/**
 * You may use this class, in addition to your own console output of the game state,
 * to see a nice graphical view of the same game state.  Here's how to use it:
 * 0. import graphics.GamePanel;
 * 1. Construct a new GamePanel once, and store a reference to it in a variable or field.
 *    Example: GamePanel gamePanel = new GamePanel();
 * 2. Whenever you print the board state to console output, also call the drawGrid method.
 *    Example: gamePanel.drawGrid(grid);
 */
public class GamePanel extends JPanel {
	private static final int cellSize = 30;
	private static final int spacingSize = 10;
	private static final long serialVersionUID = -7797149062100634188L;
    private int rows = Grid.ROWS;
    private int cols = Grid.COLS;
	private JFrame frame;
	private int[][] cells;
	
	public GamePanel() {
		Dimension dimension = new Dimension(
				cols * cellSize + (cols + 1) * spacingSize,
				(rows + 1) * (cellSize) + (cols + 2) * spacingSize
				);
		cells = new int[rows][cols];
		setPreferredSize(dimension);
		frame = new JFrame("Connect4");
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void drawGrid(Grid grid) {
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				cells[row][col] = grid.getPlayerAt(row, col);
		repaint();
	}
	
	private void paintCell(Graphics2D g, int row, int col, int player) {
		Color fill = Color.WHITE;
		Color border = Color.BLACK;
		if (player == 1) {
			fill = Color.RED;
			border = Color.RED;
		} else if (player == 2) {
			fill = Color.BLACK;
			border = Color.BLACK;
		}
		int baseX = col * (cellSize + spacingSize) + spacingSize;
		int baseY = (rows - 1 - row) * (cellSize + spacingSize) + spacingSize;
		g.setColor(fill);
		g.fillOval(baseX, baseY, cellSize, cellSize);
		g.setColor(border);
		g.drawOval(baseX, baseY, cellSize, cellSize);
		paintText(g, "" + player, row, col, Color.WHITE);
	}

	private void paintText(Graphics2D g, String text, int row, int col, Color color) {
		int baseX = col * (cellSize + spacingSize) + spacingSize;
		int baseY = (rows - 1 - row) * (cellSize + spacingSize) + spacingSize;
		g.setFont(g.getFont().deriveFont(Font.PLAIN, 18));
		FontMetrics metrics = g.getFontMetrics();
		int offsetX = (cellSize - metrics.stringWidth(text)) / 2;
		int offsetY = (cellSize - metrics.getHeight()) / 2 + metrics.getAscent();
		g.setColor(color);
		g.drawString(text, baseX + offsetX, baseY + offsetY);
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setBackground(Color.YELLOW);
		g2d.clearRect(0, 0, cols * (cellSize + spacingSize) + spacingSize, rows * (cellSize + spacingSize) + spacingSize);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				paintCell(g2d, row, col, cells[row][col]);
		for (int col = 0; col < cols; col++)
			paintText(g2d, "" + col, -1, col, Color.BLACK); // column numbers at the bottom
	}

	public void close() {
		frame.setVisible(false);
	}
}
