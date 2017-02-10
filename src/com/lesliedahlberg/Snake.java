package com.lesliedahlberg;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Time;
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
    int moveCount;
    boolean gameHasStarted;
    ScheduledExecutorService executor;

    int life;
    int blockLife;

    FFNN controller;
    public int input_neurons;
    public int hidden_neurons;
    public int output_neurons;

    int scan_width;
    int scan_height;

    Random random;

    String[] moves = new String[]{"left", "up", "right", "down"};
    public boolean swarming;

    public Snake(UIContext uiContext, Block block){
        this.uiContext = uiContext;
        this.block = block;

        scan_height = BoardSettings.columns;
        scan_width = BoardSettings.columns;

        //input_neurons = BoardSettings.rows * BoardSettings.columns;
        input_neurons = 9;

        output_neurons = 2;
        hidden_neurons = 3;

        blockLife = BoardSettings.columns*BoardSettings.rows;


        //System.out.println("IN: "+input_neurons+";HI: "+hidden_neurons+"OUT: "+output_neurons);

        controller = new FFNN(input_neurons, hidden_neurons, output_neurons);
        swarming = true;

        random = new Random();
    }

    public void clear(){
        uiContext.panel.updateUI();
    }

    public void clearSnake(){
        uiContext.panel.updateUI();
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







        aiIsPlaying = true;

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


        life = blockLife;

        moveCount = 0;
        gameHasStarted = false;
    }

    public void initSnake(){
        if(BoardSettings.Rendering) {
            uiContext.panel.updateUI();
        }
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




        BoardSettings.numberOfTailsSquares = 2;


        aiIsPlaying = true;

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
        moveCount = 0;

        life = blockLife;

        gameHasStarted = false;
        loadHead();
        loadTail();
        //listen();
    }

    private void loadHead() {
        head = new Square(uiContext.fieldCoordinates[BoardSettings.defaultHeadIndexX][BoardSettings.defaultHeadIndexY], new Dimension(BoardSettings.fieldWidth, BoardSettings.fieldHeight), BoardSettings.color1);
        uiContext.panel.add(head);
    }

    public float[] snakeState(){

        int width = 3;
        int height = width;

        float[][] map = new float[3][3];
        for(int x = 0; x < 3; x++){
            for(int y = 0; y < 3; y++) {
                map[x][y] = 0;
            }
        }

        //Get head position
        int head_x=0, head_y=0;
        for(int x = 0; x < BoardSettings.rows; x++){
            for(int y = 0; y < BoardSettings.columns; y++) {
                Point currentPoint = uiContext.fieldCoordinates[x][y];
                if(currentPoint.equals(head.getPosition())){
                    head_x = x;
                    head_y = y;
                    break;
                }
            }
        }

        int tail_value = -3;
        int head_value = -1;
        int block_value = 1;

        map[1][1] = head_value;

        //Check neighbours
        Point currentPoint;
        int x = head_x-1, y = head_y;

        if(x >= 0 && x < BoardSettings.columns && y >= 0 && y < BoardSettings.rows){
            currentPoint = uiContext.fieldCoordinates[x][y];
            for(Square s : tail){
                if(s != null){
                    if(s.getPosition().equals(currentPoint)){
                        map[0][1] = tail_value;
                    }
                }
            }
        }else{
            map[0][1] = tail_value+2;
        }

        x = head_x+1;
        y = head_y;
        if(x >= 0 && x < BoardSettings.columns && y >= 0 && y < BoardSettings.rows){
            currentPoint = uiContext.fieldCoordinates[x][y];
            for(Square s : tail){
                if(s != null){
                    if(s.getPosition().equals(currentPoint)){
                        map[2][1] = tail_value;
                    }
                }
            }
        }else{
            map[2][1] = tail_value+2;
        }


        x = head_x;
        y = head_y-1;
        if(x >= 0 && x < BoardSettings.columns && y >= 0 && y < BoardSettings.rows){
            currentPoint = uiContext.fieldCoordinates[x][y];
            for(Square s : tail){
                if(s != null){
                    if(s.getPosition().equals(currentPoint)){
                        map[1][0] = tail_value;
                    }
                }
            }
        }else{
            map[1][0] = tail_value+2;
        }

        x = head_x;
        y = head_y+1;
        if(x >= 0 && x < BoardSettings.columns && y >= 0 && y < BoardSettings.rows){
            currentPoint = uiContext.fieldCoordinates[x][y];
            for(Square s : tail){
                if(s != null){
                    if(s.getPosition().equals(currentPoint)){
                        map[1][2] = tail_value;
                    }
                }
            }
        }else{
            map[1][2] = tail_value+2;
        }






        //Get blocks
        for(x = 0; x < BoardSettings.columns; x++){
            for(y = 0; y < BoardSettings.rows; y++){
                currentPoint = uiContext.fieldCoordinates[x][y];
                for(Square s : block.getBlocks()){
                    if(s != null) {
                        if(s.getPosition().equals(currentPoint)){
                            if(x < head_x){
                                if(y < head_y){
                                    map[0][0] = block_value;
                                }else{
                                    map[0][2] = block_value;
                                }
                            }else{
                                if(y < head_y){
                                    map[2][0] = block_value;
                                }else{
                                    map[2][2] = block_value;
                                }
                            }
                        }
                    }
                }
            }
        }


        float[][] mapRel = new float[width][height];
        if(movingInDirection.equals("left")){
            for (x = 0; x < width; x++) {
                for (y = 0; y < height; y++) {
                    mapRel[x][y] = map[height-1-y][x];
                }
            }
        }else if(movingInDirection.equals("right")){
            for (x = 0; x < width; x++) {
                for (y = 0; y < height; y++) {
                    mapRel[x][y] = map[y][width-1-x];
                }
            }
        }else if(movingInDirection.equals("down")){
            for (x = 0; x < width; x++) {
                for (y = 0; y < height; y++) {
                    mapRel[x][y] = map[x][height-1-y];
                }
            }
        }else{
            for (x = 0; x < width; x++) {
                for (y = 0; y < height; y++) {
                    mapRel[x][y] = map[x][y];
                }
            }
        }

        float[] realMap = new float[width*height];
        for (x = 0; x < width; x++) {
            for (y = 0; y < height; y++) {
                realMap[y*width + x] = mapRel[x][y];
            }
        }

        return realMap;

    }

    public float[] mapSnakeRelative() {
        int width = scan_width;
        int height = scan_height;

        float[][] map = new float[width][height];

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++) {
                map[x][y] = 0;
            }
        }

        int head_x=0, head_y=0;
        for(int x = 0; x < BoardSettings.rows; x++){
            for(int y = 0; y < BoardSettings.columns; y++) {
                Point currentPoint = uiContext.fieldCoordinates[x][y];
                if(currentPoint.equals(head.getPosition())){
                    head_x = x;
                    head_y = y;
                    break;
                }
            }
        }
        int px = width/2;
        int py = height/2;

        map[px][py] = 0.8f;

        int expanse_x = (width-1)/2;
        int expanse_y = (height-1)/2;

        for(int x = head_x-expanse_x, xx = 0; x < head_x+expanse_x; x++, xx++){
            for(int y = head_y-expanse_y, yy = 0; y < head_y+expanse_y; y++, yy++) {
                if(x >= 0 && x < BoardSettings.columns && y >= 0 && y < BoardSettings.rows){
                    Point currentPoint = uiContext.fieldCoordinates[x][y];
                    for(Square s : tail){
                        if(s != null){
                            if(s.getPosition().equals(currentPoint)){
                                map[xx][yy] = 0.6f;
                            }
                        }
                    }
                    for(Square s : block.getBlocks()){
                        if(s != null) {
                            if(s.getPosition().equals(currentPoint)){
                                map[xx][yy] = -0.8f;
                            }
                        }
                    }
                }else{
                    map[xx][yy] = 2;
                }
            }
        }

        float[][] mapRel = new float[width][height];
        if(movingInDirection.equals("left")){
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    mapRel[x][y] = map[height-1-y][x];
                }
            }
        }else if(movingInDirection.equals("right")){
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    mapRel[x][y] = map[y][width-1-x];
                }
            }
        }else if(movingInDirection.equals("down")){
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    mapRel[x][y] = map[x][height-1-y];
                }
            }
        }else{
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    mapRel[x][y] = map[x][y];
                }
            }
        }
        float[] realMap = new float[width*height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                realMap[y*width + x] = mapRel[x][y];
            }
        }


        return realMap;
    }

    public float[] mapSnake3() {
        float[] map = new float[4];
        int head_x=0, head_y=0;
        for(int x = 0; x < BoardSettings.rows; x++){
            for(int y = 0; y < BoardSettings.columns; y++) {
                Point currentPoint = uiContext.fieldCoordinates[x][y];
                if(currentPoint.equals(head.getPosition())){
                    head_x = x;
                    head_y = y;
                    break;
                }
            }
        }
        for(int y = head_y; y < BoardSettings.columns && map[0] != 1; y++) {
            Point currentPoint = uiContext.fieldCoordinates[head_x][y];


            for (Square s : tail) {
                if (s != null) {
                    if (s.getPosition().equals(currentPoint)) {
                        map[0] = 1;
                        break;
                    }
                }
            }
        }
        for(int y = head_y; y >= 0 && map[1] != 1; y--) {
            Point currentPoint = uiContext.fieldCoordinates[head_x][y];

            for (Square s : tail) {
                if (s != null) {
                    if (s.getPosition().equals(currentPoint)) {
                        map[0] = 1;
                        break;
                    }
                }
            }
        }
        for(int x = head_x; x < BoardSettings.rows && map[2] != 1; x++) {
            Point currentPoint = uiContext.fieldCoordinates[x][head_y];

            for (Square s : tail) {
                if (s != null) {
                    if (s.getPosition().equals(currentPoint)) {
                        map[2] = 1;
                        break;
                    }
                }
            }
        }
        for(int x = head_x; x >= 0 && map[3] != 1; x++) {
            Point currentPoint = uiContext.fieldCoordinates[x][head_y];

            for (Square s : tail) {
                if (s != null) {
                    if (s.getPosition().equals(currentPoint)) {
                        map[3] = 1;
                        break;
                    }
                }
            }
        }
        return map;
    }

    public float[] mapSnake2(){
        int width = scan_width;
        int height = scan_height;
        float[] map = new float[width*height];

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++) {
                map[x*height + y] = 0;
            }
        }

        int head_x=0, head_y=0;
        for(int x = 0; x < BoardSettings.rows; x++){
            for(int y = 0; y < BoardSettings.columns; y++) {
                Point currentPoint = uiContext.fieldCoordinates[x][y];
                if(currentPoint.equals(head.getPosition())){
                    head_x = x;
                    head_y = y;
                    break;
                }
            }
        }
        int px = width/2;
        int py = height/2;

        //System.out.println(px +"; "+py);

        map[px*height + py] = 0.8f;

        int expanse_x = (width-1)/2;
        int expanse_y = (height-1)/2;


        for(int x = head_x-expanse_x, xx = 0; x < head_x+expanse_x; x++, xx++){
            for(int y = head_y-expanse_y, yy = 0; y < head_y+expanse_y; y++, yy++) {

                if(x >= 0 && x < BoardSettings.columns && y >= 0 && y < BoardSettings.rows){
                    Point currentPoint = uiContext.fieldCoordinates[x][y];
                    for(Square s : tail){
                    if(s != null){
                        if(s.getPosition().equals(currentPoint)){
                            map[xx*height + yy] = 0.6f;
                        }
                    }
                    }
                    for(Square s : block.getBlocks()){
                        if(s != null) {
                            if(s.getPosition().equals(currentPoint)){
                                map[xx*height + yy] = -0.8f;
                            }
                        }
                    }
                }else{

                    map[xx*height + yy] = 2;
                }
            }
        }
        return map;
    }


    public float[] mapSnake(){
        float[] map = new float[BoardSettings.rows * BoardSettings.columns];
        int y_size = BoardSettings.columns;

        for(int x = 0; x < BoardSettings.rows; x++){
            for(int y = 0; y < BoardSettings.columns; y++) {
                map[x*y_size + y] = 0;
            }
        }

        for(int x = 0; x < BoardSettings.rows; x++){
            for(int y = 0; y < BoardSettings.columns; y++){
                Point p = uiContext.fieldCoordinates[x][y];
                for(Square s : block.getBlocks()){
                    if(s != null) {
                        if(s.getPosition().equals(p)){
                            map[x*y_size + y] += 10;
                        }
                    }
                }
                for(Square s : tail){
                    if(s != null){
                        if(s.getPosition().equals(p)){
                            map[x*y_size + y] = 0.7f;
                        }
                    }
                }
                if(head.getPosition().equals(p)){
                    map[x*y_size + y] = 0.9f;
                }
            }
        }

        return map;
    }

    public void kill(){
        stop();
    }

    //AUTO-MOVE SNAKE
    public float run(long rate){
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
                    //if(aiIsPlaying){
                        movingInDirection = getAiDirection();
                    //}

                    moveSnake();
                    //System.out.println("SNAKE MOVED");
                }
            }
        };

        executor = Executors.newScheduledThreadPool(1);
        //executor.scheduleAtFixedRate(autoMoveSnakeRunnable, 0, rate, TimeUnit.NANOSECONDS);
        executor.scheduleWithFixedDelay(autoMoveSnakeRunnable,0,rate, TimeUnit.NANOSECONDS);

        try {
            //System.out.println("START WAIT");
            executor.awaitTermination(1, TimeUnit.HOURS);
            //System.out.println("WAIT END");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println("DONE RUN RUN");

        //return (float) (((float)blocksEaten/(float)moveCount) * Math.sqrt(moveCount));
        //return (float) moveCount/2 + blocksEaten*2;
        //return (float) (blocksEaten * Math.log(moveCount));
        return (blocksEaten < 1) ? (float) Math.log(moveCount) : (float) blocksEaten + moveCount;
        //return (float) (blocksEaten + Math.log10(moveCount));
    }

    public void SetFFNNWeights(float[][] input_weights, float[][] output_weights){
        controller.setWeights(input_weights, output_weights);
    }

    private String getAiDirection() {

        float[] output = controller.feed(snakeState());
        //System.out.println(output[0] + ", " + output[1]+ ", " + output[2]+ ", " + output[3]);


        /*int move = 0;
        float value = output[0];
        for(int i = 1; i < 4; i++) {
            if(output[i] > value){
                move = i;
                value = output[i];
            }
        }*/
        String mm;
        float v1 = output[0];
        float v2 = output[1];

        int direction = 0;
        while(!movingInDirection.equals(moves[direction])){
            direction++;
        }

        int left = direction-1;
        int right = direction+1;
        if(right >= 4) right = 0;
        if(left <= 0) left = 3;
        String left_move = moves[left];
        String right_move = moves[right];


        if(v1 > 0.5f && v2 > 0.5f){
            mm = movingInDirection;
        }else if(v1 > 0.5f && v2 <= 0.5f){
            mm = right_move;
        }else if(v1 <= 0.5f && v2 > 0.5f){
            mm = left_move;
        }else{
            int r = random.nextInt(2);
            mm = r == 0 ? right_move : left_move;
        }




        //System.out.println("MOVE " + moves[move]);
        //String m = moves[move];

        if(
                mm == "right" && movingInDirection == "left" ||
                mm == "left" && movingInDirection == "right" ||
                mm == "up" && movingInDirection == "down" ||
                mm == "down" && movingInDirection == "up"
        ){
            return movingInDirection;
        }
        //return moves[move];
        return mm;
    }


    private void stop(){
        if(executor != null)
            executor.shutdownNow();
        //System.out.println("EXECUTOR SHUT DOWN");
    }
    //MOVE SNAKE
    public void moveSnake(){

        if(life <= 0){
            //snakeMoving = false;
            executor.shutdownNow();
            //System.out.println("LOOPING!!!");

        }
        life--;
        //System.out.println("FORWARD");
        Point newLocationOfHead = null;
        if(movingInDirection == "right") newLocationOfHead = getRightFieldCoords(head);
        if(movingInDirection == "left") newLocationOfHead = getLeftFieldCoords(head);
        if(movingInDirection == "down") newLocationOfHead = getBottomFieldCoords(head);
        if(movingInDirection == "up") newLocationOfHead = getTopFieldCoords(head);
        //System.out.println("FORWARD2");
        if(newLocationOfHead != null){
            //System.out.println("FORWARD3");
            head.setPosition(newLocationOfHead);
            if(doesHeadTouchTale()){
                //System.out.println("FORWARD4");
                snakeMoving = false;
                //JOptionPane.showMessageDialog(null, blocksEaten * 100 + " POINTS");
                //block.stopBlockClock();
                //System.out.println("SNAKE DEAD 1");
                stop();
            }
            //System.out.println("FORWARD6");
            eatBlocks();
            moveTailWithHead(newLocationOfHead);
            moveCount++;
        }
        if(newLocationOfHead == null && snakeMoving){
            //System.out.println("FORWARD II");
            snakeMoving = false;
            //JOptionPane.showMessageDialog(null, blocksEaten * 100 + " POINTS");
            //block.stopBlockClock();
            //System.out.println("SNAKE DEAD 2");
            stop();
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
                    if(BoardSettings.Rendering) {
                        uiContext.panel.repaint();
                    }
                    addNewTailSquare();
                    blocksEaten++;
                    life += blockLife;
                    //blockLife+=1;
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

                if(BoardSettings.Rendering) {
                    uiContext.panel.add(tail[i]);
                }
            }
            if(tail[i+1] == null){
                tail[i+1] = new Square(newTailSquareLocation, new Dimension(BoardSettings.fieldWidth, BoardSettings.fieldHeight), BoardSettings.color3);
                if(BoardSettings.Rendering) {
                    uiContext.panel.add(tail[i + 1]);
                }
            }

            tail[i].setPosition(tail[i + 1].getPosition());



        }
        if(tail[BoardSettings.numberOfTailsSquares-1] == null){

            tail[BoardSettings.numberOfTailsSquares-1] = new Square(newTailSquareLocation, new Dimension(BoardSettings.fieldWidth, BoardSettings.fieldHeight), BoardSettings.color3);
            if(BoardSettings.Rendering) {
                uiContext.panel.add(tail[BoardSettings.numberOfTailsSquares - 1]);
            }

        } else {
            tail[BoardSettings.numberOfTailsSquares-1].setPosition(newTailSquareLocation);

        }

    }


}
