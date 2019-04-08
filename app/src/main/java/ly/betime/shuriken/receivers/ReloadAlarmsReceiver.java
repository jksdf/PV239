package ly.betime.shuriken.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

import javax.inject.Inject;

import ly.betime.shuriken.App;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.service.AlarmService;

public class ReloadAlarmsReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "ReloadAlarmsReceiver";

    private static final Set<String> ACTIONS =
            ImmutableSet.of(Intent.ACTION_MY_PACKAGE_REPLACED, Intent.ACTION_BOOT_COMPLETED);

    @Inject
    public AlarmService alarmService;

    @Override
    public void onReceive(Context context, Intent intent) {
        App.getComponent().inject(this);
        HandlerThread handlerThread =  new HandlerThread("database_helper");
        handlerThread.start();
        Handler handler =  new Handler(handlerThread.getLooper());
        handler.post(() -> {
            Log.i(LOG_TAG, "Resetting alarms.");
            if (intent.getAction() == null || !ACTIONS.contains(intent.getAction())) {
                Log.w(LOG_TAG, String.format("Bad intent action %s", intent.getAction()));
            }
            for (Alarm alarm : alarmService.listAlarmsSync()) {
                if (alarm.isEnabled()) {
                    alarm.setRinging(null);
                    alarmService.setAlarm(alarm, AlarmService.AlarmAction.ENABLE);
                }
            }
            Log.i(LOG_TAG, "All alarms were reset.");
        });

    }
}
