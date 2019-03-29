package ly.betime.shuriken.service;

public interface AlarmService {
    Iterable<AlarmEntity> listAlarms();
    void updateAlarm(AlarmEntity alarm);
    void createAlarm(AlarmEntity alarm);
    void removeAlarm(AlarmEntity alarm);
}
