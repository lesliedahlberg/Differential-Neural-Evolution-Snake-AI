package com.lesliedahlberg;

import javax.swing.*;
import java.util.Random;

/**
 * Created by lesliedahlberg on 2017-02-02.
 */
public class PSO {

    Testable problem;
    Interval parameterRange;
    float valueToReach;
    int populationSize;
    int dimension;

    float vmax;
    float omega;
    float c1;
    float c2;
    float f1;
    float f2;

    int iterations;

    float[][] positions;
    float[][] velocities;
    float[][] personalBest;
    float[] globalBest;

    float[] personalBestValue;
    float globalBestValue;


    Random random;

    public boolean alive;

    public PSO(Testable problem, Interval parameterRange, float valueToReach, int populationSize, int dimension){
        this.problem = problem;
        this.parameterRange = parameterRange;
        this.valueToReach = valueToReach;
        this.populationSize = populationSize;
        this.dimension = dimension;

    }

    public void Init(){
        alive = true;
        vmax = (Math.abs(parameterRange.start)+Math.abs(parameterRange.end))/2/100;
        omega = 0.8f;
        c1 = 2;
        c2 = 2;

        positions = new float[populationSize][dimension];
        velocities = new float[populationSize][dimension];
        personalBest = new float[populationSize][dimension];
        globalBest = new float[dimension];

        personalBestValue = new float[populationSize];
        globalBestValue = 0;

        random = new Random();

        for(int i = 0; i < populationSize; i++) {
            for(int j = 0; j < dimension; j++) {
                float randomValue = randomInRange(parameterRange.start, parameterRange.end);
                positions[i][j] = randomValue;
                personalBest[i][j] = randomValue;
            }
        }

        for(int j = 0; j < dimension; j++) {
            globalBest[j] = personalBest[0][j];

        }

        for(int i = 0; i < populationSize; i++){
            personalBestValue[i] = 0;
        }

        for(int i = 0; i < populationSize; i++) {
            for(int j = 0; j < dimension; j++) {
                velocities[i][j] = randomInRange(-vmax, vmax);
            }
        }
        iterations = 0;
    }

    public void test(){
        SwingWorker swingWorker = new SwingWorker<Float,Float>() {
            @Override
            protected Float doInBackground() throws Exception {
                float score = problem.Evaluate(globalBest);

                //System.out.println("BEST = ");
                //for (float a :globalBest) {
                //    System.out.print(a+"; ");
                //}

                //System.out.println("\nBEST SCORE LIVE "+score+ " == "+globalBestValue);
                return 1f;
            }
        };
        swingWorker.execute();
    }

    public void next(final int iterations){
        SwingWorker swingWorker = new SwingWorker<Float,Float>() {
            @Override
            protected Float doInBackground() throws Exception {
                iterate2(iterations);
                return 1f;
            }
        };
        swingWorker.execute();



    }


    public void iterate(){
        if(!alive) return;
        SwingWorker swingWorker = new SwingWorker<Float,Float>() {
            @Override
            protected Float doInBackground() throws Exception {
                iterate2(1);
                return 1f;
            }
        };
        swingWorker.execute();
    }

    public void iterate2(int left){
        //System.out.println("Iterate 2("+left+")");
        float bestScoreOverall = globalBestValue;

        globalBestValue = 0;
        for(int i = 0; i < 1; i++){
            globalBestValue += problem.Evaluate(globalBest);

        }
        //globalBestValue /= 4;

        for (int i = 0; i < populationSize; i++) {
            float value = 0;
            for(int j = 0; j < 2; j++){
                value += problem.Evaluate(positions[j]);
            }
            value /= 4;



            if(value > personalBestValue[i]) {
                personalBest[i] = positions[i];
                personalBestValue[i] = value;
            }

            if(value > globalBestValue) {
                //System.out.println("\nReplacing global best "+globalBestValue+" with "+value);
                /*for (float a :positions[i]) {
                    System.out.print(a+"; ");
                }*/
                //globalBest = positions[i];
                globalBest = positions[i].clone();
                globalBestValue = value;
                /*
                for (float a :globalBest) {
                    System.out.print(a+"; ");
                }*/
            }

        }

        for(int i = 0; i < populationSize; i++) {
            for(int j = 0; j < dimension; j++) {
                f1 = random.nextFloat();
                f2 = random.nextFloat();
                velocities[i][j] = omega * velocities[i][j] + c1 * f1 * (personalBest[i][j] - positions[i][j]) + c2 * f2 * (globalBest[j] - positions[i][j]);
                positions[i][j] = positions[i][j] + velocities[i][j];
            }
        }

        System.out.println("PSO GEN "+iterations+" SCORE "+globalBestValue);
        //System.out.println("GLOBAL BEST = "+globalBestValue);
        /*for (float a :globalBest) {
            System.out.print(a+"; ");
        }
        System.out.println();*/

        iterations++;
        left--;
        if(left > 0 && alive)
            iterate2(left);
    }

    public float[] getSolution(){
        return globalBest;
    }

    public boolean isSolved(){
        return problem.Evaluate(globalBest) <= valueToReach;
    }


    public float randomInRange(float upper, float lower){
        return (float) (Math.random() * (upper - lower) + lower);
    }


    public void kill() {
        alive = false;
    }

    public void live() {
        alive = true;
    }
}
