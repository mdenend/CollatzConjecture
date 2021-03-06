Collatz Conjecture Application
===========================

This application can run many different variations of the Collatz Conjecture/3N+1 problem, or the Collatz Conjecture. The primary focus is on Collatz Variants, as defined by my thesis, whose link will be provided here when it is published. For now, we provide a definition, which will be removed in the future. If you wish to edit the code, Intellij should work very well with it. We tried to get it to also run with Eclipse but decided it wasn't high on our priority list.

Collatz Variant Definition
---------------------

Since the thesis tied to this project has not been published yet, we will briefly explain what a "Collatz Variant" is.

The 3N+1 sequence can be defined recursively with this code:

	Col(N):
		1:	if N < 1 then
		2:	   return N
		3:	if N % 2 == 0 then
		4:	   return Col(N/2)
		5:	else
		6:	   return Col(3*N+1)
	
A Collataz Variant (Col_Mod) modifies the termination conditions for line 1. We add two more parameters: a set A, and a positive integer b. The new termination condition is:

	Col_Mod(N,A,b):
		if N < 1 or N % b == a_1 or N % b == a_2 or ... or N % b == a_n  then
	   		return N
		if N % 2 == 0 then
	   		return Col(N/2)
		else
	   		return Col(3*N+1)

meaning that for the n element setof A, where a is in A, if N % b == a, then Col_Mod(N,A,b) terminates.


Compiling the program:
------------------------

