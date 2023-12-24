import javax.swing.*;
import java.io.*;

public class GameIO extends JComponent {

    private final static String PATH_TO_FILELIST = "./files/saveslist.txt";
    private final JFrame frame;
    private final GameBoard board;

    public GameIO(JFrame frame, GameBoard board) {
        this.frame = frame;
        this.board = board;
    }

    public void load() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(PATH_TO_FILELIST));
        } catch (FileNotFoundException e) {
            Object[] options = { "OK" };
            JOptionPane.showMessageDialog(frame, "Click OK to continue", "Error Loading",
                    JOptionPane.ERROR_MESSAGE);
            File saves = new File(PATH_TO_FILELIST);
            try {
                reader = new BufferedReader(new FileReader(PATH_TO_FILELIST));
            } catch (FileNotFoundException fnfe) {
                throw new RuntimeException();
            }
        }
        Object[] listOfSaves = reader.lines().toArray();
        String pathToLoad = (String) JOptionPane.showInputDialog(null,
                "Choose one", "Input",
                JOptionPane.INFORMATION_MESSAGE, null,
                listOfSaves, listOfSaves[0]);
        try {
            board.load("./files/" + pathToLoad);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Click OK to continue", "Error Loading",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
