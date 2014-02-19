package edu.drexel.cs.tgmc;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

public class Main {
    public static void main(String args[]) {
        System.out.println("Creating network..");
        BasicNetwork network = EncogUtility.simpleFeedForward(318, 450, 0, 1, false);
        
        System.out.println("Loading training data..");
        MLDataSet data = EncogUtility.loadCSV2Memory(Convert.convertToEncog(true, System.getProperty("trainingData")), 318, 1, false, CSVFormat.ENGLISH, false);
        
        // 1. Backpropagation Feedforward
        MLTrain trainingType = new Backpropagation(network, data);
        // 2. ResilientPropagation Feedforward
        // MLTrain trainingType = new ResilientPropagation(network, data);
        
        // 1. Train to x minutes
        EncogUtility.trainConsole(trainingType, network, data, 3);
        // 2. Train to an error margin
        // EncogUtility.trainToError(trainingType, 0.00001);
        
        // 1. Print data, ideal value and computed value
        // EncogUtility.evaluate(network, data);
        // 2. print id, ideal value & computed value
        for (int i=0;i<50;i++) {
            final MLDataPair pair = data.get(i);
            System.out.printf("Answer %d: Ideal = %s, Computed = %s\n", 
                    i+1, 
                    EncogUtility.formatNeuralData(pair.getIdeal()), 
                    EncogUtility.formatNeuralData(network.compute(pair.getInput()))
                    );
        }

//        System.out.println("Loading evaluation data..");
//        data = EncogUtility.loadCSV2Memory(Convert.convertToEncog(false, System.getProperty("evaluationData")), 318, 0, false, CSVFormat.ENGLISH, false);
//        int i = 400000;
//        for (final MLDataPair pair : data) {
//            i++;
//            if ( Double.parseDouble(EncogUtility.formatNeuralData(network.compute(pair.getInput()))) > 0.75 ) System.out.println(i);
//        }
    }
}
