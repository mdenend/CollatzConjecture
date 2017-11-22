package collatz.compute;

import collatz.helpers.AvoidingModGrowthHelper;
import collatz.helpers.ListSizeHelper;
import collatz.helpers.MultiBaseListSizeHelper;
import collatz.helpers.UntilDecayListSizeHelper;
import collatz.utils.ModAvoidanceWrapper;
import collatz.utils.OptionsHelper;

import java.math.BigInteger;
import java.util.*;

/**
 * This class contains three key static methods:
 * 1. computeModsAvoidingMultipleBases, which feeds in the OptionsHelper and runs the Collatz Conjecture on the given range of numbers.
 * 2. computeLongestList, just sees what the longest chain in the given block of numbers is.
 * 3. computeLongestGrowth, this sees which chain has the longest length before it ends at a number lower than the start.
 * Created by Matt Denend on 3/14/17.
 */
public class ComputeMods {

    //defines big ints for 2 and 3, which are used in the Collatz Conjecture.
    public static final BigInteger TWO = BigInteger.valueOf(2);
    public static final BigInteger THREE = BigInteger.valueOf(3);

    /*
    //Both of the following methods are the OLD WAY, DON'T CALL EITHER OF THESE!
    //works ONLY for single node. Don't use if you want to test for several nodes!
    private static LinkedList<BigInteger> numModStepsOneNode (long num, long b, long n) {
        LinkedList<BigInteger> longestPath = new LinkedList<BigInteger>();
        LinkedList<BigInteger> currentPath = new LinkedList<BigInteger>();
        BigInteger number = BigInteger.valueOf(num);
        BigInteger node = BigInteger.valueOf(n);
        BigInteger base = BigInteger.valueOf(b);
        while (number.compareTo(BigInteger.ONE) > 0) {
            //long i = number % base;
            BigInteger mod = number.mod(base);
            currentPath.addLast(number);
            if (mod.compareTo(node) == 0) {
                if (currentPath.size() > longestPath.size()) {
                    longestPath = currentPath;
                }
                currentPath = new LinkedList<BigInteger>();
            }
            //hoo boy...
            //number = ((number % 2 == 0) ? number/2 : number * 3 + 1);
            if (number.mod(TWO).compareTo(BigInteger.ZERO) == 0) {
                number = number.divide(TWO);
            }
            else {
                number = number.multiply(THREE);
                number = number.add(BigInteger.ONE);
            }

        }
        return longestPath;
    }
    */

    //replaced with the method that does time efficiency, since the methods are almost the same. Kept this here for legacy/troubleshooting.
    /*
    private static void numModStepsMultipleNodes(long num, BigInteger base,
                                                 Map<Set<Integer>, MultiBaseListSizeHelper> basesMapping,
                                                 ModAvoidanceWrapper avModsHelp) {
        BigInteger number = BigInteger.valueOf(num);
        List<BigInteger> currentCollatzPath = new ArrayList<>(2000); //should never need to exceed this size.
        int index = 0;
        while (number.compareTo(BigInteger.ONE) > 0) {
            int modResult = number.mod(base).intValue();
            //first, add the current number to the currentCollatzPath. Then, see if this base hits any of the bases we're trying to avoid.
            currentCollatzPath.add(number);
            List<Set<Integer>> checkBases = avModsHelp.getMappedSets(modResult);
            if (checkBases != null) {
                for (Set<Integer> s : checkBases) {
                    MultiBaseListSizeHelper m = basesMapping.get(s);
                    m.compareCurrentChainToLongestChain(index);
                }
            }
            index++; //increment the counter for the collatzPath.

            //now run the collatz computation on the number.
            if (number.mod(TWO).compareTo(BigInteger.ZERO) == 0) {
                number = number.divide(TWO);

            } else {
                number = number.multiply(THREE);
                number = number.add(BigInteger.ONE);
            }

        }

        //NOW, we see if the new longest path exceeds the old ones, and reset the counters.
        Set<Set<Integer>> baseKeySet = basesMapping.keySet();
        for (Set<Integer> s : baseKeySet) {
            MultiBaseListSizeHelper m = basesMapping.get(s);
            m.checkIfNewChain(currentCollatzPath, num);
            m.resetCounters();
        }

    }
    */

