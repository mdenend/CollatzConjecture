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

//TODO: this code is MUCH cleaner. But I still need to delete the UpDownReverseStates Enum, which is no good, but it's going to be a bit involved.
//TODO: I should also try and get a better grasp on the UpDown mode, because the comments are confusing me.
public class Main {
    public static void main(String[] args) {


        OptionsHelper opts = new OptionsHelper(args);
        //I'll admit... this isn't the best design, but it gets the job done. Better would have been to build the Modes as classes and
        //modularize everything to these modes, but modes are sufficiently different.

        //ModeOne uses the MultiBaseListSizeHelper which extends ListSizeHelper. Wildcard (?) used to allow
        Map<Set<Integer>, ? extends ListSizeHelper> modeZeroResults = null;
        ListSizeHelper modeOneTwoResults = null;
        AvoidingModGrowthHelper modeFourResults = null;
        
        long startTime = System.currentTimeMillis(); //want to time this just to get a sense of CPU hours.


        //depending on the mode, we run a specific Collatz Computation.
        switch(opts.getMode()) {
            case 0:
                modeZeroResults = ComputeMods.computeModsAvoidingMultipleBases(opts);
                break;
            case 1:
                modeOneTwoResults = ComputeMods.computeLongestList(opts);
                break;
            case 2:
                modeOneTwoResults = ComputeMods.computeLongestGrowth(opts);
                break;
            case 3:
                UpDownComputation.computeUpDown(opts);
                break;
            case 4:
            	modeFourResults = ComputeMods.computeAvoidingModGrowth(opts);
                break;
            default:
                System.err.println("Mode does not exist.");
                System.exit(8);
                break;
        }

        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        String stamp = convertMillisecondsToTimeStamp(runTime); //didn't want to spend 30 minutes figuring out how to do timestamps; made my own.

        //this is where we print the output of the results, except for mode 3, which we do on the fly to avoid running out of memory.
        switch(opts.getMode()) {
            //case 0 is much more different: we have to put the avoided bases into string and add it to the filename.
            case 0:
                Set<Set<Integer>> key = modeZeroResults.keySet();
                for (Set<Integer> s : key) {
                    String filename = opts.getOutputFilePrefix() + OptionsHelper.avoidBasesSetToString(s) + opts.getOutputFileSuffix();
                    PrintPaths.writeOutputFile(filename, modeZeroResults.get(s), opts, stamp);
                }
                break;
            case 1: //case 1 and 2 are EXACTLY the same, so I let case 1 fall through to case 2.
            case 2:
                String filename = opts.getOutputFilePrefix() + opts.getOutputFileSuffix();
                PrintPaths.writeOutputFile(filename, modeOneTwoResults, opts, stamp);
                break;
            case 3: //case 3 prints within the Compute method, so I just output the timestamp instead.
                System.err.println("Run finished after time of " + stamp);
                break;
            case 4:
            	PrintAvoidanceGrowthTable.writeOutputFile(opts.getOutputFilePrefix() + opts.getOutputFileSuffix(), modeFourResults, opts, stamp);
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
