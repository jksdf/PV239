package ly.betime.shuriken.service;

import android.app.AlarmManager;
import android.content.Context;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import ly.betime.shuriken.activities.AlarmsActivity;

@Module
public class AlarmApiModule {
    @Provides
    public AlarmManagerApi alarmManagerApi(AlarmManager alarmManager, @Named("application")Context context) {
        return new AlarmManagerApi(alarmManager, context, AlarmsActivity.class);
    }
}
