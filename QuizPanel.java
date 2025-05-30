package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import javax.swing.Timer;
import models.Territory;
import models.Question;
import controllers.GameController;
import controllers.QuizController;

public class QuizPanel extends JPanel {
    private Territory territory;
    private GameController gameController;
    private GameMapPanel mapPanel;
    private QuizController quizController;
    
    private JLabel categoryLabel;
    private JLabel questionLabel;
    private JLabel timerLabel;
    private JProgressBar progressBar;
    private List<JButton> answerButtons;
    private JButton confirmButton;
    private JLabel resultLabel;
    
    private Question currentQuestion;
    private String selectedAnswer;
    private Timer countdownTimer;
    private int timeRemaining;
    private static final int TIME_LIMIT = 30; // секунди за отговор
    
    public QuizPanel(Territory territory, GameController gameController, GameMapPanel mapPanel) {
        this.territory = territory;
        this.gameController = gameController;
        this.mapPanel = mapPanel;
        this.quizController = new QuizController();
        
        initializeComponents();
        layoutComponents();
        loadQuestion();
        startTimer();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(44, 62, 80));
        setPreferredSize(new Dimension(600, 500));
        
        categoryLabel = new JLabel();
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        categoryLabel.setForeground(new Color(52, 152, 219));
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        timerLabel = new JLabel(String.valueOf(TIME_LIMIT));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(new Color(46, 204, 113));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        progressBar = new JProgressBar(0, TIME_LIMIT);
        progressBar.setValue(TIME_LIMIT);
        progressBar.setStringPainted(true);
        progressBar.setString("Оставащо време");
        
