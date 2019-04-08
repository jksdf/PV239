package ly.betime.shuriken.service;

import java.util.List;

import androidx.lifecycle.LiveData;
import ly.betime.shuriken.entities.Alarm;

public interface AlarmService {
    LiveData<List<Alarm>> listAlarms();
    List<Alarm> listAlarmsSync();

    LiveData<Alarm> getAlarm(int id);

    Alarm getAlarmSync(int id);

    void updateAlarm(Alarm alarm);

    void createAlarm(Alarm alarm, boolean enable);

    void removeAlarm(Alarm alarm);

    void setAlarm(Alarm alarm, AlarmAction action);

    enum AlarmAction {
        DISABLE, ENABLE, SNOOZE
    }
}
