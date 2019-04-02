package ly.betime.shuriken.dagger;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import ly.betime.shuriken.activities.ActiveAlarmActivity;
import ly.betime.shuriken.activities.AlarmsActivity;

import static android.content.Context.ALARM_SERVICE;

@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    public SharedPreferences sharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
    }

    @Provides
    @Named("application")
    public Context application() {
        return application.getApplicationContext();
    }

    @Provides
    public AlarmManager alarmManager() {
        return (AlarmManager) application.getSystemService(ALARM_SERVICE);
    }

    @Provides
    @Named("AlarmActivity")
    public Class<? extends Activity> alarmActivity() {
        return ActiveAlarmActivity.class;
    }

}
