package ly.betime.shuriken.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.room.Room;
import ly.betime.shuriken.activities.AlarmsActivity;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.persistance.AppDatabase;
import ly.betime.shuriken.service.AlarmManagerApi;
import ly.betime.shuriken.service.AlarmService;
import ly.betime.shuriken.service.AlarmServiceImpl;

import static android.content.Context.ALARM_SERVICE;

public class UpgradeReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "UpgradeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "App upgrade detected, resetting alarms.");
        if (intent.getAction() == null || !intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            Log.w(LOG_TAG, String.format("Bad intent action %s", intent.getAction()));
        }

        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "db").allowMainThreadQueries().build();
        AlarmService alarmService = new AlarmServiceImpl(db.alarmDAO(), new AlarmManagerApi((AlarmManager) context.getApplicationContext().getSystemService(ALARM_SERVICE), context.getApplicationContext(), AlarmsActivity.class));
        for (Alarm alarm : alarmService.listAlarms()) {
            if (alarm.isEnabled()) {
                alarm.setEnabled(false);
                alarmService.setAlarm(alarm, true);
            }
        }
        Log.i(LOG_TAG, "All alarms were reset after upgrade.");
    }
}
