package edu.drexel.cs.tgmc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Convert data from IBM to Encog data
 * @author dan
 *
 */
public class Convert {
    public static String convertToEncog() {
        String encogData = "/tmp/encogtrain.csv";
        int i = 0;
        try {
            System.out.println("Converting data from IBM format to Encog format..");
            FileInputStream in = new FileInputStream(System.getProperty("trainingData"));
            FileOutputStream out = new FileOutputStream(encogData);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out));
            while (true) {
                i++;
                String s = r.readLine();
                if (s==null) break;
                w.write(s.substring(s.indexOf((int)'.')+3).replace("true", "1.0").replace("false", "0.0"));
                w.write("\n");
            }
            r.close();
            w.close();
            System.out.printf("Converted %d records and saved to %s\n", i, encogData);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encogData;
    }
}
