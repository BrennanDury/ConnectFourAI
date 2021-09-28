//---------------------------------------------------------------------------------
// DO NOT MODIFY!!!
//---------------------------------------------------------------------------------

package connectFour;

import java.util.Scanner;

public class InteractivePlayer implements Player
{
    private Scanner console;
    private String playerName;
    
    public InteractivePlayer()
    {
        console = new Scanner(System.in);
        System.out.println("Please enter your name.");
        playerName = console.nextLine();
    }
    
    @Override
    public int getMoveColumn(Grid g)
    {
        int col = -1;
        while (col == -1)
        {
            System.out.println(playerName + ": Which column would you like to play?");
            String colStr = console.nextLine();
            try
            {
                col = Integer.parseInt(colStr);
            }
            catch (NumberFormatException e)
            {
                System.out.println(playerName + ": '" + colStr + "' is not a column NUMBER.  Try again.");
                continue;
            }
            
            if (col < 0 || col >= g.getCols())
            {
                System.out.println(playerName + ": Please enter a column number between 0 and " + (g.getCols() - 1));
                col = -1;
            }
            else if (g.isColumnFull(col))
            {
                System.out.println(playerName + ": That column is full.  Please stop wasting my time and pick another one.");
                col = -1;
            }
        }
        
        return col;
    }

    @Override
    public String getPlayerName()
    {
        return playerName;
    }
    @Override
    public Object[] getGenetics() {
        return null;
    }
}
