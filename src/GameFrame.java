import java.awt.*;
import javax.swing.*;

/**
 * Main frame for the Gomoku game
 */
public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private ControlPanel controlPanel;
    
    public GameFrame() {
        setTitle("Modern Gomoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Initialize game components
        gamePanel = new GamePanel();
        controlPanel = new ControlPanel(gamePanel);
        
        // Set up layout
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Apply theme
        applyModernTheme();
        
        pack();
        setLocationRelativeTo(null);
    }
    
    /**
     * Apply modern visual theme to the game
     */
    private void applyModernTheme() {
        // Set custom colors and borders
        getRootPane().setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, new Color(50, 50, 50)));
    }
}
