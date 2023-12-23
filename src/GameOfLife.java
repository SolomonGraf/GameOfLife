import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameOfLife implements Runnable {

    public void run() {
        final JFrame frame = new JFrame("Game of Life");
        ImageIcon icon = new ImageIcon(".idea/files/gameoflifeicon.png");
        frame.setIconImage(icon.getImage());
        final GameBoard gb = new GameBoard();
        frame.add(gb,BorderLayout.CENTER);

        Timer timer = new Timer(200, e -> {
            if (!gb.isPaused()) {
                gb.update();
            }
        });

        frame.add(createButtonBar(gb, timer), BorderLayout.NORTH);

        timer.start();

        frame.setLocation(0, 0);
        frame.pack();
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel createButtonBar(GameBoard gameBoard, Timer timer) {
        JButton clearButton = new JButton("Clear");
        JButton startStopButton = new JButton("Start");
        JButton nextButton = new JButton("Next");

        ButtonGroup modeButtons = new ButtonGroup();
        JRadioButton selectButton = new JRadioButton("Select", false);
        JRadioButton manualButton = new JRadioButton("Manual", true);
        JLabel delaySliderLabel = new JLabel("Delay: ");
        JSlider delaySlider = new JSlider(100,300,200);

        modeButtons.add(selectButton);
        modeButtons.add(manualButton);

        JPanel toolbar = new JPanel(new FlowLayout());
        toolbar.add(clearButton);
        toolbar.add(startStopButton);
        toolbar.add(nextButton);
        toolbar.add(selectButton);
        toolbar.add(manualButton);
        toolbar.add(delaySliderLabel);
        toolbar.add(delaySlider);

        clearButton.addActionListener(e -> {
            gameBoard.clear();
            if (!gameBoard.isPaused()) {
                gameBoard.startStop();
                startStopButton.setText("Start");
            }
        });
        startStopButton.addActionListener(e -> {
            boolean paused = gameBoard.startStop();
            startStopButton.setText(paused ? "Start" : "Stop");
            manualButton.setSelected(true);
            gameBoard.setManualMode();
        });
        nextButton.addActionListener(e -> {
            if (gameBoard.isPaused()) {
                gameBoard.update();
            }
        });

        selectButton.addActionListener(e -> gameBoard.setSelectMode());
        manualButton.addActionListener(e -> gameBoard.setManualMode());

        gameBoard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameBoard.isPaused()) {
                    gameBoard.startStop();
                    startStopButton.setText("Start");
                    gameBoard.handle(e);
                }
            }
        });

        delaySlider.addChangeListener(e -> timer.setDelay(delaySlider.getValue()));

        return toolbar;
    }

}
