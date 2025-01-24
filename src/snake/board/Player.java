/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snake.board;

/**
 *
 * @author Fanni
 */
public class Player {
    
    private String name;
    private int score;
    private String level;

    /**
     *
     * @param name
     * @param score
     * @param level
     */
    public Player(String name, int score, String level) {
        this.name = name;
        this.score = score;
        this.level = level;
    }
    
    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     *
     * @return
     */
    public String getLevel() {
        return level;
    }
    
    
}
