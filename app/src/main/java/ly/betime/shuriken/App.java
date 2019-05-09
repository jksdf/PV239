package ly.betime.shuriken;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

import androidx.multidex.MultiDexApplication;
import ly.betime.shuriken.dagger.ApplicationModule;
import ly.betime.shuriken.dagger.MyComponent;
import ly.betime.shuriken.persistance.PersistanceModule;
import ly.betime.shuriken.service.ServiceModule;

public class App extends MultiDexApplication {

    private static MyComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = ly.betime.shuriken.dagger.DaggerMyComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .persistanceModule(new PersistanceModule(this.getApplicationContext()))
                .serviceModule(new ServiceModule())
                .build();
        AndroidThreeTen.init(getApplicationContext());
    }

    public static MyComponent getComponent() {
        return component;
    }
}
