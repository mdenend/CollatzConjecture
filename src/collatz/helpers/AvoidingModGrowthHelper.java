package collatz.helpers;

import collatz.utils.AvoidingModGrowthRow;
import collatz.utils.OptionsHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mad4672 on 10/25/17.
 */
public class AvoidingModGrowthHelper{
    private int currentLongestChain;
    
    private List<AvoidingModGrowthRow> modGrowthRows;
    
  //used to help count the current number only.
    private int currentLowIndex = 0;

    //for the current number, stores these numbers.
    private int longestLowIndex = 0; //ex: Index 0 low, and Index 4 high. That's 5 elements. So... high - low + 1 to compare to the size of the top chain.
    private int longestHighIndex = 0;
    private int difference = 0;
    //private BigInteger currentLargestNumber; //TODO: Add this back in later.
    private long currentLargestNumber;
    private int numOddNumbersInChain = 0;

    public AvoidingModGrowthHelper(OptionsHelper opts) {
    	modGrowthRows = new ArrayList<>();
        //not terribly sure yet how the opts will come into play yet.
    	//currentLargestNumber = BigInteger.ZERO;
    	currentLargestNumber = 0;
        
    }

    /*
    public void addToModGrowthRows(BigInteger startingNumber, int numSteps, BigInteger largestNumberInChain, List<BigInteger> chain) {
        modGrowthRows.add(new AvoidingModGrowthRow(startingNumber, numSteps, largestNumberInChain, chain));
    }

    public int getCurrentLongestChain() {
        return currentLongestChain;
    }

    public void setCurrentLongestChain(int currentLongestChain) {
        this.currentLongestChain = currentLongestChain;
    }*/

    /**
     * Need to change this to BigInteger sometime in the future.
     * @param startingNumber Change to BigInt.
     * @param chain Change to List<BigInt>.
     */
    public void checkIfNewLongestChain(long startingNumber, List<Long> chain, int totalChainLength, int numOddNumbers) {
        if (difference > currentLongestChain) {
            currentLongestChain = difference;
            List<Long> chainToPrint = new ArrayList<Long>();
            for (int i = longestLowIndex; i <= longestHighIndex; i++) {
            	chainToPrint.add(chain.get(i));
            }
            modGrowthRows.add(new AvoidingModGrowthRow(startingNumber, difference, currentLargestNumber, chainToPrint, 
            		totalChainLength, numOddNumbers, numOddNumbersInChain));
        }

    }
    
    /**
     * This is dynamically called every time that, during a run of the Collatz Conjecture, when the current number mod base equals
     * a number that we are trying to avoid. This will check to see if this chain is longer than the previous longest chain found during
     * the run with the number. If it is, we note the indices of this chain. Things we do when we find a longer chain:
     *      -Store the difference, which was greater than the previous difference, as a field to check each time we compute.
     *      -Store the longestLowIndex using the already set currentLowIndex and the longestHighIndex as the passed parameter highIndex
     * Also, everytime this method is run, regardless of the outcome, the currentLowIndex is set to the highIndex, as we restart our search from this point.
     * @param highIndex The index in which we found that the current number in the Collatz Conjecture computation hits current number mod base equals the number
     *                  we're trying to avoid.
     */
    public void compareCurrentChainToLongestChain(int highIndex, int oddNums) {
        int newDifference = highIndex - currentLowIndex;
        if (newDifference  > difference) {
            difference = newDifference;
            longestHighIndex = highIndex;
            longestLowIndex = currentLowIndex;
            numOddNumbersInChain = oddNums;
        }
        currentLowIndex = highIndex;
    }
    
    /**
     * Resets the dynamically changing counters longestLowIndex, longestHighIndex, currentLowIndex, and difference
     * every time we finish computing the Collatz Conjecture on an input number.
     */
    public void resetCounters() {
        longestLowIndex = 0;
        longestHighIndex = 0;
        currentLowIndex = 0;
        difference = 0;
        numOddNumbersInChain = 0;
    }
    
    /*public void checkIfNewLargestNumber(BigInteger other) {
    	if (currentLargestNumber.compareTo(other) < 0) {
    		currentLargestNumber = other;
    	}
    }*/
    
    public void checkIfNewLargestNumber(long other) {
    	if (currentLargestNumber < other) {
    		currentLargestNumber = other;
    	}
    }
    
    public List<AvoidingModGrowthRow> getModGrowthRows() {
    	return modGrowthRows;
    }
    
    
    
    
}



