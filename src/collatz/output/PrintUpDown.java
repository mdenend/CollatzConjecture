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
     * This was the static method that was called when everything was taken care of, not printing as we go.
     * @param results The set of helpers to be printed.
     * @param opts OptionsHelper that has arguments.
     * @param stamp The timestamp generated after we finish running.
     */
    @Deprecated
    public static void writeOutputFile(Set<UpDownHelper> results, OptionsHelper opts, String stamp) {
        try {
            PrintWriter output = new PrintWriter(new FileWriter(opts.getOutputFilePrefix() + opts.getOutputFileSuffix()));
            output.println("Time needed to run: " + stamp);
            output.println(header);
            for (UpDownHelper u: results) {
                output.print(addCommas(u.getNumber()) + CS);
                //output.print(u.getNumber().mod(BigInteger.valueOf(opts.getOutputBase())) + CS);
                output.print(u.getNumber().mod(ComputeMods.THREE) + CS);
                output.print(addCommas(u.getDecaysTo()) + CS);
                output.print(u.getDecaysToLength() + CS);
                switch(u.getState()) {
                    case FOUND:
                        output.print(addCommas(u.getGrowsFrom()) + CS + u.getGrowsFromLength());
                        break;
                    case NOT_FOUND:
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
                output.flush();
            }
            output.close();

        } catch (IOException e) {
            System.err.println("Error writing file " + opts.getOutputFilePrefix() + opts.getOutputFileSuffix() + ":");
            e.printStackTrace();
        }
        
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
