package edu.drexel.cs.tgmc;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

public class Main {
    
    static boolean isBiasInput = true, isBiasHidden1 = true, isBiasHidden2 = true, isBiasOutput = false;
    static int hidden1 = 200, hidden2 = 0;
    static double threshold = 0.9;
    static int minute = 5; static double error = 0;
    static boolean backpropagation = false;
    static boolean keepRatioGood = true;
    
    public static void main(String args[]) {
        System.out.println("Creating network..");
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, isBiasInput, 318));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), isBiasHidden1, hidden1));
        if (hidden2 > 0) 
            network.addLayer(new BasicLayer(new ActivationSigmoid(), isBiasHidden2, hidden2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), isBiasOutput, 1));
        network.getStructure().finalizeStructure();
        network.reset();
        
        System.out.println("Loading training data..");
        MLDataSet data = EncogUtility.loadCSV2Memory(Convert.convertToEncog(true, keepRatioGood, System.getProperty("trainingData")), 318, 1, false, CSVFormat.ENGLISH, false);
        MLTrain trainingType;
        if (backpropagation) {
            // 1. Backpropagation Feedforward
            trainingType = new Backpropagation(network, data);
        } else {
            // 2. ResilientPropagation Feedforward
            trainingType = new ResilientPropagation(network, data);
        }
        //trainingType.setErrorFunction(new NewCalculationFunction());
        
        if (minute != 0) {
            // 1. Train to x minutes
            EncogUtility.trainConsole(trainingType, network, data, minute);
        } else {
            // 2. Train to an error margin
            EncogUtility.trainToError(trainingType, error);
        }
        
        int ok = 0;
        for (int i=0;i<data.getRecordCount();i++) {
            final MLDataPair pair = data.get(i);
            if (pair.getIdeal().getData(0) == 1.0 && network.compute(pair.getInput()).getData(0) > threshold) ok++;
            if (pair.getIdeal().getData(0) == 0.0 && network.compute(pair.getInput()).getData(0) <= threshold) ok++;
        }
        System.out.printf("Training size: %d, Correct with threshold %f: %d", data.getRecordCount(), threshold, ok);

        System.out.println("Loading evaluation data..");
        data = EncogUtility.loadCSV2Memory(Convert.convertToEncog(false, System.getProperty("evaluationData")), 318, 0, false, CSVFormat.ENGLISH, false);
        int i = 400000;
        for (final MLDataPair pair : data) {
            i++;
            if (network.compute(pair.getInput()).getData(0) > threshold ) System.out.printf("%d\n\n", i);
        }
    }
}
