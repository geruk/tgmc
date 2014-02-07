package edu.drexel.cs.tgmc;

import com.googlecode.jcsv.reader.CSVEntryParser;

class TrainingAnswer {
    public static final int FEATURE_LENGTH = 318;
    public final double id;
    public final double qid;
    public final double[] features;
    public final int value;
    
    public TrainingAnswer(double id, double qid, double[] features, int value) {
        this.id = id;
        this.qid = qid;
        this.features = features;
        this.value = value;
    }
}

public class TrainingAnswerParser implements CSVEntryParser<TrainingAnswer> {

    @Override
    public TrainingAnswer parseEntry(String... args) {
        double id = Double.parseDouble(args[0]);
        double qid = Double.parseDouble(args[1]);
        double[] features = new double[TrainingAnswer.FEATURE_LENGTH];
        for (int i=0; i<features.length; i++) features[i] = Double.parseDouble(args[i+2]);
        int value = Boolean.parseBoolean(args[args.length-1]) ? 1 : 0;
        return new TrainingAnswer(id, qid, features, value);
    }

}
