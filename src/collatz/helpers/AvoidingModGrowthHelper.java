package collatz.helpers;

import collatz.utils.AvoidingModGrowthRow;
import collatz.utils.OptionsHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * This is much like the MultiBaseListSizeHelper, except it was rewritten using Longs instead of BigIntegers, and includes odd number
 * counting as well.
 *
 * Created by Matthew Denend on 10/25/17.
 */

//TODO: This class is going to extend MultiBaseListSizeHelper, and we'll delete the other class we created.
    //The only time that I'll ever have space issues is in the visitedNumbers Map. I can convert this...
public class AvoidingModGrowthHelper extends MultiBaseListSizeHelper{
    private int currentLongestChainLength;
    
    private List<AvoidingModGrowthRow> modGrowthRows;
    
  //used to help count the current number only.
    //private BigInteger currentLargestNumber; //TODO: Add this back in later.
    //private long currentLargestNumber;
    private int numOddNumbersInChain = 0;

    public AvoidingModGrowthHelper(OptionsHelper opts) {
    	super(opts);
        modGrowthRows = new ArrayList<>();
        
    }


    /**
     * Need to change this to BigInteger sometime in the future.
     * @param startingNumber Change to BigInt.
     * @param chain Change to List<BigInt>.
     */
    @Override
    public void checkIfNewChainWithOddNumbers(long startingNumber, List<BigInteger> chain, int totalChainLength, int numOddNumbers) {
        if (difference > currentLongestChainLength) {
            currentLongestChainLength = difference;
            List<BigInteger> chainToPrint = new ArrayList<>();
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
    @Override
    public void compareCurrentChainToLongestChainWithOddNumbers(int highIndex, int oddNums) {
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
    @Override
    public void resetCounters() {
        super.resetCounters();
        numOddNumbersInChain = 0;
    }
    

    
    public List<AvoidingModGrowthRow> getModGrowthRows() {
    	return modGrowthRows;
    }
    
    
    
    
}



