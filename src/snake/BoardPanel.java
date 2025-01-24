/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import snake.board.Board;
import snake.board.Difficulty;
import snake.board.Direction;

/**
 *
 * @author Fanni
 */
public class BoardPanel extends JPanel {
    
    private final Board board;
    private boolean gameOver;
    
    /**
     * Initializes a new BoardPanel object with the given difficulty.
     * @param difficulty the the difficulty of the current game
     * @throws IOException
     */
    public BoardPanel(Difficulty difficulty) throws IOException {
        board = new Board(difficulty);
        this.setPreferredSize(new Dimension(32 * difficulty.size, 32 * difficulty.size));
        this.setBackground(Color.decode("#d7a85c"));
        gameOver = false;
    }
    
    /**
     * Handles the painting of BoardPanel.
     * @param graphics the Graphics object to be painted on
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        try {
            board.draw(g2);
        } catch (IOException ex) {
            Logger.getLogger(BoardPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Calls the board's changeDirection method with the direction matching the 
     * pressed key if the user pressed one of the WASD keys, does nothing otherwise.
     * @param key the key the user pressed
     * @throws IOException
     */
    public void PlayerKeyPressed(int key) throws IOException {
        switch (key) {
            case KeyEvent.VK_W -> board.changeDirection(Direction.UP);
            case KeyEvent.VK_A -> board.changeDirection(Direction.LEFT);
            case KeyEvent.VK_S -> board.changeDirection(Direction.DOWN);
            case KeyEvent.VK_D -> board.changeDirection(Direction.RIGHT);
        }
    }
    
    /**
     * Calls board's Move method and updates gameOver with its boolean return value.
     * Repaints BoardPanel after each move.
     * @throws IOException
     */
    public void Move() throws IOException {
        gameOver = !board.Move();
        this.repaint();
    }

    public boolean isGameOver() {
        return gameOver;
    }
    
    /**
     *
     * @return the number of collected apples * 20
     */
    public int updateSpeed() {
        return board.getCollectedApples() * 20;
    }
    
    public int getCollectedApples() {
        return board.getCollectedApples();
    }
    
    
}
