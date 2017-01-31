package com.lesliedahlberg;

public class Main {

    public static void main(String[] args) {
        //UIContext uiContext = new UIContext();
        //Block block = new Block(uiContext);
        //Snake snake = new Snake(uiContext, block);
        //snake.initSnake();

        //System.out.println("Hello World!");

        int input_neurons = 2;
        int hidden_neurons = 2;
        int output_neurons = 1;
        float threshold = 1;

        FFNN ffnn = new FFNN(input_neurons, hidden_neurons, output_neurons, threshold);

        float[][] input_weights = new float[][] {{1,0.5f}, {0.5f, 1}};
        float[][] output_weights = new float[][] {{1}, {1}};

        ffnn.setWeights(input_weights, output_weights);

        float[] output_0_0 = ffnn.feed(new float[]{0, 0});
        float[] output_0_1 = ffnn.feed(new float[]{0, 1});
        float[] output_1_0 = ffnn.feed(new float[]{1, 0});
        float[] output_1_1 = ffnn.feed(new float[]{1, 1});

        System.out.println(output_0_0[0]);
        System.out.println(output_0_1[0]);
        System.out.println(output_1_0[0]);
        System.out.println(output_1_1[0]);

        for(int i = 0; i < output_neurons; i++){
            //System.out.println("output["+i+"]="+output[i]);
        }


    }
}
