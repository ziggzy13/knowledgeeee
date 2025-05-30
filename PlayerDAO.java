package database;

import models.Player;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {
    
    public static boolean createPlayer(String username, String password) {
        String sql = "INSERT INTO players (username, password_hash) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static Player login(String username, String password) {
        String sql = "SELECT * FROM players WHERE username = ? AND password_hash = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPlayerFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Player getPlayerById(int id) {
        String sql = "SELECT * FROM players WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPlayerFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static boolean updatePlayerStats(int playerId, int pointsToAdd, boolean won) {
        String sql = "UPDATE players SET total_points = total_points + ?, " +
                    "games_played = games_played + 1, " +
                    "victories = victories + ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pointsToAdd);
            stmt.setInt(2, won ? 1 : 0);
            stmt.setInt(3, playerId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Player> getTopPlayers(int limit) {
        String sql = "SELECT * FROM players ORDER BY total_points DESC LIMIT ?";
        List<Player> topPlayers = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                topPlayers.add(extractPlayerFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return topPlayers;
    }
    
    private static Player extractPlayerFromResultSet(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setId(rs.getInt("id"));
        player.setUsername(rs.getString("username"));
        player.setTotalPoints(rs.getInt("total_points"));
        player.setGamesPlayed(rs.getInt("games_played"));
        player.setVictories(rs.getInt("victories"));
        return player;
    }
    
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 алгоритъмът не е наличен", e);
        }
    }
 // Добавете тези методи в PlayerDAO.java

    public static Player getPlayerByUsername(String username) {
        String sql = "SELECT * FROM players WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPlayerFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public static boolean updatePassword(int playerId, String newPassword) {
        String sql = "UPDATE players SET password_hash = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, hashPassword(newPassword));
            stmt.setInt(2, playerId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}