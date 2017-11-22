package collatz.utils;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by mad4672 on 10/25/17.
 * TODO: Need to change these to BigInteger later on.
 */
public class AvoidingModGrowthRow {
    private long startingNumber;
    private int numSteps;
    private long largestNumberInChain;
    private List<Long> chain;
    
    //newly added fields
    private int numStepsOverall;
    private int numOddNumbers;
    
    private int oddNumbersInChain;


    public AvoidingModGrowthRow(long startingNumber, int numSteps, long largestNumberInChain, List<Long> chain, 
    		int numStepsOverall, int numOddNumbers, int oddNumbersInCurrentChain) {
        this.chain = chain;
        this.largestNumberInChain = largestNumberInChain;
        this.startingNumber = startingNumber;
        this.numSteps = numSteps;
        this.numOddNumbers = numOddNumbers;
        this.numStepsOverall = numStepsOverall;
        this.oddNumbersInChain = oddNumbersInCurrentChain;
    }

    //Order will be starting number, numSteps, largestNumberInChain, then the chain (just going to print this as LinkedList form for now.
    /*@Override
    public String toString() {
        return startingNumber + "\t" + numSteps + "\t" + largestNumberInChain + "\t" + chain;
    }
    */
    
    public long getStartingNumber() {
    	return startingNumber;
    }
    
    public int getNumSteps() {
    	return numSteps;
    }
    public long getLargestNumberInChain() {
    	return largestNumberInChain;
    }
    
    public List<Long> getChain() {
    	return chain;
    }
    
    public int getNumStepsOverall() {
    	return numStepsOverall;
    }
    
    public int getNumOddNumbers() {
    	return numOddNumbers;
    }
    
    
    public int getOddNumbersInChain() {
    	return oddNumbersInChain;
    }
    


}
