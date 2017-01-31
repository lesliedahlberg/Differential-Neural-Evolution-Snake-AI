package com.lesliedahlberg;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by lesliedahlberg on 22/07/14.
 */
public class Block {
    UIContext uiContext;
    Square[] block;
    int blockIndex;
    ScheduledExecutorService executor;

    public Block(UIContext uiContext) {
        this.uiContext = uiContext;
    }

    public void initBlock() {
        if(block != null){
            for (Component comp : block) {
                if(comp != null)
                    uiContext.panel.remove(comp);
            }
        }
        block = null;
        blockIndex = 0;
        if(executor != null){
            executor.shutdown();
        }
        executor = null;
        renderBlocks();
    }

    public void startBlockAddingClock(){
        Runnable newBlocks = new Runnable() {
            @Override
            public void run() {
                newRandomBlock();
            }
        };

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(newBlocks, 0, 200, TimeUnit.MILLISECONDS);
    }

    public void stopBlockClock(){
        executor.shutdown();
    }

    private Point newRandomPoint(){
        int columns = BoardSettings.columns;
        int rows = BoardSettings.rows;

        Random random = new Random();
        int x = random.nextInt(columns);
        int y = random.nextInt(rows);

        Point point = uiContext.fieldCoordinates[x][y];

        return point;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public void newRandomBlock(){

        block[blockIndex] = new Square(newRandomPoint(), new Dimension(BoardSettings.fieldWidth, BoardSettings.fieldHeight), BoardSettings.color2);
        uiContext.panel.add(block[blockIndex]);
        uiContext.panel.repaint();
        blockIndex++;
    }


    private void renderBlocks(){
        block = new Square[BoardSettings.columns * BoardSettings.rows];
        newRandomBlock();
    }


    public Square[] getBlocks(){
        return block;
    }

    public void deleteBlock(int blockID){
        block[blockID].setVisible(false);
        block[blockID] = null;
        newRandomBlock();
    }


}
