package org.neuroph.contrib.stockBrain;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.LMS;

/**
 *
 * @author jordandearsley
 */
public class Stock {
    double percentage;
    String name;
    double rangeBottom;
    double rangeTop;
    double lastValue;
    double shares;
  
    public Stock(String stockName, double errorPercentage) throws IOException, InterruptedException{
        name = stockName;
        int maxIterations = 10000;
        double error = errorPercentage/100;
        System.out.println("Analyzing Stock: "+stockName);
        double[] dataset = makeDataSet(stockName);
        double max = findMax(dataset)+1;
        NeuralNetwork neuralNet = new MultiLayerPerceptron(5, 11, 1);
        ((LMS) neuralNet.getLearningRule()).setMaxError(error);
        ((LMS) neuralNet.getLearningRule()).setLearningRate(0.3);
        //((LMS) neuralNet.getLearningRule()).setMaxIterations(maxIterations);
        TrainingSet trainingSet = new TrainingSet();

        for(int i = 0;i<dataset.length-5;i+=5){
            trainingSet.addElement(new SupervisedTrainingElement(new double[]{dataset[i] / max, dataset[i+1] / max, dataset[i+2] / max, dataset[i+3] / max, dataset[i+4] / max }, new double[]{dataset[i+5] / max}));
        }
        File network = new File("trainedStockBrain.nnet");              
        neuralNet.learnInSameThread(trainingSet);
        TrainingSet testSet = new TrainingSet();

        double[] inputs = new double[5];
        double[] datasetInputs = dataset;
        int j = 0;
        for(int i = datasetInputs.length-1;i>datasetInputs.length-6;i--){
            inputs[j] = datasetInputs[i];
            j++;
        }
        System.out.println("Processing Last 5 Days...");
        testSet.addElement(new TrainingElement(new double[]{inputs[4] / max, inputs[3] / max, inputs[2] / max, inputs[1] / max, inputs[0] / max}));
        for (TrainingElement testElement : testSet.trainingElements()) {
            neuralNet.setInput(testElement.getInput());
            neuralNet.calculate();
            Vector<Double> networkOutput = neuralNet.getOutput();
            double output = networkOutput.get(0)*max;
            double[] input = new double[5];
            System.out.print("Input: ");
            for(int i = 0; i<5;i++){
                System.out.print(testElement.getInput().get(i)*max+", ");
            }  
            error*=max;
            rangeBottom = output-error;
            rangeTop = output+error;
            System.out.println("Predicted Next EOD: " + output);
            System.out.println("Range: "+rangeBottom+" - "+rangeTop);
            
            
            percentage = (rangeTop/max-(inputs[0]/max))/((rangeTop/max)-(rangeBottom/max))*100;

            System.out.print("UP: "+(percentage)+"% , DOWN: "+(100-percentage)+"%\n\n");
            lastValue=inputs[0];
        }
    }
    
public double getPercent(){
    return percentage;
}
public double getRangeTop(){
    return rangeTop;
}
public double getRangeBottom(){
    return rangeBottom;
}
public String getName(){
    return name;
}
public double getLastValue(){
    return lastValue;
}
public double getshares(){
    return shares;
}
public void changeShares(double modify){
    shares+=modify;
}
 
public double getCurrentValue() throws IOException{
    InputStream is = null;
    String surl = "http://download.finance.yahoo.com/d/quotes.csv?s=%40%5EDJI,"+name+"&f=l1";
    URL url = new URL(surl);
    is = url.openStream();
    BufferedReader dis = new BufferedReader(new InputStreamReader(is));
    String line = dis.readLine();
    double doubleVersion = Double.parseDouble(line);
    return doubleVersion;
}
 public double[] makeDataSet(String stock) throws IOException, InterruptedException{
        InputStream is = null;
        String surl = "http://ichart.yahoo.com/table.csv?s="+stock+"&a=11&b=1&c=2014";
        URL url = new URL(surl);
        is = url.openStream();
        
        BufferedReader dis = new BufferedReader(new InputStreamReader(is));
        String line = "poo";
        ArrayList lines = new ArrayList<String>();
        while(line != null){        
            line = dis.readLine();
            lines.add(line);
        }
        lines.remove(0);
        String[] newLines = new String[lines.size()-1];
        for(int i = 0; i < lines.size()-1; i++){
            newLines[i] = lines.get(i).toString();
        }
        ArrayList closingData = new ArrayList<String>();
        for (String myline : newLines) {
            String[] array = myline.split(",");
            closingData.add(array[4]);
        }
        Collections.reverse(closingData);
        closingData.add(getCurrentValue());
        double[] rawData = new double[closingData.size()];
        for(int i = 0; i<closingData.size();i++){
            
            String value = closingData.get(i).toString();
            double doubleVersion = Double.parseDouble(value);
            rawData[i] = doubleVersion;
        }
        lastValue = rawData[rawData.length-1];
        
        return rawData;
       
    }
    public double findMax(double[] dataset){
        double max = 0;
        for(double value: dataset){
            if(value > max){
                max = value;    
            }
        }
        return max;
    }
    
}
