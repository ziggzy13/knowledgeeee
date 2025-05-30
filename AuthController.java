package controllers;

import models.Player;
import database.PlayerDAO;

public class AuthController {
    private static Player currentPlayer = null;
    
    /**
     * Опит за влизане в системата
     * @param username потребителско име
     * @param password парола
     * @return true ако влизането е успешно
     */
    public static boolean login(String username, String password) {
        Player player = PlayerDAO.login(username, password);
        
        if (player != null) {
            currentPlayer = player;
            return true;
        }
        
        return false;
    }
    
    /**
     * Регистрация на нов играч
     * @param username потребителско име
     * @param password парола
     * @return true ако регистрацията е успешна
     */
    public static boolean register(String username, String password) {
        // Проверка дали потребителското име е свободно
        if (PlayerDAO.getPlayerByUsername(username) != null) {
            return false;
        }
        
        // Създаване на нов играч
        return PlayerDAO.createPlayer(username, password);
    }
    
    /**
     * Излизане от системата
     */
    public static void logout() {
        currentPlayer = null;
    }
    
    /**
     * Получаване на текущия играч
     * @return текущо влезлия играч или null
     */
    public static Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Проверка дали има влязъл играч
     * @return true ако има влязъл играч
     */
    public static boolean isLoggedIn() {
        return currentPlayer != null;
    }
    
    /**
     * Обновяване на данните за текущия играч от базата
     */
    public static void refreshCurrentPlayer() {
        if (currentPlayer != null) {
            Player updated = PlayerDAO.getPlayerById(currentPlayer.getId());
            if (updated != null) {
                currentPlayer = updated;
            }
        }
    }
    
    /**
     * Промяна на парола
     * @param oldPassword стара парола
     * @param newPassword нова парола
     * @return true ако промяната е успешна
     */
    public static boolean changePassword(String oldPassword, String newPassword) {
        if (currentPlayer == null) {
            return false;
        }
        
        // Проверка на старата парола
        Player check = PlayerDAO.login(currentPlayer.getUsername(), oldPassword);
        if (check == null || check.getId() != currentPlayer.getId()) {
            return false;
        }
        
        // Актуализация на паролата
        return PlayerDAO.updatePassword(currentPlayer.getId(), newPassword);
    }
    
    /**
     * Добавяне на точки към текущия играч
     * @param points точки за добавяне
     */
    public static void addPoints(int points) {
        if (currentPlayer != null) {
            PlayerDAO.updatePlayerStats(currentPlayer.getId(), points, false);
            refreshCurrentPlayer();
        }
    }
    
    /**
     * Записване на победа за текущия играч
     * @param points спечелени точки
     */
    public static void recordVictory(int points) {
        if (currentPlayer != null) {
            PlayerDAO.updatePlayerStats(currentPlayer.getId(), points, true);
            refreshCurrentPlayer();
        }
    }
}