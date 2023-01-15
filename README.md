# Introduce
This game is developed on the basis of Board.java and Coordinate.java. It mainly realizes the function of two-person combat.

# project structure
- `Board`: Checkerboard interface layout.
- `Coordinate`: Coordinate class.
- `Test`: Main program, realize the Gobang duel.

# Function
We have added some functions to Board.java. The main function of these functions is to detect whether a winner is generated.
Each time the chess pieces are placed, the winner is detected from the row, column, main diagonal and reverse diagonal directions.
- `compareColor(Color c1, Color c2)`: Detect whether the two colors are the same
- `CheckWiner(int row, int col, boolean player)`: Place chess pieces in (row, col) to detect whether a winner is generated.
- `CheckRow(int row,int col, Color color)`: Place chess pieces in (row, col) to detect whether there is a winner in row row.
- `CheckCol(int row,int col, Color color)`: Place chess pieces in (row, col) to detect whether there is a winner in col column.
- `CheckCol(int row,int col, Color color)`: Place chess pieces in (row, col) to detect whether there is a winner in the main diagonal direction.
- `CheckCol(int row,int col, Color color)`: Place chess pieces in (row, col) to detect whether there is a winner in the opposite diagonal direction.