package ly.betime.shuriken.service;

import dagger.Module;
import dagger.Provides;
import ly.betime.shuriken.dagger.MyApplication;
import retrofit2.Retrofit;

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

    @Provides
    @MyApplication
    DistanceMatrixApi distanceMatrixApi(@MyApplication Retrofit retrofit) {
        return retrofit.create(DistanceMatrixApi.class);
    }

}
