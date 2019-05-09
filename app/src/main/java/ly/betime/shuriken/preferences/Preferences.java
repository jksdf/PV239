package ly.betime.shuriken.preferences;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.HashSet;
import java.util.Set;

public final class Preferences {

    public static final String NAME = "betime.ly.preferences";

    public static final String HOUR_FORMAT_24 = "hourFormat24";

    public static final String SNOOZE_TIME = "snoozeTime";
    public static final int SNOOZE_TIME_DEFAULT = 10;

    public static final String ALARM_SOUND = "alarmSound";
    public static final String ALARM_SOUND_DEFAULT = null;

    public static final String MAX_RINGING_TIME = "MAX_RINGING_TIME";
    public static final int MAX_RINGING_TIME_DEFAULT = 10;

    public static final String VIBRATE = "vibrate";
    public static final boolean VIBRATE_DEFAULT = true;

    public static final String FIRST_START = "firstStart";
    public static final boolean FIRST_START_DEFAULT_VALUE = true;

    public static final String CALENDARS_SELECTED = "calendarsSelected";
    public static final String CALENDARS_SELECTED_DEFAULT = "";

    public static final String MORNING_TIME_ESTIMATE = "morningTimeEstimate";
    public static final int MORNING_TIME_ESTIMATE_DEFAULT = 60;

    public static final String TRAVEL_TIME_ESTIMATE = "travelTimeEstimate";
    public static final int TRAVEL_TIME_ESTIMATE_DEFAULT = 10;

    public static Set<Integer> parseInts(String source) {
        Set<Integer> ints = new HashSet<>();
        for (String part : Splitter.on(',').omitEmptyStrings().split(source)) {
            ints.add(Integer.valueOf(part));
        }
        return ints;
    }

    public static String serializeInts(Set<Integer> ints) {
        return Joiner.on(',').join(ints);
    }

    private Preferences() {
    }
}
