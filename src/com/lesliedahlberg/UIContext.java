package com.lesliedahlberg;

import javax.swing.*;
import java.awt.*;

/**
 * Created by lesliedahlberg on 2017-01-31.
 */
public class UIContext {

    JFrame frame;
    JPanel panel;
    int width;
    int height;
    Point[][] fieldCoordinates;

    public UIContext(){
        width = BoardSettings.fieldWidth * BoardSettings.columns;
        height = BoardSettings.fieldHeight * BoardSettings.rows;
        fieldCoordinates = newCoordinateArray(BoardSettings.columns, BoardSettings.rows, BoardSettings.fieldWidth, BoardSettings.fieldHeight);
        loadFrame();
        showFrame();
    }

    private Point[][] newCoordinateArray(int columns, int rows, int fieldWidth, int fieldHeight){
        Point[][] fieldCoordinates = new Point[columns][rows];
        for(int x = 0; x < columns; x++){
            for(int y = 0; y < rows; y++){
                fieldCoordinates[x][y] = new Point(fieldWidth * x, fieldHeight * y);
            }
        }
        return fieldCoordinates;
    }

    private void loadFrame(){
        frame = new JFrame(BoardSettings.gameTitle);
        panel = new JPanel();

        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(BoardSettings.background1);
        panel.setFocusable(true);

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();

    }
    private void showFrame(){
        frame.setVisible(true);
    }
}
