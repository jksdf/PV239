package ly.betime.shuriken.service;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Provides
    public AlarmService alarmService(AlarmServiceImpl alarmService) {
        return alarmService;
    }
}
