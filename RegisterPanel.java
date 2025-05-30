package views;

import javax.swing.*;
import java.awt.*;
import controllers.AuthController;
import main.Main;

public class RegisterPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JButton registerButton;
    private JButton backButton;
    private JLabel messageLabel;
    
    public RegisterPanel() {
        initializeComponents();
        layoutComponents();
        addListeners();
    }
    
    private void initializeComponents() {
        setLayout(new GridBagLayout());
        setBackground(new Color(44, 62, 80));
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        emailField = new JTextField(20);
        registerButton = new JButton("Регистрирай се");
        backButton = new JButton("Назад");
        messageLabel = new JLabel(" ");
        
        // Стилизиране
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        
        usernameField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        confirmPasswordField.setFont(fieldFont);
        emailField.setFont(fieldFont);
        
        registerButton.setFont(labelFont);
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        backButton.setFont(labelFont);
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        messageLabel.setForeground(new Color(231, 76, 60));
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Заглавие
        JLabel titleLabel = new JLabel("Регистрация");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(236, 240, 241));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        
        // Инструкции
        JLabel instructionLabel = new JLabel("Създайте нов профил за игра");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        instructionLabel.setForeground(new Color(189, 195, 199));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 20, 10);
        add(instructionLabel, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel usernameLabel = new JLabel("Потребителско име:");
        usernameLabel.setForeground(new Color(236, 240, 241));
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);
        
        // Email (опционално)
        JLabel emailLabel = new JLabel("Email (опционално):");
        emailLabel.setForeground(new Color(236, 240, 241));
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(emailLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(emailField, gbc);
        
        // Password
        JLabel passwordLabel = new JLabel("Парола:");
        passwordLabel.setForeground(new Color(236, 240, 241));
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);
        
        // Confirm Password
        JLabel confirmLabel = new JLabel("Потвърди парола:");
        confirmLabel.setForeground(new Color(236, 240, 241));
        confirmLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        add(confirmLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(confirmPasswordField, gbc);
        
        // Правила за паролата
        JLabel rulesLabel = new JLabel("<html><center>Паролата трябва да е поне 6 символа<br>и да съдържа букви и цифри</center></html>");
        rulesLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        rulesLabel.setForeground(new Color(189, 195, 199));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 10, 15, 10);
        add(rulesLabel, gbc);
        
        // Бутони
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(buttonPanel, gbc);
        
        // Съобщения
        gbc.gridy = 8;
        add(messageLabel, gbc);
    }
    
    private void addListeners() {
        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> Main.getInstance().showLoginPanel());
        
        // Enter key за регистрация
        confirmPasswordField.addActionListener(e -> handleRegister());
    }
    
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        
        // Валидации
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Моля, попълнете всички задължителни полета!", Color.RED);
            return;
        }
        
        if (username.length() < 3) {
            showMessage("Потребителското име трябва да е поне 3 символа!", Color.RED);
            return;
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showMessage("Потребителското име може да съдържа само букви, цифри и _", Color.RED);
            return;
        }
        
        if (password.length() < 6) {
            showMessage("Паролата трябва да е поне 6 символа!", Color.RED);
            return;
        }
        
        if (!password.matches(".*[a-zA-Z]+.*") || !password.matches(".*[0-9]+.*")) {
            showMessage("Паролата трябва да съдържа букви и цифри!", Color.RED);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showMessage("Паролите не съвпадат!", Color.RED);
            confirmPasswordField.setText("");
            return;
        }
        
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showMessage("Невалиден email адрес!", Color.RED);
            return;
        }
        
        // Опит за регистрация
        if (AuthController.register(username, password)) {
            showMessage("Регистрацията е успешна!", new Color(46, 204, 113));
            
            // Изчистване на полетата
            usernameField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            emailField.setText("");
            
            // Връщане към login след 2 секунди
            Timer timer = new Timer(2000, evt -> {
                Main.getInstance().showLoginPanel();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            showMessage("Потребителското име вече съществува!", Color.RED);
        }
    }
    
    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
        
        // Изчистване на съобщението след 4 секунди
        Timer timer = new Timer(4000, e -> messageLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}