package collatz.helpers;

import collatz.utils.OptionsHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

//TODO: This could use a bit more commenting.
/**
 * This class serves two different purposes:
 * 1. It is the helper for mode 1; the whole list mode.
 * 2. It is the root for helpers in modes 0 and 2. The fields and methods are extended to both of these mode helpers.
 * Created by Matt Denend on 5/23/17.
 */
public class ListSizeHelper {

    List<BigInteger> topChain; //the chain of the longest number. not private so modes 0 and 2 can interact with it.
    long longestChainNumber; //which number has the longest chain.
    private String firstCellInitial; //what prints in the first cell of the spreadsheet.



    public ListSizeHelper(OptionsHelper opts){
        this(opts, "\"Number range: " + opts.getLowNum() + "-" + opts.getHighNum() + opts.LS +
                "Base is " + opts.getOutputBase() + opts.LS);
    }
    //Number range: " + opts.getLowNum() + "-" + opts.getHighNum() + "opts.LS" +Base is 8
    /*
Number range: " + opts.getLowNum() + "-" + opts.getHighNum() + "opts.LS" +
                "Base is "+ opts.getOutputBase() + ", avoiding mod(s) " + setString + "opts.LS" +
                "Longest chain is length " + m.getTopChain().size() + "opts.LS" +
                "First number that has this chain: " + addCommas(m.getLongestChainNumber()) + "opts.LS"

 */
    public ListSizeHelper(OptionsHelper opts, String firstCell) {
        topChain = new ArrayList<>();
        //for some reason, I have to add something to this list, otherwise Java forces size to be zero, throwing an exception.
        //If topChain returns 0 (which is never a valid number in the Collatz Conjecture), then something's wrong.
        topChain.add(BigInteger.ZERO);
        longestChainNumber = -1; //means an error if this is ever the number.
        firstCellInitial = firstCell;
    }



    /**
     * This is called when the Collatz Conjecture computation has converged to 1 for given currentNumber, and determines if the
     * new chain is longer than the currently stored chain. If so, build a new chain from the entire list
     * and replace the old top chain. If tied in size, just keep the old chain. (I may fix this later.)
     * @param collatzSteps The run of the Collatz Conjecture from currentNumber to 1.
     * @param currentNumber The input number that we start our Collatz Conjecture computation from. collatzSteps.get(0) is equal to this number.
     */
    public void checkIfNewChain(List<BigInteger> collatzSteps, long currentNumber) {
        if (collatzSteps.size() > topChain.size()) {
            topChain = collatzSteps;
            longestChainNumber = currentNumber;
        }

    }

    /**
     * Returns the topChain, or the longest chain avoiding mod base congruent modulo to avoidBase.
     * @return topChain, the longest chain avoiding mod base congruent modulo to avoidBase.
     */
    public List<BigInteger> getTopChain() {
        return topChain;
    }

    /**
     * Returns the number that has the longest chain avoiding mod base congruent modulo to avoidBase.
     * @return longestChainNumber, the number that has the longest chain avoiding mod base congruent modulo to avoidBase.
     */
    public long getLongestChainNumber() {
        return longestChainNumber;
    }



    public String getFirstCellInitial() {
        return firstCellInitial;
    }

    /**
     * This is called in runCollatzWholeList for mode 1. The termination condition is to see if the number is larger than 1,
     * or if the currentCollatzPath is greater than the number of steps.
     * @param number The current number in the path.
     * @param currentCollatzPath The currentCollatzPath.
     * @param opts Options for this run.
     * @return true if the while loop should continue, false if not.
     */
    public boolean whileLoopCondition(BigInteger number, List<BigInteger> currentCollatzPath, OptionsHelper opts) {
        return number.compareTo(BigInteger.ONE) > 0 && currentCollatzPath.size() < opts.getNumSteps();
    }


}
