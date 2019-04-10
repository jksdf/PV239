package ly.betime.shuriken.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.threeten.bp.LocalDate;

import javax.inject.Inject;

import ly.betime.shuriken.App;
import ly.betime.shuriken.service.GeneratedAlarmService;

public class CalendarCheckReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "CalendarCheckReceiver";

    @Inject
    public GeneratedAlarmService generatedAlarmService;

    @Override
    public void onReceive(Context context, Intent intent) {
        App.getComponent().inject(this);
        Log.i(LOG_TAG, "Checking calendar tomorrow, adjusting alarm.");
        generatedAlarmService.get(LocalDate.now().plusDays(1));
    }
}
