package tetris;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScores extends JFrame {
    private List<Integer> scores;
    private static final String SCORES_FILE = "highscores.txt";
    
    public HighScores() {
        setTitle("Yüksek Skorlar");
        setSize(300, 400);
        setLocationRelativeTo(null);
        
        scores = loadScores();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        for (int i = 0; i < scores.size(); i++) {
            panel.add(new JLabel((i + 1) + ". " + scores.get(i)));
        }
        
        add(new JScrollPane(panel));
    }
    
    private List<Integer> loadScores() {
        List<Integer> loadedScores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                loadedScores.add(Integer.parseInt(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(loadedScores, Collections.reverseOrder());
        return loadedScores;
    }
    
    public void saveScore(int score) {
        scores.add(score);
        Collections.sort(scores, Collections.reverseOrder());
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORES_FILE))) {
            for (int s : scores) {
                writer.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 