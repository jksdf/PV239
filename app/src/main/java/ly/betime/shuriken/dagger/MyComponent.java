package ly.betime.shuriken.dagger;

import android.app.Activity;

import dagger.Component;
import ly.betime.shuriken.activities.AlarmFormActivity;
import ly.betime.shuriken.activities.AlarmsActivity;
import ly.betime.shuriken.persistance.PersistanceModule;
import ly.betime.shuriken.service.AlarmApiModule;
import ly.betime.shuriken.service.ServiceModule;

@Component(modules = {ApplicationModule.class, PersistanceModule.class, ServiceModule.class, AlarmApiModule.class})
public interface MyComponent {
    @Component.Builder
    interface Builder {
        MyComponent build();

        Builder serviceModule(ServiceModule module);

        Builder persistanceModule(PersistanceModule module);

        Builder applicationModule(ApplicationModule module);

        Builder alarmApiModule(AlarmApiModule module);
    }

    void inject(AlarmFormActivity app);

    void inject(AlarmsActivity app);
}
