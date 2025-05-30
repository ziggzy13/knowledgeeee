package models;

import java.util.List;
import java.util.ArrayList;

public class Question {
    private int id;
    private int categoryId;
    private String categoryName;
    private String questionText;
    private String correctAnswer;
    private String option2;
    private String option3;
    private String option4;
    private List<String> options;
    private int difficulty;
    
    // Конструктори
    public Question() {
        this.options = new ArrayList<>();
    }
    
    public Question(String questionText, String correctAnswer, 
                   String option2, String option3, String option4) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.options = new ArrayList<>();
        setupOptions();
    }
    
    // Методи за работа с опциите
    private void setupOptions() {
        options.clear();
        options.add(correctAnswer);
        options.add(option2);
        options.add(option3);
        options.add(option4);
    }
    
    public boolean checkAnswer(String answer) {
        return correctAnswer.equals(answer);
    }
    
    // Getters и Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    
    public String getOption2() {
        return option2;
    }
    
    public void setOption2(String option2) {
        this.option2 = option2;
    }
    
    public String getOption3() {
        return option3;
    }
    
    public void setOption3(String option3) {
        this.option3 = option3;
    }
    
    public String getOption4() {
        return option4;
    }
    
    public void setOption4(String option4) {
        this.option4 = option4;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public void setOptions(List<String> options) {
        this.options = options;
    }
    
    public int getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    
    public String getDifficultyText() {
        switch (difficulty) {
            case 1: return "Много лесно";
            case 2: return "Лесно";
            case 3: return "Средно";
            case 4: return "Трудно";
            case 5: return "Много трудно";
            default: return "Неизвестно";
        }
    }
}