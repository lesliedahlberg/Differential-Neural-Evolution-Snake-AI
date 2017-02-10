package com.lesliedahlberg;

import java.awt.*;

/**
 * Created by lesliedahlberg on 11/06/14.
 */
public class BoardSettings {

    static String gameTitle = "Square";

    static Color color1 = new Color(133, 153, 0);
    static Color color2 = new Color(220, 50, 47);
    static Color color3 = new Color(181, 137, 0);
    static Color background1 = new Color(0, 43, 54);
    static Color textColor = Color.WHITE;

    static int defaultSquareX = 0;
    static int defaultSquareY = 0;

    static int columns = 5;
    static int rows = 5;

    static int fieldWidth = 50;
    static int fieldHeight = 50;

    static int defaultHeadIndexX = columns/2;
    static int defaultHeadIndexY = rows/2;

    static int numberOfTailsSquares = 2; //(columns + rows)/10;
    static int maxPossibleAiTailSquares = (columns - 1) * 2 + (rows - 1) * 2 - 2;
    static int speed = 10;
    static int blockBonus = (columns + rows) * 6;
    static int safeAmount = 10;
    public static boolean Rendering = true;


    public BoardSettings() {
    }
}
