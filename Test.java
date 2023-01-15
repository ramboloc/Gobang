import javax.swing.*;
import java.awt.*;

public class Test {

    private static final String PLAY1="black";
    private static final String PLAY2="white";
    private static final int SIZE = 15;
    public static void main(String[] args) {
//        javax.swing.JFrame jf = new javax.swing.JFrame("test");
//
//        jf.setSize(800, 800);
//        jf.setDefaultCloseOperation(3);
//        jf.setLocationRelativeTo(null);
//        jf.setLayout(new BorderLayout());
        Board board = new Board(SIZE,SIZE);
//        jf.add(board,BorderLayout.EAST);
//        jf.setVisible(true);

        Coordinate loc;
        int row = 0,col = 0;

        boolean player=true;
        String play;
        String Winner;
        while(true){
            // Play is used to determine whether it is a black chess round. If play is true, it is a black chess round.
            // Otherwise, it is a white chess round
            play=player?PLAY1:PLAY2;

            board.displayMessage("click a location to place a peg.\n This is the "+play+" side round");
            // Select the position of the chess piece
            loc=board.getClick();
            // row num
            row=loc.getRow();
            // column num
            col=loc.getCol();
            //board.displayMessage("OK");
            // If there are no chess pieces in the entered position, place the chess pieces
            if(board.CheckPosition(row,col)){
                // Place chess pieces at the specified position
                board.putPeg(play, row, col);
                board.displayMessage(play+" round side round ends.");

                // Judge whether there is a winner. If there is a winner, the game ends, and vice versa, the game continues
                if(board.CheckWiner(row,col,player)){
                    System.out.println("win");
                    JOptionPane.showMessageDialog(null, play+" Win!");
                    break;
                }
                board.getClick();
                // Convert to opponent round
                player=!player;
            }
        }


    }

    public static boolean compareColor(Color c1,Color c2){
        return c1.getBlue()==c2.getBlue() && c1.getRed()==c2.getRed() && c1.getGreen()==c2.getGreen();
    }


}
