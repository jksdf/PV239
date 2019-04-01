package ly.betime.shuriken.service;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Named;
import javax.inject.Singleton;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import dagger.Module;
import dagger.Provides;
import ly.betime.shuriken.activities.AlarmsActivity;
import ly.betime.shuriken.helpers.LanguageTextHelper;
import ly.betime.shuriken.persistance.AppDatabase;

import static android.content.Context.ALARM_SERVICE;

@Module
public class ServiceModule {

    private final Application application;

    public ServiceModule(Application application) {
        this.application = application;
    }
//
//    @Provides
//    public SharedPreferences sharedPreferences() {
//        return PreferenceManager.getDefaultSharedPreferences(application);
//    }

//    @Provides
//    @Named("application")
//    public Context application() {
//        return application;
//    }

    @Provides
    @Singleton
    public AlarmManager alarmManager() {
        return (AlarmManager) application.getSystemService(ALARM_SERVICE);
    }



    @Provides
    public AlarmService alarmService(AppDatabase db) {
        return new AlarmServiceImpl(db.alarmDAO(), new AlarmManagerApi((AlarmManager) application.getSystemService(ALARM_SERVICE), application.getApplicationContext(), AlarmsActivity.class));
    }

    @Provides
    public LanguageTextHelper languageTextHelper() {
        return new LanguageTextHelper(application.getApplicationContext());
    }
}
