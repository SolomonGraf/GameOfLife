import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameBoard extends JComponent {

    private GameSquare[][] squares;
    private GameModel model;
    private boolean paused;

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
            model.flip(row,column);
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
        this.model = new GameModel(rows,columns);

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
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800,800);
    }

    public void handle(MouseEvent e) {
        for (int row = 0; row < model.getRows(); row++) {
            for (int column = 0; column < model.getColumns(); column++) {
                if (squares[row][column].contains(e.getX(), e.getY())) {
                    squares[row][column].handle();
                }
            }
        }
        repaint();
    }

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

    public boolean isPaused() {
        return paused;
    }
}
