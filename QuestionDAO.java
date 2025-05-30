package database;

import models.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class QuestionDAO {
    
    public static Question getRandomQuestion(int categoryId, int difficulty) {
        String sql = "SELECT q.*, c.name as category_name FROM questions q " +
                    "JOIN categories c ON q.category_id = c.id " +
                    "WHERE q.category_id = ? AND q.difficulty = ? " +
                    "ORDER BY RAND() LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            stmt.setInt(2, difficulty);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractQuestionFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static List<Question> getQuestionsByCategory(int categoryId, int limit) {
        String sql = "SELECT q.*, c.name as category_name FROM questions q " +
                    "JOIN categories c ON q.category_id = c.id " +
                    "WHERE q.category_id = ? " +
                    "ORDER BY RAND() LIMIT ?";
        
        List<Question> questions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            stmt.setInt(2, limit);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                questions.add(extractQuestionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }
    
    public static boolean addQuestion(Question question) {
        String sql = "INSERT INTO questions (category_id, question_text, " +
                    "correct_answer, option_2, option_3, option_4, difficulty) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, question.getCategoryId());
            stmt.setString(2, question.getQuestionText());
            stmt.setString(3, question.getCorrectAnswer());
            stmt.setString(4, question.getOption2());
            stmt.setString(5, question.getOption3());
            stmt.setString(6, question.getOption4());
            stmt.setInt(7, question.getDifficulty());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<String> getAllCategories() {
        String sql = "SELECT name FROM categories ORDER BY name";
        List<String> categories = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return categories;
    }
    
    public static int getCategoryIdByName(String categoryName) {
        String sql = "SELECT id FROM categories WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    private static Question extractQuestionFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("id"));
        question.setCategoryId(rs.getInt("category_id"));
        question.setQuestionText(rs.getString("question_text"));
        question.setCorrectAnswer(rs.getString("correct_answer"));
        question.setDifficulty(rs.getInt("difficulty"));
        
        // Създаваме списък с всички отговори и ги разбъркваме
        List<String> options = new ArrayList<>();
        options.add(rs.getString("correct_answer"));
        options.add(rs.getString("option_2"));
        options.add(rs.getString("option_3"));
        options.add(rs.getString("option_4"));
        Collections.shuffle(options);
        
        question.setOptions(options);
        
        // Ако има име на категорията в резултата
        try {
            question.setCategoryName(rs.getString("category_name"));
        } catch (SQLException e) {
            // Колоната може да липсва в някои заявки
        }
        
        return question;
    }
}