    // takes an input number and runs the collatz conjecture on it until it converges to 1.
    // if we have timeeff flag and space is enough, then we can store values we've visited already to avoid visiting them again.
    //
    private static void numModStepsMultipleNodesVisitedNumbers(long num, Map<Set<Integer>, MultiBaseListSizeHelper> basesMapping,
                                                               ModAvoidanceWrapper avModsHelp, Set<BigInteger> visitedNumbers,
                                                               OptionsHelper opts) {
        BigInteger number = BigInteger.valueOf(num);
        List<BigInteger> currentCollatzPath = new ArrayList<>(2000); //should never need to exceed this size.
        int index = 0;
        //either the number is one, or we finish executing after a certain number of steps.
        while(number.compareTo(BigInteger.ONE) > 0 && index < opts.getNumSteps()) {
            int modResult = number.mod(opts.getBaseBigInt()).intValue();
            //first, add the current number to the currentCollatzPath. Then, see if this base hits any of the bases we're trying to avoid.
            currentCollatzPath.add(number);
            List<Set<Integer>> checkBases = avModsHelp.getMappedSets(modResult);
            if (checkBases != null) {
                for (Set<Integer> s : checkBases) {
                    MultiBaseListSizeHelper m = basesMapping.get(s);
                    m.compareCurrentChainToLongestChain(index);
                }
            }
            index++; //increment the counter for the collatzPath.
            number = computeCollatzForward(number, visitedNumbers, opts);
        }
        //NOW, we see if the new longest path exceeds the old ones, and reset the counters.
        //Also, check if our longest chain that converges to 1 ends up exceeding any other chain.
        Set<Set<Integer>> baseKeySet = basesMapping.keySet();
        for (Set<Integer> s : baseKeySet) {
            MultiBaseListSizeHelper m = basesMapping.get(s);
            m.compareCurrentChainToLongestChain(index-1); //check if we have a new chain one more time.
            m.checkIfNewChain(currentCollatzPath, num);
            m.resetCounters();
        }
    }

    //sees if the current integer is in between the lowest value in the range, and the highest value in the range. If so, we want to add it to the range.
    //only used when we have the timeefficient solution, as we know these numbers can be skipped.
    private static boolean inBetween(BigInteger current, BigInteger low, BigInteger high) {
        return current.compareTo(low) >= 0 && current.compareTo(high) <= 0;
    }
    

