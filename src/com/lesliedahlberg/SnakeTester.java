package com.lesliedahlberg;

/**
 * Created by lesliedahlberg on 2017-02-03.
 */
public class SnakeTester implements Testable {

    UIContext uiContext;
    Block block;
    final Snake snake;
    long rate;


    public SnakeTester(UIContext uiContext, Block block, Snake snake){
        this.uiContext = uiContext;
        this.block = block;
        this.snake = snake;
        rate = BoardSettings.speed;
    }

    @Override
    public float Evaluate(float[] parameters) {
        snake.initSnake();

        float[][] input_weights = new float[snake.input_neurons+1][snake.hidden_neurons];
        float[][] output_weights = new float[snake.hidden_neurons+1][snake.output_neurons];

        int offset = (snake.input_neurons+1) * (snake.hidden_neurons);

        for(int i = 0; i < snake.input_neurons + 1; i++){
            for(int j = 0; j < snake.hidden_neurons; j++){
                //System.out.println("ARRAY_SIZE = " + parameters.length + "; INDEX + " + (i*snake.hidden_neurons + j));

                input_weights[i][j] = parameters[i*snake.hidden_neurons + j];
            }
        }

        //System.out.println("IN CONVERSION FINISHED");

        for(int i = 0; i < snake.hidden_neurons+1; i++){
            for(int j = 0; j < snake.output_neurons; j++){
                output_weights[i][j] = parameters[offset + i*snake.output_neurons + j];
            }
        }
        //System.out.println("OUT CONVERSION FINISHED");

        snake.SetFFNNWeights(input_weights, output_weights);
        //System.out.println("WEIGHTS SET!");


        float v1 = snake.run(rate);
        //float v2 = snake.run(rate);
        //float v3 = snake.run(rate);
        //float v4 = snake.run(rate);

        //float v = (v1+v2+v3+v4)/4;


        //System.out.println("DONE RUN");
        //System.out.println("SCORE 2 = "+ v);
        return v1;
    }

    public void speedUp(int i) {
        rate -= 10;
        if(rate < 10) rate = 10;
    }

    public void slowDown(int i) {
        rate += 10;
    }
}
