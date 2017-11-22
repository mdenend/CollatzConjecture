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

    /*
    I'm just going to write out the steps to run method 4 here. I think there's some things in common with the other modes,
    but I still think this is sufficiently different from before.
    Given a number as input:
        1. Figure how how many steps it takes for the number to the number to decay. Much like the untildecay mode. Code will more or less borrow from here.
            The helper will store what number is reached, and the number of steps it takes.
        2. Figure out the reverse chain part. Here's what I've thought of so far.
            A. Let's use a Queue of BigIntegers, and a count that, if it exceeds a number, we should give up. (For example, it's impossible to find any
                lower number for 3, 7, or 19, so we'd say after count attempts, we stop.)
            B. Termination condition: Either we have had too many steps (make this parameter in options), or any number in our queue is lower than the input.
            C. The algorithm for reverse computation. Given a number:
                i. If the number is odd, multiply it by 2, and put it back into the queue.
                ii. If it's even, then depending on how it stands modulo 3:
                    a. If congruent modulo to 0, we throw this number away. After multiplying it by 2, it will always be congruent modulo 0, so the only path
                        is continuing to multiply itself by 2. So it's a dead end. Don't put it back into the queue.
                    b. If congruent modulo to 1, this is actually a split case. We put in two different numbers: One that undoes the odd operation of the Collatz Conjecture,
                        and another that is this number multiplied by 2. Put both numbers into the queue.
                    c. If congruent modulo to 2, just multiply it by 2 and put it back into the queue.
            D. The helper will store, for the reverse computation:
                i. The number (if found), otherwise it will remain as -1.
                ii. The number of iterations it took to find this number. Will be >length if ran out of iterations, or a specific number if all paths led to modulo 3.
                iii. The state, either found, impossible, or notfound. Impossible is when all numbers are mod 3, notfound means we hit the limit.
        3. The final output:
            Number | Mod (base) | Mod 3 | DecaysTo | DecaysToLength | growsFrom (if found) | growsFromLength
     */

public class UpDownComputation {
    public static final int printAmount = 1000;




    /**
     * Should not be called. This has the reverse Collatz Computation, which never terminates at 7 if the length is too long.
     * @param opts OptionsHelper that is built in the arguments.
     * @return resultSet to be printed.
     */
    @Deprecated
    public static Set<UpDownHelper> computeUpDown(OptionsHelper opts) {
        //incredibly unlikely that we'll run this methodology on sets for 2^32, would be really hard to analyze a billion numbers in a CSV!
        Set<UpDownHelper> resultSet = new LinkedHashSet<>((int)(opts.getHighNum()-opts.getLowNum())/2);

        //no visitedNumbers, we need to go to ALL numbers.
        for (long i = opts.getLowNum(); i <= opts.getHighNum(); i+= 2){
            //first, go forward, young lad! For now, going to call EXACTLY the same thing as we do for the untildecay helper, then store the results we want.
            UntilDecayListSizeHelper temp = new UntilDecayListSizeHelper(opts);
            ComputeMods.runCollatzWholeList(i, temp, null, opts);
            //now, get what we need from the UntilDecay case.
            List<BigInteger> tempChain = temp.getTopChain();
            int decaysToLength = tempChain.size();
            BigInteger decaysTo = tempChain.get(decaysToLength-1);

            //NOW, the big ugly. The REVERSE computation! Pass in the values we computed and return an UpDownHelper.
            UpDownHelper helper = reverseCollatzComputation(i, decaysTo, decaysToLength, opts);
            resultSet.add(helper);
        }
        return resultSet;
    }




