import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**  Board GUI for implementation with various games
 *   Author: Kirill Levin, Troy Vasiga, Chris Ingram
 */

public class Board extends JPanel
{
  private static final int X_DIM = 60;
  private static final int Y_DIM = 60;
  private static final int X_OFFSET = 30;
  private static final int Y_OFFSET = 30;
  private static final double MIN_SCALE = 0.25;
  private static final int GAP = 10;
  private static final int FONT_SIZE = 16;
  private static final int SIZE=703;
  // Grid colours
  private static final Color GRID_COLOR_A = new Color(84,137,139);
  private static final Color GRID_COLOR_B = new Color(103,156,158);
  
  // Preset colours for pieces
  private static final Color[] COLOURS = 
    {Color.YELLOW, Color.BLUE, Color.CYAN, Color.GREEN, 
     Color.PINK, Color.WHITE, Color.RED, Color.ORANGE };
  
  // String used to indicate each colour
  private static final String[] COLOUR_NAMES = 
  {"yellow", "blue", "cyan", "green", "pink", "white", "red", "orange"};
  
  // Colour to use if a match is not found
  private static final Color DEFAULT_COLOUR = Color.BLACK;
  
  private Color[][] grid;
  private Coordinate lastClick;  // How the mouse handling thread communicates 
                                 // to the board where the last click occurred
  private String message = "";
  private int numLines = 0;
  private int[][] line = new int[4][100];  // maximum number of lines is 100
  private int columns, rows;
  
  private boolean first = true;
  private int originalWidth;
  private int originalHeight;
  private double scale;
  
