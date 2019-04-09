package ly.betime.shuriken.dagger;

import dagger.Component;
import ly.betime.shuriken.activities.ActiveAlarmActivity;
import ly.betime.shuriken.activities.AlarmFormActivity;
import ly.betime.shuriken.activities.AlarmsActivity;
import ly.betime.shuriken.persistance.PersistanceModule;
import ly.betime.shuriken.receivers.AlarmReceiver;
import ly.betime.shuriken.receivers.ReloadAlarmsReceiver;
import ly.betime.shuriken.service.ServiceModule;

@Component(modules = {ApplicationModule.class, PersistanceModule.class, ServiceModule.class})
public interface MyComponent {
    @Component.Builder
    interface Builder {
        MyComponent build();

        Builder serviceModule(ServiceModule module);

        Builder persistanceModule(PersistanceModule module);

        Builder applicationModule(ApplicationModule module);

    }

    void inject(AlarmFormActivity app);

    void inject(AlarmsActivity app);

    void inject(ActiveAlarmActivity app);

    void inject(ReloadAlarmsReceiver app);

    void inject(AlarmReceiver alarmReceiver);

}