In this directory, type:

	javac -d COMPILED_DIRECTORY src/collatz/Main.java src/collatz/compute/*.java src/collatz/helpers/*.java src/collatz/output/*.java src/collatz/utils/*.java

Where COMPILED_DIRECTORY is a directory you compile the output files. It's optional to run the -d option, but strongly recommended, because otherwise, the output files will compile in the src files.

Running the 3N+1 program:
-------------------------

To run the 3N+1 problem, you need to run:

	java -cp COMPILED_DIRECTORY collatz.Main [--mode modename] [--base baseNum] [--baseoutput ALL|EVEN|ODD|[#-#, #, #]] [--inputfile Input/InputLarge.txt] [--timeefficient] --outputfile Output/Large/outputtimeeff.csv 1 5 7 5-7

Options:
- --mode modename: Runs different analyses of the 3N+1 problem. There are 5 different modes: baseavoid, entirechain, untildecay, updown, and avoidingmodgrowth. Default is baseavoid.
	- baseavoid is the default option. This allows us to count the highest number of steps each input number in the range avoids termination of Collatz Variants, and for each variant, prints out the sequence of numbers for the longest sequence in a csv file, as well as other infrormation in the first cell.
	- avoidingmodgrowth is like baseavoid, except, for each Collatz Variant, the program prints out a table where each row corresponds to longer length sequences avoiding termination of the Collatz Variant.
        - entirechain prints the entire 3N+1 sequence, from the starting number to 1.
	- untildecay means that, for an odd number, we continue to compute the 3N+1 sequence until we have a number lower than the original number. We print out only the longest chain in the range of numbers.
	- updown is different from other modes. In a range of numbers, for each odd number, run the untildecay mode, except take the number of steps and which number the original number has become, instead of the longest 3N+1 sequence. Also, take what the shortest length a number below the input number can be, what that number is, and the number of steps (if we go backwards). The csv prints out one row per number.
	   
- --base baseNum: Changes the parameter b, the third parameter of Algorithm 2 in the thesis, to another number. Default is 8.

- --baseOutput ALL, EVEN, ODD, [#-#,#] Changes the output bases. This is only used in the baseavoid mode.
	- If ALL, bases from 2-32 are considered.
        - If EVEN, only even bases from input are considered.
        - If ODD, only odd bases from input are considered.
        - If single number, just consider that base.
        - If multiple numbers are to be used, then use brackets, commas for mutiple selections, and hyphens to denote a range of numbers from low-high. Ex. [2-8, 12] means output bases from 2-8 and 12.
- --inputFile name: take the input file and run on all numbers in the range. Input file has two lines: one for the start number, and the second for the stop number.
- --numSteps #: restricts the number of steps made in the 3N+1 sequence. Includes input number as a step. Must be greater than 1. Default is no limit.
- --onenumber #: run the program on only one number instead of a range of numbers: the given number.
- --outputFile name: changes the output file name. Should include the directory the number ought to be output to, and note that name should have ".csv" extension added. If many Collatz Variants are run, an output file is generated for each one with a distinct name for each Collatz Variant. Default name is output.csv.
- --timeefficient uses a map to store numbers that were already detected in Collatz conjecture. Only a good idea if you have enough memory for RANGE/2 BigInts.
- Last numbers are the Collatz Variants we are tracking. 1 means the Collatz Variant that terminates when an input number is congruent modulo to 1 mod b (b is the base number provided with the --base option. 2-3 means the Collatz Variant that terminates when an input number is congruent modulo to either 2 mod b or 3 mod b. Note that a hyphen means track the variant where either number causes termination. Many hyphens can be used as well, so 1-5-7 is also a valid option.

Examples:
------------------

Assume that, in our present working directory, that we have the following directories:
- out: This is COMPILED_DIRECTORY
- Input: has the input text file with the two numbers.
- Output/Batch1: where the output will go.

baseavoid example:

	java -cp out collatz.Main --inputfile Input/Input.txt --mode baseavoid --base 8 --baseoutput ALL --timeefficient --outputfile Output/Batch1/outputtimeeff.csv 1 5 7 5-7

Runs the mode baseavoid for (Base 8) Collatz Variants 1, 5, 7, and {5,7}. Does not repeat computation of already seen odd numbers. Takes the range of numbers from a input file Input/Input.txt with the correct format, and outputs the files into Output/Batch1/outputtimeeff#.csv for each of the different variants.

untildecay example:

    java -cp out collatz.Main --inputfile Input/Input.txt --mode untildecay --timeefficient --outputfile Output/untildecayrun.csv

Runs the mode untildecay. Does not repeat computation of already seen odd numbers. Takes the range of numbers from a input file Input/Input.txt with the correct format, and outputs the files into Output/untildecayrun.csv. Note that there are fewer parameters for this mode compared to baseavoid mode, of particular note, there are no dangling numbers.


Preprocessing if you want to run several programs in parallel:
--------------------------------------------------

If we want to run batches of numbers in parallel, we need to preprocess numbers to give us slices.. (This will be eliminated in future versions.)
       
    java -cp COMPILED_DIRECTORY collatz.utils.GenerateInputFiles lowNum highNum numFiles

Options:
- lowNum: The lowest number that you run the 3N+1 problem on. Number must be positive. Allows any value up to 2^63-1. Should be odd.
- highNum: The highest number that you run the 3N+1 problem on. Number must be positive. Allows any value up to 2^63-1. Must be greater than lowNum. Also should be odd.
- numFiles: The number of input files that you'll divide into.

GenerateInputFiles will figure out the slice size per number number range by computing highNum - lowNum, then divide by numberOfFiles. If the resulting slice size is an odd number, since our code is sensitive to odd/even numbers, it will terminate the code and not generate files.

The resulting input files will always start with an odd number, and end with an even number. To make things work consistently, it's best to:
- Make both the input and output numbers odd.
- Make the difference of them EXACTLY a power of 2. Ex: lowNum = 1; highNum = 2^32 + 1. The difference is 2^32.
- Make numFiles some power of 2. Good choices are 8, 128, or 1024. Any of these will divide perfectly evenly into 2^n for any int n >= 10.

After this, you'll have some number of input files stored into the directory Input.

We used Condor to run numbers in batches. For more information on using Condor, please visit https://research.cs.wisc.edu/htcondor/


Version History:
-----------------

v1.0 (8/28/18): Made this repository public.


Planned Future Edits:
------------------

- Adding in a more time efficient mode that uses all prior computations, although it is heavy on memory.
- Rewording some confusing variable names.
- Removing the GenerateInputFiles program and generating slices on the fly without input files.
- Automatically add a .csv extension to the output.
- Add in an output directory option.


