package edu.drexel.cs.tgmc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.encog.ConsoleStatusReportable;
import org.encog.util.csv.CSVFormat;
import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.input.InputFieldCSV;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.target.NormalizationStorageCSV;

/**
 * Convert data from IBM to Encog data
 * @author dan
 *
 */
public class Convert {
    public static String convertToEncog(boolean isTraining, String dataFileName) {
        String encogData1 = "encogtrain1.csv";
        String encogData2 = "encogtrain2.csv";
        int i = 0;
        try {
            int n1 = 0;
            System.out.println("Converting data from IBM format to Encog format..");
            FileInputStream in = new FileInputStream(dataFileName);
            FileOutputStream out = new FileOutputStream(encogData1);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out));
            while (true) {
                String s = r.readLine();
                if (s==null) break;
                // try to have the true answers >= 1/2 false answers.
                if (s.contains("true")) n1++;
                if (!isTraining || i <= 3*n1+5) {
                    i++;
                    w.write(s.substring(s.indexOf((int)'.')+3).replace("true", "1.0").replace("false", "0.0"));
                }
                w.write("\n");
            }
            r.close();
            w.close();
            System.out.printf("Converted %d records and saved to %s\n", i, encogData1);
            
            DataNormalization dn = new DataNormalization();
            File filtered = new File(encogData1);
            
            for (int j=0;j<318;j++) {
                InputField ifd;
                dn.addInputField(ifd = new InputFieldCSV(true, filtered, j));
                dn.addOutputField(new OutputFieldRangeMapped(ifd, 0.01 , 0.99));
            }
            if (isTraining) {
                InputField ifd;
                dn.addInputField(ifd = new InputFieldCSV(false, filtered, 318));
                dn.addOutputField(new OutputFieldRangeMapped(ifd, 0, 1), true);
            }
            File out1 = new File(encogData2);
            dn.setCSVFormat(CSVFormat.ENGLISH);
            dn.setTarget(new NormalizationStorageCSV(CSVFormat.ENGLISH, out1));
            dn.setReport(new ConsoleStatusReportable());
            dn.process();
            System.out.printf("Normalized %d records and saved to %s\n", i, encogData2);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encogData2;
    }
}