    //this time, we are going to compute the UpDown and print everything every 1000 lines or so. Reason for parallelizing this is because the
    //file gets quite big.
    //Also, we'll have to do the Collatz Computation differently. We'll do the whole list computation but it needs to be it's own method because
    //we need to return a helper.
    //DO we flush every 1000 or so? Why not. I think a TreeMap would be best here because we can dynamically add stuff and flush it out, instead of having
    //the heap get overly full as we compute.

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
    public static void computeUpDownV2(OptionsHelper opts) {
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

    /*private void runCollatzWholeList(long num, ListSizeHelper helper, Set<BigInteger> visitedNumbers, OptionsHelper opts) {
        BigInteger number = BigInteger.valueOf(num);
        List<BigInteger> currentCollatzPath = new ArrayList<>(100);

        //condition is abstracted to the helper class because mode 3 has a slightly different termination condition than mode 2.
        while(helper.whileLoopCondition(number, currentCollatzPath, opts)) {
            currentCollatzPath.add(number);
            number = computeCollatzForward(number, visitedNumbers, opts);
        }
        currentCollatzPath.add(number); //add the last number as well. Useful for Mode 3.
        helper.checkIfNewChain(currentCollatzPath, num);
    }*/

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
        boolean decayed = false;
        boolean oddDecayed = false; //also want the lowest odd number in the helper as well, this will be useful too.

            //we need to run to completion here, so only one while condition.
            //the count == 0 condition was added to allow for 1 to work properly
            while (count == 0 || current.compareTo(BigInteger.ONE) > 0) {

                current = computeCollatzForward(current);
                count++;

                boolean isOdd = current.mod(ComputeMods.TWO).compareTo(BigInteger.ONE) == 0; //better to compute this once instead of many times.
                //first, consider current to actual number, see if it's decayed. Only need to do this once.
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
                //second, see if this number is odd AND is higher than the starting number. This is the growsFrom part.
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




    /*
    2. Figure out the reverse chain part. Here's what I've thought of so far.
            A. Let's use a Queue of BigIntegers, and a count that, if it exceeds a number, we should give up. (For example, it's impossible to find any
                lower number for 3, 7, or 19, so we'd say after count attempts, we stop.)
            B. Termination condition: Either we have had too many steps (make this parameter in options), or any number in our queue is lower than the input.
            C. The algorithm for reverse computation. Given a number:
                i. If the number is odd, multiply it by 2, and put it back into the queue.
                ii. If it's even, then depending on how it stands modulo 3:
                    a. If congruent modulo to 0, we throw this number away. After multiplying it by 2, it will always be congruent modulo 0, so the only path
                        is continuing to multiply itself by 2. So it's a dead end. Don't put it back into the queue.
                    b. If congruent modulo to 1, this is actually a split case. We put in two different numbers: One that undoes the odd operation of the Collatz Conjecture,
                        and another that is this number multiplied by 2. Put both numbers into the queue.
                    c. If congruent modulo to 2, just multiply it by 2 and put it back into the queue.
            D. The helper will store, for the reverse computation:
                i. The number (if found), otherwise it will remain as -1.
                ii. The number of iterations it took to find this number. Will be >length if ran out of iterations, or a specific number if all paths led to modulo 3.
                iii. The state, either found, impossible, or notfound. Impossible is when all numbers are mod 3, notfound means we hit the limit.

     */

    //This is the attempt at the reverse Collatz Computation. It failed spectacularly.
    private static UpDownHelper reverseCollatzComputation(long i, BigInteger decaysTo, int decaysToLength, OptionsHelper opts) {
        Queue<BigInteger> queue = new LinkedList<>();
        BigInteger number = BigInteger.valueOf(i);
        queue.add(number);
        boolean smallerNumberDetected = false;
        BigInteger smallerNumber = BigInteger.valueOf(-1);
        int count = 0;
        if (opts.isReverseTickFlag() && number.mod(ComputeMods.THREE).compareTo(BigInteger.ONE) == 0){
            System.out.println("Checking odd number congruent to 1 mod 3: " + number);
        }
        Queue<BigInteger> temp = new LinkedList<>();

        while(!queue.isEmpty() && !smallerNumberDetected && count < opts.getReverseLimit()) {


            while (!queue.isEmpty()) {
                BigInteger check = queue.remove();
                //if odd, we must multiply by two.
                if (check.mod(ComputeMods.TWO).compareTo(BigInteger.ONE) == 0) {
                    temp.add(check.multiply(ComputeMods.TWO));
                } else {
                    //mod 2 case: multiply it by two and put it back into the queue.
                    if (check.mod(ComputeMods.THREE).compareTo(ComputeMods.TWO) == 0) {
                        temp.add(check.multiply(ComputeMods.TWO));

                    //mod 1 case: need to split this into two cases. Also, this is the time we check to see if we have a smaller number.
                    } else if (check.mod(ComputeMods.THREE).compareTo(BigInteger.ONE) == 0) {
                        temp.add(check.multiply(ComputeMods.TWO));
                        BigInteger vIBI = check.subtract(BigInteger.ONE).divide(ComputeMods.THREE);
                        if (vIBI.compareTo(number) <= 0) {
                            smallerNumberDetected = true;
                            smallerNumber = vIBI;
                        }
                        temp.add(vIBI);
                    } //else, mod 0 case, don't add back into the queue.
                }
            }
            queue.addAll(temp);
            temp.clear();
            count++;
        }
        UpDownReverseStates state;
        if (queue.isEmpty()) { //can only trigger if truly impossible.
            state = UpDownReverseStates.IMPOSSIBLE;
        } else if (smallerNumberDetected) { //want found to come before notfound in case if we detect a smaller number.
            state = UpDownReverseStates.FOUND;
        } else {
            state = UpDownReverseStates.NOT_FOUND;
        }

        return new UpDownHelper(number, decaysTo, decaysToLength, smallerNumber, count, state);
    }

    /*
    public static UntilDecayListSizeHelper computeLongestGrowth(OptionsHelper opts) {
        UntilDecayListSizeHelper helper = new UntilDecayListSizeHelper(opts);

        Set<BigInteger> visitedNumbers = new HashSet<>();
        //like before, we want our lower number to be odd, and higher number to be even.
        for (long i = opts.getLowNum(); i <= opts.getHighNum(); i += 2) {
            //if time efficient, we'll avoid computation if number already visited.
            if (opts.isTimeEffFlag()) {
                if (visitedNumbers.contains(BigInteger.valueOf(i))) {
                    continue;
                }
            }
            runCollatzWholeList(i, helper, visitedNumbers, opts);
        }
        return helper;

    }
     */
}
