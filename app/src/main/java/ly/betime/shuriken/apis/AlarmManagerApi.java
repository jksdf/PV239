package ly.betime.shuriken.apis;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.threeten.bp.Instant;

import javax.inject.Inject;
import javax.inject.Named;

import ly.betime.shuriken.activities.ActiveAlarmActivity;
import ly.betime.shuriken.receivers.AlarmReceiver;

import static com.google.common.base.Preconditions.checkNotNull;

public class AlarmManagerApi {

    private static final String LOG_TAG = AlarmManagerApi.class.getSimpleName();

    private final AlarmManager alarmManager;
    private final Context context;
    private final Class<?> receiver;

    @Inject
    public AlarmManagerApi(AlarmManager alarmManager, @Named("application") Context context) {
        this.alarmManager = checkNotNull(alarmManager);
        this.context = checkNotNull(context);
        this.receiver = AlarmReceiver.class;
    }

    /**
     * Cancels an alarm with the specified identifier.
     *
     * @param id of the alarm
     */
    public void cancelAlarm(int id, AlarmType alarmType) {
        Log.i(LOG_TAG, "Cancelling alarm, id: " + id);
        PendingIntent pendingIntent = getPendingIntent(id, alarmType, false);
        if (pendingIntent == null) {
            Log.w(LOG_TAG, "Pending alarm not found, id: " + id);
            return;
        }
        this.alarmManager.cancel(pendingIntent);
    }

    /**
     * Sets an alarm.
     *
     * @param id          of the alarm.
     * @param type        of the alarm
     * @param triggerTime milliseconds since epoch when to trigger the alarm.
     */
    public void setAlarm(int id, AlarmType type, long triggerTime) {
        Log.i(LOG_TAG, "Setting alarm " + id + " of type " + type + " to " + Instant.ofEpochMilli(triggerTime) + " as " + triggerTime);
        PendingIntent pendingIntent = getPendingIntent(id, type, true);
        if (Build.VERSION.SDK_INT >= 21) {
            this.alarmManager.setAlarmClock(
                    new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent), pendingIntent);
        } else {
            this.alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private PendingIntent getPendingIntent(int id, AlarmType type, boolean enable) {
        Intent intent = new Intent(this.context, this.receiver);
        intent.putExtra(ActiveAlarmActivity.ALARM_ID_EXTRA_NAME, id);
        intent.putExtra(ActiveAlarmActivity.ALARM_ID_EXTRA_TYPE, type.idx);
        return PendingIntent.getBroadcast(
                context, id * 2 + type.idx, intent, enable ? 0 : PendingIntent.FLAG_NO_CREATE);
    }

    public enum AlarmType {
        NORMAL(0), GENERATED(1);

        private int idx;

        AlarmType(int idx) {
            this.idx = idx;
        }

        public static AlarmType fromIndex(int index) {
            switch (index) {
                case 0:
                    return NORMAL;
                case 1:
                    return GENERATED;
                default:
                    throw new IndexOutOfBoundsException();
            }
        }
    }
}
