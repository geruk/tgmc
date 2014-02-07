package edu.drexel.cs.tgmc;

import java.io.File;

import org.encog.ml.data.buffer.BufferedMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

public class Main {
    public static void main(String args[]) {
        BasicNetwork network = new BasicNetwork();
        // input layer. 319nodes ~ 319 values from data set
        network.addLayer(new BasicLayer(318));
        // hidden layer. weights will be calculated here
        network.addLayer(new BasicLayer(100));
        // output layer. True or False. 1 or 0.
        network.addLayer(new BasicLayer(1));
        network.getStructure().finalizeStructure();
        network.reset();
        System.out.println("Training the neural network..");
        
        BufferedMLDataSet data = new BufferedMLDataSet(new File(""));
    }
}