  /** A constructor to build a 2D board.
   */
  public Board(int rows, int cols)
  {
    super( true );
    JFrame f = new JFrame( "Board game" );
    
    this.columns = cols;
    this.rows = rows;
    originalWidth = 2*X_OFFSET+X_DIM*cols;
    originalHeight = 2*Y_OFFSET+Y_DIM*rows+GAP+FONT_SIZE;
    
    this.setPreferredSize( new Dimension( originalWidth, originalHeight ) );
                                          
    f.setResizable(true);
    this.grid = new Color[cols][rows];
    // *********************my code**********
//    JPanel jp1=new JPanel();
//    jp1.setBackground(Color.ORANGE);
//    jp1.setPreferredSize(new Dimension(100, 100));
//
//    f.add(jp1,BorderLayout.EAST);
//    javax.swing.JButton jbu1 = new javax.swing.JButton("Cancel Placement");
//    jp1.add(jbu1);
//
//    javax.swing.JButton jbu2 = new javax.swing.JButton("Robot");
//    jp1.add(jbu2);
//
//    javax.swing.JButton jbu3 = new javax.swing.JButton("1V1");
//    jp1.add(jbu3);

    //this.setBackground(Color.BLACK);

//    JButton button = new JButton("ok");
//
//    f.setSize(1000,1000);
//
//    f.setLocationRelativeTo(null);
//
//    // 向frame中添加一个按钮
//    f.add(button);
//    f.setVisible(true);

    //f.add(this,BorderLayout.CENTER);
    //****************************



    this.addMouseListener(
      new MouseInputAdapter() 
      {
        /** A method that is called when the mouse is clicked
         */
        public void mouseClicked(MouseEvent e) 
        { 
          int x = (int)e.getPoint().getX();
          int y = (int)e.getPoint().getY();
      
          // We need to by synchronized to the parent class so we can wake
          // up any threads that might be waiting for us
          synchronized(Board.this)
          {
            int curX = (int)Math.round(X_OFFSET*scale);
            int curY = (int)Math.round(Y_OFFSET*scale);
            int nextX = (int)Math.round((X_OFFSET+X_DIM*grid.length)*scale);
            int nextY = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale);
            
            // Subtract one from high end so clicks on the black edge
            // don't yield a row or column outside of board because of
            // the way the coordinate is calculated.
            if (x >= curX && y >= curY && x < nextX && y < nextY)
            {
              lastClick = new Coordinate(y,x);
              // Notify any threads that would be waiting for a mouse click
              Board.this.notifyAll() ;
            } /* if */
          } /* synchronized */
        } /* mouseClicked */
      } /* anonymous MouseInputAdapater */
    );
    
    
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setContentPane( this );
    f.pack();
    f.setVisible(true);
  }
  
  /** A constructor to build a 1D board.
   */
  public Board(int cols)
  {
    this(1, cols);
  }
 
  private void paintText(Graphics g)
  {
    g.setColor( this.getBackground() );
    g.setFont(new Font(g.getFont().getFontName(), Font.ITALIC+Font.BOLD, (int)(Math.round(FONT_SIZE*scale))));
    
    int x = (int)Math.round(X_OFFSET*scale);
    int y = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale + GAP  ) ;
    
    g.fillRect(x,y, this.getSize().width, (int)Math.round(GAP+FONT_SIZE*scale) );
    g.setColor( Color.black );
    g.drawString(message, x, y + (int)Math.round(FONT_SIZE*scale));
  }
  
  private void paintGrid(Graphics g)
  {
    super.paintComponent(g); // fix ghosting problem
    for (int i = 0; i < this.grid.length; i++)
    {
      for (int j = 0; j < this.grid[i].length; j++)
      {    
        if ((i%2 == 0 && j%2 != 0) || (i%2 != 0 && j%2 == 0))
          g.setColor(GRID_COLOR_A);
        else
          g.setColor(GRID_COLOR_B);
        int curX = (int)Math.round((X_OFFSET+X_DIM*i)*scale);
        int curY = (int)Math.round((Y_OFFSET+Y_DIM*j)*scale);
        int nextX = (int)Math.round((X_OFFSET+X_DIM*(i+1))*scale);
        int nextY = (int)Math.round((Y_OFFSET+Y_DIM*(j+1))*scale);
        int deltaX = nextX-curX; 
        int deltaY = nextY-curY;
                                   
        g.fillRect( curX, curY, deltaX, deltaY );
        Color curColour = this.grid[i][j];
        if (curColour != null) // Draw pegs if they exist
        {
          g.setColor(curColour);
          g.fillOval(curX+deltaX/4, curY+deltaY/4, deltaX/2, deltaY/2);
        }
      }
    }
    ((Graphics2D) g).setStroke( new BasicStroke(0.5f) );
    g.setColor(Color.BLACK);
    int curX = (int)Math.round(X_OFFSET*scale);
    int curY = (int)Math.round(Y_OFFSET*scale);
    int nextX = (int)Math.round((X_OFFSET+X_DIM*grid.length)*scale);
    int nextY = (int)Math.round((Y_OFFSET+Y_DIM*grid[0].length)*scale);
    g.drawRect(curX, curY, nextX-curX, nextY-curY);
  }
  
  private void drawLine(Graphics g)
  {
    for (int i =0; i < numLines; i++ ) 
    {
      ((Graphics2D) g).setStroke( new BasicStroke( 5.0f*(float)scale) );
      g.drawLine( (int)Math.round((X_OFFSET+X_DIM/2.0+line[0][i]*X_DIM)*scale), 
                  (int)Math.round((Y_OFFSET+Y_DIM/2.0+line[1][i]*Y_DIM)*scale), 
                  (int)Math.round((X_OFFSET+X_DIM/2.0+line[2][i]*X_DIM)*scale), 
                  (int)Math.round((Y_OFFSET+Y_DIM/2.0+line[3][i]*Y_DIM)*scale) );
    }
  }
  
  /**
   * Convert a String to the corresponding Color defaulting to Black 
   * with an invald input
   */
  public Color convertColour( String theColour )
  {

    for( int i=0; i<COLOUR_NAMES.length; i++ )
    {
      if( COLOUR_NAMES[i].equalsIgnoreCase( theColour ) ){
        //System.out.println(COLOURS[i]);
        return COLOURS[i];
      }
    }
    
    return DEFAULT_COLOUR;
  }
 
  
  /** The method that draws everything
   */
  public void paintComponent( Graphics g ) 
  {
    this.setScale();
    this.paintGrid(g);
    this.drawLine(g);
    this.paintText(g);
    //this.paint(g);
  }
  
  public void setScale()
  {
    double width = (0.0+this.getSize().width) / this.originalWidth;
    double height = (0.0+this.getSize().height) / this.originalHeight;
    this.scale = Math.max( Math.min(width,height), MIN_SCALE ); 
  }
  
  /** Sets the message to be displayed under the board
   */
  public void displayMessage(String theMessage)
  {
    message = theMessage;
    this.repaint();
  }
  
  
  /** This method will save the value of the colour of the peg in a specific 
    * spot.  theColour is restricted to 
    *   "yellow", "blue", "cyan", "green", "pink", "white", "red", "orange"  
    * Otherwise the colour black will be used. 
    */
  public void putPeg(String theColour, int row, int col)
  {
    this.grid[col][row] = this.convertColour( theColour );
    this.repaint();
  }
  
  /** Same as putPeg above but for 1D boards
   */
  public void putPeg(String theColour, int col)
  {
    this.putPeg( theColour, 0, col );
  }
  
  /** Remove a peg from the gameboard.
   */
  public void removePeg(int row, int col)
  {
    this.grid[col][row] = null;
    repaint();
  }
  
  /** Same as removePeg above but for 1D boards
   */
  public void removePeg(int col)
  {
    this.grid[col][0] = null;
    repaint();
  }
   
  /** Draws a line on the board using the given co-ordinates as endpoints
   */
  public void drawLine(int row1, int col1, int row2, int col2)
  {
    this.line[0][numLines]=col1;
    this.line[1][numLines]=row1;
    this.line[2][numLines]=col2;
    this.line[3][numLines]=row2;
    this.numLines++;
    repaint();
  }

  /** Removes one line from a board given the co-ordinates as endpoints
   * If there is no such line, nothing happens
   * If multiple lines, all copies are removed
   */
  
  public void removeLine(int row1, int col1, int row2, int col2) 
  {
    int curLine = 0;
    while (curLine < this.numLines) 
    {
      // Check for either endpoint being specified first in our line table
      if ( (line[0][curLine] == col1 && line[1][curLine] == row1 &&
            line[2][curLine] == col2 && line[3][curLine] == row2)   || 
           (line[2][curLine] == col1 && line[3][curLine] == row1 &&
            line[0][curLine] == col2 && line[1][curLine] == row2) )
      {
        // found a matching line: overwrite with the last one
        numLines--;
        line[0][curLine] = line[0][numLines];
        line[1][curLine] = line[1][numLines];
        line[2][curLine] = line[2][numLines];
        line[3][curLine] = line[3][numLines];
        curLine--; // perhaps the one we copied is also a match
      }
      curLine++;
       
    }
    repaint();
  }
  
  /** Waits for user to click somewhere and then returns the click.
   */
  public Coordinate getClick()
  {
      Coordinate returnedClick = null;
      synchronized(this) {
          lastClick = null;
          while (lastClick == null)
          {
              try {
                  this.wait();
              } catch(Exception e) {
                  // We'll never call Thread.interrupt(), so just consider
                  // this an error.
                  e.printStackTrace();
                  System.exit(-1) ;
              } /* try */
          }
    
          int col = (int)Math.floor((lastClick.getCol()-X_OFFSET*scale)/X_DIM/scale);
          int row = (int)Math.floor((lastClick.getRow()-Y_OFFSET*scale)/Y_DIM/scale);
          
          // Put this into a new object to avoid a possible race.
          returnedClick = new Coordinate(row,col);
      }
      return returnedClick;
  }  
  
  /** Same as getClick above but for 1D boards
   */
  public int getPosition()
  {
    return this.getClick().getCol();
  }
  
  public int getColumns()
  {
    return this.grid.length;
  }
    
  public int getRows()
  {
    return this.grid[0].length;
  }
  // ************************* my code content******************************
  // The boolean variable player indicates whether the sunspot round
  public boolean CheckWiner(int row, int col, boolean player){

    Color color=(player)?this.convertColour("black"):this.convertColour("white");
    if(CheckRow(row,col,color)) return true;
    if(CheckCol(row,col,color)) return true;
    if(CheckLeftDig(row,col,color)) return true;
    return CheckRightDig(row, col, color);
  }
  private boolean CheckRow(int row,int col, Color color){
    int sum=1;
    //Scan left to find the number of pieces with the same color as the current position on the left
    //If you encounter chess pieces with different colors, jump out of the loop immediately
    for(int i=col-1;i>=0;i--){
      boolean test2=compareColor(this.grid[i][row],color);
      if(compareColor(this.grid[i][row],color)){
        sum++;
      }else {
        break;
      }
    }
    //Scan right to find the number of pieces with the same color as the current position on the right
    //If you encounter chess pieces with different colors, jump out of the loop immediately
    for(int i=col+1;i<this.columns;i++){
      boolean test=compareColor(this.grid[i][row],color);
      if(compareColor(this.grid[i][row],color)){
        sum++;
      }else {
        break;
      }
    }
    return sum >= 5;
  }

  private boolean CheckCol(int row,int col, Color color){
    int sum=1;
    //Scan up to find the number of pieces with the same color as the current position
    //If you encounter chess pieces with different colors, jump out of the loop immediately
    for(int i=row-1;i>=0;i--){
      if(compareColor(this.grid[col][i],color)){
        sum++;
      }else {
        break;
      }
    }
    //Scan down to find the number of pieces with the same color as the current position on the right
    //If you encounter chess pieces with different colors, jump out of the loop immediately
    for(int i=row+1;i<this.rows;i++){
      if(compareColor(this.grid[col][i],color)){
        sum++;
      }else {
        break;
      }
    }
    return sum >= 5;
  }
  private boolean CheckLeftDig(int row,int col, Color color){
    int sum=1;
    //Scan up and left to find the number of pieces with the same color as the current position
    //If you encounter chess pieces with different colors, jump out of the loop immediately
    int init=row-col>0?col:row;
    for(int i=1;i<=init;i++){
      if(col-i<0) break;
      if(row-i<0) break;
      if(compareColor(this.grid[col-i][row-i],color)){
        sum++;
      }else {
        break;
      }
    }
    //Scan left and down to find the number of chess pieces with the same color as the current position
    //If you encounter chess pieces with different colors, jump out of the loop immediately
    init=row-col>0?row:col;
    for(int i=1;i<=this.columns-init;i++){
      if(col+i>=15) break;
      if(row+i>=15) break;
      if(compareColor(this.grid[col+i][row+i],color)){
        sum++;
      }else {
        break;
      }
    }
    return sum >= 5;
  }

  private boolean CheckRightDig(int row,int col, Color color){
    int sum=1;
    //Scan up and right to find the number of pieces with the same color as the current position
    //If you encounter chess pieces with different colors, jump out of the loop immediately
    int init=row-col>0?row:col;
    for(int i=1;i<=init;i++){
      if(col+i>=15) break;
      if(row-i<0) break;
      if(compareColor(this.grid[col+i][row-i],color)){
        sum++;
      }else {
        break;
      }
    }
    //Scan right and down to find the number of chess pieces with the same color as the current position
    //If you encounter chess pieces with different colors, jump out of the loop immediately
    init=row-col>0?col:row;
    for(int i=1;i<=init;i++){
      if(col-i<0) break;
      if(row+i>=15) break;
      if(compareColor(this.grid[col-i][row+i],color)){
        sum++;
      }else {
        break;
      }
    }
    return sum >= 5;
  }


//  public Color[][] getBoard(){
//    return this.grid;
//  }

  public boolean compareColor(Color c1,Color c2){
    if(c1==null) return false;
    return c1.getBlue()==c2.getBlue() && c1.getRed()==c2.getRed() && c1.getGreen()==c2.getGreen();
  }
  // Check the position to see if chess pieces can be placed
  public boolean CheckPosition(int row, int col){
    return this.grid[col][row]==null;
  }
  //****************************** end code********************************
}
