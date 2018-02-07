package collatz.compute;

//import collatz.helpers.AvoidingModGrowthHelper;
//import collatz.helpers.MultiBaseRecordTrackingListSizeHelper;
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



    // takes an input number and runs the collatz conjecture on it until it converges to 1.
    // if we have timeeff flag and space is enough, then we can store values we've visited already to avoid visiting them again.
    //
    private static void numModStepsMultipleNodesVisitedNumbers(long num, Map<Set<Integer>, ? extends MultiBaseListSizeHelper> basesMapping,
                                                               ModAvoidanceWrapper avModsHelp,
                                                               OptionsHelper opts) {
        BigInteger number = BigInteger.valueOf(num);
        List<BigInteger> currentCollatzPath = new ArrayList<>(2000); //should never need to exceed this size.
        int index = 0;

        //Change 2.1 Add totolOddNumbers and oddNumbersInCurrentChain declaration here, used by mode 4.
        int totalOddNumbers = 0;
        

        //either the number is one, or we finish executing after a certain number of steps.
        while(number.compareTo(BigInteger.ONE) > 0 && index < opts.getNumSteps()) {
            //Change 3. For each base combination, checkIfNewLargestNumber.
            for (Set<Integer> s: basesMapping.keySet()) {
                MultiBaseListSizeHelper m = basesMapping.get(s);
                m.checkIfNewLargestNumber(number);
            }
            int modResult = number.mod(opts.getBaseBigInt()).intValue();
            //first, add the current number to the currentCollatzPath. Then, see if this base hits any of the bases we're trying to avoid.
            currentCollatzPath.add(number);
            List<Set<Integer>> checkBases = avModsHelp.getMappedSets(modResult);
            if (checkBases != null) {
                for (Set<Integer> s : checkBases) {
                    MultiBaseListSizeHelper m = basesMapping.get(s);
                    //Change 4.1 If mode 0, call below. Else, if mode 4, call compareCurrentChainToLongestChainWithOddNumbers. Else, throw Exception.
                    if (opts.getMode() == 0) {
                        m.compareCurrentChainToLongestChain(index);
                    } else if (opts.getMode() == 4) {
                        m.compareCurrentChainToLongestChain(index);
                    } else {
                        throw new IllegalStateException("Unsupported mode detected! Mode" + opts.getMode());
                    }

                    
                }
            }
            //Change 2.3: Increment both odd number counts if number is odd, in mode 4 only.
            if (opts.getMode() == 4 && number.mod(TWO).compareTo(BigInteger.ONE) == 0) {
                totalOddNumbers++;
                for (Set<Integer> s : basesMapping.keySet()) {
                	basesMapping.get(s).incrementChainOddNumbers();
                }
            }
            index++; //increment the counter for the collatzPath.
            number = computeCollatzForward(number, opts);
        }
        //NOW, we see if the new longest path exceeds the old ones, and reset the counters.
        //Also, check if our longest chain that converges to 1 ends up exceeding any other chain.
        //Change 5: Add in the final 1 to the collatz path too.
        currentCollatzPath.add(number);

        //Check if odd number one more time, if mode 4...
        if (opts.getMode() == 4 && number.mod(TWO).compareTo(BigInteger.ONE) == 0) {
            totalOddNumbers++;
            for (Set<Integer> s : basesMapping.keySet()) {
            	basesMapping.get(s).incrementChainOddNumbers();
            }
        }
        
        
        //Change 4.2: If mode 0, call compareCurrentChainToLongestChain and checkIfNewChain.
        //Else, if mode 4, call compareCurrentChainToLongestChainWithOddNumbers and compareCurrentChainToLongestChainWithOddNumbers.
        //Else, throw exception.

        
        

        Set<Set<Integer>> baseKeySet = basesMapping.keySet();
        for (Set<Integer> s : baseKeySet) {
            MultiBaseListSizeHelper m = basesMapping.get(s);
            m.compareCurrentChainToLongestChain(index); //check if we have a new chain one more time.
            if(opts.getMode() == 0) {
                m.checkIfNewChain(currentCollatzPath, num);
            } else if (opts.getMode() == 4) {                
                m.checkIfNewChainWithOddNumbers(num, currentCollatzPath, index+1, totalOddNumbers);
            } else {
                throw new IllegalStateException("Unsupported mode detected! Mode" + opts.getMode());
            }

            m.resetCounters();
        }
    }

    //sees if the current integer is in between the lowest value in the range, and the highest value in the range. If so, we want to add it to the range.
    //only used when we have the timeefficient solution, as we know these numbers can be skipped.
    private static boolean inBetween(BigInteger current, BigInteger low, BigInteger high) {
        return current.compareTo(low) >= 0 && current.compareTo(high) <= 0;
    }
    

    //modularized this because it's going to be called a couple other times.
    private static BigInteger computeCollatzForward(BigInteger number, OptionsHelper opts) {
        BigInteger result;
        if (number.mod(TWO).compareTo(BigInteger.ZERO) == 0) {
            result = number.divide(TWO);
        }
        else {
            //option only needed for time efficient solution.
            if (opts.isTimeEffFlag()) {
                //only check if number is odd... as we're only analyzing odd numbers.
                if (inBetween(number, opts.getLowNumBigInteger(), opts.getHighNumBigInteger())) {
                    opts.visitedNumbers().add(number);
                }
            }
            number = number.multiply(THREE);
            result = number.add(BigInteger.ONE);
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
    public static void computeModsAvoidingMultipleBases(OptionsHelper opts, Map<Set<Integer>, ? extends MultiBaseListSizeHelper> basesMapping) {
        ModAvoidanceWrapper avModsHelp = ModAvoidanceWrapper.getWrapping(opts.getAvoidBases());

        //these three are only used in time efficient solution, not otherwise, but not huge if not being used.
        //Set<BigInteger> visitedNumbers = new HashSet<>();// if we've seen a number in the collatz conjecture already, then why visit it again??

        //like before, we want our lower number to be odd, and higher number to be even.
        for (long i = opts.getLowNum(); i <= opts.getHighNum(); i += 2) {

            //if space efficient, we'll avoid computation if number already visited.
            if (opts.isTimeEffFlag()) {
                if (opts.visitedNumbers().contains(i)) {
                    continue;
                }
            }
            numModStepsMultipleNodesVisitedNumbers(i, basesMapping, avModsHelp, opts);
        }
    }

    /**
     * Used to strictly find the longest list in some block of numbers. No mod avoidance computation.
     * @param opts The options helper that has information parsed from the arguments.
     * @return The ListSizeHelper that has the largest list information.
     */
    public static ListSizeHelper computeLongestList(OptionsHelper opts) {
        ListSizeHelper helper = new ListSizeHelper(opts);

        //Set<BigInteger> visitedNumbers = new HashSet<>();
        //like before, we want our lower number to be odd, and higher number to be even.
        for (long i = opts.getLowNum(); i <= opts.getHighNum(); i += 2) {
            //if time efficient, we'll avoid computation if number already visited.
            if (opts.isTimeEffFlag()) {
                if (opts.visitedNumbers().contains(i)) {
                    continue;
                }
            }
            runCollatzWholeList(i, helper, opts);
        }
        return helper;

    }

    /**
     * This is only run for modes 2 and 3, not otherwise. Just computes the Collatz Conjecture, for the 
     * given number num. 
     * @param num The number we're running the Collatz Sequence on.
     * @param helper The helper class that'll store the results we have.
     * @param opts The OptionsHelper containing our choices.
     */
    static void runCollatzWholeList(long num, ListSizeHelper helper, OptionsHelper opts) {
        BigInteger number = BigInteger.valueOf(num);
        List<BigInteger> currentCollatzPath = new ArrayList<>(100);

        //condition is abstracted to the helper class because mode 3 has a slightly different termination condition than mode 2.
        while(helper.whileLoopCondition(number, currentCollatzPath, opts)) {
            currentCollatzPath.add(number);
            number = computeCollatzForward(number, opts);
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

        //Set<BigInteger> visitedNumbers = new HashSet<>();
        //like before, we want our lower number to be odd, and higher number to be even.
        for (long i = opts.getLowNum(); i <= opts.getHighNum(); i += 2) {
            //if time efficient, we'll avoid computation if number already visited.
            if (opts.isTimeEffFlag()) {
                if (opts.visitedNumbers().contains(i)) {
                    continue;
                }
            }
            runCollatzWholeList(i, helper, opts);
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
    /*
    public static AvoidingModGrowthHelper computeAvoidingModGrowth(OptionsHelper opts) {
        AvoidingModGrowthHelper helper = new AvoidingModGrowthHelper(opts);
        //still a lot of work to do here. thinking, OK, so I'll run some mod avoidance stuff, like always, but I don't think I can really utilize any of the methods
        //that are already here. The reason is that I need to run the while loop and

        //Set<Long> visitedNumbers = new HashSet<>();
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
                if (opts.visitedNumbers().contains(i)) {
                    //visitedNumbers.remove(num);//can remove the number; we'll never see it again. Saves space.
                	continue;
                }
            }
        	
        	computeOneAvoidingModGrowthNumber(i, helper, avoidBase, opts);
        	
        }
        
        return helper;
    }*/
    /**
     * This shouldn't be a separate method from the multi bases approach, if I fix my design correctly.
     * @param number Change this to BigInteger.
     * @param helper 
     * @param avoidBase 
     * @param opts
     */
    /*
    private static void computeOneAvoidingModGrowthNumber(long number, 
    		AvoidingModGrowthHelper helper, int avoidBase, OptionsHelper opts) {
    
    	
    	List<Long> currentCollatzPath = new ArrayList<>(1000); //TODO: Change this to BigInt.
    	int index = 0;
    	int oddNumbers = 0; //counting odd numbers as another approach to hardness, but excluding last 1.
    	long currentNumber = number; //change this to BigInteger.
        BigInteger currentBigInt = BigInteger.valueOf(currentNumber);
    	int oddNumbersInCurrentChain = 0;
    	
    	while (currentNumber > 1  && index < opts.getNumSteps()) {
    		helper.checkIfNewLargestNumber(currentBigInt);
    		long modResult = currentNumber % opts.getOutputBase();//currentNumber.mod(opts.getBaseBigInt()).intValue();
    		currentCollatzPath.add(currentNumber);
    		if (modResult == avoidBase) {
    			helper.compareCurrentChainToLongestChainWithOddNumbers(index, oddNumbersInCurrentChain);
    			oddNumbersInCurrentChain = 0;
    			
    		}
    		if (currentNumber % 2 == 1) {
    			oddNumbers++;
    			oddNumbersInCurrentChain++;
    		}
    		index++;
    		currentNumber = computeCollatzForward(currentBigInt, opts).longValue(); //super hacky for now. One step at a time...
    		
    	}
    	//we'll add 1 to the end of this chain too.
    	
    		
    	currentCollatzPath.add(currentNumber);
    	oddNumbers++;
    	oddNumbersInCurrentChain++;
    	helper.compareCurrentChainToLongestChainWithOddNumbers(index, oddNumbersInCurrentChain); //normally must subtract but we added an extra number in.
    	helper.checkIfNewChainWithOddNumbers(number, currentCollatzPath, index+1, oddNumbers); //index+1 is the total chain length.
    	helper.resetCounters();
    	
    }
*/
    /*
    The following code is all of the new Mode 4 code. It'll be Mode 5 for now, until I verify that it in fact works.
     */

    /**
     * This is the main method to call a computation of the Collatz Conjecture for mode 0. This method will continue to run from the low number in opts
     * to the high number in opts.
     * @param opts The OptionsHelper that has the options flagged for the arguments that we input into the command line.
     * @return A Map that has keys that are sets of integers, which are the bases that were avoided, and these each point to a MultiBaseListSizeHelper,
     * which helps store and process our results. See MultiBaseListSizeHelper class for more details.
     */
    /*public static Map<Set<Integer>, MultiBaseRecordTrackingListSizeHelper> computeModsAvoidingMultipleBasesAndTrackingRecords(OptionsHelper opts) {

        Map<Set<Integer>, MultiBaseRecordTrackingListSizeHelper> basesMapping = new LinkedHashMap<>();
        Set<Set<Integer>> avoidanceMods = opts.getAvoidBases();

        //this builds a map that, when an integer is fed into this object (w/ getMappedSets), any sets that have the integer in them are returned,
        // and speeds up the computation significantly.
        ModAvoidanceWrapper avModsHelp = ModAvoidanceWrapper.getWrapping(avoidanceMods);

        for (Set<Integer> s: avoidanceMods) {
            basesMapping.put(s, new MultiBaseRecordTrackingListSizeHelper(opts));
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
            numModStepsMultipleNodesVisitedNumbersTrackingRecords(i, basesMapping, avModsHelp, visitedNumbers, opts);
        }
        return basesMapping;
    }*/

    // takes an input number and runs the collatz conjecture on it until it converges to 1.
    // if we have timeeff flag and space is enough, then we can store values we've visited already to avoid visiting them again.
    //
    /*private static void numModStepsMultipleNodesVisitedNumbersTrackingRecords(long num,
                                                                              Map<Set<Integer>, MultiBaseRecordTrackingListSizeHelper> basesMapping,
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
                    MultiBaseRecordTrackingListSizeHelper m = basesMapping.get(s);
                    m.compareCurrentChainToLongestChain(index);
                }
            }
            index++; //increment the counter for the collatzPath.
            number = computeCollatzForward(number, opts);
        }
        //NOW, we see if the new longest path exceeds the old ones, and reset the counters.
        //Also, check if our longest chain that converges to 1 ends up exceeding any other chain.
        //TODO: Here, we need to check if we have a record, and append it to our records table.
        Set<Set<Integer>> baseKeySet = basesMapping.keySet();
        for (Set<Integer> s : baseKeySet) {
            MultiBaseListSizeHelper m = basesMapping.get(s);
            m.compareCurrentChainToLongestChain(index-1); //check if we have a new chain one more time.
            m.checkIfNewChain(currentCollatzPath, num);
            m.resetCounters();
        }
    }*/



}

//extra comment to check git
