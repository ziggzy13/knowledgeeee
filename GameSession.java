package models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GameSession {
    private int id;
    private String mapSeed;
    private int turnNumber;
    private Timestamp createdAt;
    private List<Player> players;
    private Map<String, Territory> territoryMap;
    private Player currentPlayer;
    private int mapWidth;
    private int mapHeight;
    
    // Константи за играта
    public static final int DEFAULT_MAP_WIDTH = 20;
    public static final int DEFAULT_MAP_HEIGHT = 15;
    public static final int STARTING_ARMIES = 5;
    public static final int ARMIES_PER_TURN = 3;
    
    // Конструктори
    public GameSession() {
        this.players = new ArrayList<>();
        this.territoryMap = new HashMap<>();
        this.mapWidth = DEFAULT_MAP_WIDTH;
        this.mapHeight = DEFAULT_MAP_HEIGHT;
        this.turnNumber = 1;
    }
    
    // Методи за управление на играта
    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
    }
    
    public void removePlayer(Player player) {
        players.remove(player);
    }
    
    public void addTerritory(Territory territory) {
        String key = territory.getX() + "," + territory.getY();
        territoryMap.put(key, territory);
    }
    
    public Territory getTerritory(int x, int y) {
        String key = x + "," + y;
        return territoryMap.get(key);
    }
    
    public List<Territory> getPlayerTerritories(int playerId) {
        List<Territory> playerTerritories = new ArrayList<>();
        for (Territory territory : territoryMap.values()) {
            if (territory.getOwnerId() == playerId) {
                playerTerritories.add(territory);
            }
        }
        return playerTerritories;
    }
    
    public int getPlayerTerritoryCount(int playerId) {
        return getPlayerTerritories(playerId).size();
    }
    
    public int getPlayerArmyCount(int playerId) {
        int totalArmies = 0;
        for (Territory territory : getPlayerTerritories(playerId)) {
            totalArmies += territory.getArmySize();
        }
        return totalArmies;
    }
    
    public void nextTurn() {
        turnNumber++;
        int currentIndex = players.indexOf(currentPlayer);
        currentIndex = (currentIndex + 1) % players.size();
        currentPlayer = players.get(currentIndex);
    }
    
    public boolean isGameOver() {
        // Играта приключва ако някой играч контролира 70% от картата
        for (Player player : players) {
            int territories = getPlayerTerritoryCount(player.getId());
            double percentage = (double) territories / territoryMap.size();
            if (percentage >= 0.7) {
                return true;
            }
        }
        return false;
    }
    
    public Player getWinner() {
        Player winner = null;
        int maxTerritories = 0;
        
        for (Player player : players) {
            int territories = getPlayerTerritoryCount(player.getId());
            if (territories > maxTerritories) {
                maxTerritories = territories;
                winner = player;
            }
        }
        
        return winner;
    }
    
    // Getters и Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getMapSeed() {
        return mapSeed;
    }
    
    public void setMapSeed(String mapSeed) {
        this.mapSeed = mapSeed;
    }
    
    public int getTurnNumber() {
        return turnNumber;
    }
    
    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    
    public Map<String, Territory> getTerritoryMap() {
        return territoryMap;
    }
    
    public void setTerritoryMap(Map<String, Territory> territoryMap) {
        this.territoryMap = territoryMap;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    
    public int getMapWidth() {
        return mapWidth;
    }
    
    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }
    
    public int getMapHeight() {
        return mapHeight;
    }
    
    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }
}