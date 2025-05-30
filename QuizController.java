package controllers;

import models.Question;
import database.QuestionDAO;
import java.util.*;

public class QuizController {
    private Map<String, Integer> categoryScores;
    private int totalQuestionsAnswered;
    private int correctAnswers;
    private List<Question> recentQuestions;
    
    public QuizController() {
        this.categoryScores = new HashMap<>();
        this.totalQuestionsAnswered = 0;
        this.correctAnswers = 0;
        this.recentQuestions = new ArrayList<>();
    }
    
    /**
     * Получаване на случаен въпрос от дадена категория и трудност
     * @param category категория на въпроса
     * @param difficulty трудност (1-5)
     * @return въпрос или null ако няма наличен
     */
    public Question getRandomQuestion(String category, int difficulty) {
        // Получаване на ID на категорията
        int categoryId = QuestionDAO.getCategoryIdByName(category);
        
        if (categoryId == -1) {
            // Ако категорията не съществува, използваме категория по подразбиране
            categoryId = 1; // История
        }
        
        // Получаване на въпрос от базата данни
        Question question = QuestionDAO.getRandomQuestion(categoryId, difficulty);
        
        // Ако няма въпрос с точната трудност, опитваме с близка
        if (question == null) {
            // Опит с по-лесен въпрос
            if (difficulty > 1) {
                question = QuestionDAO.getRandomQuestion(categoryId, difficulty - 1);
            }
            
            // Опит с по-труден въпрос
            if (question == null && difficulty < 5) {
                question = QuestionDAO.getRandomQuestion(categoryId, difficulty + 1);
            }
            
            // Ако все още няма, вземаме случаен от категорията
            if (question == null) {
                List<Question> categoryQuestions = QuestionDAO.getQuestionsByCategory(categoryId, 1);
                if (!categoryQuestions.isEmpty()) {
                    question = categoryQuestions.get(0);
                }
            }
        }
        
        // Проверка дали въпросът не е бил зададен скоро
        if (question != null && !wasRecentlyAsked(question)) {
            addToRecentQuestions(question);
        }
        
        return question;
    }
    
    /**
     * Получаване на серия от въпроси за куиз
     * @param category категория
     * @param count брой въпроси
     * @return списък с въпроси
     */
    public List<Question> getQuizQuestions(String category, int count) {
        int categoryId = QuestionDAO.getCategoryIdByName(category);
        
        if (categoryId == -1) {
            return new ArrayList<>();
        }
        
        List<Question> questions = QuestionDAO.getQuestionsByCategory(categoryId, count);
        
        // Филтриране на скоро зададени въпроси
        questions.removeIf(this::wasRecentlyAsked);
        
        // Добавяне към списъка със скоро зададени
        questions.forEach(this::addToRecentQuestions);
        
        return questions;
    }
    
    /**
     * Записване на отговор на въпрос
     * @param question въпросът
     * @param answer даденият отговор
     * @param timeSpent време за отговор в секунди
     * @return true ако отговорът е правилен
     */
    public boolean submitAnswer(Question question, String answer, int timeSpent) {
        totalQuestionsAnswered++;
        
        boolean isCorrect = question.checkAnswer(answer);
        
        if (isCorrect) {
            correctAnswers++;
            
            // Актуализиране на резултата за категорията
            String category = question.getCategoryName();
            categoryScores.put(category, categoryScores.getOrDefault(category, 0) + 1);
            
            // Бонус точки за бърз отговор
            int bonusPoints = calculateSpeedBonus(timeSpent, question.getDifficulty());
            if (bonusPoints > 0) {
                AuthController.addPoints(bonusPoints);
            }
        }
        
        return isCorrect;
    }
    
    /**
     * Изчисляване на бонус точки за скорост
     */
    private int calculateSpeedBonus(int timeSpent, int difficulty) {
        if (timeSpent <= 5) {
            return difficulty * 3; // Много бърз отговор
        } else if (timeSpent <= 10) {
            return difficulty * 2; // Бърз отговор
        } else if (timeSpent <= 15) {
            return difficulty; // Нормална скорост
        }
        
        return 0; // Без бонус
    }
    
    /**
     * Проверка дали въпрос е бил зададен скоро
     */
    private boolean wasRecentlyAsked(Question question) {
        return recentQuestions.stream()
                .anyMatch(q -> q.getId() == question.getId());
    }
    