    //modularized this because it's going to be called a couple other times.
    private static BigInteger computeCollatzForward(BigInteger number, Set<BigInteger> visitedNumbers, OptionsHelper opts) {
        BigInteger result;
        if (number.mod(TWO).compareTo(BigInteger.ZERO) == 0) {
            result = number.divide(TWO);
        }
        else {
            //option only needed for time efficient solution.
            if (opts.isTimeEffFlag()) {
                //only check if number is odd... as we're only analyzing odd numbers.
                if (inBetween(number, opts.getLowNumBigInteger(), opts.getHighNumBigInteger())) {
                    visitedNumbers.add(number);
                }
            }
            number = number.multiply(THREE);
            result = number.add(BigInteger.ONE);
        }
        return result;
    }

    
  //modularized this because it's going to be called a couple other times.
    //separate method for longs.
    private static long computeCollatzForward(long number, Set<Long> visitedNumbers, OptionsHelper opts) {
        long result;
        if (number % 2 == 0) {
            result = number / 2;
        }
        else {
            //option only needed for time efficient solution.
            if (opts.isTimeEffFlag()) {
                //only check if number is odd... as we're only analyzing odd numbers.
                if (inBetween(number, opts.getLowNum(), opts.getHighNum())) {
                    visitedNumbers.add(number);
                }
            }            
            result = (number * 3) + 1;
        }
        return result;
    }
    //sees if the current integer is in between the lowest value in the range, and the highest value in the range. If so, we want to add it to the range.
    //only used when we have the timeefficient solution, as we know these numbers can be skipped.
    private static boolean inBetween(long current, long low, long high) {
        return current >= low && current <= high;
    }

    
    /**
     * This is the main method to call a computation of the Collatz Conjecture for mode 0. This method will continue to run from the low number in opts
     * to the high number in opts.
     * @param opts The OptionsHelper that has the options flagged for the arguments that we input into the command line.
     * @return A Map that has keys that are sets of integers, which are the bases that were avoided, and these each point to a MultiBaseListSizeHelper,
     * which helps store and process our results. See MultiBaseListSizeHelper class for more details.
     */
    public static Map<Set<Integer>, MultiBaseListSizeHelper> computeModsAvoidingMultipleBases(OptionsHelper opts) {
        
        Map<Set<Integer>, MultiBaseListSizeHelper> basesMapping = new LinkedHashMap<>();
        Set<Set<Integer>> avoidanceMods = opts.getAvoidBases();

        //this builds a map that, when an integer is fed into this object (w/ getMappedSets), any sets that have the integer in them are returned,
        // and speeds up the computation significantly.
        ModAvoidanceWrapper avModsHelp = ModAvoidanceWrapper.getWrapping(avoidanceMods);

        for (Set<Integer> s: avoidanceMods) {
            basesMapping.put(s, new MultiBaseListSizeHelper(opts, s));
        }

        //these three are only used in time efficient solution, not otherwise, but not huge if not being used.
        Set<BigInteger> visitedNumbers = new HashSet<>();// if we've seen a number in the collatz conjecture already, then why visit it again??

        //like before, we want our lower number to be odd, and higher number to be even.
        for (long i = opts.getLowNum(); i <= opts.getHighNum(); i += 2) {

            //if space efficient, we'll avoid computation if number already visited.
            if (opts.isTimeEffFlag()) {
                if (visitedNumbers.contains(BigInteger.valueOf(i))) {
                    continue;
                }
            }
            numModStepsMultipleNodesVisitedNumbers(i, basesMapping, avModsHelp, visitedNumbers, opts);
        }
        return basesMapping;
    }

