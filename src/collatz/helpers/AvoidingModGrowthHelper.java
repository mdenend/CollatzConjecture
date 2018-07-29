package collatz.helpers;

import collatz.utils.AvoidingModGrowthRow;
import collatz.utils.OptionsHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * This is much like the MultiBaseListSizeHelper, except it tracks odd numbers, and stores rows instead of a single chain.
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
    
    //first number keeps track of number of odd numbers in chain. 
    //Second one keeps track what count of odd numbers is. It gets reset when we hit a mod number. 
    private int oddNumbersInChain = 0;
    private int currentOddChainCount = 0;

    public AvoidingModGrowthHelper(OptionsHelper opts) {
    	super(opts);
        modGrowthRows = new ArrayList<>();
        
    }



    @Override
    public void checkIfNewChainWithOddNumbers(long startingNumber, List<BigInteger> chain, int totalChainLength, int numOddNumbers) {
        if (difference > currentLongestChainLength) {
            currentLongestChainLength = difference;
            List<BigInteger> chainToPrint = new ArrayList<>();
            for (int i = longestLowIndex; i <= longestHighIndex; i++) {
            	chainToPrint.add(chain.get(i));
            }
            modGrowthRows.add(new AvoidingModGrowthRow(startingNumber, difference, currentLargestNumber, chainToPrint, 
            		totalChainLength, numOddNumbers, oddNumbersInChain));
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
    public void compareCurrentChainToLongestChain(int highIndex) {
        int newDifference = highIndex - currentLowIndex;
        if (newDifference  > difference) {
            difference = newDifference;
            longestHighIndex = highIndex;
            longestLowIndex = currentLowIndex;
            oddNumbersInChain = currentOddChainCount;
        }
        currentLowIndex = highIndex;
        currentOddChainCount = 0;
    }
    
    /**
     * Resets the dynamically changing counters longestLowIndex, longestHighIndex, currentLowIndex, and difference
     * every time we finish computing the Collatz Conjecture on an input number.
     */
    @Override
    public void resetCounters() {
        super.resetCounters();
        oddNumbersInChain = 0;
        currentOddChainCount = 0;
    }
    

    
    public List<AvoidingModGrowthRow> getModGrowthRows() {
    	return modGrowthRows;
    }
    
    @Override
    public void incrementChainOddNumbers() {
    	currentOddChainCount++;
    }

    
    
    
}



