package ly.betime.shuriken.service;

import java.util.List;

import ly.betime.shuriken.entities.Alarm;

public interface AlarmService {
    List<Alarm> listAlarms();

    void updateAlarm(Alarm alarm);

    void createAlarm(Alarm alarm);

    void removeAlarm(Alarm alarm);

    void setAlarm(Alarm alarm, boolean enable);
}
