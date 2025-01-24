/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snake.board;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import static snake.board.Direction.UP;
import static snake.board.Direction.LEFT;
import static snake.board.Direction.DOWN;
import static snake.board.Direction.RIGHT;
import static snake.board.Tile.EMPTY;
import static snake.board.Tile.SNAKE;
import static snake.board.Tile.APPLE;
import static snake.board.Tile.ROCK;

/**
 *
 * @author Fanni
 */
public class Board {
    
    private final ArrayList<Point> snake;
    private final Tile[][] tiles;
    private final Random rand;
    private int collectedApples;
    private Direction direction;
    
    private final int TILE_SIZE = 32;
    private final Image sand;
    private final Image snakeHead;
    private final Image snakeRattle;
    private final Image snakeBody;
    private final Image snakeBodyCorner;
    private final Image apple;
    private final Image rock;
    
    /**
     * Initializes a Board object of a given difficulty.
     * @param difficulty the difficulty of the current game
     * @throws IOException
     */
    public Board(Difficulty difficulty) throws IOException {
        int size = difficulty.size;
        tiles = new Tile[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                tiles[i][j] = EMPTY;
            }
        }
        rand = new Random();
        direction = Direction.values()[rand.nextInt(4)];
        snake = new ArrayList<>();
        placeSnake();
        generateRocks(size / 2 * difficulty.rockDensity);
        generateApple();
        collectedApples = 0;
        
