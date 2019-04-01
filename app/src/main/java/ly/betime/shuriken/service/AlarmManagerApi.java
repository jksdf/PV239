package ly.betime.shuriken.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import static com.google.common.base.Preconditions.checkNotNull;

public class AlarmManagerApi {

    private static final String LOG_TAG = AlarmManagerApi.class.getSimpleName();

    private final AlarmManager alarmManager;
    private final Context context;
    private final Class<?> receiver;

    @Inject
    public AlarmManagerApi(AlarmManager alarmManager, @Named("application") Context context, Class<?> receiver) {
        this.alarmManager = checkNotNull(alarmManager);
        this.context = checkNotNull(context);
        this.receiver = checkNotNull(receiver);
    }

    /**
     * Cancels an alarm with the specified identifier.
     * @param id of the alarm
     */
    public void cancelAlarm(int id) {
        PendingIntent pendingIntent = getPendingIntent(id, false);
        if (pendingIntent == null) {
            Log.w(LOG_TAG, "Pending alarm not found, id: " + id);
            return;
        }
        this.alarmManager.cancel(pendingIntent);
    }

    /**
     * Sets an alarm.
     * @param id of the alarm.
     * @param triggerTime milliseconds since epoch when to trigger the alarm.
     */
    public void setAlarm(int id, long triggerTime) {
        PendingIntent pendingIntent = getPendingIntent(id, true);
        if (Build.VERSION.SDK_INT >= 21) {
            this.alarmManager.setAlarmClock(
                    new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent), pendingIntent);
        } else {
            this.alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private PendingIntent getPendingIntent(int id, boolean enable) {
        Intent intent = new Intent(this.context, this.receiver);
        return PendingIntent.getBroadcast(
                context, id, intent, enable ? 0 : PendingIntent.FLAG_NO_CREATE);
    }
}
