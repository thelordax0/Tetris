package tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.sound.sampled.*;
import java.io.File;
import java.awt.image.BufferedImage;

public class GameBoard extends JFrame {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 40;
    private static final int PADDING = 40;
    private static final int TARGET_FPS = 144;
    private static final int GAME_SPEED = 1000;
    
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;
    private static final int GAME_AREA_WIDTH = BLOCK_SIZE * BOARD_WIDTH + (PADDING * 2);
    private static final int GAME_AREA_HEIGHT = BLOCK_SIZE * BOARD_HEIGHT + (PADDING * 2);
    
    private Timer timer;
    private boolean isPaused = false;
    private int score = 0;
    private int[][] board;
    private Shape currentShape;
    private JLabel scoreLabel;
    private Clip clearSound;
    private Clip dropSound;
    private Clip rotateSound;
    private Clip gameOverSound;
    private JLabel fpsLabel;
    private int frameCount = 0;
    private long lastFpsTime = 0;
    private int fps = 0;
    private BufferedImage offscreenBuffer;
    private JPanel gamePanel;
    
    public GameBoard() {
        setTitle("Tetris - Oyun");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setFocusable(true);
        
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        currentShape = new Shape();
        
        scoreLabel = new JLabel("SKOR: 0");
        scoreLabel.setForeground(Color.GREEN);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 32));
        
        fpsLabel = new JLabel("FPS: 0");
        fpsLabel.setForeground(Color.GREEN);
        fpsLabel.setFont(new Font("Monospace", Font.BOLD, 24));
        
        gamePanel = new JPanel() {
            {
                setDoubleBuffered(true);
                setOpaque(true);
                setPreferredSize(new Dimension(GAME_AREA_WIDTH, GAME_AREA_HEIGHT));
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
        gamePanel.setBackground(Color.BLACK);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(0, 0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel topPanel = new JPanel(new BorderLayout(20, 0));
        topPanel.setBackground(new Color(0, 0, 20));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(fpsLabel, BorderLayout.WEST);
        topPanel.add(scoreLabel, BorderLayout.EAST);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(0, 0, 20));
        
        JPanel gameWrapper = new JPanel();
        gameWrapper.setBackground(Color.BLACK);
        gameWrapper.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
        gameWrapper.add(gamePanel);
        
        centerPanel.add(gameWrapper);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        setLayout(new BorderLayout());
        add(mainPanel);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        moveShape(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveShape(1);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveShapeDown();
                        break;
                    case KeyEvent.VK_SPACE:
                        dropShape();
                        break;
                    case KeyEvent.VK_UP:
                        rotateShape();
                        break;
                    case KeyEvent.VK_P:
                        togglePause();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        dispose();
                        new MainMenu().setVisible(true);
                        break;
                }
            }
        });
        
        timer = new Timer(GAME_SPEED, e -> gameLoop());
        timer.start();
        
        Timer fpsTimer = new Timer(1000/TARGET_FPS, e -> updateFPS());
        fpsTimer.setCoalesce(false);
        fpsTimer.start();
        
        loadSounds();
    }
    
    private void loadSounds() {
        try {
            clearSound = loadSound("src/main/resources/assets/sounds/clear.wav");
            dropSound = loadSound("src/main/resources/assets/sounds/drop.wav");
            rotateSound = loadSound("src/main/resources/assets/sounds/rotate.wav");
            gameOverSound = loadSound("src/main/resources/assets/sounds/gameover.wav");
        } catch (Exception e) {
            System.err.println("Ses dosyaları yüklenemedi: " + e.getMessage());
        }
    }
    
    private Clip loadSound(String path) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (Exception e) {
            System.err.println("Ses dosyası yüklenemedi: " + path);
            return null;
        }
    }
    
    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    private void updateFPS() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastFpsTime > 1000) {
            fps = frameCount;
            frameCount = 0;
            lastFpsTime = currentTime;
            fpsLabel.setText("FPS: " + fps);
        }
        
        if (!isPaused) {
            gamePanel.repaint();
        }
    }
    
    private void drawGame(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
        
        g.setColor(new Color(40, 40, 40));
        for (int row = 0; row <= BOARD_HEIGHT; row++) {
            g.drawLine(PADDING, row * BLOCK_SIZE + PADDING, 
                      BOARD_WIDTH * BLOCK_SIZE + PADDING, row * BLOCK_SIZE + PADDING);
        }
        for (int col = 0; col <= BOARD_WIDTH; col++) {
            g.drawLine(col * BLOCK_SIZE + PADDING, PADDING, 
                      col * BLOCK_SIZE + PADDING, BOARD_HEIGHT * BLOCK_SIZE + PADDING);
        }
        
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == 1) {
                    drawBlock(g, col * BLOCK_SIZE + PADDING, row * BLOCK_SIZE + PADDING, Color.CYAN);
                }
            }
        }
        
        if (currentShape != null) {
            drawShape(g);
        }
    }
    
    private void drawBlock(Graphics g, int x, int y, Color color) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(
            x, y, color,
            x + BLOCK_SIZE, y + BLOCK_SIZE, color.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
    }
    
    private void drawShape(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int[][] shape = currentShape.getShape();
        
        GradientPaint gradient = new GradientPaint(
            0, 0, Color.RED,
            BLOCK_SIZE, BLOCK_SIZE, Color.ORANGE
        );
        g2d.setPaint(gradient);
        
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int x = (currentShape.getCurrentX() + col) * BLOCK_SIZE + PADDING;
                    int y = (currentShape.getCurrentY() + row) * BLOCK_SIZE + PADDING;
                    g2d.fillRect(x, y, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }
    }
    
    private void gameLoop() {
        if (!isPaused) {
            if (!moveShapeDown()) {
                placeShape();
                removeFullLines();
                if (!createNewShape()) {
                    gameOver();
                }
            }
            gamePanel.repaint();
        }
    }
    
    private boolean moveShape(int dx) {
        int newX = currentShape.getCurrentX() + dx;
        if (isValidMove(currentShape.getShape(), newX, currentShape.getCurrentY())) {
            currentShape.setCurrentX(newX);
            gamePanel.repaint();
            return true;
        }
        return false;
    }
    
    private boolean moveShapeDown() {
        int newY = currentShape.getCurrentY() + 1;
        if (isValidMove(currentShape.getShape(), currentShape.getCurrentX(), newY)) {
            currentShape.setCurrentY(newY);
            gamePanel.repaint();
            return true;
        }
        return false;
    }
    
    private void dropShape() {
        while (moveShapeDown()) {}
        playSound(dropSound);
    }
    
    private void rotateShape() {
        int[][] rotated = rotateMatrix(currentShape.getShape());
        if (isValidMove(rotated, currentShape.getCurrentX(), currentShape.getCurrentY())) {
            currentShape.setShape(rotated);
            playSound(rotateSound);
            gamePanel.repaint();
        }
    }
    
    private int[][] rotateMatrix(int[][] matrix) {
        int[][] rotated = new int[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                rotated[j][matrix.length - 1 - i] = matrix[i][j];
            }
        }
        return rotated;
    }
    
    private boolean isValidMove(int[][] shape, int newX, int newY) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int x = newX + col;
                    int y = newY + row;
                    
                    if (x < 0 || x >= BOARD_WIDTH || y >= BOARD_HEIGHT) {
                        return false;
                    }
                    
                    if (y >= 0 && board[y][x] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private void placeShape() {
        int[][] shape = currentShape.getShape();
        int posX = currentShape.getCurrentX();
        int posY = currentShape.getCurrentY();
        
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    board[posY + row][posX + col] = 1;
                }
            }
        }
    }
    
    private void removeFullLines() {
        int linesRemoved = 0;
        
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            boolean isLineFull = true;
            
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == 0) {
                    isLineFull = false;
                    break;
                }
            }
            
            if (isLineFull) {
                linesRemoved++;
                for (int r = row; r > 0; r--) {
                    System.arraycopy(board[r-1], 0, board[r], 0, BOARD_WIDTH);
                }
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    board[0][col] = 0;
                }
                row++;
            }
        }
        
        if (linesRemoved > 0) {
            playSound(clearSound);
            score += linesRemoved * 100;
            scoreLabel.setText("SKOR: " + score);
        }
    }
    
    private boolean createNewShape() {
        currentShape = new Shape();
        return isValidMove(currentShape.getShape(), currentShape.getCurrentX(), currentShape.getCurrentY());
    }
    
    private void gameOver() {
        timer.stop();
        playSound(gameOverSound);
        JOptionPane.showMessageDialog(this, "Oyun Bitti! Skorunuz: " + score);
        HighScores highScores = new HighScores();
        highScores.saveScore(score);
        dispose();
        new MainMenu().setVisible(true);
    }
    
    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
        } else {
            timer.start();
        }
    }
} 