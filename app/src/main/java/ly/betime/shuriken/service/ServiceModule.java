package ly.betime.shuriken.service;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Provides
    public AlarmService alarmService(AlarmServiceImpl alarmService) {
        return alarmService;
    }

    @Provides
    public GeneratedAlarmService generatedAlarmService(GeneratedAlarmServiceImpl generatedAlarmService) {
        return generatedAlarmService;
    }

    @Provides EventPreparationEstimator eventPreparationEstimator(EventPreparationEstimatorImpl impl) {
        return impl;
    }
}
