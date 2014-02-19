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
    public static void main(String args[]) {
        System.out.println("Creating network..");
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 318));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 100));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
        network.getStructure().finalizeStructure();
        network.reset();
        
        System.out.println("Loading training data..");
        MLDataSet data = EncogUtility.loadCSV2Memory(Convert.convertToEncog(true, System.getProperty("trainingData")), 318, 1, false, CSVFormat.ENGLISH, false);
        
        // 1. Backpropagation Feedforward
        // Backpropagation trainingType = new Backpropagation(network, data);
        // 2. ResilientPropagation Feedforward
        MLTrain trainingType = new ResilientPropagation(network, data);
        //trainingType.setErrorFunction(new NewCalculationFunction());
        
        // 1. Train to x minutes
        EncogUtility.trainConsole(trainingType, network, data, 1);
        // 2. Train to an error margin
        // EncogUtility.trainToError(trainingType, 0.00001);
        
        // 1. Print data, ideal value and computed value
        // EncogUtility.evaluate(network, data);
        // 2. print id, ideal value & computed value
        int ok = 0;
        double threshold = 0.7;
        for (int i=0;i<data.getRecordCount();i++) {
            final MLDataPair pair = data.get(i);
            if (pair.getIdeal().getData(0) == 1.0 && network.compute(pair.getInput()).getData(0) > 0.7) ok++;
            if (pair.getIdeal().getData(0) == 0.0 && network.compute(pair.getInput()).getData(0) <= 0.7) ok++;
        }
        System.out.printf("Training size: %d, Correct with threshold %f: %d", data.getRecordCount(), threshold, ok);

        System.out.println("Loading evaluation data..");
        data = EncogUtility.loadCSV2Memory(Convert.convertToEncog(false, System.getProperty("evaluationData")), 318, 0, false, CSVFormat.ENGLISH, false);
        int i = 400000;
        for (final MLDataPair pair : data) {
            i++;
            if ( Double.parseDouble(EncogUtility.formatNeuralData(network.compute(pair.getInput()))) > 0.75 ) System.out.println(i);
        }
    }
}
