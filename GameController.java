package controllers;

import models.*;
import database.*;
import java.util.*;

public class GameController {
    private GameSession currentSession;
    private Random random;
    
    public GameController() {
        this.random = new Random();
    }
    
    /**
     * Започване на нова игра
     * @param difficulty ниво на трудност (1-3)
     */
    public void startNewGame(int difficulty) {
        // Създаване на нова игрова сесия
        String mapSeed = generateMapSeed();
        int sessionId = GameSessionDAO.createNewGameSession(mapSeed);
        
        if (sessionId > 0) {
            currentSession = new GameSession();
            currentSession.setId(sessionId);
            currentSession.setMapSeed(mapSeed);
            
            // Добавяне на играчи
            Player humanPlayer = AuthController.getCurrentPlayer();
            currentSession.addPlayer(humanPlayer);
            currentSession.setCurrentPlayer(humanPlayer);
            
            // Добавяне на AI играчи според трудността
            int aiPlayers = 2 + difficulty;
            for (int i = 0; i < aiPlayers; i++) {
                Player aiPlayer = new Player("AI_" + (i + 1));
                aiPlayer.setId(-(i + 1)); // Отрицателни ID за AI
                currentSession.addPlayer(aiPlayer);
            }
            
            // Генериране на картата
            generateMap();
            
            // Разпределяне на начални територии
            distributeStartingTerritories();
        }
    }
    
