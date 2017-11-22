package collatz.helpers;

/**
 * This is basically useless. I created this for the Reverse Collatz Computation but I found it to be impractical.
 * I should not be using this but I still am for the more efficient method. Too lazy to fix.
 * Created by Matt Denend on 5/25/17.
 */
public enum UpDownReverseStates {
    FOUND (0),
    IMPOSSIBLE (1),
    NOT_FOUND (2);

    private final int state;

    UpDownReverseStates(int state) {
        this.state = state;
    }

    /*public int getState(){
        return state;
    }*/
}
