package collatz.compute;

import collatz.helpers.UntilDecayListSizeHelper;
import collatz.helpers.UpDownHelper;
import collatz.helpers.UpDownReverseStates;
import collatz.output.PrintUpDown;
import collatz.utils.OptionsHelper;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;


//TODO: Write stuff here.
/**
 * This class contains a method in which you can compute the updown mode. This mode is different from all other modes because of the fact that it
 * outputs an entirely different sheet. It doesn't have a longest chain; instead, it finds, for EVERY ODD NUMBER, what the first
 * number lower than it is, and what lower odd number grows to it. If the map has too many elements in it, the heap will get too full, so this
 * method prints after a certain number of elements (printAmount), clearing out the map of those elements.
 * Created by Matt Denend on 5/25/17.
 */

public class UpDownComputation {
    public static final int printAmount = 1000;


    /**
     * Takes an input sequence of odd numbers and runs the Collatz Conjecture on all of them. For each number, we run Collatz forward.
     * We look for three things while running this sequence:
     *      1. A number that is lower than the initial number. We store that, as well as how many steps we took to get from the initial number to this one.
     *      2. Same as 1, but the first odd number instead.
     *      3. Any odd number that is greater than the initial number will be stored as well, presuming this odd number hasn't been found already with a shorter length.
     * Also, as of note, the printing is interlaced with this computation method, unlike other modes. This is to keep the size of the map from growing too large.
     * Every 1000 numbers, we print to our output file and remove numbers that are printed, since we don't need them anymore.
     * @param opts The OptionsHelper built from Main.
     */
    public static void computeUpDown(OptionsHelper opts) {
        Map<BigInteger, UpDownHelper> infoToPrint = new TreeMap<>();
        long lowBound = opts.getLowNum();
        long i = -1;
        try {
            PrintUpDown output = new PrintUpDown(opts);
            for (i = opts.getLowNum(); i <= opts.getHighNum(); i+= 2) {
                computeCollatzBothDirections(i, infoToPrint);
                //I might change the printAmount later, make it a parameter or something.
                if (i % printAmount == printAmount-1) {
                    output.flushToOutputFile(infoToPrint, lowBound, i);
                    lowBound = i;
                }
            }
            //NOTE: This will only get those in the range. Is that OK? Do I want outside stragglers too?
            output.flushToOutputFile(infoToPrint, lowBound, i);
            output.closeStream();
            System.out.println("Number of numbers in map before thrown away:" + infoToPrint.size());
        } catch (IOException e) {
            System.err.println("Failed at number" + i);
            e.printStackTrace();
        }
    }

    //same as in ComputeCollatz but much simpler as we just need to compute the actual number change, and not store visited numbers.
    private static BigInteger computeCollatzForward(BigInteger number) {
        if (number.mod(ComputeMods.TWO).compareTo(BigInteger.ZERO) == 0) {
            return number.divide(ComputeMods.TWO);
        }
        else {
            return number.multiply(ComputeMods.THREE).add(BigInteger.ONE);
        }
    }

    //this is the helper called for each number.
    private static void computeCollatzBothDirections(long num, Map<BigInteger, UpDownHelper> m) {
        BigInteger number = BigInteger.valueOf(num);
        BigInteger current = number;
        UpDownHelper thisNumHelp;
        int count = 0;
        boolean decayed = false; //flag to stop checking if a number is smaller than the first number.
        boolean oddDecayed = false; //also want the lowest odd number in the helper as well, this will be useful too.

            //we need to run to completion here, so only one while condition.
            //the count == 0 condition was added to allow for 1 to work properly
            while (count == 0 || current.compareTo(BigInteger.ONE) > 0) {

                current = computeCollatzForward(current);
                count++;

                boolean isOdd = current.mod(ComputeMods.TWO).compareTo(BigInteger.ONE) == 0; //comparison done here for both Case 1 and 1.5

                //Case 1: consider current to starting number, see if it's decayed. Only need to do this check once, hence the decayed flag.
                if (!decayed && current.compareTo(number) <= 0) {
                    if (!m.containsKey(number)) {
                        if(isOdd)
                            oddDecayed = true;
                        thisNumHelp = new UpDownHelper(number, current, count, true);
                        m.put(number, thisNumHelp);
                    } else {
                        thisNumHelp = m.get(number);
                        thisNumHelp.setDecaysToOnce(current, count);
                    }
                    decayed = true;
                }

                //Case 1.5: If oddDecayed hasn't happened yet, then we set it here. Note that this will only be called if
                //our initial number isn't odd... the constructor for UpDownHelper will initialize if the number is odd.
                if (!oddDecayed && isOdd && current.compareTo(number) <= 0) {
                    thisNumHelp = m.get(number);
                    thisNumHelp.setDecaysToOddOnce(current, count);
                    oddDecayed = true;
                }
                //Case 2: see if this number is odd AND is higher than the starting number. This is the growsFrom part.
                //this can happen any amount of times.
                if (current.compareTo(number) >= 0 && isOdd) {
                    if (!m.containsKey(current)) {
                        thisNumHelp = new UpDownHelper(current, number, count, false);
                        m.put(current, thisNumHelp);
                    } else {
                        thisNumHelp = m.get(current);
                        thisNumHelp.checkIfGrowsFromSmallerLength(number, count);
                    }
                }



            }


    }
}