    /**
     * Добавяне на въпрос към списъка със скоро зададени
     */
    private void addToRecentQuestions(Question question) {
        recentQuestions.add(question);
        
        // Пазим само последните 20 въпроса
        if (recentQuestions.size() > 20) {
            recentQuestions.remove(0);
        }
    }
    
    /**
     * Получаване на статистики за категория
     */
    public int getCategoryScore(String category) {
        return categoryScores.getOrDefault(category, 0);
    }
    
    /**
     * Получаване на най-силната категория на играча
     */
    public String getStrongestCategory() {
        return categoryScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Няма");
    }
    
    /**
     * Получаване на процент правилни отговори
     */
    public double getAccuracyPercentage() {
        if (totalQuestionsAnswered == 0) {
            return 0.0;
        }
        
        return (double) correctAnswers / totalQuestionsAnswered * 100;
    }
    
    /**
     * Нулиране на статистиките
     */
    public void resetStatistics() {
        categoryScores.clear();
        totalQuestionsAnswered = 0;
        correctAnswers = 0;
        recentQuestions.clear();
    }
    
    /**
     * Зареждане на примерни въпроси в базата данни
     */
    public static void loadSampleQuestions() {
        // История
        addSampleQuestion(1, "Коя година е основана България?", 
                         "681", "879", "1185", "863", 2);
        
        addSampleQuestion(1, "Кой е първият български хан?", 
                         "Аспарух", "Крум", "Кубрат", "Тервел", 1);
        
        addSampleQuestion(1, "В коя битка Цар Симеон I побеждава византийците през 917 г.?", 
                         "Битката при Ахелой", "Битката при Анхиало", 
                         "Битката при Върбишкия проход", "Битката при Клокотница", 3);
        
        // География
        addSampleQuestion(2, "Коя е най-високата планина в България?", 
                         "Рила", "Пирин", "Стара планина", "Родопи", 1);
        
        addSampleQuestion(2, "Колко държави граничат с България?", 
                         "5", "4", "6", "3", 2);
        
        addSampleQuestion(2, "Кой е най-дългият река в България?", 
                         "Дунав", "Марица", "Искър", "Струма", 3);
        
        // Наука
        addSampleQuestion(3, "Кой е химичният символ на златото?", 
                         "Au", "Ag", "Fe", "Cu", 1);
        
        addSampleQuestion(3, "Колко планети има в Слънчевата система?", 
                         "8", "9", "7", "10", 2);
        
        addSampleQuestion(3, "Коя е скоростта на светлината във вакуум?", 
                         "299,792,458 м/с", "300,000,000 м/с", 
                         "299,000,000 м/с", "301,000,000 м/с", 4);
        
        // Изкуство
        addSampleQuestion(4, "Кой е нарисувал 'Мона Лиза'?", 
                         "Леонардо да Винчи", "Микеланджело", 
                         "Рафаело", "Ботичели", 1);
        
        addSampleQuestion(4, "Кой български художник е известен с картината 'Ръченица'?", 
                         "Иван Мърквичка", "Владимир Димитров-Майстора", 
                         "Златю Бояджиев", "Дечко Узунов", 1);
        
        // Спорт
        addSampleQuestion(5, "Колко играчи има в един волейболен отбор на терена?", 
                         "6", "5", "7", "4", 2);
        
        addSampleQuestion(5, "В коя година България спечели световното първенство по волейбол?", 
                         "Никога", "1970", "1986", "1994", 1);
    }
    
    /**
     * Помощен метод за добавяне на примерен въпрос
     */
    private static void addSampleQuestion(int categoryId, String text, 
                                        String correct, String opt2, 
                                        String opt3, String opt4, int difficulty) {
        Question q = new Question();
        q.setCategoryId(categoryId);
        q.setQuestionText(text);
        q.setCorrectAnswer(correct);
        q.setOption2(opt2);
        q.setOption3(opt3);
        q.setOption4(opt4);
        q.setDifficulty(difficulty);
        
        QuestionDAO.addQuestion(q);
    }
    
    // Getters за статистики
    public int getTotalQuestionsAnswered() {
        return totalQuestionsAnswered;
    }
    
    public int getCorrectAnswers() {
        return correctAnswers;
    }
    
    public Map<String, Integer> getCategoryScores() {
        return new HashMap<>(categoryScores);
    }
}