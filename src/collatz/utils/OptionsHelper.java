package collatz.utils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

//TODO: Definitely needs some comment work. Got quite a bit done today, but still need to do more work.
/**
 * Straightforward class that allows to add more options relatively easily.
 * Created by Matt Denend on 3/17/17.
 */
public class OptionsHelper {


    //TODO: This is totally wrong, I need to have an option to change the lineseparator to the preferred one. I won't worry about it for now...
    //the line separator is initialized here, because we want for the JVM System to determine this property, which could change
    //from OS to OS. So changing it runtime could make things easier.
    public final String LS;


    private BaseOutputHelper baseHelp; //a helper class to handle the many different options for baseoutput.
    private int outputBase; //this is the base that we are analyzing. Or rather, the number of nodes in our graph.
    private List<Integer> printBases; //bases that will be printed.
    private Set<Set<Integer>> avoidBases; //bases that are to be avoided, only in the multiple bases mode.

    private long lowNum; // the lowest number we'll check for.
    private long highNum; // the highest number we'll check for.

    //the file name is separated into prefix and suffix by the period. If mode 0, we want add which bases are avoided
    //to the file name, so we do so after the prefix, and before the suffix.
    private String outputFilePrefix;
    private String outputFileSuffix;

    private boolean timeEffFlag; //if this is on, we'll store any numbers we've visited. More time efficient, but less space efficient.


    //the BigInteger forms of lowNum, highNum, and outputBase.
    private BigInteger lowNumBigInt;
    private BigInteger highNumBigInt;
    private BigInteger baseBigInt;

    private int numSteps; //the number of steps we can take in a chain.

    //0 for base avoidance, 1 for entire chain, 2 for seeing how long an odd number grows until decay, and 3 for the odd number until decay, and backwards collatz until lower number.
    //could have done an enum, but too lazy.
    private int mode;

    //these fields are for the deprecated reverse Collatz computation.
    private int reverseLimit;
    private boolean reverseTickFlag;