    /**
     * Генериране на seed за картата
     */
    private String generateMapSeed() {
        return "MAP_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }
    
    /**
     * Генериране на игровата карта
     */
    private void generateMap() {
        int width = currentSession.getMapWidth();
        int height = currentSession.getMapHeight();
        
        String[] categories = {"История", "География", "Наука", "Изкуство", "Спорт"};
        String[] terrains = {Territory.TERRAIN_PLAINS, Territory.TERRAIN_FOREST, 
                           Territory.TERRAIN_MOUNTAIN, Territory.TERRAIN_WATER};
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Territory territory = new Territory(x, y);
                
                // Случаен терен (80% суша, 20% вода)
                if (random.nextDouble() < 0.8) {
                    territory.setTerrainType(terrains[random.nextInt(3)]);
                } else {
                    territory.setTerrainType(Territory.TERRAIN_WATER);
                }
                
                // Случайна категория
                territory.setCategoryType(categories[random.nextInt(categories.length)]);
                
                currentSession.addTerritory(territory);
            }
        }
    }
    
    /**
     * Разпределяне на начални територии
     */
    private void distributeStartingTerritories() {
        List<Territory> landTerritories = new ArrayList<>();
        
        // Събиране на всички територии, които не са вода
        for (Territory t : currentSession.getTerritoryMap().values()) {
            if (!Territory.TERRAIN_WATER.equals(t.getTerrainType())) {
                landTerritories.add(t);
            }
        }
        
        Collections.shuffle(landTerritories);
        
        // Даване на по 3 територии на всеки играч
        int territoriesPerPlayer = 3;
        int playerIndex = 0;
        
        for (int i = 0; i < currentSession.getPlayers().size() * territoriesPerPlayer; i++) {
            if (i < landTerritories.size()) {
                Territory territory = landTerritories.get(i);
                Player player = currentSession.getPlayers().get(playerIndex);
                
                territory.setOwnerId(player.getId());
                territory.setOwnerName(player.getUsername());
                territory.setArmySize(GameSession.STARTING_ARMIES);
                
                // Първата територия е столица
                if (i < currentSession.getPlayers().size()) {
                    territory.setCapital(true);
                    territory.setBuildingType(Territory.BUILDING_CASTLE);
                }
                
                GameSessionDAO.saveTerritory(currentSession.getId(), player.getId(), territory);
                
                playerIndex = (playerIndex + 1) % currentSession.getPlayers().size();
            }
        }
    }
    
    /**
     * Завладяване на територия
     */
    public boolean captureTerritory(Territory territory) {
        if (territory.isOwned() || Territory.TERRAIN_WATER.equals(territory.getTerrainType())) {
            return false;
        }
        
        Player currentPlayer = currentSession.getCurrentPlayer();
        territory.setOwnerId(currentPlayer.getId());
        territory.setOwnerName(currentPlayer.getUsername());
        territory.setArmySize(1);
        
        // Запазване в базата данни
        GameSessionDAO.saveTerritory(currentSession.getId(), currentPlayer.getId(), territory);
        
        // Добавяне на точки
        AuthController.addPoints(10);
        
        return true;
    }
    
    /**
     * Атака на територия
     */
    public boolean attackTerritory(Territory territory) {
        if (!territory.isOwned() || territory.getOwnerId() == currentSession.getCurrentPlayer().getId()) {
            return false;
        }
        
        // Проста битка - атакуващият печели ако отговори правилно
        Player attacker = currentSession.getCurrentPlayer();
        
        // Намаляване на армиите на защитника
        int newArmySize = Math.max(0, territory.getArmySize() - 2);
        territory.setArmySize(newArmySize);
        
        // Ако армиите са 0, територията се завладява
        if (newArmySize == 0) {
            territory.setOwnerId(attacker.getId());
            territory.setOwnerName(attacker.getUsername());
            territory.setArmySize(1);
            territory.setBuildingType(Territory.BUILDING_NONE);
            territory.setCapital(false);
            
            AuthController.addPoints(20);
        } else {
            AuthController.addPoints(5);
        }
        
        GameSessionDAO.saveTerritory(currentSession.getId(), territory.getOwnerId(), territory);
        
        return true;
    }
    
    /**
     * Подобрение на територия
     */
    public boolean upgradeTerritory(Territory territory) {
        if (!territory.isOwned() || territory.getOwnerId() != currentSession.getCurrentPlayer().getId()) {
            return false;
        }
        
        // Добавяне на армии
        territory.setArmySize(territory.getArmySize() + 2);
        
        // Ако няма сграда, добавяне на случайна
        if (Territory.BUILDING_NONE.equals(territory.getBuildingType())) {
            String[] buildings = {Territory.BUILDING_LIBRARY, Territory.BUILDING_BARRACKS, 
                                Territory.BUILDING_MARKET};
            territory.setBuildingType(buildings[random.nextInt(buildings.length)]);
        }
        
        GameSessionDAO.saveTerritory(currentSession.getId(), territory.getOwnerId(), territory);
        
        AuthController.addPoints(5);
        
        return true;
    }
    
    /**
     * Край на хода
     */
    public void endTurn() {
        // Добавяне на армии според броя територии
        Player currentPlayer = currentSession.getCurrentPlayer();
        int territoriesCount = currentSession.getPlayerTerritoryCount(currentPlayer.getId());
        int newArmies = GameSession.ARMIES_PER_TURN + territoriesCount / 3;
        
        // Разпределяне на новите армии случайно
        List<Territory> playerTerritories = currentSession.getPlayerTerritories(currentPlayer.getId());
        for (int i = 0; i < newArmies && !playerTerritories.isEmpty(); i++) {
            Territory randomTerritory = playerTerritories.get(random.nextInt(playerTerritories.size()));
            randomTerritory.setArmySize(randomTerritory.getArmySize() + 1);
            GameSessionDAO.saveTerritory(currentSession.getId(), currentPlayer.getId(), randomTerritory);
        }
        
        // Преминаване към следващ играч
        currentSession.nextTurn();
        GameSessionDAO.updateTurnNumber(currentSession.getId(), currentSession.getTurnNumber());
        
        // AI ход (ако е AI играч)
        if (currentSession.getCurrentPlayer().getId() < 0) {
            performAITurn();
        }
        
        // Проверка за край на играта
        if (currentSession.isGameOver()) {
            endGame();
        }
    }
    
    /**
     * AI логика за ход
     */
    private void performAITurn() {
        Player aiPlayer = currentSession.getCurrentPlayer();
        
        // AI прави 2-3 действия на ход
        int actions = 2 + random.nextInt(2);
        
        for (int i = 0; i < actions; i++) {
            // 50% шанс за атака, 50% за завладяване/подобрение
            if (random.nextBoolean()) {
                // Опит за атака
                List<Territory> enemyTerritories = getAdjacentEnemyTerritories(aiPlayer.getId());
                if (!enemyTerritories.isEmpty()) {
                    Territory target = enemyTerritories.get(random.nextInt(enemyTerritories.size()));
                    // AI има 70% шанс за успех
                    if (random.nextDouble() < 0.7) {
                        attackTerritory(target);
                    }
                }
            } else {
                // Опит за завладяване или подобрение
                List<Territory> emptyTerritories = getAdjacentEmptyTerritories(aiPlayer.getId());
                if (!emptyTerritories.isEmpty()) {
                    Territory target = emptyTerritories.get(random.nextInt(emptyTerritories.size()));
                    // AI има 80% шанс за успех при завладяване
                    if (random.nextDouble() < 0.8) {
                        captureTerritory(target);
                    }
                } else {
                    // Подобрение на случайна своя територия
                    List<Territory> ownTerritories = currentSession.getPlayerTerritories(aiPlayer.getId());
                    if (!ownTerritories.isEmpty()) {
                        Territory target = ownTerritories.get(random.nextInt(ownTerritories.size()));
                        upgradeTerritory(target);
                    }
                }
            }
        }
        
        // Автоматичен край на хода
        endTurn();
    }
    
    /**
     * Намиране на съседни вражески територии
     */
    private List<Territory> getAdjacentEnemyTerritories(int playerId) {
        List<Territory> enemyTerritories = new ArrayList<>();
        
        for (Territory playerTerritory : currentSession.getPlayerTerritories(playerId)) {
            for (Territory neighbor : getNeighbors(playerTerritory)) {
                if (neighbor.isOwned() && neighbor.getOwnerId() != playerId) {
                    enemyTerritories.add(neighbor);
                }
            }
        }
        
        return enemyTerritories;
    }
    
    /**
     * Намиране на съседни празни територии
     */
    private List<Territory> getAdjacentEmptyTerritories(int playerId) {
        List<Territory> emptyTerritories = new ArrayList<>();
        
        for (Territory playerTerritory : currentSession.getPlayerTerritories(playerId)) {
            for (Territory neighbor : getNeighbors(playerTerritory)) {
                if (!neighbor.isOwned() && !Territory.TERRAIN_WATER.equals(neighbor.getTerrainType())) {
                    emptyTerritories.add(neighbor);
                }
            }
        }
        
        return emptyTerritories;
    }
    
    /**
     * Намиране на съседни територии
     */
    private List<Territory> getNeighbors(Territory territory) {
        List<Territory> neighbors = new ArrayList<>();
        int x = territory.getX();
        int y = territory.getY();
        
        // Хексагонални съседи
        int[][] offsets = (x % 2 == 0) ?
            new int[][]{{0,-1}, {1,-1}, {1,0}, {0,1}, {-1,0}, {-1,-1}} :
            new int[][]{{0,-1}, {1,0}, {1,1}, {0,1}, {-1,1}, {-1,0}};
        
        for (int[] offset : offsets) {
            Territory neighbor = currentSession.getTerritory(x + offset[0], y + offset[1]);
            if (neighbor != null) {
                neighbors.add(neighbor);
            }
        }
        
        return neighbors;
    }
    
    /**
     * Край на играта
     */
    private void endGame() {
        Player winner = currentSession.getWinner();
        
        if (winner != null && winner.getId() == AuthController.getCurrentPlayer().getId()) {
            // Човешкият играч е победител
            int bonusPoints = 100 + currentSession.getPlayerTerritoryCount(winner.getId()) * 5;
            AuthController.recordVictory(bonusPoints);
        }
        
        // Показване на резултати
        javax.swing.JOptionPane.showMessageDialog(null,
            "Играта приключи!\nПобедител: " + (winner != null ? winner.getUsername() : "Няма"),
            "Край на играта",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Getters
    public GameSession getCurrentSession() {
        return currentSession;
    }
    
    public Player getCurrentPlayer() {
        return currentSession != null ? currentSession.getCurrentPlayer() : null;
    }
}