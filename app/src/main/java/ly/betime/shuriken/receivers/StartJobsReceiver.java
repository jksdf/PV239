package ly.betime.shuriken.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

import javax.inject.Inject;

import ly.betime.shuriken.App;
import ly.betime.shuriken.BuildConfig;

public class StartJobsReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "StartJobsReceiver";
    //TODO(slivka): move to settings
    private static final long POLL_RATE = BuildConfig.DEBUG ? 60000L : AlarmManager.INTERVAL_HALF_HOUR;
    private static final Set<String> ACTIONS =
            ImmutableSet.of(Intent.ACTION_MY_PACKAGE_REPLACED, Intent.ACTION_BOOT_COMPLETED);

    @Inject
    public AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || !ACTIONS.contains(intent.getAction())) {
            Log.w(LOG_TAG, String.format("Bad intent action %s", intent.getAction()));
        }
        Log.i(LOG_TAG, "Starting a calendar checker with poll rate " + POLL_RATE);
        App.getComponent().inject(this);
        Intent jobIntent = new Intent(context.getApplicationContext(), CalendarCheckReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(context.getApplicationContext(), 0, jobIntent, 0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, POLL_RATE, broadcast);
        context.getApplicationContext().sendBroadcast(jobIntent);
    }
}
