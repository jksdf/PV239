package ly.betime.shuriken.dagger;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.threeten.bp.ZoneId;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import ly.betime.shuriken.activities.ActiveAlarmActivity;
import ly.betime.shuriken.activities.AlarmsActivity;
import ly.betime.shuriken.preferences.Preferences;

import static android.content.Context.ALARM_SERVICE;

@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    public SharedPreferences sharedPreferences() {
        return application.getApplicationContext().getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE);
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

    @Provides
    public ZoneId zoneId() {
        return ZoneId.systemDefault();
    }

}
