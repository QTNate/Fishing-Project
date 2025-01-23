import javax.swing.*;

public class App {

    public static void main(String[] args) throws Exception {
        int boardWidth = 800;
        int boardHeight = 600;

        // Create the game window (JFrame)
        JFrame frame = new JFrame("Fishing Game");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the Fishing panel that handles gameplay
        Fishing fishing = new Fishing(boardWidth, boardHeight);
        frame.add(fishing);
        frame.pack();
    }
}