import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GameModel {

    private final int rows;
    private final int columns;
    private boolean[][] board;
    private boolean[] births;
    private boolean[] survivals;

    private boolean[][] clipboard;

    public GameModel(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.board = new boolean[rows][columns];
        this.births = new boolean[]{false,false, true, false, false, false, false, false, false};
        this.survivals = new boolean[]{false,false, false, false, false, false, false, false, false};
    }

    public void update() {
        boolean[][] newBoard = new boolean[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int neighbors = checkNeighbors(row,column);
                if (board[row][column]) {
                    newBoard[row][column] = survivals[neighbors];
                } else {
                    newBoard[row][column] = births[neighbors];
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

    public void rules(String newRules) {
        if (!newRules.matches("^B[0-9]*/S[0-9]*$")) {
            throw new IllegalArgumentException("Must be in format B#/S#");
        }
        String[] birthRules = newRules.split("/")[0].substring(1).split("");
        String[] survivalRules = newRules.split("/")[1].substring(1).split("");
        for (int i = 0; i < births.length; i++) {
            births[i] = Arrays.asList(birthRules).contains(String.valueOf(i));
        }
        for (int i = 0; i < survivals.length; i++) {
            survivals[i] = Arrays.asList(survivalRules).contains(String.valueOf(i));
        }
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

    public void save(String filepath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
        for (boolean[] line : board) {
            StringBuilder string = new StringBuilder(line.length);
            for (boolean b : line) {
                string.append(b ? "0" : ".");
            }
            writer.write(string.toString());
            writer.newLine();
        }
        writer.close();
    }

    public void load(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        List<String> lines = reader.lines().toList();
        List<boolean[]> booleanLines = new ArrayList<>();
        for (String line : lines) {
            boolean[] values = new boolean[line.length()];
            for (int i = 0; i < line.length(); i++) {
                values[i] = String.valueOf(line.charAt(i)).equals("0");
            }
            booleanLines.add(values);
        }
        boolean[][] newBoard = new boolean[Math.max(rows,lines.get(0).length())][Math.max(columns,lines.size())];
        for (int row = 0; row < rows; row++) {
            System.arraycopy(booleanLines.get(row),0,newBoard[row],0,booleanLines.get(row).length);
        }
    }
}
