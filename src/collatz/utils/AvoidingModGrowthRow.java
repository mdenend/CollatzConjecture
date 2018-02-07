package collatz.utils;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by mad4672 on 10/25/17.
 */
public class AvoidingModGrowthRow {
    private long startingNumber;
    private int numSteps;
    private BigInteger largestNumberInChain;
    private List<BigInteger> chain;
    
    //newly added fields
    private int numStepsOverall;
    private int numTotalOddNumbers;
    
    private int oddNumbersInChain;


    public AvoidingModGrowthRow(long startingNumber, int numSteps, BigInteger largestNumberInChain, List<BigInteger> chain,
    		int numStepsOverall, int numTotalOddNumbers, int oddNumbersInCurrentChain) {
        this.chain = chain;
        this.largestNumberInChain = largestNumberInChain;
        this.startingNumber = startingNumber;
        this.numSteps = numSteps;
        this.numTotalOddNumbers = numTotalOddNumbers;
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
    public BigInteger getLargestNumberInChain() {
    	return largestNumberInChain;
    }
    
    public List<BigInteger> getChain() {
    	return chain;
    }
    
    public int getNumStepsOverall() {
    	return numStepsOverall;
    }
    
    public int getNumTotalOddNumbers() {
    	return numTotalOddNumbers;
    }
    
    
    public int getOddNumbersInChain() {
    	return oddNumbersInChain;
    }
    


}
