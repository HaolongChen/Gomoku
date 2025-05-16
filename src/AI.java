import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI player for the Gomoku game using alpha-beta pruning
 */
public class AI {
    private static final int MAX_DEPTH = 3;  // Maximum depth for alpha-beta pruning
    private static final int WIN_SCORE = 100000;  // Score for a winning position
    private static final Random random = new Random();
    
    // Player values
    private static final int EMPTY = 0;
    private static final int PLAYER = 1;  // Human player (black)
    private static final int AI = 2;      // AI player (white)
    
    /**
     * Get the next move for the AI player
     * @param board the current game board
     * @return the point where the AI will place its stone
     */
    public Point getNextMove(int[][] board) {
        // First move optimization - if board is empty or nearly empty, 
        // play near the center for better performance
        boolean isEmpty = true;
        int stones = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != EMPTY) {
                    isEmpty = false;
                    stones++;
                }
            }
        }
        
        if (isEmpty) {
            // First move - play near center
            int center = board.length / 2;
            return new Point(center, center);
        }
        
        if (stones <= 2) {
            // For the first few moves, play near the center or near existing stones
            // to improve performance
            return getInitialMove(board);
        }
        
        // Use alpha-beta pruning for subsequent moves
        int bestScore = Integer.MIN_VALUE;
        Point bestMove = null;
        
        // Only consider areas close to existing stones
        List<Point> candidates = getCandidateMoves(board);
        
        for (Point move : candidates) {
            // Try this move
            board[move.y][move.x] = AI;
            
            // Calculate score for this move
            int score = minimax(board, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            
            // Undo move
            board[move.y][move.x] = EMPTY;
            
            // Update best move
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            } else if (score == bestScore && random.nextInt(10) < 3) {
                // Occasionally choose an equally good move for variety
                bestMove = move;
            }
        }
        
        return bestMove;
    }
    
    /**
     * Get initial move for AI when the board is mostly empty
     */
    private Point getInitialMove(int[][] board) {
        int center = board.length / 2;
        int variance = 3; // How far from center to look
        
        // Try to find a move near the player's stone
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == PLAYER) {
                    // Play near player's stone
                    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
                    for (int[] dir : directions) {
                        int newI = i + dir[0];
                        int newJ = j + dir[1];
                        if (isValidPosition(board, newI, newJ) && board[newI][newJ] == EMPTY) {
                            return new Point(newJ, newI);
                        }
                    }
                }
            }
        }
        
        // Otherwise play near center
        int offset = random.nextInt(variance) - variance/2;
        int offsetY = random.nextInt(variance) - variance/2;
        int x = center + offset;
        int y = center + offsetY;
        
        if (x < 0) x = 0;
        if (x >= board.length) x = board.length - 1;
        if (y < 0) y = 0;
        if (y >= board.length) y = board.length - 1;
        
        // Make sure spot is empty
        if (board[y][x] == EMPTY) {
            return new Point(x, y);
        } else {
            // Find a nearby empty spot
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    int newY = y + i;
                    int newX = x + j;
                    if (isValidPosition(board, newY, newX) && board[newY][newX] == EMPTY) {
                        return new Point(newX, newY);
                    }
                }
            }
        }
        
        // Fallback to first empty position
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == EMPTY) {
                    return new Point(j, i);
                }
            }
        }
        
        return null; // Board is full
    }
    
    /**
     * Find candidate moves for the AI to consider
     * This improves performance by only looking at relevant areas of the board
     */
    private List<Point> getCandidateMoves(int[][] board) {
        List<Point> candidates = new ArrayList<>();
        boolean[][] considered = new boolean[board.length][board.length];
        
        // Consider positions near existing stones
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != EMPTY) {
                    // Add empty positions around this stone
                    for (int di = -2; di <= 2; di++) {
                        for (int dj = -2; dj <= 2; dj++) {
                            int ni = i + di;
                            int nj = j + dj;
                            if (isValidPosition(board, ni, nj) && board[ni][nj] == EMPTY && !considered[ni][nj]) {
                                candidates.add(new Point(nj, ni));
                                considered[ni][nj] = true;
                            }
                        }
                    }
                }
            }
        }
        
        // If no candidates found (unlikely), consider all empty positions
        if (candidates.isEmpty()) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (board[i][j] == EMPTY) {
                        candidates.add(new Point(j, i));
                    }
                }
            }
        }
        
        return candidates;
    }
    
    /**
     * Check if a position is valid on the board
     */
    private boolean isValidPosition(int[][] board, int i, int j) {
        return i >= 0 && i < board.length && j >= 0 && j < board[i].length;
    }
    
    /**
     * Minimax algorithm with alpha-beta pruning
     */
    private int minimax(int[][] board, int depth, boolean isMaximizing, int alpha, int beta) {
        // Check terminal conditions
        int score = evaluateBoard(board);
        
        // If this is a terminal node (win/loss/draw) or maximum depth reached
        if (Math.abs(score) >= WIN_SCORE || depth == MAX_DEPTH || isBoardFull(board)) {
            return score;
        }
        
        List<Point> availableMoves = getCandidateMoves(board);
        
        if (isMaximizing) {
            // AI's turn (maximizing)
            int maxScore = Integer.MIN_VALUE;
            for (Point move : availableMoves) {
                if (board[move.y][move.x] == EMPTY) {
                    // Try this move
                    board[move.y][move.x] = AI;
                    
                    // Recursively get score
                    int currentScore = minimax(board, depth + 1, false, alpha, beta);
                    
                    // Undo move
                    board[move.y][move.x] = EMPTY;
                    
                    // Update max score and alpha
                    maxScore = Math.max(maxScore, currentScore);
                    alpha = Math.max(alpha, currentScore);
                    
                    // Alpha-beta pruning
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return maxScore;
        } else {
            // Player's turn (minimizing)
            int minScore = Integer.MAX_VALUE;
            for (Point move : availableMoves) {
                if (board[move.y][move.x] == EMPTY) {
                    // Try this move
                    board[move.y][move.x] = PLAYER;
                    
                    // Recursively get score
                    int currentScore = minimax(board, depth + 1, true, alpha, beta);
                    
                    // Undo move
                    board[move.y][move.x] = EMPTY;
                    
                    // Update min score and beta
                    minScore = Math.min(minScore, currentScore);
                    beta = Math.min(beta, currentScore);
                    
                    // Alpha-beta pruning
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return minScore;
        }
    }
    
    /**
     * Check if the board is full (draw)
     */
    private boolean isBoardFull(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Evaluate the current board state and return a score
     * Positive scores favor the AI, negative scores favor the player
     */
    private int evaluateBoard(int[][] board) {
        // Check for wins first
        int aiScore = checkWinningConditions(board, AI);
        int playerScore = checkWinningConditions(board, PLAYER);
        
        if (aiScore >= WIN_SCORE) return WIN_SCORE;
        if (playerScore >= WIN_SCORE) return -WIN_SCORE;
        
        // Otherwise calculate positional score
        return calculatePositionalScore(board);
    }
    
    /**
     * Check if a player has won or is close to winning
     */
    private int checkWinningConditions(int[][] board, int player) {
        // Check for 5 in a row
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == player) {
                    // Check horizontal, vertical, and two diagonal directions
                    if (checkDirection(board, i, j, 0, 1, player, 5) || 
                        checkDirection(board, i, j, 1, 0, player, 5) || 
                        checkDirection(board, i, j, 1, 1, player, 5) || 
                        checkDirection(board, i, j, 1, -1, player, 5)) {
                        return WIN_SCORE;
                    }
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Check if there are n consecutive stones of the player in a direction
     */
    private boolean checkDirection(int[][] board, int startI, int startJ, int dI, int dJ, int player, int count) {
        int endI = startI + (count - 1) * dI;
        int endJ = startJ + (count - 1) * dJ;
        
        // Check if the end position is within the board
        if (endI < 0 || endI >= board.length || endJ < 0 || endJ >= board[0].length) {
            return false;
        }
        
        // Check all positions in this direction
        for (int i = 0; i < count; i++) {
            int currentI = startI + i * dI;
            int currentJ = startJ + i * dJ;
            if (board[currentI][currentJ] != player) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Calculate positional score based on patterns on the board
     */
    private int calculatePositionalScore(int[][] board) {
        int score = 0;
        
        // Check horizontal, vertical, and diagonal patterns
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != EMPTY) {
                    // For each stone, check patterns in all directions
                    int player = board[i][j];
                    int playerFactor = (player == AI) ? 1 : -1;
                    
                    // Check patterns in 8 directions
                    int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}, {0, -1}, {-1, 0}, {-1, -1}, {-1, 1}};
                    
                    for (int[] dir : directions) {
                        score += playerFactor * evaluateDirection(board, i, j, dir[0], dir[1], player);
                    }
                }
            }
        }
        
        // Consider center positions more valuable
        int center = board.length / 2;
        for (int i = center - 2; i <= center + 2; i++) {
            for (int j = center - 2; j <= center + 2; j++) {
                if (i >= 0 && i < board.length && j >= 0 && j < board.length) {
                    if (board[i][j] == AI) {
                        score += 5;
                    } else if (board[i][j] == PLAYER) {
                        score -= 5;
                    }
                }
            }
        }
        
        return score;
    }
    
    /**
     * Evaluate patterns in a specific direction
     */
    private int evaluateDirection(int[][] board, int startI, int startJ, int dI, int dJ, int player) {
        int score = 0;
        
        // Count consecutive stones and open ends
        int count = countConsecutive(board, startI, startJ, dI, dJ, player);
        
        // Award points based on consecutive stones
        if (count == 4) score = 1000;
        else if (count == 3) score = 100;
        else if (count == 2) score = 10;
        else if (count == 1) score = 1;
        
        // Check if both ends are open (more threatening)
        boolean startOpen = isOpenEnd(board, startI - dI, startJ - dJ);
        boolean endOpen = isOpenEnd(board, startI + count * dI, startJ + count * dJ);
        
        if (startOpen && endOpen) {
            score *= 2;  // Double score for open-ended patterns
        }
        
        return score;
    }
    
    /**
     * Count consecutive stones in a direction
     */
    private int countConsecutive(int[][] board, int startI, int startJ, int dI, int dJ, int player) {
        int count = 1;  // Start with the current stone
        
        // Count in the forward direction
        int i = startI + dI;
        int j = startJ + dJ;
        
        while (isValidPosition(board, i, j) && board[i][j] == player) {
            count++;
            i += dI;
            j += dJ;
        }
        
        return count;
    }
    
    /**
     * Check if a position is a valid open end (empty position)
     */
    private boolean isOpenEnd(int[][] board, int i, int j) {
        return isValidPosition(board, i, j) && board[i][j] == EMPTY;
    }
}
