package ly.betime.shuriken;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

//import ly.betime.shuriken.dagger.DaggerMyComponent;
import ly.betime.shuriken.dagger.ApplicationModule;
import ly.betime.shuriken.dagger.DaggerMyComponent;
import ly.betime.shuriken.dagger.MyComponent;
import ly.betime.shuriken.persistance.PersistanceModule;
import ly.betime.shuriken.service.ServiceModule;

public class App extends Application {

    public static MyComponent component;

//    @Inject
//    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerMyComponent.builder().ApplicationModule(new ApplicationModule(this)).persistanceModule(new PersistanceModule()).serviceModule(new ServiceModule(this)).build();
        AndroidThreeTen.init(getApplicationContext());
    }

//    @Override
//    public AndroidInjector<Activity> activityInjector() {
//        return dispatchingActivityInjector;
//    }
}
