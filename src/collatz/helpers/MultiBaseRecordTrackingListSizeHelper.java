package collatz.helpers;

import collatz.utils.OptionsHelper;

import java.util.Set;

/**
 * Created by mad4672 on 2/4/18.
 */
//TODO: Why am I even extending MultiBaseListSizeHelper? There's almost no similarities between the two approaches.
public class MultiBaseRecordTrackingListSizeHelper extends MultiBaseListSizeHelper {

    //constructor should be exactly the same. I just called super().
    public MultiBaseRecordTrackingListSizeHelper(OptionsHelper opts) {
        super(opts);
    }

}
