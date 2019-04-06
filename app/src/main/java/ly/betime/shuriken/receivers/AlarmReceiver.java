package ly.betime.shuriken.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;


import javax.inject.Inject;
import javax.inject.Named;

import ly.betime.shuriken.App;
import ly.betime.shuriken.activities.ActiveAlarmActivity;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.service.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "AlarmReceiver";

    @Inject
    @Named("AlarmActivity")
    public Class<? extends Activity> alarmActivity;

    @Inject
    public AlarmService alarmService;

    @Override
    public void onReceive(Context context, Intent intent) {
        App.getComponent().inject(this);
        Log.i(LOG_TAG, "Received alarm");
        int alarmId = intent.getIntExtra(ActiveAlarmActivity.ALARM_ID_EXTRA_NAME, -1);
        if (alarmId == -1) {
            return;
        }
        Alarm alarm = alarmService.getAlarm(alarmId);
        if (alarm == null) {
            return;
        }
        long secondsDifference =
                Math.abs(ChronoUnit.SECONDS.between(alarm.getRinging(), LocalDateTime.now()));
        if (secondsDifference > 60) {
            Log.i(LOG_TAG, "Alarm triggered too late.");
            alarmService.setAlarm(alarm, AlarmService.AlarmAction.DISABLE);
            return;
        }
        Intent newIntent =
                new Intent(context, alarmActivity)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(ActiveAlarmActivity.ALARM_ID_EXTRA_NAME, alarmId);
        context.startActivity(newIntent);
    }
}