        sand = ImageIO.read(getClass().getResource("/res/sand.png"));
        snakeHead = ImageIO.read(getClass().getResource("/res/snake_head.png"));
        snakeRattle = ImageIO.read(getClass().getResource("/res/snake_rattle.png"));
        snakeBody = ImageIO.read(getClass().getResource("/res/snake_body.png"));
        snakeBodyCorner = ImageIO.read(getClass().getResource("/res/snake_body_corner.png"));
        apple = ImageIO.read(getClass().getResource("/res/apple.png"));
        rock = ImageIO.read(getClass().getResource("/res/rock.png"));
    }
    
    private void generateRocks(int n) throws IOException {
        if (n == 0) return;
        Point p = new Point(rand.nextInt(tiles.length), rand.nextInt(tiles.length));
        if (isFree(p) && !isTileInDirection(p)) {
            tiles[p.x][p.y] = ROCK;
            generateRocks(--n);
        }
        else {
            generateRocks(n);
        }
    }
    
    private boolean isTileInDirection(Point p) {
        int x = p.x - snakeHead().x;
        int y = p.y - snakeHead().y;
        if (x != 0) x /= Math.abs(x);
        if (y != 0) y /= Math.abs(y);
        return x == direction.x && y == direction.y;
    }
    
    private void generateApple() throws IOException {
        Point p = new Point(rand.nextInt(tiles.length), rand.nextInt(tiles.length));
        if (isGoodTileForApple(p)) {
            tiles[p.x][p.y] = APPLE;
        }
        else {
            generateApple();
        }
    }
    
    private boolean isGoodTileForApple(Point p) {
        if (!isFree(p)) return false;
        
        int x = p.x;
        int y = p.y;
        int count = 0;
        if (!isFree(x - 1, y)) count++;
        if (!isFree(x + 1, y)) count++;
        if (!isFree(x, y - 1)) count++;
        if (!isFree(x, y + 1)) count++;
        if (count > 2) return false;
        
        if (!isFree(x - 1, y - 1)) return false;
        if (!isFree(x + 1, y - 1)) return false;
        if (!isFree(x - 1, y + 1)) return false;
        return isFree(x + 1, y + 1);
    }
    
    private Tile get(Point position) {
        if (position.x < 0 || position.y < 0 
            || position.x >= tiles.length || position.y >= tiles.length) {
            return null;
        }
        return tiles[position.x][position.y];
    }
    
    private boolean isFree(Point p) {
        return get(p) != null && get(p).equals(EMPTY);
    }
    
    private boolean isFree(int x, int y) {
        return isFree(new Point(x, y));
    }
    
    private void placeSnake() {
        Point h = new Point(tiles.length / 2, tiles.length / 2);
        Point r = new Point(h.x - direction.x, h.y - direction.y);
        tiles[h.x][h.y] = SNAKE; // head
        tiles[r.x][r.y] = SNAKE; // rattle
        snake.add(h);
        snake.add(r);
    }
    
    private Point snakeHead() {
        return snake.getFirst();
    }
    
    private Point snakeRattle() {
        return snake.getLast();
    }
    
    /**
     * Updates the direction attribute to the new direction if it's not a
     * backwards movement.
     * @param dir the new direction
     */
    public void changeDirection(Direction dir) {
        Point p = new Point(snakeHead().x + dir.x, snakeHead().y + dir.y);
        if (!p.equals(snake.get(1))) {
            direction = dir; // if snake is not trying to move backwards
        }
    }
    
    /**
     * Handles the movement of snake in the current direction.
     * @return false if next tile in direction would be ROCK or null,
     *         true otherwise
     * @throws IOException
     */
    public boolean Move() throws IOException {
        Point p = new Point(snakeHead().x + direction.x, 
                            snakeHead().y + direction.y);
        if (!canMove(p)) {
            return p.equals(snake.get(1));
        }
        boolean appleOnTile = get(p).equals(APPLE);
        tiles[p.x][p.y] = SNAKE;
        snake.addFirst(p);
        if (appleOnTile) {
            collectedApples++;
            generateApple();
        }
        else {
            tiles[snakeRattle().x][snakeRattle().y] = EMPTY;
            snake.removeLast();
        }
        return true;
    }
    
    private boolean canMove(Point p) {
        return get(p) != null 
               && (isFree(p) || get(p).equals(APPLE));
    }

    /**
     *
     * @return
     */
    public int getCollectedApples() {
        return collectedApples;
    }
    
    /**
     * Draws the board's tiles.
     * @param g2 the Graphics2D object to be drawn on
     * @throws IOException
     */
    public void draw(Graphics2D g2) throws IOException {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                int x = i * TILE_SIZE;
                int y = j * TILE_SIZE;
                switch (tiles[i][j]) {
                    case EMPTY -> drawTile(sand, g2, x, y);
                    case SNAKE -> drawSnake(g2, x, y);
                    case APPLE -> drawTile(apple, g2, x, y);
                    case ROCK -> drawTile(rock, g2, x, y);
                }
            }
        }
    }
    
    private void drawSnake(Graphics2D g2, int x, int y) throws IOException {
        Point p = new Point(x / TILE_SIZE, y / TILE_SIZE);
        Image img;
        if (p.equals(snakeHead())) {
            img = drawSnakeHead();
        }
        else if (p.equals(snakeRattle())) {
            img = drawSnakeRattle(p);
        }
        else {
            img = drawSnakeBody(p);
        }
        drawTile(img, g2, x, y);
    }
    
    private Image drawSnakeHead() {
        Image img = null;
        switch (direction) {
            case UP -> img = snakeHead;
            case LEFT -> img = rotateImage(-90, snakeHead);
            case DOWN -> img = rotateImage(180, snakeHead);
            case RIGHT -> img = rotateImage(90, snakeHead);
        }
        return img;
    }
    
    private Image drawSnakeRattle(Point p) {
        Image img = null;
        int dirX = p.x - snake.get(snake.size() - 2).x;
        int dirY = p.y - snake.get(snake.size() - 2).y;
        if (dirX == UP.x && dirY == UP.y) {
            img = snakeRattle;
        }
        if (dirX == LEFT.x && dirY == LEFT.y) {
            img = rotateImage(-90, snakeRattle);
        }
        if (dirX == DOWN.x && dirY == DOWN.y) {
            img = rotateImage(180, snakeRattle);
        }
        if (dirX == RIGHT.x && dirY == RIGHT.y) {
            img = rotateImage(90, snakeRattle);
        }
        return img;
    }
    
    private Image drawSnakeBody(Point p) {
        Image img;
        int ind = snake.indexOf(p);
        Point prev = new Point(p.x - snake.get(ind - 1).x, p.y - snake.get(ind - 1).y);
        Point next = new Point(p.x - snake.get(ind + 1).x, p.y - snake.get(ind + 1).y);
        if (directionMatches(prev, UP, next, LEFT)) {
            img = rotateImage(-90, snakeBodyCorner);
        }
        else if (directionMatches(prev, UP, next, RIGHT)) {
            img = snakeBodyCorner;
        }
        else if (directionMatches(prev, DOWN, next, LEFT)) {
            img = rotateImage(180, snakeBodyCorner);
        }
        else if (directionMatches(prev, DOWN, next, RIGHT)) {
            img = rotateImage(90, snakeBodyCorner);
        }
        else if (directionMatches(prev, LEFT, next, RIGHT)) {
            img = rotateImage(90, snakeBody);
        }
        else {
            img = snakeBody;
        }
        return img;
    }
    
    private boolean directionMatches(Point prev, Direction prevD, Point next, Direction nextD) {
        return (prev.x == prevD.x && prev.y == prevD.y 
                && next.x == nextD.x && next.y == nextD.y)
                || (prev.x == nextD.x && prev.y == nextD.y 
                && next.x == prevD.x && next.y == prevD.y);
    }
    
    private void drawTile(Image tile, Graphics2D g2, int x, int y) {
        g2.drawImage(tile, x, y, TILE_SIZE, TILE_SIZE, null);
    }
    
    private BufferedImage rotateImage(int degree, Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        BufferedImage newImage = new BufferedImage(
                height, width, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = newImage.createGraphics();
        g2d.rotate(Math.toRadians(degree), height / 2, width / 2);
        g2d.drawImage(image, 0, 0, null);

        return newImage;
    }
    
    
}
