package views;

import javax.swing.*;
import java.awt.*;
import controllers.AuthController;
import controllers.GameController;
import main.Main;

public class MainMenuPanel extends JPanel {
    private JButton newGameButton;
    private JButton continueGameButton;
    private JButton multiplayerButton;
    private JButton statisticsButton;
    private JButton settingsButton;
    private JButton logoutButton;
    private JLabel welcomeLabel;
    
    public MainMenuPanel() {
        initializeComponents();
        layoutComponents();
        addListeners();
        updateWelcomeMessage();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(44, 62, 80));
        
        // Инициализиране на бутоните
        newGameButton = createMenuButton("Нова игра", new Color(52, 152, 219));
        continueGameButton = createMenuButton("Продължи игра", new Color(155, 89, 182));
        multiplayerButton = createMenuButton("Мултиплейър", new Color(46, 204, 113));
        statisticsButton = createMenuButton("Статистики", new Color(241, 196, 15));
        settingsButton = createMenuButton("Настройки", new Color(149, 165, 166));
        logoutButton = createMenuButton("Изход", new Color(231, 76, 60));
        
        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(236, 240, 241));
    }
    
    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 60));
        
        // Hover ефект
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void layoutComponents() {
        // Горен панел с поздрав
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(34, 49, 63));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        topPanel.add(welcomeLabel);
        add(topPanel, BorderLayout.NORTH);
        
        // Централен панел с бутони
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(new Color(44, 62, 80));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        
        // Заглавие
        JLabel titleLabel = new JLabel("ИМПЕРИЯ НА ЗНАНИЕТО");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setForeground(new Color(236, 240, 241));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 40, 0);
        centerPanel.add(titleLabel, gbc);
        
        // Бутони
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridy = 1;
        centerPanel.add(newGameButton, gbc);
        
        gbc.gridy = 2;
        centerPanel.add(continueGameButton, gbc);
        
        gbc.gridy = 3;
        centerPanel.add(multiplayerButton, gbc);
        
        gbc.gridy = 4;
        centerPanel.add(statisticsButton, gbc);
        
        gbc.gridy = 5;
        centerPanel.add(settingsButton, gbc);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(30, 0, 10, 0);
        centerPanel.add(logoutButton, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Долен панел с информация
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(34, 49, 63));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel versionLabel = new JLabel("Версия 1.0 | © 2024 Империя на Знанието");
        versionLabel.setForeground(new Color(127, 140, 141));
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        bottomPanel.add(versionLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void addListeners() {
        newGameButton.addActionListener(e -> startNewGame());
        continueGameButton.addActionListener(e -> continueGame());
        multiplayerButton.addActionListener(e -> showMultiplayerOptions());
        statisticsButton.addActionListener(e -> showStatistics());
        settingsButton.addActionListener(e -> showSettings());
        logoutButton.addActionListener(e -> logout());
    }
    
    private void updateWelcomeMessage() {
        if (AuthController.getCurrentPlayer() != null) {
            String username = AuthController.getCurrentPlayer().getUsername();
            int points = AuthController.getCurrentPlayer().getTotalPoints();
            welcomeLabel.setText(String.format("Добре дошъл, %s! Точки: %d", username, points));
        }
    }
    
    private void startNewGame() {
        String[] options = {"Лесно", "Средно", "Трудно"};
        int choice = JOptionPane.showOptionDialog(this,
                "Изберете ниво на трудност:",
                "Нова игра",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        
        if (choice >= 0) {
            GameController controller = new GameController();
            controller.startNewGame(choice + 1);
            // Тук ще превключим към GameMapPanel
            JOptionPane.showMessageDialog(this, "Играта започва!");
        }
    }
    
    private void continueGame() {
        JOptionPane.showMessageDialog(this, 
                "Функцията за продължаване на игра ще бъде добавена скоро!",
                "В разработка",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showMultiplayerOptions() {
        JOptionPane.showMessageDialog(this, 
                "Мултиплейър режимът ще бъде добавен скоро!",
                "В разработка",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showStatistics() {
        // Тук ще покажем статистиките на играча
        if (AuthController.getCurrentPlayer() != null) {
            var player = AuthController.getCurrentPlayer();
            String stats = String.format(
                "Статистики за %s:\n\n" +
                "Общо точки: %d\n" +
                "Изиграни игри: %d\n" +
                "Победи: %d\n" +
                "Процент победи: %.1f%%",
                player.getUsername(),
                player.getTotalPoints(),
                player.getGamesPlayed(),
                player.getVictories(),
                player.getWinRate()
            );
            
            JOptionPane.showMessageDialog(this, stats, "Статистики", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showSettings() {
        JOptionPane.showMessageDialog(this, 
                "Настройките ще бъдат добавени скоро!",
                "В разработка",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Сигурни ли сте, че искате да излезете?",
                "Потвърждение",
                JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            AuthController.logout();
            Main.getInstance().showLoginPanel();
        }
    }
}