package database;

import models.GameSession;
import models.Territory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameSessionDAO {
    
    public static int createNewGameSession(String mapSeed) {
        String sql = "INSERT INTO game_sessions (map_seed, turn_number, created_at) " +
                    "VALUES (?, 1, NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                     Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, mapSeed);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    public static GameSession getGameSession(int gameId) {
        String sql = "SELECT * FROM game_sessions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                GameSession session = new GameSession();
                session.setId(rs.getInt("id"));
                session.setMapSeed(rs.getString("map_seed"));
                session.setTurnNumber(rs.getInt("turn_number"));
                session.setCreatedAt(rs.getTimestamp("created_at"));
                return session;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static boolean updateTurnNumber(int gameId, int turnNumber) {
        String sql = "UPDATE game_sessions SET turn_number = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, turnNumber);
            stmt.setInt(2, gameId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean saveTerritory(int gameId, int playerId, Territory territory) {
        String sql = "INSERT INTO player_territories " +
                    "(game_id, player_id, x_coord, y_coord, building_type, army_size) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "building_type = VALUES(building_type), " +
                    "army_size = VALUES(army_size)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gameId);
            stmt.setInt(2, playerId);
            stmt.setInt(3, territory.getX());
            stmt.setInt(4, territory.getY());
            stmt.setString(5, territory.getBuildingType());
            stmt.setInt(6, territory.getArmySize());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Territory> getPlayerTerritories(int gameId, int playerId) {
        String sql = "SELECT * FROM player_territories " +
                    "WHERE game_id = ? AND player_id = ?";
        
        List<Territory> territories = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gameId);
            stmt.setInt(2, playerId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Territory territory = new Territory();
                territory.setX(rs.getInt("x_coord"));
                territory.setY(rs.getInt("y_coord"));
                territory.setBuildingType(rs.getString("building_type"));
                territory.setArmySize(rs.getInt("army_size"));
                territory.setOwnerId(playerId);
                territories.add(territory);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return territories;
    }
    
    public static List<Territory> getAllTerritories(int gameId) {
        String sql = "SELECT pt.*, p.username FROM player_territories pt " +
                    "JOIN players p ON pt.player_id = p.id " +
                    "WHERE pt.game_id = ?";
        
        List<Territory> territories = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gameId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Territory territory = new Territory();
                territory.setX(rs.getInt("x_coord"));
                territory.setY(rs.getInt("y_coord"));
                territory.setBuildingType(rs.getString("building_type"));
                territory.setArmySize(rs.getInt("army_size"));
                territory.setOwnerId(rs.getInt("player_id"));
                territory.setOwnerName(rs.getString("username"));
                territories.add(territory);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return territories;
    }
}