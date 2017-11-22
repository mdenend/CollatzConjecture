package collatz.output;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import collatz.helpers.AvoidingModGrowthHelper;
import collatz.utils.AvoidingModGrowthRow;
import collatz.utils.OptionsHelper;

public class PrintAvoidanceGrowthTable {
	
	public static final String CS = "\t";
	
	public static void writeOutputFile(String filename, AvoidingModGrowthHelper helper, OptionsHelper opts, String stamp) {
		String firstCell =  "Time of computation: " + stamp + "\"";
        BigInteger baseBI = BigInteger.valueOf(opts.getOutputBase());
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            writer.println(firstCell);
            writer.println();
            
            
            //new columns: Starting Number, Log_2 staring number, NumberOfStepsAvoidingModMod#, 
            //"Hardness" approximation, Largest Number in Chain, Number Of Total Steps, Number of Odd Numbers, chain.  
            writer.print("Starting Number" + CS);
            writer.print("Log_2(starting number)" + CS);
            writer.print("Number of steps avoiding mod" + CS);
            writer.print("Hardness approximation" + CS);
            writer.print("Largest Number in Chain" + CS);
            writer.print("Total steps in overall chain" + CS);
            writer.print("Number of odd numbers visited" + CS);
            writer.print("Number of odd numbers in longest chain" + CS);
            writer.print("Chain" + CS);
            writer.println("Chain Reversed, Delimited");
            writer.flush();
            
            for (AvoidingModGrowthRow r : helper.getModGrowthRows()) {
            	double log2num = Math.log10(r.getStartingNumber()) / Math.log10(2);
            	double hardnessApprox = r.getNumSteps() / log2num;
            	writer.print(r.getStartingNumber() + CS + log2num + CS + r.getNumSteps() + CS + hardnessApprox + CS);
            	writer.print(r.getLargestNumberInChain() + CS + r.getNumStepsOverall() + CS + r.getNumOddNumbers() + CS
            				+ r.getOddNumbersInChain() + CS + r.getChain() + CS);
            	//reverse the chain, and output just numbers and tabs. This will help my analysis of chain similarity.
            	List<Long> chain = r.getChain();
            	Collections.reverse(chain);
            	int i;
            	for (i = 0; i < chain.size()-1; i++) {
            		writer.print(chain.get(i) + CS);
            	}
            	writer.println(chain.get(i));
            	writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing file " + filename + ":");
            e.printStackTrace();
        }
	}
	

}
