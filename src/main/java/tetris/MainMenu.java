package tetris;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainMenu extends JFrame {
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 20);  // Koyu lacivert
    private static final Color BUTTON_COLOR = new Color(50, 50, 200);
    private static final Color BUTTON_HOVER_COLOR = new Color(70, 70, 220);
    
    private JButton playButton;
    private JButton exitButton;
    private JPanel scorePanel;
    private List<Integer> scores;
    
    public MainMenu() {
        setTitle("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);  // Pencere çerçevesini kaldır
        
        // Ana panel
        JPanel mainPanel = new JPanel(new BorderLayout(50, 50));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
        
        // Başlık
        JLabel titleLabel = new JLabel("TETRIS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 120));
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        
        // Butonlar paneli
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        buttonPanel.setOpaque(false);
        
        playButton = createStyledButton("Oyuna Başla");
        exitButton = createStyledButton("Çıkış");
        
        buttonPanel.add(playButton);
        buttonPanel.add(exitButton);
        
        // Skor paneli
        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(new Color(0, 0, 0, 80));
        scorePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.CYAN, 2),
            BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        
        // Merkez panel
        JPanel centerPanel = new JPanel(new BorderLayout(50, 50));
        centerPanel.setOpaque(false);
        
        // Butonları ve skor panelini yatayda ortala
        JPanel horizontalPanel = new JPanel(new GridBagLayout());
        horizontalPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 50, 0, 50);
        
        JPanel buttonWrapper = new JPanel(new BorderLayout());
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(buttonPanel, BorderLayout.NORTH);
        horizontalPanel.add(buttonWrapper, gbc);
        
        gbc.gridx = 1;
        horizontalPanel.add(scorePanel, gbc);
        
        centerPanel.add(horizontalPanel, BorderLayout.CENTER);
        
        // Ana panel yerleşimi
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        loadScores();
        setupActions();
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Parlama efekti
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255, 50),
                                                    0, getHeight(), new Color(255, 255, 255, 0));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, x, y);
                
                g2d.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(300, 60));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void setupActions() {
        playButton.addActionListener(e -> {
            GameBoard gameBoard = new GameBoard();
            gameBoard.setVisible(true);
            this.dispose();
        });
        
        exitButton.addActionListener(e -> System.exit(0));
    }
    
    private void loadScores() {
        JLabel titleLabel = new JLabel("YÜKSEK SKORLAR");
        titleLabel.setForeground(Color.GREEN);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scorePanel.add(titleLabel);
        scorePanel.add(Box.createVerticalStrut(30));
        
        scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("highscores.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(Integer.parseInt(line));
            }
        } catch (IOException e) {
            System.err.println("Skor dosyası okunamadı: " + e.getMessage());
        }
        
        Collections.sort(scores, Collections.reverseOrder());
        
        for (int i = 0; i < Math.min(10, scores.size()); i++) {
            JLabel scoreLabel = new JLabel((i + 1) + ". " + scores.get(i));
            scoreLabel.setForeground(Color.WHITE);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
            scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            scorePanel.add(scoreLabel);
            scorePanel.add(Box.createVerticalStrut(20));
        }
    }
} 