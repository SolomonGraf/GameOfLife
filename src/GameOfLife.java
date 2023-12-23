import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameOfLife implements Runnable {

    public void run() {
        final JFrame frame = new JFrame("Game of Life");
        ImageIcon icon = new ImageIcon(".idea/files/gameoflifeicon.png");
        frame.setIconImage(icon.getImage());
        final GameBoard gb = new GameBoard();
        frame.add(gb,BorderLayout.CENTER);
        frame.add(createButtonBar(gb), BorderLayout.NORTH);

        Timer timer = new Timer(200, e -> {
            if (!gb.isPaused()) {
                gb.update();
            }
        });
        timer.start();

        frame.setLocation(0, 0);
        frame.pack();
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel createButtonBar(GameBoard gameBoard) {
        JButton clearButton = new JButton("Clear");
        JButton startStopButton = new JButton("Start");
        JButton nextButton = new JButton("Next");

        JPanel toolbar = new JPanel(new FlowLayout());
        toolbar.add(clearButton);
        toolbar.add(startStopButton);
        toolbar.add(nextButton);

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameBoard.clear();
                if (!gameBoard.isPaused()) {
                    gameBoard.startStop();
                    startStopButton.setText("Start");
                }
            }
        });

        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean paused = gameBoard.startStop();
                startStopButton.setText(paused ? "Start" : "Stop");
            }
        });

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

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameBoard.isPaused()) {
                    gameBoard.update();
                }
            }
        });

        return toolbar;
    }

}
