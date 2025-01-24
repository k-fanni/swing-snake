/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import snake.board.Database;
import snake.board.Difficulty;
import snake.board.Player;

/**
 *
 * @author Fanni
 */
public class GameGUI {
    
    private BoardPanel boardPanel;
    private JFrame frame;
    private JPanel infoPanel;
    private JLabel timeLabel;
    private JLabel collectedApplesLabel;
    private Timer timer;
    private long startTime;
    private Timer stepTimer;
    private Difficulty difficulty;
    private Database playerScores;

    /**
     * Initializes a new GameGUI object.
     * @param difficulty the difficulty of the current game
     * @throws IOException
     * @throws SQLException
     */
    public GameGUI(Difficulty difficulty) throws IOException, SQLException {
        playerScores = new Database();
        
        frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(new KeyPressListener());
        frame.setFocusable(true);
        frame.setFocusTraversalKeysEnabled(false);
        
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1));
        timeLabel = new JLabel("");
        infoPanel.add(timeLabel);
        collectedApplesLabel = new JLabel("");
        infoPanel.add(collectedApplesLabel);
        infoPanel.setBackground(Color.BLACK);
        timeLabel.setFont(new Font("Courier", Font.BOLD, 14));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        timeLabel.setForeground(Color.WHITE);
        collectedApplesLabel.setFont(new Font("Courier", Font.BOLD, 14));
        collectedApplesLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        collectedApplesLabel.setForeground(Color.WHITE);
        
        frame.getContentPane().add(infoPanel, BorderLayout.NORTH);
        
        makeBoard(difficulty);
        
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLabel.setText("Time: " + ElapsedTime() + " seconds");
                updateAppleLabel();
            }
        });
        startTime = System.currentTimeMillis();
        timer.start();
        
        
        stepTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    boardPanel.Move();
                } catch (IOException ex) {
                    Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (stepTimer.getDelay() > 100) {
                    stepTimer.setDelay(stepTimer.getInitialDelay() - boardPanel.updateSpeed());
                }
                if (boardPanel.isGameOver()) {
                    try {
                        EndGame();
                    } catch (IOException ex) {
                        Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        stepTimer.start();
        
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu menu = new JMenu("Game");
        JMenu newGame = new JMenu("New");
        
        menuBar.add(menu);
        menu.add(newGame);
        for (Difficulty d : Difficulty.values()) {
            JMenuItem sizeOption = new JMenuItem(d.name());
            newGame.add(sizeOption);
            sizeOption.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.getContentPane().remove(boardPanel);
//                    frame.getContentPane().remove(infoPanel);
                    try {
                        timer.stop();
                        stepTimer.stop();
                        makeBoard(d);
                        startTime = System.currentTimeMillis();timer.start();
                        stepTimer.start();
                        
                    } catch (IOException ex) {
                        Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    frame.pack();
                }
            });
        }
        JMenuItem topPlayers = new JMenuItem("Top players");
        menu.add(topPlayers);
        JPanel levelSelector = new JPanel();
        JComboBox options = new JComboBox(Difficulty.values());
        levelSelector.add(options);
        topPlayers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                    frame, levelSelector, "Top players", JOptionPane.PLAIN_MESSAGE);
                String selectedLevel = options.getSelectedItem().toString();
                try {
                    JPanel topList = new JPanel();
                    List<Player> players;
                    players = playerScores.getTopPlayers(selectedLevel);
                    topList.setLayout(new GridLayout(players.size() + 1, 4));
                    JLabel positionLabel = new JLabel("");
                    positionLabel.setBackground(Color.GREEN);
                    topList.add(positionLabel);
                    JLabel nameLabel = new JLabel("Name");
                    nameLabel.setBackground(Color.GREEN);
                    topList.add(nameLabel);
                    JLabel scoreLabel = new JLabel("Score");
                    scoreLabel.setBackground(Color.GREEN);
                    topList.add(scoreLabel);
                    JLabel levelLabel = new JLabel("Level");
                    levelLabel.setBackground(Color.GREEN);
                    topList.add(levelLabel);
                    positionLabel.setOpaque(true);
                    nameLabel.setOpaque(true);
                    scoreLabel.setOpaque(true);
                    levelLabel.setOpaque(true);
                    int i = 1;
                    for (Player player : players) {
                        topList.add(new JLabel(String.valueOf(i + ".")));
                        i++;
                        topList.add(new JLabel(player.getName()));
                        topList.add(new JLabel(String.valueOf(player.getScore())));
                        topList.add(new JLabel(player.getLevel()));
                    }
                    JOptionPane.showMessageDialog(
                        frame, topList, "Top players", JOptionPane.PLAIN_MESSAGE);
                } catch (SQLException ex) {
                    Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        JMenuItem exitMenu = new JMenuItem("Exit");
        menu.add(exitMenu);
        exitMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }
    
    private void makeBoard(Difficulty difficulty) throws IOException {
        this.difficulty = difficulty;
        boardPanel = new BoardPanel(difficulty);
        frame.getContentPane().add(boardPanel, BorderLayout.CENTER);
        boardPanel.repaint();
    }
    
    private long ElapsedTime() {
        return (long) (System.currentTimeMillis() - startTime) / 1000;
    }
    
    private void updateAppleLabel() {
        collectedApplesLabel.setText(boardPanel.getCollectedApples() + " apples collected.");
    }
    
    private void EndGame() throws IOException, SQLException {
        timer.stop();
        stepTimer.stop();
        
        Object[] buttons = {"Save score", "New game", "Exit"};
        int submit = JOptionPane.showOptionDialog(boardPanel, 
            " You lost in " + ElapsedTime() + " seconds.", "GAME OVER!", 
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
            buttons, buttons[1]);
        if (submit == 0) {
            String playerName = "";
            while ("".equals(playerName)) {
                playerName = JOptionPane.showInputDialog(boardPanel, 
                "Enter your name to save your score:", "Save your score",
                JOptionPane.PLAIN_MESSAGE);
            }
            if (playerName != null && !"".equals(playerName)) {
                playerScores.putNewScore(
                    playerName, boardPanel.getCollectedApples(), difficulty.toString());
            }
            
            
        }
        if (submit == 1) {
            makeBoard(difficulty);
            startTime = System.currentTimeMillis();
            timer.start();
            stepTimer.start();
        }
        if (submit == 2) {
            System.exit(0);
        }
    }
    
    
    
    class KeyPressListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            try {
                boardPanel.PlayerKeyPressed(e.getKeyCode());
                if (boardPanel.isGameOver()) EndGame();
            } catch (IOException ex) {
                Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}
        
    }
    
}
