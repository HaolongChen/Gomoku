import java.awt.*;
import javax.swing.*;

/**
 * Panel that provides controls for the Gomoku game
 */
public class ControlPanel extends JPanel {
    private GamePanel gamePanel;
    
    // UI components
    private JButton newGameButton;
    private JLabel titleLabel;
    private JLabel infoLabel;
    
    public ControlPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        
        // Set up the panel
        setPreferredSize(new Dimension(200, 600));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        setBackground(new Color(245, 245, 250));
        
        // Create and add UI components
        createComponents();
    }
    
    private void createComponents() {
        // Top section with game logo/title
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        
        titleLabel = new JLabel("GOMOKU");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("By Haolong");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(subtitleLabel);
        topPanel.add(Box.createVerticalStrut(40));
        
        // Center section with game info
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        // Game info
        JLabel rulesTitle = new JLabel("Game Rules:");
        rulesTitle.setFont(new Font("Arial", Font.BOLD, 16));
        rulesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextArea rulesText = new JTextArea("""
            • Place stones on intersections
            • Black goes first (you)
            • Connect 5 stones in a row
              (horizontally, vertically, or diagonally)
            • First to connect 5 wins!
            """);
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setFont(new Font("Arial", Font.PLAIN, 14));
        rulesText.setOpaque(false);
        rulesText.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        rulesText.setAlignmentX(Component.LEFT_ALIGNMENT);
        rulesText.setFocusable(false); // Disable focus
        
        // Info label for game status
        infoLabel = new JLabel("You play as Black");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setForeground(new Color(50, 120, 50));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        centerPanel.add(rulesTitle);
        centerPanel.add(rulesText);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(infoLabel);
        
        // Bottom section with buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        
        newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 16));
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.setMaximumSize(new Dimension(150, 40));
        newGameButton.addActionListener(e -> gamePanel.resetGame());
        
        // Style the button
        newGameButton.setBackground(new Color(70, 130, 180));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFocusPainted(false);
        newGameButton.setBorderPainted(false);
        
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setMaximumSize(new Dimension(150, 35));
        exitButton.addActionListener(e -> System.exit(0));
        
        // Style the exit button
        exitButton.setBackground(new Color(200, 200, 200));
        exitButton.setForeground(Color.BLACK);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        
        bottomPanel.add(Box.createVerticalGlue());
        bottomPanel.add(newGameButton);
        bottomPanel.add(Box.createVerticalStrut(15));
        bottomPanel.add(exitButton);
        bottomPanel.add(Box.createVerticalStrut(30));
        
        // Add all sections to the panel
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Update the game status information
     */
    public void updateStatus(String status) {
        infoLabel.setText(status);
    }
}
