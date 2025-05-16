import javax.swing.SwingUtilities;

/**
 * Main class for the Gomoku game application
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame gameFrame = new GameFrame();
            gameFrame.setVisible(true);
        });
    }
}
