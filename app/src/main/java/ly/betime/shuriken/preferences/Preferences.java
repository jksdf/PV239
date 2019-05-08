package ly.betime.shuriken.preferences;

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

    private Preferences() {
    }
}
