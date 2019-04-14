package ly.betime.shuriken.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
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
        HandlerThread handlerThread = new HandlerThread("alarm_receiver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            Log.i(LOG_TAG, "Received alarm");
            int alarmId = intent.getIntExtra(ActiveAlarmActivity.ALARM_ID_EXTRA_NAME, -1);
            if (alarmId == -1) {
                Log.e(LOG_TAG, "Alarm has no ID");
                return;
            }
            int alarmType = intent.getIntExtra(ActiveAlarmActivity.ALARM_ID_EXTRA_TYPE, -1);
            if (alarmType == -1) {
                Log.e(LOG_TAG, "Alarm has no type");
                return;
            }
            Alarm alarm = alarmService.getAlarmSync(alarmId);
            if (alarm == null) {
                Log.e(LOG_TAG, "Alarm not found");
                return;
            }
            if (alarm.getRinging() == null) {
                Log.e(LOG_TAG, "Alarm ringing not set.");
                return;
            }
            long secondsDifference =
                    Math.abs(ChronoUnit.SECONDS.between(alarm.getRinging(), LocalDateTime.now()));
            if (secondsDifference > 60) {
                Log.w(LOG_TAG, "Alarm triggered too late.");
                alarmService.setAlarm(alarm, AlarmService.AlarmAction.DISABLE);
                return;
            }
            Log.i(LOG_TAG, "Alarm starting now");
            Intent newIntent =
                    new Intent(context, alarmActivity)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(ActiveAlarmActivity.ALARM_ID_EXTRA_NAME, alarmId)
                            .putExtra(ActiveAlarmActivity.ALARM_ID_EXTRA_TYPE, alarmType);
            context.startActivity(newIntent);
        });

    }
}
