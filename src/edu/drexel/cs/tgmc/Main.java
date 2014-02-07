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
        BasicNetwork network = EncogUtility.simpleFeedForward(318, 200, 40, 1, true);
        
        System.out.println("Loading data..");
        MLDataSet data = EncogUtility.loadCSV2Memory(Convert.convertToEncog(), 318, 1, false, CSVFormat.ENGLISH, false);
        
        // 1. Backpropagation Feedforward
        MLTrain trainingType = new Backpropagation(network, data);
        // 2. ResilientPropagation Feedforward
        // MLTrain trainingType = new ResilientPropagation(network, data);
        
        // 1. Train to x minutes
        EncogUtility.trainConsole(trainingType, network, data, 1);
        // 2. Train to an error margin
        // EncogUtility.trainToError(trainingType, 0.01);
        
        // 1. Print data, ideal value and computed value
        // EncogUtility.evaluate(network, data);
        // 2. print id, ideal value & computed value
        for (int i=0;i<data.getRecordCount();i++) {
            final MLDataPair pair = data.get(i);
            System.out.printf("Answer %d: Ideal = %s, Computed = %s", 
                    i+1, 
                    EncogUtility.formatNeuralData(pair.getIdeal()), 
                    EncogUtility.formatNeuralData(network.compute(pair.getInput()))
                    );
            if (i>=10) break;
        }
    }
}
