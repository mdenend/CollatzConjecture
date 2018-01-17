package collatz.output;

import collatz.compute.ComputeMods;
import collatz.helpers.UpDownHelper;
import collatz.utils.OptionsHelper;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * This class, unlike other printing utilities in PrintPaths, actually builds an object that contains the PrintWriter that we append the file to.
 * Was done like this because the PrintWriter needed to be kept open as we compute, and we wanted to flush some batch of UpDownHelpers
 * to the file all at once.
 * Created by Matt Denend on 5/27/17.
 */
public class PrintUpDown {
    public static final String CS = "\t";
    public static final String header = "Number" + CS + "Mod 3" + CS + "DecaysTo" + CS + "DecaysToLength" + CS +
            "DecaysToFirstOdd" + CS + "DecaysToFirstOddLength" + CS + "GrowsFrom (if found)" + CS  +"GrowsFromLength";


    private PrintWriter output;


    public PrintUpDown(OptionsHelper opts) throws IOException {
        output = new PrintWriter(new FileWriter(opts.getOutputFilePrefix() + opts.getOutputFileSuffix()));
        output.println(header);
    }

    /**
     * This both prints UpDownHelpers, and removes them because we don't care about lower numbers.
     * @param m The map containing the BigInteger and UpDownHelper pairs.
     * @param low The lowest value to print
     * @param high The highest value to print
     * @throws IOException
     */
    public void flushToOutputFile(Map<BigInteger, UpDownHelper> m, long low, long high) throws IOException{
        for (long i = low; i < high; i+= 2) {
            UpDownHelper u = m.remove(BigInteger.valueOf(i));
            output.print(addCommas(u.getNumber()) + CS);
            output.print(u.getNumber().mod(ComputeMods.THREE) + CS);
            output.print(addCommas(u.getDecaysTo()) + CS);
            output.print(u.getDecaysToLength() + CS);
            output.print(addCommas(u.getDecaysToOdd()) + CS);
            output.print(u.getDecaysToOddLength() + CS);
            switch(u.getState()) {
                case FOUND:
                    output.print(addCommas(u.getGrowsFrom()) + CS + u.getGrowsFromLength());
                    break;
                case NOT_FOUND: //this should never execute.
                    output.print("UNK" + CS + ">" + u.getGrowsFromLength());
                    break;
                case IMPOSSIBLE:
                    output.print("N/A" + CS + u.getGrowsFromLength());
                    break;
                default:
                    System.err.println("Error printing growsFrom information for number " + u.getNumber());
                    break;
            }
            output.println();
        }
        output.flush();
    }

    /**
     * Closes the PrintStream after we're finished writing the file.
     */
    public void closeStream() {
        output.close();
    }

    //adds commas to a BigInteger, returns a String.
    private static String addCommas(BigInteger num) {
        return  NumberFormat.getNumberInstance(Locale.US).format(num);
    }
    
}
