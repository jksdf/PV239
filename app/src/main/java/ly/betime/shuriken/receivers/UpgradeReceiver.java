package ly.betime.shuriken.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import javax.inject.Inject;

import ly.betime.shuriken.App;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.service.AlarmService;

public class UpgradeReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "UpgradeReceiver";

    @Inject
    public AlarmService alarmService;

    @Override
    public void onReceive(Context context, Intent intent) {
        App.getComponent().inject(this);
        Log.i(LOG_TAG, "App upgrade detected, resetting alarms.");
        if (intent.getAction() == null || !intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            Log.w(LOG_TAG, String.format("Bad intent action %s", intent.getAction()));
        }

        for (Alarm alarm : alarmService.listAlarms()) {
            if (alarm.isEnabled()) {
                alarm.setRinging(null);
                alarmService.setAlarm(alarm, AlarmService.AlarmAction.ENABLE);
            }
        }
        Log.i(LOG_TAG, "All alarms were reset after upgrade.");
    }
}
