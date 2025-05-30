package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class IconGenerator {
    private static Map<String, ImageIcon> iconCache = new HashMap<>();
    
    public static ImageIcon getIcon(String iconName, int size) {
        String key = iconName + "_" + size;
        
        if (iconCache.containsKey(key)) {
            return iconCache.get(key);
        }
        
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Антиалиасинг за по-гладки изображения
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Генериране на икона според името
        switch (iconName) {
            case "history":
                drawHistoryIcon(g2d, size);
                break;
            case "geography":
                drawGeographyIcon(g2d, size);
                break;
            case "science":
                drawScienceIcon(g2d, size);
                break;
            case "art":
                drawArtIcon(g2d, size);
                break;
            case "sport":
                drawSportIcon(g2d, size);
                break;
            case "first_victory":
                drawVictoryIcon(g2d, size);
                break;
            case "conqueror":
                drawConquerorIcon(g2d, size);
                break;
            case "scholar":
                drawScholarIcon(g2d, size);
                break;
            case "quick_mind":
                drawQuickMindIcon(g2d, size);
                break;
            case "castle":
                drawCastleIcon(g2d, size);
                break;
            case "library":
                drawLibraryIcon(g2d, size);
                break;
            case "barracks":
                drawBarracksIcon(g2d, size);
                break;
            case "market":
                drawMarketIcon(g2d, size);
                break;
            default:
                drawDefaultIcon(g2d, size);
        }
        
        g2d.dispose();
        
        ImageIcon icon = new ImageIcon(image);
        iconCache.put(key, icon);
        
        return icon;
    }
    
    // История - книга/свитък
    private static void drawHistoryIcon(Graphics2D g2d, int size) {
        g2d.setColor(new Color(139, 69, 19)); // Кафяво
        g2d.fillRoundRect(size/6, size/6, size*2/3, size*2/3, size/8, size/8);
        
        g2d.setColor(new Color(255, 248, 220)); // Бежово
        g2d.fillRoundRect(size/5, size/4, size*3/5, size/2, size/10, size/10);
        
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(size/20f));
        // Линии за текст
        for (int i = 0; i < 3; i++) {
            int y = size/3 + i * size/8;
            g2d.drawLine(size/4, y, size*3/4, y);
        }
    }
    
    // География - глобус
    private static void drawGeographyIcon(Graphics2D g2d, int size) {
        // Основа на глобуса
        g2d.setColor(new Color(52, 152, 219)); // Синьо за океани
        g2d.fillOval(size/6, size/6, size*2/3, size*2/3);
        
        // Континенти
        g2d.setColor(new Color(46, 204, 113)); // Зелено за земя
        g2d.fillArc(size/4, size/4, size/3, size/3, 45, 90);
        g2d.fillArc(size/2, size/3, size/4, size/4, -30, 120);
        
        // Меридиани
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(size/30f));
        g2d.drawOval(size/6, size/6, size*2/3, size*2/3);
        g2d.drawLine(size/2, size/6, size/2, size*5/6);
        g2d.drawArc(size/3, size/6, size/3, size*2/3, 0, 180);
    }
    
    // Наука - атом
    private static void drawScienceIcon(Graphics2D g2d, int size) {
        int centerX = size / 2;
        int centerY = size / 2;
        
        // Орбити
        g2d.setColor(new Color(155, 89, 182)); // Лилаво
        g2d.setStroke(new BasicStroke(size/20f));
        
        // Три елиптични орбити
        g2d.drawOval(size/6, size/3, size*2/3, size/3);
        
        Graphics2D g2d2 = (Graphics2D) g2d.create();
        g2d2.rotate(Math.toRadians(60), centerX, centerY);
        g2d2.drawOval(size/6, size/3, size*2/3, size/3);
        g2d2.dispose();
        
        g2d2 = (Graphics2D) g2d.create();
        g2d2.rotate(Math.toRadians(-60), centerX, centerY);
        g2d2.drawOval(size/6, size/3, size*2/3, size/3);
        g2d2.dispose();
        
        // Ядро
        g2d.setColor(new Color(231, 76, 60)); // Червено
        g2d.fillOval(centerX - size/10, centerY - size/10, size/5, size/5);
        
        // Електрони
        g2d.setColor(new Color(52, 152, 219)); // Синьо
        g2d.fillOval(size/6 - size/20, centerY - size/20, size/10, size/10);
        g2d.fillOval(size*5/6 - size/20, centerY - size/20, size/10, size/10);
        g2d.fillOval(centerX - size/20, size/6 - size/20, size/10, size/10);
    }
    
    // Изкуство - палитра
    private static void drawArtIcon(Graphics2D g2d, int size) {
        // Палитра
        g2d.setColor(new Color(139, 69, 19)); // Кафяво за дърво
        g2d.fillOval(size/5, size/4, size*3/5, size/2);
        
        // Дупка за палец
        g2d.setColor(Color.WHITE);
        g2d.fillOval(size*3/5, size/3, size/6, size/6);
        
        // Цветни петна
        g2d.setColor(new Color(231, 76, 60)); // Червено
        g2d.fillOval(size/3, size/3, size/8, size/8);
        
        g2d.setColor(new Color(52, 152, 219)); // Синьо
        g2d.fillOval(size/2, size*2/5, size/8, size/8);
        
        g2d.setColor(new Color(241, 196, 15)); // Жълто
        g2d.fillOval(size*2/5, size/2, size/8, size/8);
        
        g2d.setColor(new Color(46, 204, 113)); // Зелено
        g2d.fillOval(size/4, size/2, size/8, size/8);
    }
    
    // Спорт - топка
    private static void drawSportIcon(Graphics2D g2d, int size) {
        // Футболна топка
        g2d.setColor(Color.WHITE);
        g2d.fillOval(size/5, size/5, size*3/5, size*3/5);
        
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(size/20f));
        g2d.drawOval(size/5, size/5, size*3/5, size*3/5);
        
        // Шестоъгълници
        int centerX = size / 2;
        int centerY = size / 2;
        int hexSize = size / 8;
        
        // Централен шестоъгълник
        Polygon hex = createHexagon(centerX, centerY, hexSize);
        g2d.fillPolygon(hex);
        
        // Околни шестоъгълници
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(72 * i);
            int x = centerX + (int)(hexSize * 2 * Math.cos(angle));
            int y = centerY + (int)(hexSize * 2 * Math.sin(angle));
            hex = createHexagon(x, y, hexSize/2);
            g2d.fillPolygon(hex);
        }
    }
    
    // Победа - трофей
    private static void drawVictoryIcon(Graphics2D g2d, int size) {
        g2d.setColor(new Color(255, 215, 0)); // Злато
        
        // Чаша
        g2d.fillArc(size/4, size/3, size/2, size/3, 0, 180);
        g2d.fillRect(size*3/8, size/2, size/4, size/6);
        
        // Дръжки
        g2d.setStroke(new BasicStroke(size/15f));
        g2d.drawArc(size/6, size*2/5, size/6, size/5, 90, 180);
        g2d.drawArc(size*2/3, size*2/5, size/6, size/5, 270, 180);
        
        // Основа
        g2d.fillRect(size/3, size*2/3, size/3, size/12);
        g2d.fillRect(size/4, size*3/4, size/2, size/8);
        
        // Звезда
        g2d.setColor(Color.WHITE);
        drawStar(g2d, size/2, size*2/5, size/10, size/20);
    }
    
    // Завоевател - корона
    private static void drawConquerorIcon(Graphics2D g2d, int size) {
        g2d.setColor(new Color(255, 215, 0)); // Злато
        
        // Основа на короната
        g2d.fillRect(size/5, size/2, size*3/5, size/4);
        
        // Зъбци
        int[] xPoints = {size/5, size/3, size*2/5, size/2, size*3/5, size*2/3, size*4/5, size*4/5, size/5};
        int[] yPoints = {size/2, size/3, size/2, size/4, size/2, size/3, size/2, size*3/4, size*3/4};
        g2d.fillPolygon(xPoints, yPoints, 9);
        
        // Скъпоценни камъни
        g2d.setColor(new Color(231, 76, 60)); // Червен
        g2d.fillOval(size/2 - size/16, size*5/8, size/8, size/8);
        
        g2d.setColor(new Color(52, 152, 219)); // Син
        g2d.fillOval(size/3 - size/16, size*5/8, size/8, size/8);
        
        g2d.setColor(new Color(46, 204, 113)); // Зелен
        g2d.fillOval(size*2/3 - size/16, size*5/8, size/8, size/8);
    }
    
    // Учен - шапка за дипломиране
    private static void drawScholarIcon(Graphics2D g2d, int size) {
        // Шапка
        g2d.setColor(Color.BLACK);
        g2d.fillRect(size/4, size/2, size/2, size/6);
        
        // Горна част (квадрат)
        int[] xPoints = {size/5, size/2, size*4/5, size/2};
        int[] yPoints = {size/2, size/3, size/2, size*2/3};
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        // Пискюл
        g2d.setColor(new Color(255, 215, 0)); // Злато
        g2d.setStroke(new BasicStroke(size/20f));
        g2d.drawLine(size/2, size/2, size*3/5, size/4);
        g2d.fillOval(size*3/5 - size/12, size/4 - size/12, size/6, size/6);
    }
    
    // Бърз ум - светкавица
    private static void drawQuickMindIcon(Graphics2D g2d, int size) {
        g2d.setColor(new Color(241, 196, 15)); // Жълто
        
        int[] xPoints = {size*3/5, size*2/5, size*2/5, size/3, size*2/3, size*3/5, size*3/5};
        int[] yPoints = {size/6, size/2, size*3/5, size*5/6, size/2, size*2/5, size/6};
        
        g2d.fillPolygon(xPoints, yPoints, 7);
        
        // Контур
        g2d.setColor(new Color(243, 156, 18)); // По-тъмно жълто
        g2d.setStroke(new BasicStroke(size/30f));
        g2d.drawPolygon(xPoints, yPoints, 7);
    }
    
    // Замък
    private static void drawCastleIcon(Graphics2D g2d, int size) {
        g2d.setColor(new Color(127, 140, 141)); // Сиво
        
        // Основа
        g2d.fillRect(size/4, size/2, size/2, size/3);
        
        // Кули
        g2d.fillRect(size/5, size/3, size/6, size/2);
        g2d.fillRect(size*2/3, size/3, size/6, size/2);
        
        // Зъбци
        for (int i = 0; i < 3; i++) {
            g2d.fillRect(size/5 + i*size/12, size/4, size/16, size/12);
            g2d.fillRect(size*2/3 + i*size/12, size/4, size/16, size/12);
        }
        
        // Врата
        g2d.setColor(new Color(139, 69, 19)); // Кафяво
        g2d.fillArc(size*2/5, size*2/3, size/5, size/4, 0, 180);
        g2d.fillRect(size*2/5, size*3/4, size/5, size/12);
    }
    
    // Библиотека
    private static void drawLibraryIcon(Graphics2D g2d, int size) {
        // Рафт
        g2d.setColor(new Color(139, 69, 19)); // Кафяво
        g2d.fillRect(size/6, size/4, size*2/3, size/16);
        g2d.fillRect(size/6, size*3/4, size*2/3, size/16);
        
        // Книги
        Color[] bookColors = {
            new Color(231, 76, 60),   // Червено
            new Color(52, 152, 219),  // Синьо
            new Color(46, 204, 113),  // Зелено
            new Color(241, 196, 15),  // Жълто
            new Color(155, 89, 182)   // Лилаво
        };
        
        for (int i = 0; i < 5; i++) {
            g2d.setColor(bookColors[i]);
            g2d.fillRect(size/5 + i*size/8, size/3, size/10, size*2/5);
        }
    }
    
    // Казарма
    private static void drawBarracksIcon(Graphics2D g2d, int size) {
        // Сграда
        g2d.setColor(new Color(44, 62, 80)); // Тъмно синьо
        g2d.fillRect(size/4, size/3, size/2, size/2);
        
        // Покрив
        int[] xPoints = {size/5, size/2, size*4/5};
        int[] yPoints = {size/3, size/5, size/3};
        g2d.setColor(new Color(192, 57, 43)); // Тъмно червено
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Знаме
        g2d.setColor(new Color(231, 76, 60)); // Червено
        g2d.fillRect(size/2, size/5, size/4, size/6);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(size/30f));
        g2d.drawLine(size/2, size/5, size/2, size/2);
    }
    
    // Пазар
    private static void drawMarketIcon(Graphics2D g2d, int size) {
        // Тезгях
        g2d.setColor(new Color(139, 69, 19)); // Кафяво
        g2d.fillRect(size/5, size/2, size*3/5, size/3);
        
        // Тента
        g2d.setColor(new Color(231, 76, 60)); // Червено
        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0) {
                g2d.setColor(new Color(231, 76, 60)); // Червено
            } else {
                g2d.setColor(Color.WHITE);
            }
            g2d.fillArc(size/5 + i*size*3/20, size/3, size*3/20, size/4, 0, 180);
        }
        
        // Стоки
        g2d.setColor(new Color(241, 196, 15)); // Жълто
        g2d.fillOval(size/3, size*3/5, size/10, size/10);
        g2d.setColor(new Color(46, 204, 113)); // Зелено
        g2d.fillOval(size/2, size*3/5, size/10, size/10);
    }
    
    // По подразбиране
    private static void drawDefaultIcon(Graphics2D g2d, int size) {
        g2d.setColor(new Color(149, 165, 166));
        g2d.fillOval(size/4, size/4, size/2, size/2);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, size/3));
        g2d.drawString("?", size*2/5, size*5/8);
    }
    
    // Помощни методи
    private static Polygon createHexagon(int centerX, int centerY, int radius) {
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            int angle = 60 * i;
            int x = centerX + (int) (radius * Math.cos(Math.toRadians(angle)));
            int y = centerY + (int) (radius * Math.sin(Math.toRadians(angle)));
            hexagon.addPoint(x, y);
        }
        return hexagon;
    }
    
    private static void drawStar(Graphics2D g2d, int centerX, int centerY, int outerRadius, int innerRadius) {
        int numPoints = 10;
        int[] xPoints = new int[numPoints];
        int[] yPoints = new int[numPoints];
        
        for (int i = 0; i < numPoints; i++) {
            double angle = Math.toRadians(-90 + i * 36);
            int radius = (i % 2 == 0) ? outerRadius : innerRadius;
            xPoints[i] = centerX + (int) (radius * Math.cos(angle));
            yPoints[i] = centerY + (int) (radius * Math.sin(angle));
        }
        
        g2d.fillPolygon(xPoints, yPoints, numPoints);
    }
}