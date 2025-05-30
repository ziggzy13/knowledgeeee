package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import models.Territory;
import models.GameSession;
import controllers.GameController;
import utils.IconGenerator;


public class GameMapPanel extends JPanel {
    private static final int HEX_SIZE = 40;
    private static final int MAP_WIDTH = 15;
    private static final int MAP_HEIGHT = 10;
    
    private GameController gameController;
    private GameSession gameSession;
    private Map<Point, HexButton> hexButtons;
    private Territory selectedTerritory;
    
    private JPanel mapPanel;
    private JPanel infoPanel;
    private JLabel turnLabel;
    private JLabel playerInfoLabel;
    private JLabel territoryInfoLabel;
    private JButton endTurnButton;
    private JButton actionButton;
    
    public GameMapPanel(GameController controller, GameSession session) {
        this.gameController = controller;
        this.gameSession = session;
        this.hexButtons = new HashMap<>();
        
        initializeComponents();
        layoutComponents();
        generateMap();
        updateDisplay();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(34, 49, 63));
        
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawHexGrid(g);
            }
        };
        mapPanel.setBackground(new Color(44, 62, 80));
        mapPanel.setLayout(null);
        
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(52, 73, 94));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        turnLabel = new JLabel("Ход: 1");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        turnLabel.setForeground(Color.WHITE);
        
        playerInfoLabel = new JLabel();
        playerInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        playerInfoLabel.setForeground(Color.WHITE);
        
        territoryInfoLabel = new JLabel();
        territoryInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        territoryInfoLabel.setForeground(Color.WHITE);
        
        endTurnButton = new JButton("Завърши ход");
        styleButton(endTurnButton, new Color(231, 76, 60));
        
        actionButton = new JButton("Действие");
        styleButton(actionButton, new Color(46, 204, 113));
        actionButton.setEnabled(false);
    }
    
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
    }
    
    private void layoutComponents() {
        // Централен панел с картата
        JScrollPane scrollPane = new JScrollPane(mapPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        
        // Десен информационен панел
        infoPanel.add(turnLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(playerInfoLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(new JLabel("Избрана територия:"));
        infoPanel.add(territoryInfoLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        infoPanel.add(actionButton);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(endTurnButton);
        
        add(infoPanel, BorderLayout.EAST);
        
        // Listeners
        endTurnButton.addActionListener(e -> endTurn());
        actionButton.addActionListener(e -> performAction());
    }
    
    private void generateMap() {
        int mapPixelWidth = MAP_WIDTH * HEX_SIZE * 3 / 2 + HEX_SIZE / 2;
        int mapPixelHeight = MAP_HEIGHT * HEX_SIZE * 2 + HEX_SIZE;
        mapPanel.setPreferredSize(new Dimension(mapPixelWidth, mapPixelHeight));
        
        for (int row = 0; row < MAP_HEIGHT; row++) {
            for (int col = 0; col < MAP_WIDTH; col++) {
                Point hexCoord = new Point(col, row);
                HexButton hexButton = new HexButton(col, row);
                
                // Изчисляване на позицията
                int x = col * HEX_SIZE * 3 / 2;
                int y = row * HEX_SIZE * 2 + (col % 2) * HEX_SIZE;
                
                hexButton.setBounds(x - HEX_SIZE / 2, y - HEX_SIZE / 2, 
                                   HEX_SIZE * 2, HEX_SIZE * 2);
                hexButton.addActionListener(e -> selectTerritory(hexButton));
                
                mapPanel.add(hexButton);
                hexButtons.put(hexCoord, hexButton);
            }
        }
    }
    
    private void drawHexGrid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Рисуване на линии между хексагоните
        g2d.setColor(new Color(127, 140, 141));
        g2d.setStroke(new BasicStroke(1));
        
        for (HexButton hex : hexButtons.values()) {
            hex.drawHexagon(g2d);
        }
    }
    
    private void selectTerritory(HexButton hexButton) {
        // Деселектиране на предишната
        if (selectedTerritory != null) {
            Point oldCoord = new Point(selectedTerritory.getX(), selectedTerritory.getY());
            HexButton oldHex = hexButtons.get(oldCoord);
            if (oldHex != null) {
                oldHex.setSelected(false);
            }
        }
        
        // Селектиране на новата
        hexButton.setSelected(true);
        selectedTerritory = gameSession.getTerritory(hexButton.getHexX(), hexButton.getHexY());
        
        updateTerritoryInfo();
        updateActionButton();
    }
    
    private void updateDisplay() {
        turnLabel.setText("Ход: " + gameSession.getTurnNumber());
        
        if (gameSession.getCurrentPlayer() != null) {
            var player = gameSession.getCurrentPlayer();
            int territories = gameSession.getPlayerTerritoryCount(player.getId());
            int armies = gameSession.getPlayerArmyCount(player.getId());
            
            playerInfoLabel.setText(String.format(
                "<html>Играч: <b>%s</b><br>" +
                "Територии: %d<br>" +
                "Армии: %d<br>" +
                "Точки: %d</html>",
                player.getUsername(),
                territories,
                armies,
                player.getTotalPoints()
            ));
        }
    }
    
    private void updateTerritoryInfo() {
        if (selectedTerritory == null) {
            territoryInfoLabel.setText("<html>Няма избрана<br>територия</html>");
        } else {
            String info = String.format(
                "<html>Позиция: [%d, %d]<br>" +
                "Собственик: %s<br>" +
                "Армии: %d<br>" +
                "Сграда: %s</html>",
                selectedTerritory.getX(),
                selectedTerritory.getY(),
                selectedTerritory.getOwnerName() != null ? 
                    selectedTerritory.getOwnerName() : "Никой",
                selectedTerritory.getArmySize(),
                selectedTerritory.getBuildingType()
            );
            territoryInfoLabel.setText(info);
        }
    }
    
    private void updateActionButton() {
        if (selectedTerritory == null) {
            actionButton.setEnabled(false);
            actionButton.setText("Действие");
        } else if (!selectedTerritory.isOwned()) {
            actionButton.setEnabled(true);
            actionButton.setText("Завладей");
        } else if (selectedTerritory.getOwnerId() == gameSession.getCurrentPlayer().getId()) {
            actionButton.setEnabled(true);
            actionButton.setText("Подобри");
        } else {
            actionButton.setEnabled(true);
            actionButton.setText("Атакувай");
        }
    }
    
    private void performAction() {
        if (selectedTerritory == null) return;
        
        // Показваме QuizPanel за действието
        QuizPanel quizPanel = new QuizPanel(selectedTerritory, gameController, this);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                   "Въпрос за действие", true);
        dialog.setContentPane(quizPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void endTurn() {
        gameController.endTurn();
        updateDisplay();
        JOptionPane.showMessageDialog(this, "Ходът завърши. Следващ играч!");
    }
    
    public void refreshMap() {
        // Обновяване на картата след действие
        for (Map.Entry<Point, HexButton> entry : hexButtons.entrySet()) {
            Point coord = entry.getKey();
            HexButton hex = entry.getValue();
            Territory territory = gameSession.getTerritory(coord.x, coord.y);
            
            if (territory != null && territory.isOwned()) {
                hex.setOwner(territory.getOwnerId());
                hex.setArmyCount(territory.getArmySize());
            }
        }
        
        updateDisplay();
        updateTerritoryInfo();
        mapPanel.repaint();
    }
    
    // Вътрешен клас за хексагонален бутон
    private class HexButton extends JButton {
        private int hexX, hexY;
        private boolean selected;
        private int ownerId;
        private int armyCount;
        
        public HexButton(int x, int y) {
            this.hexX = x;
            this.hexY = y;
            this.selected = false;
            this.ownerId = -1;
            this.armyCount = 0;
            
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
        }
        
        public void drawHexagon(Graphics2D g2d) {
            int centerX = getX() + getWidth() / 2;
            int centerY = getY() + getHeight() / 2;
            
            Polygon hexagon = createHexagon(centerX, centerY, HEX_SIZE);
            
            // Цвят според собственика
            if (ownerId > 0) {
                g2d.setColor(getPlayerColor(ownerId));
                g2d.fillPolygon(hexagon);
            }
            
            // Граница
            g2d.setColor(selected ? Color.YELLOW : Color.GRAY);
            g2d.setStroke(new BasicStroke(selected ? 3 : 1));
            g2d.drawPolygon(hexagon);
            
            // Текст за армии
            if (armyCount > 0) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                String text = String.valueOf(armyCount);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = centerX - fm.stringWidth(text) / 2;
                int textY = centerY + fm.getHeight() / 4;
                g2d.drawString(text, textX, textY);
            }
        }
        
        private Polygon createHexagon(int centerX, int centerY, int radius) {
            Polygon hexagon = new Polygon();
            for (int i = 0; i < 6; i++) {
                int angle = 60 * i + 30;
                int x = centerX + (int) (radius * Math.cos(Math.toRadians(angle)));
                int y = centerY + (int) (radius * Math.sin(Math.toRadians(angle)));
                hexagon.addPoint(x, y);
            }
            return hexagon;
        }
        
        private Color getPlayerColor(int playerId) {
            Color[] colors = {
                new Color(231, 76, 60),   // Червено
                new Color(52, 152, 219),  // Синьо
                new Color(46, 204, 113),  // Зелено
                new Color(241, 196, 15)   // Жълто
            };
            return colors[playerId % colors.length];
        }
        
        // Getters и setters
        public int getHexX() { return hexX; }
        public int getHexY() { return hexY; }
        public void setSelected(boolean selected) { 
            this.selected = selected;
            repaint();
        }
        public void setOwner(int ownerId) { this.ownerId = ownerId; }
        public void setArmyCount(int count) { this.armyCount = count; }
    }
    ImageIcon historyIcon = IconGenerator.getIcon("history", 32); // 32x32 пиксела
    JLabel label = new JLabel("История", historyIcon, JLabel.LEFT);

    JButton castleButton = new JButton("Замък", IconGenerator.getIcon("castle", 24));

    ImageIcon bigTrophyIcon = IconGenerator.getIcon("first_victory", 64);

}