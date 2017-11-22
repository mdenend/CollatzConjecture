package collatz;


import collatz.compute.UpDownComputation;
import collatz.helpers.AvoidingModGrowthHelper;
import collatz.helpers.ListSizeHelper;
import collatz.helpers.MultiBaseListSizeHelper;
import collatz.output.PrintAvoidanceGrowthTable;
import collatz.output.PrintPaths;
import collatz.utils.OptionsHelper;
import collatz.compute.ComputeMods;

import java.util.*;


public class Main {
    public static void main(String[] args) {

        //TODO: 1. Add a single number option. This overrides the inputFile option. If both options exist, throw an exception.
            //Done and tested.
                //TODO 1.5: Not just a single number option, but add a numSteps option where I just output the interesting options. So I can replay the interesting chains for part 4.
                    //Done and tested.
        //TODO 1.6: An option to just output the chain on terminal instead of writing to an output file? Probably optional.
        //TODO: 2. Add an option to strictly print the chain, no base avoidances. I don't think this is terribly necessary. NO! It is if we're going to just print out the interesting chains.
            //I've built a ListSizeHelper that will help make this much easier. All that's left is to build a method in ComputeMods to do this.
            //Done and done, and tested. Not documented yet.
            //TODO: Add last element too. This will make part 3 easier.
        //TODO: 3. Add an option to, instead of considering which bases we avoid, have the trigger be to see how long a number grows before it decays. Get all of these chains and output them as before.
            //The ListSizeHelper class can be extended to make some new Helper class, and I'm even possibly thinking of making this more or less work with the second mode
            //because they're almost exactly the same, except the condition is slightly different: this terminates more often.
            //ACTUALLY... no, it's more like case 1, really. Generate the entire list, and determine which part is longest when triggered.
            //The ONLY difference between case 1 and 3's helpers is that the lower index is ONLY set if the number is odd in case 3, while in case 1 it's set at the new number.
            //OK. NO. I was originally right. It is very like case 1, except the condition is different: we STOP when we have ANY lower number. Why? Because we've already
            //checked the lower number! So this should be a simple case to figure out.
            //TODO: To make #3 work, let's build a Helper class that extends ListSizeHelper. I think the only difference between 2 and 3
            //TODO: is the condition in the while loop is different. This can be modulated into ListSizeHelper and overwritten in the sub class.
            //Done! Also tested.
        //TODO: 4. The trickiest part. I think this may merit a different Main method entirely, as it's so different. For each odd number, determine:
                //TODO: 4.1. The length of the shortest chain to get to a number below it (and what is that number?)
                //TODO: 4.2. The length of the shortest chain that a number below it will get to the number.
        //          TODO: This has to be BACKWARDS collatz computation, and will have to consider split cases. Also, if the length gets to be 100, we stop and give
                //TODO: up. I realized this problem is undecidable. 7, for instance, never has come from a number lower than it. Neither has 3. Are these common?
        //TODO: Put these in output files.

        //TODO: OK! I think I've got method 4 written up. Still need to do some heavy testing on it, of course. I think it should be OK, but I'm curious
        //TODO: on what to tweak the steps value to.



        //Look at this stuff below, not anything above.
        //TODO: Hello from the future! I think I did a LOT of excellent leg work here. I feel like this code was much better written, as opposed to the mess
        //TODO: I saw on my CS machine. Anyhow, I do need to perform these TODOs before I can share this code with Marijn. However, there is something a bit
        //TODO: more pressing! I need to get Marijn that table of numbers to see records of chains of length 1 mod 8, from 1 to 1 billion.
        /*
        I don't think that it'll take me too much work. All I'll need to do:
        1. Make a new mode. Call it avoidingmodgrowth mode. Or something like that.
        2. Create a helper class that should hold an ArrayList of "Rows" (which are, themselves, a class, probably in Utils), which keeps track of:
            1. The number we're flagging.
            2. The count of the steps we took to avoid 1 mod 8.
            3. What the maximum number is.
            4. The physical chain that avoids 1 mod 8.
        We also need to have the helper let us know what the longest chain actually is.
        I think it makes sense for this helper to extend ListSizeHelper, because I think ListSizeHelper does contain pertinent information.
            //TODO: I thought, at first, that no, it wasn't, but now I see that it is in fact quite relevant to extend this class from ListSizeHelper. Then,
            //TODO: all I would need to do is to store the largest number if our mode wants us to (is mode 4). I would have to do things differently for
            //TODO: checkNewChain too, but otherwise, all the same!
        3. In ComputeMods, make a method that utilizes all facets of the helper.
            I'll probably have to look at the logic for storing a chain and figuring out longest numbers being avoided, once again.
            Will have to keep track of the maximum number. The helper will have the max size.
        4. Finally, something to print everything. Should be a piece of cake; Create a class call PrintModTable and Print the headers and all the rows from the
        table.

         */

        //TODO: Then, there are a couple of final steps!
        //TODO: 1. Clean the hell up out of this code. There's just tons and tons of comments that need to be removed, and
        //TODO: 2. I need to better document the methods. Otherwise, when I open this up again 3 months later, I'm gonna be like what the hell.
        //TODO: 3. Finally, clean up the README.


        //TODO: Replace all

        /*
        Thought: Add in a --mode option.
            The default is the base avoidance mode (maybe baseavoid), which will execute the program like it has been before.
            Then, we can have a mode that just prints entire chains instead of avoid bases. Maybe entirechain.
            Then, we can have a mode that only computes the Collatz Conjecture until we reach a lower number, and store that result somehow. Maybe untildecay.
                (Can I store everything? I seriously doubt it. That would eventually get huge. Need to think about this. Maybe store the longest such chain in a block?)
            Then, the fourth mode which I probably will use a different main method for since it's so different: the forward/backward chain mode, maybe updown,
                and store each number, target number,  and chain values for both cases. If I'm clever, maybe I can come up with a slightly different approach for large bulk numbers. Biggest thing is to
                 figure out how I can modularize what I have and do both forward and backward. I'll probably run from 1-10000 initially.
         */

        /*
            Another thought: For each mode, build a class that handles this specific mode, and a root abstract class that can handle the different modes of execution.
            Really plan this out here. What do each of these modes have in common with one another, and what makes them different? Going to take quite a bit of thought
            but I think that if I do this correctly, I should be able to add new modes as Marijn wants to see different things. I think THIS will really leverage
            the power of OOP. But I need to design carefully.

         */


        // Options:
        //--base #: Changes the base of our graph to #. If no input, default is 8.
        //--inputFile name: take the input file and consider the range.
        //--baseOutput ALL, EVEN, ODD, [#-#,#] Changes the output bases.
        //    If ALL, bases from 2-32 are considered.
        //    If EVEN, only even bases from input are considered.
        //    If ODD,  only odd bases from input are considered.
        //    If single number, just consider that base.
        //    If brackets detected [] (or something else), then take any range low-high in hyphen, and other numbers in commas. Ex. [2-8, 12] means numbers from 2-8 and 12.
        //--mode baseavoid|entirechain|untildecay|updown Changes the various modes in which we can analyze the collatz conjecture:
        //  baseavoid is the default option. This allows us to check, for multiple bases, how long it takes for us to hit a base.
        //  entirechain just prints the entire chain, from the starting number to 1. Does not print 1, but it's trivial that 2 goes to 1, so this is unnecessary.
        //  untildecay means that, for an odd number, we continue to run until we have a number lower than the original number. We then check to see if the chain is longer than previous one.
        //  updown is a quite different mode. In a set of numbers, for each odd number, determine how long it can run until decay
        //      (EXACTLY the same as case 3, except we don't care about the list, just what number it becomes and number of steps), and
        //      what's the shortest length that a number below it can be, what's that number, and the number of steps (if we go backwards). So csv here is VERY different from other cases.
        //--numSteps #: restricts the number of steps made in the Collatz Conjecture. Includes input number as a step. Must be greater than 1. Usually done with whole list mode.
        //--onenumber # only run the Collatz Conjecture on this number.
        //--outputFile name: outputs the text to name_avoidingBase#.csv, where # is/are the base(s) that this file corresponds to. If nothing, name will just default to output (with .csv suffix)
        //    Also splits at the last period and adds the avoid bases between the name and suffix.
        //--reverselimit #: sets the maximum amount of steps we can reverse compute for the 4th mode, otherwise it may run infinitely. Only used in 4th mode, not anywhere else.
        //--timeefficient uses a map to store numbers that were already detected in Collatz conjecture. Only a good idea if you have enough memory for RANGE/2 BigInts.
        //Last numbers are the ones that are supposed that we're tracking. 1 means avoid 1 mod b (b is the base number, 2,3 means avoid both 2 mod b and 3 mod b.

        OptionsHelper opts = new OptionsHelper(args);
        Map<Set<Integer>, ? extends ListSizeHelper> modeOneResults = null; //the FIRST time I've ever used the wildcard in Java.
        ListSizeHelper modeTwoThreeResults = null;

        AvoidingModGrowthHelper modeFiveResults = null;
        
        long startTime = System.currentTimeMillis(); //want to time this just to get a sense of CPU hours.

        switch(opts.getMode()) {
            case 0:
                modeOneResults = ComputeMods.computeModsAvoidingMultipleBases(opts); //this is where the magic happens.
                break;
            case 1:
                modeTwoThreeResults = ComputeMods.computeLongestList(opts);
                break;
            case 2:
                modeTwoThreeResults = ComputeMods.computeLongestGrowth(opts);
                break;
            case 3:
                //modeFourResults = computeUpDown(opts);
                UpDownComputation.computeUpDownV2(opts);
                break;
            case 4:
            	modeFiveResults = ComputeMods.computeAvoidingModGrowth(opts);
                break;
            default:
                System.err.println("Mode does not exist.");
                System.exit(8);
                break;
        }

        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        String stamp = convertMillisecondsToTimeStamp(runTime); //didn't want to spend 30 minutes figuring out how to do timestamps; made my own.

        //for each result, print the outputs!
        switch(opts.getMode()) {
            //case 0 is much more different: we have to put the avoided bases into string and add it to the filename.
            case 0:
                Set<Set<Integer>> key = modeOneResults.keySet();
                for (Set<Integer> s : key) {
                    String filename = opts.getOutputFilePrefix() + MultiBaseListSizeHelper.avoidBasesSetToString(s) + opts.getOutputFileSuffix();
                    PrintPaths.writeOutputFile(filename, modeOneResults.get(s), opts, stamp);
                }
                break;
            case 1: //case 1 and 2 are EXACTLY the same, so I let case 1 fall through to case 2.
            case 2:
                String filename = opts.getOutputFilePrefix() + opts.getOutputFileSuffix();
                PrintPaths.writeOutputFile(filename, modeTwoThreeResults, opts, stamp);
                break;
            case 3: //case 3 prints within the Compute method, so I just output the timestamp instead.
                System.err.println("Run finished after time of " + stamp);
                break;
            case 4:
            	PrintAvoidanceGrowthTable.writeOutputFile(opts.getOutputFilePrefix() + opts.getOutputFileSuffix(), modeFiveResults, opts, stamp); 
            	break;
        }


    }

    //Probably don't need this but didn't want to spend an hour figuring out how to do it with Java code. Seemed too complicated...
    private static String convertMillisecondsToTimeStamp(long ms) {
        long msStamp = ms % 1000;
        long toSeconds = ms / 1000;
        long secStamp = toSeconds % 60;
        long toMinutes = toSeconds / 60;
        long minStamp = toMinutes % 60;
        long hourStamp = toMinutes / 60;
        return String.format("%d:%02d:%02d.%03d", hourStamp, minStamp, secStamp, msStamp);
    }
}
