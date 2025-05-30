package models;

public class Territory {
    private int x;
    private int y;
    private int ownerId;
    private String ownerName;
    private String terrainType;
    private String categoryType;
    private String buildingType;
    private int armySize;
    private boolean isCapital;
    
    // Типове терени
    public static final String TERRAIN_PLAINS = "PLAINS";
    public static final String TERRAIN_FOREST = "FOREST";
    public static final String TERRAIN_MOUNTAIN = "MOUNTAIN";
    public static final String TERRAIN_WATER = "WATER";
    
    // Типове сгради
    public static final String BUILDING_NONE = "NONE";
    public static final String BUILDING_CASTLE = "CASTLE";
    public static final String BUILDING_LIBRARY = "LIBRARY";
    public static final String BUILDING_BARRACKS = "BARRACKS";
    public static final String BUILDING_MARKET = "MARKET";
    
    // Конструктори
    public Territory() {
        this.buildingType = BUILDING_NONE;
        this.armySize = 0;
        this.isCapital = false;
    }
    
    public Territory(int x, int y) {
        this();
        this.x = x;
        this.y = y;
    }
    
    // Utility методи
    public boolean isOwned() {
        return ownerId > 0;
    }
    
    public boolean hasArmy() {
        return armySize > 0;
    }
    
    public boolean hasBuilding() {
        return !BUILDING_NONE.equals(buildingType);
    }
    
    public int getDefenseBonus() {
        switch (buildingType) {
            case BUILDING_CASTLE: return 5;
            case BUILDING_BARRACKS: return 3;
            default: return 0;
        }
    }
    
    public int getKnowledgeBonus() {
        return BUILDING_LIBRARY.equals(buildingType) ? 2 : 0;
    }
    
    // Getters и Setters
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public String getTerrainType() {
        return terrainType;
    }
    
    public void setTerrainType(String terrainType) {
        this.terrainType = terrainType;
    }
    
    public String getCategoryType() {
        return categoryType;
    }
    
    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }
    
    public String getBuildingType() {
        return buildingType;
    }
    
    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }
    
    public int getArmySize() {
        return armySize;
    }
    
    public void setArmySize(int armySize) {
        this.armySize = armySize;
    }
    
    public boolean isCapital() {
        return isCapital;
    }
    
    public void setCapital(boolean capital) {
        isCapital = capital;
    }
    
    @Override
    public String toString() {
        return String.format("Territory[%d,%d] - Owner: %s, Army: %d, Building: %s",
                x, y, ownerName != null ? ownerName : "None", armySize, buildingType);
    }
}