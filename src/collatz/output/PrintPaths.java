package collatz.output;

import collatz.helpers.ListSizeHelper;
import collatz.utils.OptionsHelper;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
//TODO: This needs descriptions.
/**
 * Created by mad4672 on 3/14/17.
 */
public class PrintPaths {
    public static final String CS = "\t";

    public static void printLongestPath(List<BigInteger> list, boolean isBinary) {
        Iterator<BigInteger> i = list.iterator();
        while (i.hasNext()) {
            BigInteger cur = i.next();
            if (isBinary) {
                System.out.println(cur.toString(2));
            }
            else
                System.out.println(String.format("%,d", cur));
        }

    }

    public static String printListNice(List<Long> nums) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < nums.size()-1; i++) {
            b.append(addCommas(nums.get(i)) + ", ");
        }
        b.append(addCommas(nums.get(nums.size()-1)));
        return b.toString();
    }

    public static String avoidBasesSetToString(Set<Integer> bases) {
        StringBuffer s = new StringBuffer();
        Iterator<Integer> i = bases.iterator();
        while (i.hasNext()) {
            s.append(i.next());
            if (i.hasNext()) {
                s.append("-");
            }
        }
        return s.toString();
    }


    public static String addCommas(long num) {
        return String.format("%,d", num);
    }

    public static String addCommas(BigInteger num) {
        return  NumberFormat.getNumberInstance(Locale.US).format(num);
    }


    public static void writeOutputFile(String filename, ListSizeHelper m, OptionsHelper opts, String stamp) {
        //String setString = avoidBasesSetToString(bases);

        String firstCell = m.getFirstCellInitial() +
                "Longest chain is length " + m.getTopChain().size() + opts.LS +
                "First number that has this chain: " + addCommas(m.getLongestChainNumber()) + opts.LS +
                "Time of computation: " + stamp + "\"";
        BigInteger baseBI = BigInteger.valueOf(opts.getOutputBase());
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            writer.println(firstCell);
            writer.println();
            writer.print("Mod Base " + opts.getOutputBase() + CS);
            List<Integer> printBases = opts.getPrintBases();
            int i;
            for (i = 0; i < printBases.size(); i++) {
                writer.print("Base: " + printBases.get(i) + CS);
            }
            writer.println();
            writer.flush();
            List<BigInteger> chain = m.getTopChain();
            for (BigInteger bi : chain) {
                String biCom = addCommas(bi);
                writer.print(bi.mod(baseBI) + CS + "\"" + biCom + "\"" + CS);
                for (i = 0; i < printBases.size(); i++) {
                    int baseToConvertTo = printBases.get(i);
                    if (baseToConvertTo == 10) {
                        continue;
                    }
                    writer.print("\"" + bi.toString(baseToConvertTo) + "\"" + CS);
                }
                writer.println();
                writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing file " + filename + ":");
            e.printStackTrace();
        }

    }

    public static void main (String[] args) {
        BigInteger i = BigInteger.valueOf(2734);
        String s = i.toString(22);
        System.out.println(s);
    }

}