    //I think this is the longest constructor I've ever written. It parses all the possible arguments that can be had.
    public OptionsHelper (String[] args) {

        LS = System.getProperty("line.separator");
        baseHelp = new BaseOutputHelper();

        //defaults: iterate through a base 8 graph, output at least base 10 always (even for all ODD bases!)
        outputBase = 8;
        printBases = new ArrayList<>();
        printBases.add(10);

        avoidBases = new LinkedHashSet<>();

        //these are "demo" mode values if we don't have an input file. Good for debugging, or to just show off the program.
        lowNum = 1;
        highNum = 100;

        //if no filename given, use output.csv
        outputFilePrefix = "output";
        outputFileSuffix = ".csv";

        timeEffFlag = false;

        //these stay null unless we need them.
        lowNumBigInt = null;
        highNumBigInt = null;

        numSteps = Integer.MAX_VALUE; //by default, use largest value so we'll never terminate after a ridiculous number of steps.

        mode = 0; //for the legacy tests that I've used before.

        reverseLimit = 100; //just a guess at a reasonable reverse limit. This may change later.

        reverseTickFlag = false;

        //these must be mutually exclusive, so we check if this is the case.
        boolean singleOutputFlag = false;
        boolean batchInputFlag = false;


        //this part parses the options. last options will be the avoided bases.
        int i = 0;
        while (i < args.length) {
            String check = args[i];
            if (check.startsWith("--")) {
                check = check.substring(2).toLowerCase(); //remove front hyphens, analyze!
                switch (check) {
                    case "base":
                        outputBase = tryNumberParse(args[i+1]);
                        baseBigInt = BigInteger.valueOf(outputBase);
                        break;
                    case "baseoutput":
                        String baseInput = args[i+1];
                        parseBaseOutput(baseInput);
                        break;
                    case "inputfile":
                        batchInputFlag = true;
                        if (singleOutputFlag) {
                            System.err.println("Can't have both inputfile and onenumber options active.");
                            System.exit(6);
                        }
                        try {
                            File file = new File(args[i+1]);
                            //System.out.println(file.getAbsolutePath());
                            Scanner s = new Scanner(file);
                            lowNum = s.nextLong();
                            highNum = s.nextLong();
                            s.close();
                        } catch (IOException e) {
                            System.err.println("IO Error with file " + check + " Reason:");
                            e.printStackTrace();
                            System.exit(5);
                        }
                        break;
                    case "mode":
                        switch (args[i+1]) {
                            case "baseavoid":
                                mode = 0;
                                break;
                            case "entirechain":
                                mode = 1;
                                break;
                            case "untildecay":
                                mode = 2;
                                break;
                            case "updown":
                                mode = 3;
                                break;
                            case "avoidingmodgrowth":
                            	mode = 4;
                            	break;
                            default:
                                System.err.println("Invalid mode detected: " + args[i+1]);
                                System.exit(8);
                        }
                        break;

                    case "numsteps":
                        numSteps = tryNumberParse(args[i+1]);
                        if (numSteps < 2) {
                            System.err.println("numsteps must be at least 2.");
                            System.exit(7);
                        }
                        break;
                    case "onenumber":
                        singleOutputFlag = true;
                        if (batchInputFlag) {
                            System.err.println("Can't have both inputfile and onenumber options active.");
                            System.exit(9);
                        }
                        lowNum = tryLongParse(args[i+1]);
                        highNum = lowNum;
                        break;
                    case "outputfile":
                        String filename = args[i+1];
                        if (filename.contains(".")) {
                            int index = filename.lastIndexOf(".");
                            outputFilePrefix = filename.substring(0, index);
                            outputFileSuffix = filename.substring(index, filename.length());

                        } else {
                            outputFilePrefix = filename;
                        }

                        break;
                    case "reverselimit":
                        reverseLimit = tryNumberParse(args[i+1]);
                        break;
                    case "reverseticks":
                        reverseTickFlag = true;
                        i--;
                        break;
                    case "timeefficient":
                        timeEffFlag = true;
                        i--; //all other options need the next arg, so just decrement then increase by 2.
                        break;
                    default:
                        System.err.println("Invalid option given: " + check);
                        System.exit(2);
                    break;
                }
                i += 2;
            } else if (mode == 0 || mode == 4){ //Builds the bases avoided part, ONLY if mode = 0. All of these numbers should be at the end of the input.
                Set<Integer> avoidSet = new HashSet<>();
                if (check.contains("-")) { //add all of these numbers to the set.
                    String[] range = check.split("\\-");
                    for (String s: range) {
                        int addNum = tryNumberParse(s);
                        avoidSet.add(addNum);
                    }
                } else {
                    int addNum = tryNumberParse(check);
                    avoidSet.add(addNum);
                }
                avoidBases.add(avoidSet);
                i++;
            } else {
                System.err.println("Invalid option given:" + check);
                System.exit(10);
            }
        }
    }

    //this code takes the input for the --outputbase option and processes it. Takes a few methods, as the process is complicated.
    private void parseBaseOutput(String baseInput) {
        //if input has braces surrounding it, then we need to separate the arguments based off of their commas, and parse each one.
        if (baseInput.startsWith("[") && baseInput.endsWith("]")) {
            String[] values = baseInput.substring(1,baseInput.length()-1).split(",");
            for (String s: values) {
                parseBaseOutputHelp(s);
            }
        } else {
            parseBaseOutputHelp(baseInput);
        }

        //if all option selected, add all ints from 2 to 32 to the set. Anything already appearing won't be added
        if (baseHelp.all) {
            for (int i = 2; i <= 32; i++) {
                baseHelp.intsToAdd.add(i);
            }
        }

        //now, build the list based off of our set!
        for (int i : baseHelp.intsToAdd) {
            if (i == 10) { //10 is always available by default.
                continue;
            }
            if (!baseHelp.evens && i % 2 == 0) {
                continue;
            }
            if (!baseHelp.odds && i % 2 == 1) {
                continue;
            }
            printBases.add(i);
        }
    }


    //this method takes one part of the several possible comma-separated arguments for the output bases argument, and parses it.
    //It could be either a single number, a range of numbers, or text options ALL, EVEN, or ODD>
    private void parseBaseOutputHelp(String value) {
        if (value.contains("-")) {
            String[] range = value.split("\\-");
            int low = tryNumberParse(range[0]);
            int high = tryNumberParse(range[1]);
            for (int j = low; j <= high; j++) {
                baseHelp.intsToAdd.add(j);
            }
        } else if (isInteger(value)) {
            int num = tryNumberParse(value);
            baseHelp.intsToAdd.add(num);
        } else {
            parseBaseOutputText(value);
        }

    }


