import javax.swing.*;
import java.io.*;
import java.util.Arrays;

public class GameIO extends JComponent {

    private final static String PATH_TO_FILELIST = "./files/saveslist.txt";
    private final JFrame frame;
    private final GameBoard board;

    public GameIO(JFrame frame, GameBoard board) {
        this.frame = frame;
        this.board = board;
    }

    public void load() {
        Object[] listOfSaves = getSaves();
        String pathToLoad = (String) JOptionPane.showInputDialog(null,
                "Choose one", "Input",
                JOptionPane.INFORMATION_MESSAGE, null,
                listOfSaves, listOfSaves[0]);
        try {
            board.load("./files/" + pathToLoad + ".txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Click OK to continue", "Error Loading",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] getSaves() {
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
        String[] listOfSaves = reader.lines().toArray(String[]::new);
        try {
            reader.close();
        } catch (IOException ignored) {}
        return listOfSaves;
    }

    public void save() {
        String name = null;
        String[] saves = getSaves();
        while (name == null) {
            String potentialName = JOptionPane.showInputDialog(frame,"Input Save File Name",
                            "newSave",JOptionPane.QUESTION_MESSAGE);
            if (potentialName.matches("^\\S$")) {
                JOptionPane.showMessageDialog(frame, "Names must not have spaces\nClick OK to continue",
                        "Space in Name", JOptionPane.INFORMATION_MESSAGE);
            } else if (Arrays.asList(saves).contains(potentialName)){
                JOptionPane.showMessageDialog(frame, "Save with that name already exists\nClick OK to continue",
                        "File already exists", JOptionPane.INFORMATION_MESSAGE);
            } else {
                name = potentialName.toLowerCase();
            }
        }
        try {
            board.save("./files/" + name + ".txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_TO_FILELIST,true));
            writer.write(name);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSave() {
        // TO DO
    }

}
