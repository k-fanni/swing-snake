/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package snake.board;

/**
 *
 * @author Fanni
 */
public enum Difficulty {
    EASY(19, 1),
    MEDIUM(17, 2),
    HARD(15, 3);
    
    public final int size, rockDensity;
    
    Difficulty(int size, int rockDensity) {
        this.size = size;
        this.rockDensity = rockDensity;
    }
}