    //checks if a string option is one of the three valid options: ALL, EVEN, or ODD. Anything else terminates this application.
    private void parseBaseOutputText(String baseInput) {
        switch (baseInput.toUpperCase().trim()) {
            case "ALL":
                baseHelp.all = true;
                break;
            case "EVEN":
                baseHelp.odds = false;
                break;
            case "ODD":
                baseHelp.evens = false;
                break;
            default:
                System.err.println("Invalid input for --baseoutput option: " + baseInput);
                System.exit(3);
        }
    }

    //a catch all method for attempting to parse numbers. If the parse fails, an exception is caught and the error message is output,
    //causing for the application to quit.
    private int tryNumberParse (String tryParseInt) {
        int result = -1;
        try {
            result= Integer.parseInt(tryParseInt);
        } catch (NumberFormatException e) {
            System.err.println("Error with parsing an integer: Attempted to parse string " + tryParseInt);
            e.printStackTrace();
            System.exit(4);
        }
        return result;
    }

    //same as tryNumberParse, but returns a long instead.
    private long tryLongParse (String tryParseLong) {
        long result = -1;
        try {
            result= Long.parseLong(tryParseLong);
        } catch (NumberFormatException e) {
            System.err.println("Error with parsing an long: Attempted to parse string " + tryParseLong);
            e.printStackTrace();
            System.exit(4);
        }
        return result;
    }


    //checks if a string is an integer.
    private boolean isInteger(String str) {
        return str.matches("\\d+");
    }


    /**
     * This is a inner class that helps us parse which bases we're going to output on our spreadsheet. Was designed just
     * to clean the code a little bit.
     */
    private class BaseOutputHelper {
        boolean evens = true;
        boolean odds = true;
        boolean all = false;
        Set<Integer> intsToAdd = new LinkedHashSet<>();
    }

    public String toString() {
        return "Base of graph: " + outputBase + LS + "Bases we are outputting: " + printBases + LS +
        "Mods we are excluding: " + avoidBases + LS + "Range of numbers: " + lowNum + "-" + highNum + "\n"
                + "Name of output file: " + outputFilePrefix + outputFileSuffix;
    }

    public int getOutputBase() {
        return outputBase;
    }

    public List<Integer> getPrintBases() {
        return printBases;
    }

    public Set<Set<Integer>> getAvoidBases() {
        return avoidBases;
    }

    public long getLowNum() {
        return lowNum;
    }

    public long getHighNum(){
        return highNum;
    }

    public BigInteger getLowNumBigInteger() {
        if (lowNumBigInt == null) {
            lowNumBigInt = BigInteger.valueOf(lowNum);
        }
        return  lowNumBigInt;
    }

    public BigInteger getHighNumBigInteger() {
        if (highNumBigInt == null) {
            highNumBigInt = BigInteger.valueOf(highNum);
        }
        return  highNumBigInt;
    }

    public BigInteger getBaseBigInt () {
        return baseBigInt;
    }

    public int getNumSteps() {
        return numSteps;
    }

    public int getMode() {
        return mode;
    }

    /*//kind of a hacky way to just get the first set, for legacy method only. Should never be used.
    public int getFirstAvoidBase() {
        for (Set<Integer> s : avoidBases) {
            for (int i: s) {
                return i;
            }
        }
        return -1;
    }*/

    public int getReverseLimit(){ return  reverseLimit;}

    public String getOutputFilePrefix() {
        return outputFilePrefix;
    }

    public String getOutputFileSuffix() {
        return outputFileSuffix;
    }

    //public boolean isSingleOutputFlag() {return  singleOutputFlag; }



    public boolean isTimeEffFlag() {return timeEffFlag; }

    public boolean isReverseTickFlag() {
        return reverseTickFlag;
    }

    //used for testing the options whenever I add them.
    public static void main(String[] args) {
        //OptionsHelper opts = new OptionsHelper(args);
        System.out.println(System.getProperty("sun.arch.data.model")); 
    }

}
