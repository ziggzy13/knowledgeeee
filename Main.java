package main;

import javax.swing.*;
import views.LoginPanel;
import views.MainMenuPanel;
import views.RegisterPanel;
import controllers.AuthController;
import database.DatabaseConnection;

public class Main extends JFrame {
    private static Main instance;
    private JPanel currentPanel;
    
    private Main() {
        setTitle("Империя на Знанието");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Инициализиране на връзката с базата данни
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(this, 
                "Грешка при свързване с базата данни!", 
                "Грешка", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Започваме с login панела	
        showLoginPanel();
    }
    
    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }
    
    public void switchPanel(JPanel newPanel) {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = newPanel;
        add(currentPanel);
        revalidate();
        repaint();
    }
    
    public void showLoginPanel() {
        switchPanel(new LoginPanel());
    }
    
    public void showMainMenu() {
        switchPanel(new MainMenuPanel());
    }
    
    public static void main(String[] args) {
        // Задаване на Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Стартиране на приложението в EDT
        SwingUtilities.invokeLater(() -> {
            Main game = Main.getInstance();
            game.setVisible(true);
        });
    }
    public void showRegisterPanel() {
        switchPanel(new RegisterPanel());
    }
}