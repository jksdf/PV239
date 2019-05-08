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
import ly.betime.shuriken.R;

public class StartJobsReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "StartJobsReceiver";
    private static final Set<String> ACTIONS =
            ImmutableSet.of(Intent.ACTION_MY_PACKAGE_REPLACED, Intent.ACTION_BOOT_COMPLETED);

    @Inject
    public AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || !ACTIONS.contains(intent.getAction())) {
            Log.w(LOG_TAG, String.format("Bad intent action %s", intent.getAction()));
        }
        long pollRate =
                BuildConfig.DEBUG
                        ? context.getResources().getInteger(R.integer.poll_rate_debug)
                        : context.getResources().getInteger(R.integer.poll_rate_normal);
        Log.i(LOG_TAG, "Starting a calendar checker with poll rate " + pollRate);
        App.getComponent().inject(this);
        Intent jobIntent = new Intent(context.getApplicationContext(), CalendarCheckReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(context.getApplicationContext(), 0, jobIntent, 0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, pollRate, broadcast);
        context.getApplicationContext().sendBroadcast(jobIntent);
    }
}
