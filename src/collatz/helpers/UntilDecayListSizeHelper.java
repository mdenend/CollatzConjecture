package collatz.helpers;

import collatz.utils.OptionsHelper;

import java.math.BigInteger;
import java.util.List;

/**
 * This Helper is exactly the same as the Whole List Helper, except for the fact that the while loop condition is different.
 * Created by Matt Denend on 5/23/17.
 */
public class UntilDecayListSizeHelper extends ListSizeHelper {

    public UntilDecayListSizeHelper(OptionsHelper opts) {
        super(opts); //change input text? Not too sure yet.
    }

    @Override
    /**
     * WhileLoopCondition returns true if the following are true:
     * 1. The super condition of the number being greater than one and smaller than max path size are true, AND
     * 2. Either:
     *  a. The path size is 0, meaning no elements have been inserted yet.
     *  b. The given number is larger than the first number in the list.
     */
    public boolean whileLoopCondition(BigInteger number, List<BigInteger> currentCollatzPath, OptionsHelper opts) {
        return super.whileLoopCondition(number, currentCollatzPath, opts) &&
                (currentCollatzPath.size() == 0 || number.compareTo(currentCollatzPath.get(0)) > 0);
    }

}
