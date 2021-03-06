package ly.betime.shuriken.dagger;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.util.concurrent.MoreExecutors;

import org.threeten.bp.ZoneId;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import ly.betime.shuriken.activities.ActiveAlarmActivity;
import ly.betime.shuriken.preferences.Preferences;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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

    @Provides
    @MyApplication
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(1);
    }

    @Provides
    @MyApplication
    public Executor executor(@MyApplication ExecutorService service) {
        return service;
    }

    @Provides
    @MyApplication
    public Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