        answerButtons = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            JButton button = new JButton();
            styleAnswerButton(button);
            answerButtons.add(button);
        }
        
        confirmButton = new JButton("Потвърди отговор");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 16));
        confirmButton.setBackground(new Color(46, 204, 113));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.setEnabled(false);
        
        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void styleAnswerButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(127, 140, 141), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(500, 60));
        
        // Hover ефект
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(41, 128, 185));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled() && !button.getBackground().equals(new Color(241, 196, 15))) {
                    button.setBackground(new Color(52, 73, 94));
                }
            }
        });
    }
    
    private void layoutComponents() {
        // Горен панел с категория и таймер
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(34, 49, 63));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel timerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        timerPanel.setOpaque(false);
        timerPanel.add(timerLabel);
        timerPanel.add(progressBar);
        
        topPanel.add(categoryLabel, BorderLayout.WEST);
        topPanel.add(timerPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Централен панел с въпрос и отговори
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(44, 62, 80));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Въпрос
        JPanel questionPanel = new JPanel();
        questionPanel.setOpaque(false);
        questionPanel.setLayout(new BorderLayout());
        questionLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));
        questionPanel.add(questionLabel, BorderLayout.CENTER);
        centerPanel.add(questionPanel);
        
        // Отговори
        for (JButton button : answerButtons) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(button);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Долен панел с бутон за потвърждение и резултат
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(34, 49, 63));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        bottomPanel.add(confirmButton);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bottomPanel.add(resultLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Listeners
        confirmButton.addActionListener(e -> checkAnswer());
        
        for (int i = 0; i < answerButtons.size(); i++) {
            final int index = i;
            answerButtons.get(i).addActionListener(e -> selectAnswer(index));
        }
    }
    
    private void loadQuestion() {
        // Зареждане на въпрос според категорията на територията
        String category = territory.getCategoryType();
        if (category == null) {
            category = "История"; // По подразбиране
        }
        
        int difficulty = calculateDifficulty();
        currentQuestion = quizController.getRandomQuestion(category, difficulty);
        
        if (currentQuestion != null) {
            displayQuestion();
        } else {
            // Ако няма въпроси, показваме съобщение
            JOptionPane.showMessageDialog(this, 
                "Няма налични въпроси в тази категория!", 
                "Грешка", 
                JOptionPane.ERROR_MESSAGE);
            closeDialog();
        }
    }
    
    private int calculateDifficulty() {
        // Трудността зависи от това какво действие се извършва
        if (!territory.isOwned()) {
            return 2; // Лесно за празни територии
        } else if (territory.getOwnerId() == gameController.getCurrentPlayer().getId()) {
            return 3; // Средно за подобрения
        } else {
            return 4; // Трудно за атаки
        }
    }
    
    private void displayQuestion() {
        categoryLabel.setText("Категория: " + currentQuestion.getCategoryName());
        
        // Форматиране на въпроса с word wrap
        String wrappedQuestion = "<html><center>" + 
            currentQuestion.getQuestionText().replace("\n", "<br>") + 
            "</center></html>";
        questionLabel.setText(wrappedQuestion);
        
        // Показване на отговорите
        List<String> options = currentQuestion.getOptions();
        for (int i = 0; i < answerButtons.size() && i < options.size(); i++) {
            answerButtons.get(i).setText((char)('А' + i) + ") " + options.get(i));
            answerButtons.get(i).setEnabled(true);
        }
    }
    
    private void selectAnswer(int index) {
        // Деселектиране на всички бутони
        for (JButton button : answerButtons) {
            button.setBackground(new Color(52, 73, 94));
            button.setBorder(BorderFactory.createLineBorder(new Color(127, 140, 141), 2));
        }
        
        // Селектиране на избрания
        answerButtons.get(index).setBackground(new Color(241, 196, 15));
        answerButtons.get(index).setBorder(BorderFactory.createLineBorder(new Color(241, 196, 15), 3));
        
        selectedAnswer = currentQuestion.getOptions().get(index);
        confirmButton.setEnabled(true);
    }
    
    private void checkAnswer() {
        stopTimer();
        disableButtons();
        
        boolean correct = currentQuestion.checkAnswer(selectedAnswer);
        
        // Показване на резултата
        if (correct) {
            resultLabel.setText("✓ Правилен отговор!");
            resultLabel.setForeground(new Color(46, 204, 113));
            
            // Маркиране на правилния отговор
            for (int i = 0; i < answerButtons.size(); i++) {
                if (currentQuestion.getOptions().get(i).equals(selectedAnswer)) {
                    answerButtons.get(i).setBackground(new Color(46, 204, 113));
                    answerButtons.get(i).setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 3));
                }
            }
            
            // Изпълнение на действието
            performSuccessfulAction();
        } else {
            resultLabel.setText("✗ Грешен отговор!");
            resultLabel.setForeground(new Color(231, 76, 60));
            
            // Показване на правилния отговор
            for (int i = 0; i < answerButtons.size(); i++) {
                String option = currentQuestion.getOptions().get(i);
                if (option.equals(currentQuestion.getCorrectAnswer())) {
                    answerButtons.get(i).setBackground(new Color(46, 204, 113));
                    answerButtons.get(i).setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 3));
                } else if (option.equals(selectedAnswer)) {
                    answerButtons.get(i).setBackground(new Color(231, 76, 60));
                    answerButtons.get(i).setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 3));
                }
            }
        }
        
        // Затваряне на диалога след 3 секунди
        Timer closeTimer = new Timer(3000, e -> closeDialog());
        closeTimer.setRepeats(false);
        closeTimer.start();
    }
    
    private void performSuccessfulAction() {
        if (!territory.isOwned()) {
            // Завладяване на територия
            gameController.captureTerritory(territory);
        } else if (territory.getOwnerId() == gameController.getCurrentPlayer().getId()) {
            // Подобрение на територия
            gameController.upgradeTerritory(territory);
        } else {
            // Атака на територия
            gameController.attackTerritory(territory);
        }
        
        // Обновяване на картата
        if (mapPanel != null) {
            mapPanel.refreshMap();
        }
    }
    
    private void startTimer() {
        timeRemaining = TIME_LIMIT;
        
        countdownTimer = new Timer(1000, e -> {
            timeRemaining--;
            timerLabel.setText(String.valueOf(timeRemaining));
            progressBar.setValue(timeRemaining);
            
            // Промяна на цвета при малко оставащо време
            if (timeRemaining <= 10) {
                timerLabel.setForeground(new Color(231, 76, 60));
                progressBar.setForeground(new Color(231, 76, 60));
            } else if (timeRemaining <= 20) {
                timerLabel.setForeground(new Color(241, 196, 15));
                progressBar.setForeground(new Color(241, 196, 15));
            }
            
            if (timeRemaining <= 0) {
                stopTimer();
                timeOut();
            }
        });
        
        countdownTimer.start();
    }
    
    private void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
    }
    
    private void timeOut() {
        disableButtons();
        resultLabel.setText("⏰ Времето изтече!");
        resultLabel.setForeground(new Color(231, 76, 60));
        
        // Показване на правилния отговор
        for (int i = 0; i < answerButtons.size(); i++) {
            if (currentQuestion.getOptions().get(i).equals(currentQuestion.getCorrectAnswer())) {
                answerButtons.get(i).setBackground(new Color(46, 204, 113));
                answerButtons.get(i).setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 3));
            }
        }
        
        // Затваряне след 3 секунди
        Timer closeTimer = new Timer(3000, e -> closeDialog());
        closeTimer.setRepeats(false);
        closeTimer.start();
    }
    
    private void disableButtons() {
        for (JButton button : answerButtons) {
            button.setEnabled(false);
        }
        confirmButton.setEnabled(false);
    }
    
    private void closeDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }
}