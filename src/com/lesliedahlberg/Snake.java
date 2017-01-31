package com.lesliedahlberg;


import sun.plugin2.message.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by lesliedahlberg on 22/07/14.
 */
public class Snake {
    UIContext uiContext;
    Square head;
    Block block;
    Square[] tail;
    Point[] tailSquaresLocations;
    boolean removedHoveredBlock;
    String movingInDirection;
    boolean snakeMoving;
    boolean firstMoveInitiated;
    boolean aiIsPlaying;
    int blocksEaten;
    boolean gameHasStarted;
    ScheduledExecutorService executor;

    public Snake(UIContext uiContext, Block block){
        this.uiContext = uiContext;
        this.block = block;
    }

    public void initSnake(){
        if(executor != null) {
            executor.shutdown();
        }
        if(tail != null){
            for (Component comp : tail) {
                if(comp != null)
                    uiContext.panel.remove(comp);
            }
        }
        if(head != null){
            uiContext.panel.remove(head);
        }
        executor = null;
        head = null;
        block.initBlock();
        tail = null;
        tailSquaresLocations = null;
        removedHoveredBlock = false;
        movingInDirection = "left";
        snakeMoving = true;
        firstMoveInitiated = false;
        aiIsPlaying = false;
        blocksEaten = 0;
        gameHasStarted = false;
        loadHead();
        loadTail();
        listen();
    }

    private void loadHead() {
        head = new Square(uiContext.fieldCoordinates[BoardSettings.defaultHeadIndexX][BoardSettings.defaultHeadIndexY], new Dimension(BoardSettings.fieldWidth, BoardSettings.fieldHeight), BoardSettings.color1);
        uiContext.panel.add(head);
    }

    //AUTO-MOVE SNAKE
    private void autoMove(){
        //block.stopBlockClock();
        gameHasStarted = true;
        Runnable autoMoveSnakeRunnable = new Runnable() {
            @Override
            public void run() {
                if(movingInDirection != null && snakeMoving){
                    /*if(firstMoveInitiated == false){
                        firstMoveInitiated = true;
                        //game.addNewBlocks();
                    }*/
                    if(aiIsPlaying){
                        movingInDirection = getAiDirection();
                    }

                    moveSnake();
                }
            }
        };

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(autoMoveSnakeRunnable, 0, BoardSettings.speed, TimeUnit.MILLISECONDS);
    }

    private String getAiDirection() {
        return "left";
    }

    //MOVE SNAKE
    public void moveSnake(){
        Point newLocationOfHead = null;
        if(movingInDirection == "right") newLocationOfHead = getRightFieldCoords(head);
        if(movingInDirection == "left") newLocationOfHead = getLeftFieldCoords(head);
        if(movingInDirection == "down") newLocationOfHead = getBottomFieldCoords(head);
        if(movingInDirection == "up") newLocationOfHead = getTopFieldCoords(head);
        if(newLocationOfHead != null){
            head.setPosition(newLocationOfHead);
            if(doesHeadTouchTale()){
                snakeMoving = false;
                JOptionPane.showMessageDialog(null, blocksEaten * 100 + " POINTS");
                block.stopBlockClock();
            }
            eatBlocks();
            moveTailWithHead(newLocationOfHead);
        }
        if(newLocationOfHead == null && snakeMoving){
            snakeMoving = false;
            JOptionPane.showMessageDialog(null, blocksEaten * 100 + " POINTS");
            block.stopBlockClock();
        }
    }

    //BLOCK EATER
    private void eatBlocks(){
        removedHoveredBlock = false;
        for(int i = 0; i < block.getBlockIndex(); i++){
            if(block.getBlocks()[i] != null) {
                if((block.getBlocks()[i].getPosition().getX() == head.getPosition().getX())
                        && (block.getBlocks()[i].getPosition().getY() == head.getPosition().getY())) {
                    block.deleteBlock(i);
                    removedHoveredBlock = true;
                    uiContext.panel.repaint();
                    addNewTailSquare();
                    blocksEaten++;
                }
            }
        }
    }

    //BLOCK CHECKER
    private boolean isBlock(Point point){
        for(int i = 0; i < block.getBlockIndex(); i++){
            if(block.getBlocks()[i] != null) {
                if((block.getBlocks()[i].getPosition().getX() == point.getX())
                        && (block.getBlocks()[i].getPosition().getY() == point.getY())) {
                    return true;
                }
            }
        }
        return false;
    }

    //SCANNER
    private boolean doesFieldExist(int x, int y){
        if(x >= 0 && y >= 0 && x < BoardSettings.columns && y < BoardSettings.rows){
            return true;
        }
        return false;
    }

    private int getSquareXIndex(Square s){
        Point point = s.getPosition();
        return getFieldIndexX(point);
    }

    private int getSquareYIndex(Square s){
        Point point = s.getPosition();
        return getFieldIndexY(point);
    }

    public Point getTopFieldCoords(Square s){
        int fieldIndexX = getSquareXIndex(s);
        int fieldIndexY = getSquareYIndex(s);
        if(doesFieldExist(fieldIndexX, fieldIndexY - 1)){
            return uiContext.fieldCoordinates[fieldIndexX][fieldIndexY - 1];
        }
        return null;
    }

