package edu.drexel.cs.tgmc;

import java.io.InputStream;

import org.encog.EncogError;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

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

    /**
     * Import the CSV into the specified dataset.
     * 
     * @param set
     *            The dataset to import into.
     * @param inputSize
     *            The number of input values.
     * @param idealSize
     *            The number of ideal values.
     * @param istream
     *            The InputStream to read from.
     * @param headers
     *            True, if headers are present.
     * @param format
     *            The CSV format.
     * @author HeatonResearch
     */
    public static void importCSV(final MLDataSet set, final int inputSize,
            final int skipSize, final int idealSize, final InputStream istream,
            final boolean headers, final CSVFormat format) {
        
        /**
         * This method is copied from Heaton's Encog.
         * Changes: Skip first two values: AnswerID, QuestionID. Read Boolean ideal value instead of Double. 
         */

        int line = 0;

        final ReadCSV csv = new ReadCSV(istream, false, format);

        while (csv.next()) {
            line++;
            BasicMLData input = null, ideal = null;

            if (inputSize + idealSize != csv.getColumnCount()) {
                throw new EncogError("Line #" + line + " has "
                        + csv.getColumnCount()
                        + " columns, but dataset expects "
                        + (inputSize + idealSize) + " columns.");
            }

            if (inputSize > 0) {
                input = new BasicMLData(inputSize);
            }
            if (idealSize > 0) {
                ideal = new BasicMLData(idealSize);
            }

            final BasicMLDataPair pair = new BasicMLDataPair(input, ideal);
            int index = 0;
            
            // Skip the first two values of AnswerID & QuestionID
            for (int i = 0; i < skipSize; i++) {
                csv.get(index++);
            }

            for (int i = 0; i < inputSize; i++) {
                if (input != null)
                    input.setData(i, csv.getDouble(index++));
            }
            
            // Read the Boolean value at the last column
            for (int i = 0; i < idealSize; i++) {
                if (ideal != null)
                    ideal.setData(i, Boolean.parseBoolean(csv.get(index++)) ? 1.0 : 0.0);
            }

            set.add(pair);
        }
        csv.close();
    }

}
