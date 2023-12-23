public class Selection {
    private final int row;
    private final int column;
    private final int height;
    private final int length;

    public Selection(int row, int column, int height, int length) {
        this.row = row;
        this.column = column;
        this.height = height;
        this.length = length;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }
}
