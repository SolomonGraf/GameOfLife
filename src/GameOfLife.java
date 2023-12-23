import javax.swing.*;
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
        JButton copyButton = new JButton("Copy");

        ButtonGroup modeButtons = new ButtonGroup();
        JRadioButton selectButton = new JRadioButton("Select", false);
        JRadioButton manualButton = new JRadioButton("Manual", true);
        JRadioButton pasteButton = new JRadioButton("Paste", false);

        JLabel delaySliderLabel = new JLabel("Delay: ");
        JSlider delaySlider = new JSlider(100,300,200);

        modeButtons.add(selectButton);
        modeButtons.add(manualButton);
        modeButtons.add(pasteButton);

        JPanel toolbar = new JPanel(new FlowLayout());
        toolbar.add(clearButton);
        toolbar.add(startStopButton);
        toolbar.add(nextButton);
        toolbar.add(copyButton);
        toolbar.add(selectButton);
        toolbar.add(manualButton);
        toolbar.add(pasteButton);
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
        copyButton.addActionListener(e -> gameBoard.copy());

        selectButton.addActionListener(e -> gameBoard.setCopyMode());
        manualButton.addActionListener(e -> gameBoard.setManualMode());
        pasteButton.addActionListener(e -> gameBoard.setPasteMode());

        gameBoard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameBoard.isPaused()) {
                    gameBoard.startStop();
                    startStopButton.setText("Start");
                    gameBoard.handle(e);
                } else if (gameBoard.inPasteMode()) {
                    manualButton.setSelected(true);
                    gameBoard.setManualMode();
                }
            }
        });

        delaySlider.addChangeListener(e -> timer.setDelay(delaySlider.getValue()));

        return toolbar;
    }

}
