package edu.drexel.cs.tgmc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

import edu.drexel.cs.tgmc.Convert;

public class Main {
    // variables to set up new nn 
    static boolean isBiasInput = true, isBiasHidden1 = true, isBiasHidden2 = true, isBiasOutput = false;
    static int hidden1 = 240, hidden2 = 0;
    static int minute = 90; static double error = 0;
    static boolean backpropagation = false;
    static boolean keepRatioGood = false; //true to train on 10k records
    
    // variables to test old nn
    static double threshold = 0.75;
    static String networkFileToLoad = null; //null; // if null will not load
//    static String[] fileLoads = {"1392848029023.eg", "1393091956276.eg", "1392982546106.eg", "1392831312093.eg"};
//    static String[] fileLoads = {"1392810540501.eg"};
    static String networkFileToSave = null; // if null will save using time
    static String outputTextFile = "subm.txt";
    static boolean reconvert = true;
    
    public static void main(String args[]) {
        System.out.println("Creating network..");
        BasicNetwork network = null;
        MLTrain trainingType = null;
        String csvfile = null;
        csvfile = (reconvert) ? Convert.convertToEncog(true, keepRatioGood, System.getProperty("trainingData")) : "encogtraint.csv";
        MLDataSet data = null;
        data = EncogUtility.loadCSV2Memory(csvfile, 318, 1, false, CSVFormat.ENGLISH, false);
        
//        BasicNetwork[] networks = new BasicNetwork[fileLoads.length];
//        for (int ii = 0; ii < fileLoads.length; ii++) {
//        	networks[ii] = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(fileLoads[ii]));
//        }
//        System.out.println("Loading evaluation data..");
//        csvfile = (reconvert) ? Convert.convertToEncog(false, keepRatioGood, System.getProperty("evaluationData")) : "encogtraine.csv";
//        data = EncogUtility.loadCSV2Memory(csvfile, 318, 0, false, CSVFormat.ENGLISH, false);
//        int i = 400000;
//        try {
//            FileWriter out = new FileWriter(outputTextFile);
//            for (final MLDataPair pair : data) {
//                i++;
//                int result = 0;
//                double total = 0.0;
//                for (int j = 0; j < networks.length; j++) {
//                	double v = networks[j].compute(pair.getInput()).getData(0);
//                	if (v > threshold ) 
//                    {
//                        result++;
//                    }
//                	total += v;
//                }
////                if (result > networks.length/2) {
//                if (total/networks.length >= threshold) {
//                	out.write(i + "\n");
//                }
//            }
//            System.out.printf("zzz\n");
//
//            out.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        System.exit(0);
        
        if (networkFileToLoad != null) {
            network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(networkFileToLoad));
        } else {
            network = new BasicNetwork();
            network.addLayer(new BasicLayer(null, isBiasInput, 318));
            network.addLayer(new BasicLayer(new ActivationSigmoid(), isBiasHidden1, hidden1));
            if (hidden2 > 0) 
                network.addLayer(new BasicLayer(new ActivationSigmoid(), isBiasHidden2, hidden2));
            network.addLayer(new BasicLayer(new ActivationSigmoid(), isBiasOutput, 1));
            network.getStructure().finalizeStructure();
            network.reset();
            System.out.println("Loading training data..");
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
        }
        
        int n1 = 0, n0 = 0, g1 = 0, g0 = 0;
        for (int i=0;i<data.getRecordCount();i++) {
            final MLDataPair pair = data.get(i);
            if (pair.getIdeal().getData(0) == 0.0) n0++;
            if (pair.getIdeal().getData(0) == 1.0) n1++;
            if (pair.getIdeal().getData(0) == 1.0 && network.compute(pair.getInput()).getData(0) > threshold) g1++;
            if (pair.getIdeal().getData(0) == 0.0 && network.compute(pair.getInput()).getData(0) <= threshold) g0++;
        }
        System.out.printf("Training size: %d, Correct with threshold %f: %d/%d correct 1s, %d/%d correct 0s", data.getRecordCount(), threshold, g1, n1, g0, n0);
        
        if (networkFileToLoad == null) {
            if (networkFileToSave == null) {
                EncogDirectoryPersistence.saveObject(new File(new Date().getTime() + ".eg"), network);
                EncogDirectoryPersistence.saveObject(new File("trainingcontinuation.train"), trainingType.pause());
            }
            else
                EncogDirectoryPersistence.saveObject(new File(networkFileToSave), network);
        }

        System.out.println("Loading evaluation data..");
        csvfile = (reconvert) ? Convert.convertToEncog(false, keepRatioGood, System.getProperty("evaluationData")) : "encogtraine.csv";
        data = EncogUtility.loadCSV2Memory(csvfile, 318, 0, false, CSVFormat.ENGLISH, false);
        int i = 400000;
        try {
            FileWriter out = new FileWriter(outputTextFile);
            for (final MLDataPair pair : data) {
                i++;
                if (network.compute(pair.getInput()).getData(0) > threshold ) 
                {
                    out.write(i + "\n");
                }
            }
            System.out.printf("zzz\n");

            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
