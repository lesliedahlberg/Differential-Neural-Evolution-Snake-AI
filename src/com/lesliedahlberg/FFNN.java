package com.lesliedahlberg;

/**
 * Created by lesliedahlberg on 2017-01-31.
 */
public class FFNN {
    float[][] input_weight;
    float[][] output_weight;
    int input_neurons;
    int hidden_neurons;
    int output_neurons;

    public FFNN(int input_neurons, int hidden_neurons, int output_neurons){
        this.input_neurons = input_neurons;
        this.hidden_neurons = hidden_neurons;
        this.output_neurons = output_neurons;
    }

    public void setWeights(float[][] input_weight, float[][] output_weight){
        this.input_weight = input_weight;
        this.output_weight = output_weight;

    }

    public float sigmoid(float x){
        return (float) (1.0 / (1.0 + Math.exp(-x)));
    }

    public float[] feed(float[] input){
        float[] hidden = new float[hidden_neurons];
        float[] output = new float[output_neurons];
        for(int i = 0; i < input_neurons; i++){
            for(int j = 0; j < hidden_neurons; j++){
                hidden[j] += input_weight[i][j] * input[i];
            }
        }
        for(int j = 0; j < hidden_neurons; j++){
            hidden[j] += input_weight[input_neurons][j] * -1;
        }
        for(int j = 0; j < hidden_neurons; j++){
            hidden[j] = sigmoid(hidden[j]);
        }
        for(int i = 0; i < hidden_neurons; i++){
            for(int j = 0; j < output_neurons; j++){
                output[j] += output_weight[i][j] * hidden[i];
            }
        }
        for(int j = 0; j < output_neurons; j++){
            output[j] += output_weight[output_neurons][j] * -1;
        }
        for(int j = 0; j < output_neurons; j++){
            output[j] = sigmoid(output[j]);
        }
        return output;
    }

}
