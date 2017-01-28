package info.benallen.jeeves.jeevescontroller;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by benallen on 28/01/2017.
 */

public class Helper {
    public static String debugInfo(Map<Integer, Float> activeBeacons) {
        String output = "";
        SortedSet<Integer> keys = new TreeSet<>(activeBeacons.keySet());
        for (Integer key : keys) {
            Float value = activeBeacons.get(key);
            output += key + " - " + value + "\n";
        }
        return output;
    }
}