    public Point getBottomFieldCoords(Square s){

        int fieldIndexX = getSquareXIndex(s);
        int fieldIndexY = getSquareYIndex(s);

        if(doesFieldExist(fieldIndexX, fieldIndexY + 1)){

            return uiContext.fieldCoordinates[fieldIndexX][fieldIndexY + 1];
        }
        return null;
    }

    public Point getLeftFieldCoords(Square s){
        int fieldIndexX = getSquareXIndex(s);
        int fieldIndexY = getSquareYIndex(s);

        if(doesFieldExist(fieldIndexX - 1, fieldIndexY)){
            return uiContext.fieldCoordinates[fieldIndexX - 1][fieldIndexY];
        }
        return null;
    }

    public Point getRightFieldCoords(Square s){
        int fieldIndexX = getSquareXIndex(s);
        int fieldIndexY = getSquareYIndex(s);

        if(doesFieldExist(fieldIndexX + 1, fieldIndexY)){
            return uiContext.fieldCoordinates[fieldIndexX + 1][fieldIndexY];
        }
        return null;
    }

    //CHECKER
    private boolean doesHeadTouchTale(){
        for(int i = 0; i < tail.length; i++){
            if(tail[i] != null){
                if((tail[i].getPosition().getX() == head.getPosition().getX())
                        && (tail[i].getPosition().getY() == head.getPosition().getY())){

                    //game.terminate();
                    return true;

                }
            }
        }
        return false;
    }

    private int getFieldIndexX(Point point){
        for(int x = 0; x < BoardSettings.columns; x++){
            int y = 0;
            if(point.getX() == uiContext.fieldCoordinates[x][y].getX()){
                return x;
            }
        }
        return -1;
    }

    private int getFieldIndexY(Point point){
        for(int y = 0; y < BoardSettings.rows; y++){
            int x = 0;
            if(point.getY() == uiContext.fieldCoordinates[x][y].getY()){
                return y;
            }
        }
        return -1;
    }

    public boolean isNewFieldInsideGrid(Point newLocationOfHead){
        int indexX = getFieldIndexX(newLocationOfHead);
        int indexY = getFieldIndexY(newLocationOfHead);

        if(indexX == -1 || indexY == -1){
            return false;
        }

        return true;
    }

    //LISTEN
    public void listen(){

        uiContext.panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if(!aiIsPlaying) {
                    switch (keyEvent.getKeyCode()) {
                        case KeyEvent.VK_RIGHT:
                            movingInDirection = "right";

                            break;
                        case KeyEvent.VK_LEFT:
                            movingInDirection = "left";

                            break;
                        case KeyEvent.VK_DOWN:
                            movingInDirection = "down";

                            break;
                        case KeyEvent.VK_UP:
                            movingInDirection = "up";

                            break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });
        uiContext.panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_1){

                    if(!gameHasStarted){
                        aiIsPlaying = true;
                        autoMove();
                    }

                }
                if(keyEvent.getKeyCode() == KeyEvent.VK_2){

                    if(!gameHasStarted){
                        aiIsPlaying = false;
                        autoMove();
                    }

                }
                if(keyEvent.getKeyCode() == KeyEvent.VK_3){

                    initSnake();

                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

    }


    //TAIL
    private void loadTail() {
        initializeTailAndLocations();
    }

    private int getMaxTailSquares(){
        return (BoardSettings.columns * BoardSettings.rows) - 1;
    }

    private void initializeTailAndLocations(){
        int maxTails = getMaxTailSquares();
        tail = new Square[maxTails];
        tailSquaresLocations = new Point[maxTails];
    }

    public void addNewTailSquare(){
        BoardSettings.numberOfTailsSquares++;
    }

    public void moveTailWithHead(Point newTailSquareLocation){


        for(int i = 0; i < BoardSettings.numberOfTailsSquares-1; i++){



            if(tail[i] == null){
                tail[i] = new Square(newTailSquareLocation, new Dimension(BoardSettings.fieldWidth, BoardSettings.fieldHeight), BoardSettings.color3);


                uiContext.panel.add(tail[i]);
            }
            if(tail[i+1] == null){
                tail[i+1] = new Square(newTailSquareLocation, new Dimension(BoardSettings.fieldWidth, BoardSettings.fieldHeight), BoardSettings.color3);
                uiContext.panel.add(tail[i+1]);
            }

            tail[i].setPosition(tail[i + 1].getPosition());



        }
        if(tail[BoardSettings.numberOfTailsSquares-1] == null){

            tail[BoardSettings.numberOfTailsSquares-1] = new Square(newTailSquareLocation, new Dimension(BoardSettings.fieldWidth, BoardSettings.fieldHeight), BoardSettings.color3);

            uiContext.panel.add(tail[BoardSettings.numberOfTailsSquares-1]);

        } else {
            tail[BoardSettings.numberOfTailsSquares-1].setPosition(newTailSquareLocation);

        }

    }


}
