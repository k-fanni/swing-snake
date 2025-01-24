/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snake.board;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Fanni
 */
public class Database {
    
    int maxScores;
    PreparedStatement insertStatement;
    PreparedStatement deleteStatement;
    Connection connection;

    /**
     * Initializes a new Database object.
     * @throws SQLException
     */
    public Database() throws SQLException {
        maxScores = 10;
        Properties connectionProps = new Properties();
        connectionProps.put("user", "root");
        connectionProps.put("password", "");
        connectionProps.put("serverTimezone", "UTC");
        String dbURL = "jdbc:mysql://localhost:3306/snakeGame";
        connection = DriverManager.getConnection(dbURL, connectionProps);
        String insertQuery = 
            "INSERT INTO SNAKEGAME (TIMESTAMP, NAME, SCORE, LEVEL) VALUES (?, ?, ?, ?)";
        insertStatement = connection.prepareStatement(insertQuery);
        String deleteQuery = "DELETE FROM SNAKEGAME WHERE NAME=? AND SCORE=? AND LEVEL=?";
        deleteStatement = connection.prepareStatement(deleteQuery);
    }
    
    /**
     *
     * @return the top 10 players based on scores
     * @throws SQLException
     */
    public List<Player> getTopPlayers(String selectedLevel) throws SQLException {
        String query = "SELECT * FROM SNAKEGAME WHERE LEVEL=?";
        PreparedStatement getStatement = connection.prepareStatement(query);
        getStatement.setString(1, selectedLevel);
        ArrayList<Player> topPlayers = new ArrayList<>();
//        Statement stmt = connection.createStatement();
        ResultSet results = getStatement.executeQuery();
        while (results.next()) {
            String name = results.getString("NAME");
            int score = results.getInt("SCORE");
            String level = results.getString("LEVEL");
            topPlayers.add(new Player(name, score, level));
        }
        sortTopPlayers(topPlayers);
        if (topPlayers.size() < 10) return topPlayers;
        return topPlayers.subList(0, 10);
    }
    
    private void sortTopPlayers(ArrayList<Player> topPlayers) {
        Collections.sort(topPlayers, (Player a, Player b) -> {
            return b.getScore() - a.getScore();
        });
    }
    
    /**
     *
     * @param name the name of the new player
     * @param score the score of the new player
     * @param level the level of the game the new player played
     * @throws SQLException
     */
    public void putNewScore(String name, int score, String level) throws SQLException {
        insertScore(name, score, level);
    }
    
    private void insertScore(String name, int score, String level) throws SQLException {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        insertStatement.setTimestamp(1, ts);
        insertStatement.setString(2, name);
        insertStatement.setInt(3, score);
        insertStatement.setString(4, level);
        insertStatement.executeUpdate();
    }
    
    private void deleteScores(String name, int score, String level) throws SQLException {
        deleteStatement.setString(1, name);
        deleteStatement.setInt(2, score);
        deleteStatement.setString(3, level);
        deleteStatement.executeUpdate();
    }
    
}
