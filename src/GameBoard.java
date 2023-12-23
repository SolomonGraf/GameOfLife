import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameBoard extends JComponent implements Mode {

    private final GameSquare[][] squares;
    private final GameModel model;
    private boolean paused;
    private Mode mode;
    private Selection currentSelection;

    public class ManualMode implements Mode {
        @Override
        public void handle(MouseEvent e) {
            for (int row = 0; row < model.getRows(); row++) {
                for (int column = 0; column < model.getColumns(); column++) {
                    if (squares[row][column].contains(e.getX(), e.getY())) {
                        squares[row][column].handle();
                    }
                }
            }
        }
    }

    public class SelectStartMode implements Mode {
        @Override
        public void handle(MouseEvent e) {
            mode = new SelectEndMode(getArrayValue(e.getY()),getArrayValue(e.getX()));
        }
    }

    public class SelectEndMode implements Mode {

        private final int startRow;
        private final int startColumn;

        public SelectEndMode(int startRow, int startColumn) {
            this.startRow = startRow;
            this.startColumn = startColumn;
        }
        @Override
        public void handle(MouseEvent e) {
            int endRow = getArrayValue(e.getY());
            int endColumn = getArrayValue(e.getX());
            currentSelection = new Selection(Math.min(startRow,endRow),Math.min(startColumn,endColumn),
                    Math.abs(startRow - endRow),Math.abs(startColumn - endColumn));
            mode = new SelectStartMode();
        }

        public int getStartRow() {
            return startRow;
        }

        public int getStartColumn() {
            return startColumn;
        }
    }

    class GameSquare {
        private final int row;
        private final int column;

        public GameSquare(int row,int column) {
            this.row = row;
            this.column = column;
        }

        public void draw(Graphics g) {
            g.setColor(model.isHighlighted(row,column) ? Color.DARK_GRAY : Color.GRAY);
            g.fillRect(column*Constants.SQUARE_SIZE,row*Constants.SQUARE_SIZE,Constants.SQUARE_SIZE,Constants.SQUARE_SIZE);
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.drawRect(column*Constants.SQUARE_SIZE,row*Constants.SQUARE_SIZE,Constants.SQUARE_SIZE,Constants.SQUARE_SIZE);
            ((Graphics2D) g).setStroke(new BasicStroke(1));
        }

        // Handling

        public boolean contains(int x, int y) {
            return (x >= column * Constants.SQUARE_SIZE && x <= (column + 1) * Constants.SQUARE_SIZE
                    && y >= row * Constants.SQUARE_SIZE && y <= (row + 1) * Constants.SQUARE_SIZE );
        }

        public void handle() {
            if (mode.getClass().equals(ManualMode.class)) {
                model.flip(row, column);
            }
        }

    }

    public GameBoard() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        // add 1 to rows/columns to ensure "overflow"

        int rows = height/Constants.SQUARE_SIZE + 1;
        int columns = width/Constants.SQUARE_SIZE + 1;

        squares = new GameSquare[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                squares[row][column] = new GameSquare(row,column);
            }
        }

        this.paused = true;
        this.mode = new ManualMode();
        this.model = new GameModel(rows,columns);
        this.currentSelection = null;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (paused) {
                    handle(e);
                }
            }
        });

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        for (int row = 0; row < model.getRows(); row++) {
            for (int column = 0; column < model.getColumns(); column++) {
                squares[row][column].draw(g);
            }
        }

        if (mode.getClass().equals(SelectEndMode.class)) {
            g.setColor(Constants.OFF_WHITE);
            g.fillOval(((SelectEndMode) mode).getStartColumn() * Constants.SQUARE_SIZE - 10,
                    ((SelectEndMode) mode).getStartRow() * Constants.SQUARE_SIZE - 10, 20, 20);
        } else if (mode.getClass().equals(SelectStartMode.class)) {
            if (currentSelection != null) {
                g.setColor(Constants.OFF_WHITE);
                g.drawRect(currentSelection.getColumn() * Constants.SQUARE_SIZE,
                        currentSelection.getRow() * Constants.SQUARE_SIZE,
                        currentSelection.getLength() * Constants.SQUARE_SIZE,
                        currentSelection.getHeight() * Constants.SQUARE_SIZE);
            }
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800,800);
    }

    public void handle(MouseEvent e) {
        mode.handle(e);
        repaint();
    }

    // Button Calls

    public void update() {
        model.update();
        repaint();
    }

    public boolean startStop() {
        this.paused = !this.paused;
        return paused;
    }

    public void clear() {
        this.model.clear();
        repaint();
    }

    // Mode Changes

    public void setManualMode() {
        currentSelection = null;
        mode = new ManualMode();
    }

    public void setSelectMode() {
        mode = new SelectStartMode();
    }

    // Aux functions

    public boolean isPaused() {
        return paused;
    }

    public int getArrayValue(int v) {
        return v / Constants.SQUARE_SIZE;
    }

}
