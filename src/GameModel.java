public class GameModel {

    private final int rows;
    private final int columns;
    private boolean[][] board;

    private boolean[][] clipboard;

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
    public boolean isInPreview(int row, int column, int mouseRow, int mouseColumn) {
        if (clipboard == null) {
            return false;
        }
        try {
            if (row >= mouseRow && row < mouseRow + clipboard.length
                    && column >= mouseColumn && column < mouseColumn + clipboard[0].length) {
                return true;
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    public boolean isPreviewed(int row, int column, int mouseRow, int mouseColumn) {
        if (clipboard == null) {
            return false;
        }
        try {
            if (row >= mouseRow && row < mouseRow + clipboard.length
                    && column >= mouseColumn && column < mouseColumn + clipboard[0].length) {
                return clipboard[mouseRow-row][mouseColumn-column];
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    public void flip(int row, int column) {
        board[row][column] = !board[row][column];
    }

    public void clear() {
        board = new boolean[rows][columns];
    }

    public void copy(Selection selection) {
        clipboard = new boolean[selection.height()][selection.length()];
        for (int row = 0; row < selection.height(); row++) {
            System.arraycopy(board[selection.row() + row],selection.column(),
                    clipboard[row],0,selection.length());
        }
    }

    public void paste(int row, int column) {
        if (clipboard != null && clipboard.length != 0 && clipboard[0].length != 0) {
            for (int i = 0; i < clipboard.length; i++) {
                System.arraycopy(clipboard[i], 0, board[row + i], column, clipboard[i].length);
            }
        }
    }
}
