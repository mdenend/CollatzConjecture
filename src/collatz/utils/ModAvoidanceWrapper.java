package collatz.utils;

import java.util.*;

/**
 * This class allows us to take an input integer and map to the avoidance set of sets much faster than iterating through each element.
 * Created by Matt Denend on 3/17/17.
 */
public class ModAvoidanceWrapper {

    private Map<Integer, List<Set<Integer>>> mapping;

    private ModAvoidanceWrapper(Set<Set<Integer>> in) {
        mapping = new HashMap<>();
        for (Set<Integer> s: in) {
            for (int i: s) {
                List<Set<Integer>> list;
                if (!mapping.containsKey(i)) {
                    list = new ArrayList<>();
                } else {
                    list = mapping.get(i);
                }
                list.add(s);
                mapping.put(i, list);
            }
        }
    }

    /**
     * Returns the mapping of sets for a specific integer.
     * @param base The number we are checking the mapping for.
     * @return the mapping for the given number, or null if it doesn't exist.
     */
    public List<Set<Integer>> getMappedSets(int base) {
        if (!mapping.containsKey(base)) {
            return null;
        } else {
            return mapping.get(base);
        }
    }

    public static ModAvoidanceWrapper getWrapping(Set<Set<Integer>> avoidanceBases) {
        return new ModAvoidanceWrapper(avoidanceBases);
    }

}
