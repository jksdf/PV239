package ly.betime.shuriken.persistance;

import dagger.Component;
import ly.betime.shuriken.activities.AlarmFormActivity;
import ly.betime.shuriken.dagger.ApplicationModule;
import ly.betime.shuriken.service.ServiceModule;

@Component(modules = {ApplicationModule.class, PersistanceModule.class, ServiceModule.class})
public interface PersistanceComponent {
//    @Component.Builder
//    interface Builder {
//        ly.betime.shuriken.dagger.MyComponent build();
//
//        ly.betime.shuriken.dagger.MyComponent.Builder serviceModule(ServiceModule module);
//
//        ly.betime.shuriken.dagger.MyComponent.Builder persistanceModule(PersistanceModule module);
//
//        ly.betime.shuriken.dagger.MyComponent.Builder ApplicationModule(ApplicationModule module);
//    }

    void inject(AlarmFormActivity app);
}

