package collatz.utils;

import java.io.*;

//TODO: This definitely needs comments. Particularly, what are the parameters?

public class GenerateInputFiles {


    public static void main(String[] args) throws IOException {
        long lowestNumber = Long.parseLong(args[0]);
        if (lowestNumber <= 0) {
            System.err.println("Low number must be a positive integer!");
            System.exit(1);
        }
        long highestNumber = Long.parseLong(args[1]);
        if (highestNumber <= 0) {
            System.err.println("High number must be a positive integer!");
            System.exit(2);
        }
        int numFiles = Integer.parseInt(args[2]);
        long diff = highestNumber - lowestNumber;

        if (diff <= 0) {
            System.err.println("High number is not greater than or equal to lower number!");
            System.exit(3);
        }
        long slice = diff / numFiles;

        //if slices isn't even, it's going to be a whole lot harder to work with. So abort.
        if (slice % 2 == 1) {
            System.err.println("Slices need to be even!");
            System.err.println(diff + "/" + numFiles + "=" + slice);
            System.exit(4);
        }

        //if slices doesn't divide into highest number evenly, we need to make another file.
        if (diff % slice != 0)
            numFiles++;
        long num = lowestNumber+1;
        //test the results before writing them in files
        File inputDirectory = new File("Input");
        if (!inputDirectory.exists()) {
            inputDirectory.mkdir();
        } else if (!inputDirectory.isDirectory()) {
            System.err.println("ERROR: Input exists in directory " + System.getProperty("user.dir"));
            System.err.println("and is not a directory!");
            System.exit(5);

        }
        for (int i = 0; i < numFiles; i++) {
            //System.out.print("File number " + i + " Range is: " + num + " to ");
            PrintWriter w = new PrintWriter(new File("Input/input" + i + ".txt"));
            w.println(num);
            num += slice - 1;
            w.println(num);
            num++;
            w.close();
        }
    }

}
