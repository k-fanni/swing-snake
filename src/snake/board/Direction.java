/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package snake.board;


/**
 *
 * @author Fanni
 */
public enum Direction {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0);
    
    public final int x, y;
    
    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
