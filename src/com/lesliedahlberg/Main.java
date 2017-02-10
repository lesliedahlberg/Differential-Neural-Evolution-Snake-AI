package com.lesliedahlberg;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        final UIContext uiContext = new UIContext();
        Block block = new Block(uiContext);
        final Snake snake = new Snake(uiContext, block);
        snake.initSnake();

        final SnakeTester snakeTester = new SnakeTester(uiContext, block, snake);


        int dimension = (snake.input_neurons+1) * (snake.hidden_neurons) + (snake.hidden_neurons+1) * (snake.output_neurons);

        System.out.println("DIMENSION = " + dimension);

        final PSO swarm = new PSO(snakeTester, new Interval(-1,1), 0.000001f, 25, dimension);
        swarm.Init();


        uiContext.panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_1){
                    //System.out.println("PRESS 1");
                    snakeTester.rate = 200*1000000;
                    BoardSettings.Rendering = true;
                    swarm.test();
                }
                if(keyEvent.getKeyCode() == KeyEvent.VK_2){
                    //System.out.println("PRESS 2");
                    snakeTester.rate = 200*1000000;
                    BoardSettings.Rendering = true;
                    swarm.iterate();
                }
                if(keyEvent.getKeyCode() == KeyEvent.VK_3){
                    //System.out.println("PRESS 3");
                    snakeTester.rate = 1;
                    BoardSettings.Rendering = false;
                    swarm.iterate();


                }
                if(keyEvent.getKeyCode() == KeyEvent.VK_4){
                    snakeTester.rate = 1;
                    BoardSettings.Rendering = false;
                    swarm.next(5);


                }
                if(keyEvent.getKeyCode() == KeyEvent.VK_5){
                    snakeTester.rate = 1;
                    BoardSettings.Rendering = false;
                    swarm.next(50);


                }
                if(keyEvent.getKeyCode() == KeyEvent.VK_6){
                    snakeTester.rate = 1;
                    BoardSettings.Rendering = false;
                    swarm.next(100);


                }
                if(keyEvent.getKeyCode() == KeyEvent.VK_F){
                    //snakeTester.speedUp(2);
                    snakeTester.rate = 1;
                    //System.out.println("SPEED = "+ snakeTester.rate);


                }
                if(keyEvent.getKeyCode() == KeyEvent.VK_S){
                    //snakeTester.slowDown(2);
                    //System.out.println("SPEED = "+ snakeTester.rate);
                    snakeTester.rate = 75*1000000;


                }
                if(keyEvent.getKeyCode() == KeyEvent.VK_L){
                    snake.swarming = false;



                }

                if(keyEvent.getKeyCode() == KeyEvent.VK_K){

                    snake.swarming = true;

                }

                if(keyEvent.getKeyCode() == KeyEvent.VK_R){

                    BoardSettings.Rendering = true;

                }

                if(keyEvent.getKeyCode() == KeyEvent.VK_T){

                    BoardSettings.Rendering = false;

                }


                if(keyEvent.getKeyCode() == KeyEvent.VK_0){
                    //System.out.println("PRESS 0");
                    snake.kill();
                    snake.clearSnake();
                    uiContext.flush();

                }
            }


            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

    }



}
