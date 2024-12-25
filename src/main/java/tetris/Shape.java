package tetris;

import java.util.Random;

public class Shape {
    private int[][] shape;
    private int currentX;
    private int currentY;
    private int currentRotation;
    
    private static final int[][][] SHAPES = {
        // I şekli
        {{1, 1, 1, 1}},
        // O şekli
        {{1, 1}, {1, 1}},
        // T şekli
        {{0, 1, 0}, {1, 1, 1}},
        // L şekli
        {{1, 0}, {1, 0}, {1, 1}},
        // J şekli
        {{0, 1}, {0, 1}, {1, 1}},
        // S şekli
        {{0, 1, 1}, {1, 1, 0}},
        // Z şekli
        {{1, 1, 0}, {0, 1, 1}}
    };
    
    public Shape() {
        Random random = new Random();
        shape = SHAPES[random.nextInt(SHAPES.length)];
        currentX = 4;
        currentY = 0;
        currentRotation = 0;
    }
    
    // Getter ve setter metodları
    public int[][] getShape() {
        return shape;
    }
    
    public void setShape(int[][] shape) {
        this.shape = shape;
    }
    
    public int getCurrentX() {
        return currentX;
    }
    
    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }
    
    public int getCurrentY() {
        return currentY;
    }
    
    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }
    
    public int getCurrentRotation() {
        return currentRotation;
    }
    
    public void setCurrentRotation(int currentRotation) {
        this.currentRotation = currentRotation;
    }
} 