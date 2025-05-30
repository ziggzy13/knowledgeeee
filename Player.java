package models;

public class Player {
    private int id;
    private String username;
    private int totalPoints;
    private int gamesPlayed;
    private int victories;
    
    // Конструктори
    public Player() {}
    
    public Player(String username) {
        this.username = username;
        this.totalPoints = 0;
        this.gamesPlayed = 0;
        this.victories = 0;
    }
    
    // Getters и Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public int getTotalPoints() {
        return totalPoints;
    }
    
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
    
    public int getGamesPlayed() {
        return gamesPlayed;
    }
    
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }
    
    public int getVictories() {
        return victories;
    }
    
    public void setVictories(int victories) {
        this.victories = victories;
    }
    
    // Utility методи
    public double getWinRate() {
        if (gamesPlayed == 0) return 0.0;
        return (double) victories / gamesPlayed * 100;
    }
    
    @Override
    public String toString() {
        return username + " - Точки: " + totalPoints + 
               ", Победи: " + victories + "/" + gamesPlayed;
    }
}