import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GameBoard extends JComponent implements Mode {

    private final GameSquare[][] squares;
    private final GameModel model;
    private boolean paused;
    private Mode mode;
    private Selection currentSelection;

    private class ManualMode implements Mode {
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

    private class CopyStartMode implements Mode {
        @Override
        public void handle(MouseEvent e) {
            mode = new CopyEndMode(getArrayValue(e.getY()),getArrayValue(e.getX()));
        }
    }

    private class CopyEndMode implements Mode {

        private final int startRow;
        private final int startColumn;

        public CopyEndMode(int startRow, int startColumn) {
            this.startRow = startRow;
            this.startColumn = startColumn;
        }
        @Override
        public void handle(MouseEvent e) {
            int endRow = getArrayValue(e.getY()) + 1;
            int endColumn = getArrayValue(e.getX()) + 1;
            currentSelection = new Selection(Math.min(startRow,endRow),Math.min(startColumn,endColumn),
                    Math.abs(startRow - endRow),Math.abs(startColumn - endColumn));
            mode = new CopyStartMode();
            if (currentSelection != null){
                model.copy(currentSelection);
            }
        }

        public int getStartRow() {
            return startRow;
        }

        public int getStartColumn() {
            return startColumn;
        }
    }

    private class PasteMode implements Mode {
        @Override
        public void handle(MouseEvent e) {
            model.paste(getArrayValue(e.getY()),getArrayValue(e.getX()));
            repaint();
        }
    }

    private class GameSquare {
        private final int row;
        private final int column;

        public GameSquare(int row,int column) {
            this.row = row;
            this.column = column;
        }

        public void draw(Graphics g) {
//            int mouseRow = getArrayValue(MouseInfo.getPointerInfo().getLocation().y);
//            int mouseColumn = getArrayValue(MouseInfo.getPointerInfo().getLocation().x);
//            if (model.isPreviewed(row,column,mouseRow,mouseColumn) && inMode(PasteMode.class)) {
//                g.setColor(Constants.EPHEMERAL_DARKGREY);
//            } else if (model.isInPreview(row,column,mouseRow,mouseColumn) && inMode(PasteMode.class)) {
//                g.setColor(Constants.EPHEMERAL_GREY);
            if (model.isHighlighted(row,column)) {
                g.setColor(Color.DARK_GRAY);
            } else {
                g.setColor(Color.GRAY);
            }
            g.fillRect(column*Constants.SQUARE_SIZE,row*Constants.SQUARE_SIZE,
                    Constants.SQUARE_SIZE,Constants.SQUARE_SIZE);
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke(1.25F));
            g.drawRect(column*Constants.SQUARE_SIZE,row*Constants.SQUARE_SIZE,
                    Constants.SQUARE_SIZE,Constants.SQUARE_SIZE);
            ((Graphics2D) g).setStroke(new BasicStroke(1));
        }

        // Handling

        public boolean contains(int x, int y) {
            return (x >= column * Constants.SQUARE_SIZE && x <= (column + 1) * Constants.SQUARE_SIZE
                    && y >= row * Constants.SQUARE_SIZE && y <= (row + 1) * Constants.SQUARE_SIZE );
        }

        public void handle() {
            if (inMode(ManualMode.class)) {
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

        if (inMode(CopyEndMode.class)) {
            g.setColor(Constants.OFF_WHITE);
            g.fillOval(((CopyEndMode) mode).getStartColumn() * Constants.SQUARE_SIZE - 3,
                    ((CopyEndMode) mode).getStartRow() * Constants.SQUARE_SIZE - 3, 6, 6);
        } else if (inMode(CopyStartMode.class)) {
            if (currentSelection != null) {
                g.setColor(Constants.OFF_WHITE);
                ((Graphics2D) g).setStroke(new BasicStroke(1.5F));
                g.drawRect(currentSelection.column() * Constants.SQUARE_SIZE,
                        currentSelection.row() * Constants.SQUARE_SIZE,
                        currentSelection.length() * Constants.SQUARE_SIZE,
                        currentSelection.height() * Constants.SQUARE_SIZE);
                ((Graphics2D) g).setStroke(new BasicStroke(1));
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

    public void load(String filePath) throws IOException {
        model.load(filePath);
        repaint();
    }

    public void save(String filepath) throws IOException {
        model.save(filepath);
    }

    public void changeRules(String input) throws IllegalArgumentException {
        paused = true;
        model.rules(input);
    }

    // Mode Changes

    public void setManualMode() {
        currentSelection = null;
        mode = new ManualMode();
        repaint();
    }

    public void setCopyMode() {
        mode = new CopyStartMode();
        repaint();
    }

    public void setPasteMode() {
        mode = new PasteMode();
        repaint();
    }

    // Aux functions

    public boolean isPaused() {
        return paused;
    }

    private int getArrayValue(int v) {
        return v / Constants.SQUARE_SIZE;
    }

    private boolean inMode(Class<?> mode) {
        return this.mode.getClass().equals(mode);
    }
}
