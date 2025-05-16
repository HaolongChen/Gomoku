import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Panel that displays and handles the Gomoku game board
 */
public class GamePanel extends JPanel {
    // Game constants
    public static final int BOARD_SIZE = 15; // 15x15 standard board
    public static final int CELL_SIZE = 40;  // Size of each cell in pixels
    public static final int BOARD_MARGIN = 30; // Margin around the board
    
    // Colors
    private final Color BACKGROUND_COLOR = new Color(240, 217, 181); // Wooden board color
    private final Color LINE_COLOR = new Color(90, 60, 40);  // Dark brown lines
    private final Color HOVER_COLOR = new Color(45, 45, 45, 150);  // Translucent hover highlight
    private final Color BLACK_STONE = new Color(45, 45, 45);  // Almost black
    private final Color WHITE_STONE = new Color(240, 240, 240);  // Almost white
    private final Color LAST_MOVE_INDICATOR = new Color(255, 80, 80, 180);  // Translucent red
    
    // Game state
    private int[][] board; // 0: empty, 1: black, 2: white
    private boolean isPlayerTurn = true; // Player goes first with black stones
    private boolean gameOver = false;
    private Point lastMove = null;
    private Point hoverPoint = null;
    
    // AI player
    private AI ai;
    
    public GamePanel() {
        // Initialize board
        board = new int[BOARD_SIZE][BOARD_SIZE];
        
        // Initialize AI
        ai = new AI();
        
        // Set panel size
        int boardWidth = BOARD_SIZE * CELL_SIZE + 2 * BOARD_MARGIN;
        int boardHeight = BOARD_SIZE * CELL_SIZE + 2 * BOARD_MARGIN;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        
        // Add mouse listeners for interaction
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameOver && isPlayerTurn) {
                    handlePlayerMove(e.getX(), e.getY());
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHoverPosition(e.getX(), e.getY());
            }
        });
    }
    
    /**
     * Update the position where the player is hovering
     */
    private void updateHoverPosition(int x, int y) {
        Point boardPos = getBoardPosition(x, y);
        if (boardPos != null && board[boardPos.y][boardPos.x] == 0) {
            hoverPoint = boardPos;
        } else {
            hoverPoint = null;
        }
        repaint();
    }
    
    /**
     * Handle a player's move on the board
     */
    private void handlePlayerMove(int x, int y) {
        Point boardPos = getBoardPosition(x, y);
        
        // Check if the click is on the board and the cell is empty
        if (boardPos != null && board[boardPos.y][boardPos.x] == 0) {
            // Place the player's stone (1 for black)
            board[boardPos.y][boardPos.x] = 1;
            lastMove = boardPos;
            
            // Check if the player won
            if (checkWin(boardPos.y, boardPos.x, 1)) {
                gameOver = true;
                JOptionPane.showMessageDialog(this, "You Win!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Switch turns
                isPlayerTurn = false;
                repaint();
                
                // Let AI make a move (after a short delay to make it feel more natural)
                Timer timer = new Timer(300, e -> {
                    makeAIMove();
                    ((Timer) e.getSource()).stop();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    /**
     * Have the AI make its move
     */
    private void makeAIMove() {
        if (!gameOver) {
            // Get AI's move
            Point aiMove = ai.getNextMove(board);
            
            if (aiMove != null) {
                // Place the AI's stone (2 for white)
                board[aiMove.y][aiMove.x] = 2;
                lastMove = aiMove;
                
                // Check if AI won
                if (checkWin(aiMove.y, aiMove.x, 2)) {
                    gameOver = true;
                    JOptionPane.showMessageDialog(this, "AI Wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                }
                
                // Switch turns back to player
                isPlayerTurn = true;
                repaint();
            }
        }
    }
    
    /**
     * Converts screen coordinates to board position
     * Returns null if outside the board
     */
    private Point getBoardPosition(int x, int y) {
        // Calculate board position from screen coordinates
        int boardX = (x - BOARD_MARGIN + CELL_SIZE/2) / CELL_SIZE;
        int boardY = (y - BOARD_MARGIN + CELL_SIZE/2) / CELL_SIZE;
        
        // Check if within board bounds
        if (boardX >= 0 && boardX < BOARD_SIZE && boardY >= 0 && boardY < BOARD_SIZE) {
            return new Point(boardX, boardY);
        }
        return null;
    }
    
    /**
     * Checks if the last move resulted in a win
     */
    private boolean checkWin(int row, int col, int player) {
        // Define the eight directions to check (horizontal, vertical, two diagonals)
        int[][] directions = {
            {1, 0}, {0, 1}, {1, 1}, {1, -1},
            {-1, 0}, {0, -1}, {-1, -1}, {-1, 1}
        };
        
        // Check each of the four lines (horizontal, vertical, two diagonals)
        for (int i = 0; i < 4; i++) {
            int count = 1; // Count of stones in a row (including the last move)
            
            // Check in both directions
            for (int j = 0; j < 2; j++) {
                int dirIndex = i + j * 4;
                int dr = directions[dirIndex][0];
                int dc = directions[dirIndex][1];
                
                int r = row + dr;
                int c = col + dc;
                
                // Count consecutive stones in this direction
                while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == player) {
                    count++;
                    r += dr;
                    c += dc;
                }
            }
            
            // Check if we found 5 in a row
            if (count >= 5) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Reset the game to its initial state
     */
    public void resetGame() {
        // Clear the board
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
            }
        }
        
        // Reset game state
        isPlayerTurn = true;
        gameOver = false;
        lastMove = null;
        hoverPoint = null;
        
        // Repaint the board
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable antialiasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw board background
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw grid lines
        g2d.setColor(LINE_COLOR);
        g2d.setStroke(new BasicStroke(1));
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            // Draw horizontal line
            int y = BOARD_MARGIN + i * CELL_SIZE;
            g2d.drawLine(BOARD_MARGIN, y, BOARD_MARGIN + (BOARD_SIZE - 1) * CELL_SIZE, y);
            
            // Draw vertical line
            int x = BOARD_MARGIN + i * CELL_SIZE;
            g2d.drawLine(x, BOARD_MARGIN, x, BOARD_MARGIN + (BOARD_SIZE - 1) * CELL_SIZE);
        }
        
        // Draw stars/dots on the board (traditional Gomoku board has 5 dots)
        g2d.setColor(LINE_COLOR);
        int[] starPoints = {3, 7, 11}; // Positions for the star points (3-3, 3-7, etc.)
        int dotSize = 8;
        
        for (int i : starPoints) {
            for (int j : starPoints) {
                int x = BOARD_MARGIN + i * CELL_SIZE - dotSize/2;
                int y = BOARD_MARGIN + j * CELL_SIZE - dotSize/2;
                g2d.fillOval(x, y, dotSize, dotSize);
            }
        }
        
        // Draw hover indicator
        if (hoverPoint != null && !gameOver && isPlayerTurn) {
            g2d.setColor(HOVER_COLOR);
            int x = BOARD_MARGIN + hoverPoint.x * CELL_SIZE;
            int y = BOARD_MARGIN + hoverPoint.y * CELL_SIZE;
            g2d.fillOval(x - CELL_SIZE/3, y - CELL_SIZE/3, CELL_SIZE*2/3, CELL_SIZE*2/3);
        }
        
        // Draw stones
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != 0) {
                    int x = BOARD_MARGIN + j * CELL_SIZE;
                    int y = BOARD_MARGIN + i * CELL_SIZE;
                    
                    // Stone shadow for 3D effect
                    g2d.setColor(new Color(0, 0, 0, 60));
                    g2d.fillOval(x - CELL_SIZE/3 + 2, y - CELL_SIZE/3 + 2, CELL_SIZE*2/3, CELL_SIZE*2/3);
                    
                    // Draw stone
                    g2d.setColor(board[i][j] == 1 ? BLACK_STONE : WHITE_STONE);
                    g2d.fillOval(x - CELL_SIZE/3, y - CELL_SIZE/3, CELL_SIZE*2/3, CELL_SIZE*2/3);
                    
                    // Add highlight to white stones for 3D effect
                    if (board[i][j] == 2) {
                        g2d.setColor(new Color(255, 255, 255, 80));
                        g2d.fillOval(x - CELL_SIZE/3 + 3, y - CELL_SIZE/3 + 3, CELL_SIZE/3, CELL_SIZE/3);
                    }
                }
            }
        }
        
        // Highlight the last move
        if (lastMove != null) {
            int x = BOARD_MARGIN + lastMove.x * CELL_SIZE;
            int y = BOARD_MARGIN + lastMove.y * CELL_SIZE;
            
            g2d.setColor(LAST_MOVE_INDICATOR);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - CELL_SIZE/3 - 2, y - CELL_SIZE/3 - 2, CELL_SIZE*2/3 + 4, CELL_SIZE*2/3 + 4);
        }
        
        // Draw game status
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        if (gameOver) {
            g2d.drawString("Game Over", 20, getHeight() - 10);
        } else {
            g2d.drawString(isPlayerTurn ? "Your Turn" : "AI Thinking...", 20, getHeight() - 10);
        }
    }
}
