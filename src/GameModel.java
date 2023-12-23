public class GameModel {

    private final int rows;
    private final int columns;
    private boolean[][] board;

    public GameModel(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.board = new boolean[rows][columns];
    }

    public void update() {
        boolean[][] newBoard = new boolean[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int neighbors = checkNeighbors(row,column);
                if (board[row][column]) {
                    newBoard[row][column] = neighbors == 2 || neighbors == 3;
                } else {
                    newBoard[row][column] = neighbors == 3;
                }
            }
        }
        board = newBoard;
    }

    private int checkNeighbors(int row, int column) {
        int neighbors = 0;
        for (int r = Math.max(0, row - 1); r <= Math.min(rows - 1, row + 1); r++) {
            for (int c = Math.max(0, column - 1); c <= Math.min(columns - 1, column + 1); c++) {
                if (board[r][c]) {
                    neighbors++;
                }
            }
        }
        if (board[row][column]) {
            neighbors--;
        }
        return neighbors;
    }

    public int getRows() {
        return rows;
    }
    public int getColumns() {
        return columns;
    }

    public boolean isHighlighted(int row, int column) {
        return board[row][column];
    }

    public void flip(int row, int column) {
        board[row][column] = !board[row][column];
    }

    public void clear() {
        board = new boolean[rows][columns];
    }
}
