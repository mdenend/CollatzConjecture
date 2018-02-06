package collatz.utils;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper class that is space efficient. Adds numbers > INT_MAX to a long mapping, all others are in an Int mapping.
 * Created by Matt Denend on 2/5/18.
 */
public class VisitedNumbers {
    private Set<Integer> visitedInts;
    private Set<Long> visitedLongs;

    public VisitedNumbers() {
        visitedInts = new HashSet<>();
        visitedLongs = new HashSet<>();
    }

    /**
     * Adds the appropriate BigInteger converted to long (as code will NEVER run numbers > LONG_MAX), then adds to right map.
     * @param bigNum
     */
    public void add(BigInteger bigNum){
        long number = bigNum.longValue();
        if (mustBeALong(number)){
            visitedLongs.add(number);
        } else {
            visitedInts.add((int)number);
        }
    }

    /**
     * Checks if VisitedNumbers has a number. Takes long because we iterate through all numbers as longs
     * @param number a LONG that we check either mapping to see if it's present.
     * @return
     */
    public boolean contains(long number) {
        return mustBeALong(number) ? visitedLongs.contains(number) : visitedInts.contains((int)number);
    }

    private boolean mustBeALong(long number) {
        return number > Integer.MAX_VALUE;
    }

}