    /**
     * Used to strictly find the longest list in some block of numbers. No mod avoidance computation.
     * @param opts The options helper that has information parsed from the arguments.
     * @return The ListSizeHelper that has the largest list information.
     */
    public static ListSizeHelper computeLongestList(OptionsHelper opts) {
        ListSizeHelper helper = new ListSizeHelper(opts);

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

    /**
     * This is only run for modes 2 and 3, not otherwise. Just computes the Collatz Conjecture, for the 
     * given number num. 
     * @param num The number we're running the Collatz Sequence on.
     * @param helper The helper class that'll store the results we have.
     * @param visitedNumbers The set of numbers we've visited so far.
     * @param opts The OptionsHelper containing our choices.
     */
    static void runCollatzWholeList(long num, ListSizeHelper helper, Set<BigInteger> visitedNumbers, OptionsHelper opts) {
        BigInteger number = BigInteger.valueOf(num);
        List<BigInteger> currentCollatzPath = new ArrayList<>(100);

        //condition is abstracted to the helper class because mode 3 has a slightly different termination condition than mode 2.
        while(helper.whileLoopCondition(number, currentCollatzPath, opts)) {
            currentCollatzPath.add(number);
            number = computeCollatzForward(number, visitedNumbers, opts);
        }
        currentCollatzPath.add(number); //add the last number as well. Useful for Mode 3.
        helper.checkIfNewChain(currentCollatzPath, num);
    }

    /**
     * Used to find the longest list of numbers that are a growth from the input number. Almost exactly the same as the whole list computation,
     * except the fact that the while loop terminates as soon as we arrive at a lower number, so we built a helper to handle that.
     * @param opts The options helper that has information parsed from the arguments.
     * @return The UntilDecayListSizeHelper that has the largest list information. UntilDecayListSizeHelper is exactly the same as ListSizeHelper,
     * except for the fact that the while condition is different.
     */
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


    /**
     * FOR NOW, going to work with STRICTLY longs, NOT BIGINTS. Because I can't save enough space for all the visited numbers.
     * Other things to do:
     * 1. Print as I go.
     * @param opts
     * @return
     */
    public static AvoidingModGrowthHelper computeAvoidingModGrowth(OptionsHelper opts) {
        AvoidingModGrowthHelper helper = new AvoidingModGrowthHelper(opts);
        //still a lot of work to do here. thinking, OK, so I'll run some mod avoidance stuff, like always, but I don't think I can really utilize any of the methods
        //that are already here. The reason is that I need to run the while loop and

        Set<Long> visitedNumbers = new HashSet<>();
        //Set<BigInteger> visitedNumbers = new HashSet<>(); TODO: Add back in later.
        
        Set<Set<Integer>> avoidBases = opts.getAvoidBases();
    	
    	if (avoidBases.size() > 1) {
    		System.err.println("WARNING! Only grabbing first base for now. Will optimize this later on.");
    	}
    	
    	Set<Integer> avoidBaseSet = avoidBases.iterator().next();
    	int avoidBase = avoidBaseSet.iterator().next();
        
        
        for (long i = opts.getLowNum(); i <= opts.getHighNum(); i+= 2) {
        	//BigInteger num = BigInteger.valueOf(i); TODO:Add back in later. Also replace i with num.
        	if (opts.isTimeEffFlag()) {
                if (visitedNumbers.contains(i)) {
                    //visitedNumbers.remove(num);//can remove the number; we'll never see it again. Saves space.
                	continue;
                }
            }
        	
        	computeOneAvoidingModGrowthNumber(i, helper, avoidBase, opts, visitedNumbers);
        	
        }
        
        return helper;
    }
    /**
     * FOR NOW, going to make this do Longs instead of BigInts. Need to change other things later.
     * @param number Change this to BigInteger.
     * @param helper 
     * @param avoidBase 
     * @param opts
     * @param visitedNumbers Change this to BigInteger.
     */
    private static void computeOneAvoidingModGrowthNumber(long number, 
    		AvoidingModGrowthHelper helper, int avoidBase, OptionsHelper opts,
    		Set<Long> visitedNumbers) {
    
    	
    	List<Long> currentCollatzPath = new ArrayList<>(1000); //TODO: Change this to BigInt.
    	int index = 0;
    	int oddNumbers = 0; //counting odd numbers as another approach to hardness, but excluding last 1.
    	long currentNumber = number; //change this to BigInteger.
    	int oddNumbersInCurrentChain = 0;
    	
    	while (currentNumber > 1  && index < opts.getNumSteps()) {
    		helper.checkIfNewLargestNumber(currentNumber);
    		long modResult = currentNumber % opts.getOutputBase();//currentNumber.mod(opts.getBaseBigInt()).intValue();
    		currentCollatzPath.add(currentNumber);
    		if (modResult == avoidBase) {
    			helper.compareCurrentChainToLongestChain(index, oddNumbersInCurrentChain);
    			oddNumbersInCurrentChain = 0;
    			
    		}
    		if (currentNumber % 2 == 1) {
    			oddNumbers++;
    			oddNumbersInCurrentChain++;
    		}
    		index++;
    		currentNumber = computeCollatzForward(currentNumber, visitedNumbers, opts);
    		
    	}
    	//we'll add 1 to the end of this chain too.
    	
    		
    	currentCollatzPath.add(currentNumber);
    	oddNumbers++;
    	oddNumbersInCurrentChain++;
    	helper.compareCurrentChainToLongestChain(index, oddNumbersInCurrentChain); //normally must subtract but we added an extra number in.
    	helper.checkIfNewLongestChain(number, currentCollatzPath, index+1, oddNumbers); //index+1 is the total chain length.
    	helper.resetCounters();
    	
/*
 *  // takes an input number and runs the collatz conjecture on it until it converges to 1.
    // if we have timeeff flag and space is enough, then we can store values we've visited already to avoid visiting them again.
    //
    private static void numModStepsMultipleNodesVisitedNumbers(long num, Map<Set<Integer>, MultiBaseListSizeHelper> basesMapping,
                                                               ModAvoidanceWrapper avModsHelp, Set<BigInteger> visitedNumbers,
                                                               OptionsHelper opts) {
        BigInteger number = BigInteger.valueOf(num);
        List<BigInteger> currentCollatzPath = new ArrayList<>(2000); //should never need to exceed this size.
        int index = 0;
        //either the number is one, or we finish executing after a certain number of steps.
        while(number.compareTo(BigInteger.ONE) > 0 && index < opts.getNumSteps()) {
            int modResult = number.mod(opts.getBaseBigInt()).intValue();
            //first, add the current number to the currentCollatzPath. Then, see if this base hits any of the bases we're trying to avoid.
            currentCollatzPath.add(number);
            List<Set<Integer>> checkBases = avModsHelp.getMappedSets(modResult);
            if (checkBases != null) {
                for (Set<Integer> s : checkBases) {
                    MultiBaseListSizeHelper m = basesMapping.get(s);
                    m.compareCurrentChainToLongestChain(index);
                }
            }
            index++; //increment the counter for the collatzPath.
            number = computeCollatzForward(number, visitedNumbers, opts);
        }
        //NOW, we see if the new longest path exceeds the old ones, and reset the counters.
        //Also, check if our longest chain that converges to 1 ends up exceeding any other chain.
        Set<Set<Integer>> baseKeySet = basesMapping.keySet();
        for (Set<Integer> s : baseKeySet) {
            MultiBaseListSizeHelper m = basesMapping.get(s);
            m.compareCurrentChainToLongestChain(index-1); //check if we have a new chain one more time.
            m.checkIfNewChain(currentCollatzPath, num);
            m.resetCounters();
        }
    }
 */
    	
    	
    }

    /*
    private static void numModStepsMultipleNodesVisitedNumbers(long num, Map<Set<Integer>, MultiBaseListSizeHelper> basesMapping,
                                                 ModAvoidanceWrapper avModsHelp, Set<BigInteger> visitedNumbers,
                                                 OptionsHelper opts) {
        y BigInteger number = BigInteger.valueOf(num);
        y List<BigInteger> currentCollatzPath = new ArrayList<>(2000); //should never need to exceed this size.
        y int index = 0;
        //either the number is one, or we finish executing after a certain number of steps.
        y while(number.compareTo(BigInteger.ONE) > 0 && index < opts.getNumSteps()) {
          n  int modResult = number.mod(opts.getBaseBigInt()).intValue();
            //first, add the current number to the currentCollatzPath. Then, see if this base hits any of the bases we're trying to avoid.
          y  currentCollatzPath.add(number);
          n  List<Set<Integer>> checkBases = avModsHelp.getMappedSets(modResult);
          n  if (checkBases != null) {
          n      for (Set<Integer> s : checkBases) {
          n          MultiBaseListSizeHelper m = basesMapping.get(s);
          n          m.compareCurrentChainToLongestChain(index);
          n      }
          n  }
          y  index++; //increment the counter for the collatzPath.
          y  number = computeCollatzForward(number, visitedNumbers, opts);
        }
        //NOW, we see if the new longest path exceeds the old ones, and reset the counters.
        //Also, check if our longest chain that converges to 1 ends up exceeding any other chain.
        n Set<Set<Integer>> baseKeySet = basesMapping.keySet();
        n for (Set<Integer> s : baseKeySet) {
        n    MultiBaseListSizeHelper m = basesMapping.get(s);
        n    m.compareCurrentChainToLongestChain(index-1); //check if we have a new chain one more time.
        y    m.checkIfNewChain(currentCollatzPath, num);
        n    m.resetCounters();
        n }
    }
     */

    //Should never be called, just plopping this code here for reference. Untested with new way. Assumes that only first base is avoided.
    /*public static MultiBaseListSizeHelper computeModsOldWay(OptionsHelper opts) {
        MultiBaseListSizeHelper help = new MultiBaseListSizeHelper(opts);
        //List<Long> topNums = new ArrayList<Long>(); //I've decided that I don't care about topNums anymore. Just get whatever the lowest number is.
        LinkedList<BigInteger> topChain = new LinkedList<BigInteger>();
        for (long i = opts.getLowNum(); i <= opts.getHighNum(); i += 2) {
            int avoidBase = opts.getFirstAvoidBase();
            if (avoidBase <= 0) {
                System.err.println("Error: Wrong avoid base!");
                System.exit(42);
            }
            LinkedList<BigInteger> cur = numModStepsOneNode(i, opts.getOutputBase(), avoidBase);
            help.compareTopChain(cur, i);
        }
        return help;
    }*/


}
