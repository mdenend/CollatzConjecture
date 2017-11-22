package collatz.helpers;

import collatz.compute.ComputeMods;

import java.math.BigInteger;



/**
 * Used for the updown mode. Since this mode is so different from the other modes, the helper is not at all related to ListSizeHelper.
 * There's only one constructor, and it's complex, as it has the same parameters but three different cases.
 * 1. This UpDownHelper was created when we find an odd number for the first time that's larger than some lower input. This sets the growsFrom and
 * growsFromLength parameters. These can be changed anytime later, provided the other growsFromLength is shorter than the current one. The
 * decaysTo, decaysToLength, decaysToOdd, and decaysToOddLength parameters are left unitialized, and won't be set until we run this odd number, in which
 * case, we set each of these numbers once.
 * 2. This UpDownHelper was created when the Collatz is being run on the input number, and we find a smaller number. We set the fact that we found this smaller
 * number in the constructor, and flag that we can't change it anymore. We still need to find a lower odd number, however. We also note that no numbers grows
 * into this number.
 * 3. Like case 2, but the lower number is also odd. We flag that neither can be changed.
 * Created by Matt Denend on 5/24/17.
 */
public class UpDownHelper{

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

    private BigInteger number; //the number that this UpDownHelper refers to.

    //the first integer lower than number, the length it takes to get from number to decaysTo, and a flag to indicate that we can't modify this exactly once.
    private BigInteger decaysTo;
    private int decaysToLength;
    private boolean decaysToLocked;


    //Like the above fields, but must also be odd.
    private BigInteger decaysToOdd;
    private int decaysToOddLength;
    private boolean decaysToOddLocked;


    //the number that a particular Collatz computation sequence starts from to reach this number, the number of steps, and a enum (state)
    //to flag whether or not such an integer has been found for this number.
    private BigInteger growsFrom;
    private int growsFromLength;
    private UpDownReverseStates state;

    /**
     * Complex constructor that needed a flag isDecaysTo, because two different constructors otherwise have the same input.
     * Case 1: When isDecaysTo is true, constructor if the helper is built from the inital decaysTo parameters.
     * This means that it can't decay from anything, and parameters are finalized after this is built. We also check to see
     * if the input number is odd. If so, we flag that we can't change that either.
     * Case 2: When isDecaysTo is false, the inputs are
     * @param otherChainNumber either the first number that it's decayed to, or the number that it's grown from.
     * @param lengthToOtherChainNumber the number of Collatz steps that it has taken us to construct this Helper.
     */
    public UpDownHelper(BigInteger number, BigInteger otherChainNumber, int lengthToOtherChainNumber, boolean isDecaysTo) {
        this.number = number;

        //these two default values should never be needed, but including them here anyways.
        decaysToOdd = BigInteger.valueOf(-1);
        decaysToOddLength = -1;

        decaysToOddLocked = false; //initially, only true if odd and decaysTo.

        //if we flag this as such, we note that number decaysTo the input otherChainNumber. We also check to see if it's odd.
        if (isDecaysTo) {
            this.decaysTo = otherChainNumber;
            this.decaysToLength = lengthToOtherChainNumber;
            growsFrom = null;
            growsFromLength = 0;
            state = UpDownReverseStates.IMPOSSIBLE;

            //if number is odd, also note that decaysToOdd is locked.
            if(decaysTo.mod(ComputeMods.TWO).compareTo(BigInteger.ONE) == 0) {
                decaysToOdd = otherChainNumber;
                decaysToOddLength = lengthToOtherChainNumber;
                decaysToOddLocked = true;
            }

        } else {
            growsFrom = otherChainNumber;
            growsFromLength = lengthToOtherChainNumber;
            state = UpDownReverseStates.FOUND;
        }
        decaysToLocked = isDecaysTo; //if we pass true as decaysTo, then can't modify decaysTo. Otherwise, we can modify it ONCE.
    }


    /**
      Don't use this, as it's only for the first attempt of UpDown, with the reverse collatz computation.
     * @param number
     * @param decaysTo
     * @param decaysToLength
     * @param growsFrom
     * @param growsFromLength
     * @param state
     */
    @Deprecated
    public UpDownHelper(BigInteger number, BigInteger decaysTo, int decaysToLength, BigInteger growsFrom, int growsFromLength,
                        UpDownReverseStates state) {
        this.state = state;
        this.growsFrom = growsFrom;
        this.growsFromLength = growsFromLength;
        this.decaysTo = decaysTo;
        this.decaysToLength = decaysToLength;
        this.number = number;
    }



    //a bunch of get methods.
    public BigInteger getNumber() {
        return number;
    }

    public UpDownReverseStates getState() {
        return state;
    }

    public BigInteger getDecaysTo() {
        return decaysTo;
    }

    public BigInteger getGrowsFrom() {
        return growsFrom;
    }

    public BigInteger getDecaysToOdd() {
        return decaysToOdd;
    }

    public int getGrowsFromLength() {
        return growsFromLength;
    }

    public int getDecaysToLength() {
        return decaysToLength;
    }

    public int getDecaysToOddLength() {
        return decaysToOddLength;
    }

    /**
     * This sets the decaysTo and decaysToLength only once, if the flag indicates that we haven't set these.
     * @param decaysTo A BigInteger that is less than number.
     * @param decaysToLength How many steps it takes for number to become decaysTo.
     */
    public void setDecaysToOnce(BigInteger decaysTo, int decaysToLength) {
        if (!decaysToLocked) {
            this.decaysTo = decaysTo;
            this.decaysToLength = decaysToLength;
            decaysToLocked = true;
        } else {
            System.err.println("Can't modify number " + number + " with decaysToValue of " + decaysTo + ": already locked.");
        }
    }
    /**
     * Exactly the same as setDecaysToOnce, except the input number must be odd.
     * @throws IllegalArgumentException if decaysTo is not an odd number.
     * @param decaysTo A BigInteger that is less than number.
     * @param decaysToLength How many steps it takes for number to become decaysTo.
     */
    public void setDecaysToOddOnce(BigInteger decaysTo, int decaysToLength) {
        if(decaysTo.mod(ComputeMods.TWO).compareTo(BigInteger.ONE) != 0) {
            throw new IllegalArgumentException("decaysToOdd is not odd!");
        }
        if (!decaysToOddLocked) {
            this.decaysToOdd = decaysTo;
            this.decaysToOddLength = decaysToLength;
            decaysToOddLocked = true;
        } else {
            System.err.println("Can't modify number " + number + " with decaysToValueOdd of " + decaysToOdd + ": already locked.");
        }
    }


    /**
     * If we've found some integer that growsFrom more than once (possible), we need to take the SHORTER LENGTH of the two
     * candidates.
     * @param growsFrom the candidate number.
     * @param growsFromLength the candidate length.
     */
    public void checkIfGrowsFromSmallerLength(BigInteger growsFrom, int growsFromLength) {
        if (state == UpDownReverseStates.FOUND && this.growsFromLength > growsFromLength) {
            this.growsFrom = growsFrom;
            this.growsFromLength = growsFromLength;
        }

    }

}
