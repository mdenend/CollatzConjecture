package collatz.helpers;

import collatz.utils.OptionsHelper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * MultiBaseListSizeHelper is a class that builds objects that store a couple of bits of information:
 * 1. The longest chain that has currently been computed (from superclass ListSizeHelper)
 * 2. Which number has that longest chain (from superclass ListSizeHelper).
 *
 * It also stores other information that is used during a run of the Collatz Conjecture of a number:
 * 1. currentLowIndex: The previous index which hit modulo base equals the number we're avoiding.
 * 2. longestLowIndex: The low index which hit modulo base equals the number we're avoiding for the longest chain in a specific number so far.
 * 3. longestHighIndex: The high index which hit modulo base equals the number we're avoiding for the longest chain in a specific number so far.
 * 4. difference: Shorthand for longestHighIndex - longestLowIndex.
 * Created by Matthew Denend on 3/17/17.
 */
public class MultiBaseListSizeHelper extends ListSizeHelper{



    //these four numbers are used to track locations of the longest chain avoiding x mod 8 for some x. They are indices of the entire
    //chain, which is done to allow multiple bases to be checked at the same time.
    //see above JavaDoc comment for the whole class.
    int currentLowIndex = 0;
    int longestLowIndex = 0;
    int longestHighIndex = 0;
    int difference = 0;

    //Do we ever want to use this field?? Probably not.
    //private Set<Integer> baseAvoiding;

    /**
     * This constructor exists for the MultiBaseRecordTrackingListSizeHelper to extend, because we don't need a first cell in this case.
     * @param opts OptionsHelper.
     */
    public MultiBaseListSizeHelper(OptionsHelper opts) {
        super(opts);
    }


    //constructor should be exactly the same. I just called super().
    public MultiBaseListSizeHelper(OptionsHelper opts, Set<Integer> baseAvoiding) {
        super(opts, "\"Number range: " + opts.getLowNum() + "-" + opts.getHighNum() + opts.LS +
                "Base is "+ opts.getOutputBase() + ", avoiding mod(s) " + OptionsHelper.avoidBasesSetToString(baseAvoiding) + opts.LS);
    }



    /**
     * This is called when the Collatz Conjecture computation has converged to 1 for given currentNumber, and determines if the
     * new chain is longer than the currently stored chain. If so, build a new chain that has the numbers from longestLowIndex
     * to longestHighIndex from the collatzSteps array (the chain of the computed numbers in the collatzConjecture from currentNumber
     * to 1) and replace the old top chain. If tied in size, just keep the old chain. (I may fix this later.)
     * This overrides the parent class.
     * @param collatzSteps The run of the Collatz Conjecture from currentNumber to 1.
     * @param currentNumber The input number that we start our Collatz Conjecture computation from. collatzSteps.get(0) is equal to this number.
     */
    @Override
    public void checkIfNewChain(List<BigInteger> collatzSteps, long currentNumber) {
        int newSize = longestHighIndex - longestLowIndex + 1;
        if (newSize > topChain.size()) {
            topChain = new ArrayList<BigInteger>(newSize);
            for (int i = longestLowIndex; i <= longestHighIndex; i++) {
                topChain.add(collatzSteps.get(i));
            }
            longestChainNumber = currentNumber;
        }

    }

    /**
     * Only exists for the subclass AvoidingModGrowthHelper to override. Calling this throws an exception.
     * @param startingNumber
     * @param chain
     * @param totalChainLength
     * @param numOddNumbers
     */
    public void checkIfNewChainWithOddNumbers(long startingNumber, List<BigInteger> chain, int totalChainLength, int numOddNumbers) {
        throw new UnsupportedOperationException("No need for odd numbers in mode 0");
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
    public void compareCurrentChainToLongestChain(int highIndex) {
        int newDifference = highIndex - currentLowIndex;
        if (highIndex - currentLowIndex  > difference) {
            difference = newDifference;
            longestHighIndex = highIndex;
            longestLowIndex = currentLowIndex;
        }
        currentLowIndex = highIndex;
    }

    /**
     * Only exists for the subclass AvoidingModGrowthHelper to override. Calling this throws an exception.
     * @param highIndex
     * @param oddNums
     */
    public void compareCurrentChainToLongestChainWithOddNumbers(int highIndex, int oddNums) {
        throw new UnsupportedOperationException("No need for odd numbers in mode 0");
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
    }





}
