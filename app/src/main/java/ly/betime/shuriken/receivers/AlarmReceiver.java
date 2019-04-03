package ly.betime.shuriken.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import ly.betime.shuriken.App;
import ly.betime.shuriken.activities.ActiveAlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Inject
    @Named("AlarmActivity")
    public Class<? extends Activity> alarmActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        App.getComponent().inject(this);
        Log.i("AlarmRec", "Received alarm");
        Intent newIntent =
                new Intent(context, alarmActivity)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(
                                ActiveAlarmActivity.ALARM_ID_EXTRA_NAME,
                                intent.getIntExtra(ActiveAlarmActivity.ALARM_ID_EXTRA_NAME, -1));
        context.startActivity(newIntent);
    }
}
