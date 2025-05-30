package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import controllers.AuthController;
import main.Main;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    
    public LoginPanel() {
        initializeComponents();
        layoutComponents();
        addListeners();
    }
    
    private void initializeComponents() {
        setLayout(new GridBagLayout());
        setBackground(new Color(44, 62, 80));
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Влизане");
        registerButton = new JButton("Регистрация");
        messageLabel = new JLabel(" ");
        
        // Стилизиране
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        
        usernameField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        
        loginButton.setFont(labelFont);
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        registerButton.setFont(labelFont);
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        messageLabel.setForeground(new Color(231, 76, 60));
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Заглавие
        JLabel titleLabel = new JLabel("Империя на Знанието");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(236, 240, 241));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        
        // Подзаглавие
        JLabel subtitleLabel = new JLabel("Завладей света с мощта на знанието!");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(189, 195, 199));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 30, 10);
        add(subtitleLabel, gbc);
        
        // Username
        JLabel usernameLabel = new JLabel("Потребителско име:");
        usernameLabel.setForeground(new Color(236, 240, 241));
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);
        
        // Password
        JLabel passwordLabel = new JLabel("Парола:");
        passwordLabel.setForeground(new Color(236, 240, 241));
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);
        
        // Бутони
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(buttonPanel, gbc);
        
        // Съобщения
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(messageLabel, gbc);
    }
    
    private void addListeners() {
        loginButton.addActionListener(e -> handleLogin());
       // ТОВА Е ОПЦИЯ 1, А ДОЛНОТО Е ОПЦИЯ 2 ;;;;;;;;;;;;; registerButton.addActionListener(e -> handleRegister());
        registerButton.addActionListener(e -> showRegisterPanel());
        
        // Enter key за login
        ActionListener enterAction = e -> handleLogin();
        usernameField.addActionListener(enterAction);
        passwordField.addActionListener(enterAction);
    }
    private void showRegisterPanel() {
        Main.getInstance().switchPanel(new RegisterPanel());
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Моля, попълнете всички полета!", Color.RED);
            return;
        }
        
        if (AuthController.login(username, password)) {
            Main.getInstance().showMainMenu();
        } else {
            showMessage("Грешно потребителско име или парола!", Color.RED);
            passwordField.setText("");
        }
    }
    
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Моля, попълнете всички полета!", Color.RED);
            return;
        }
        
        if (username.length() < 3) {
            showMessage("Потребителското име трябва да е поне 3 символа!", Color.RED);
            return;
        }
        
        if (password.length() < 6) {
            showMessage("Паролата трябва да е поне 6 символа!", Color.RED);
            return;
        }
        
        if (AuthController.register(username, password)) {
            showMessage("Регистрацията е успешна! Може да влезете.", new Color(46, 204, 113));
            passwordField.setText("");
        } else {
            showMessage("Потребителското име вече съществува!", Color.RED);
        }
    }
    
    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
        
        // Изчистване на съобщението след 3 секунди
        Timer timer = new Timer(3000, e -> messageLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}