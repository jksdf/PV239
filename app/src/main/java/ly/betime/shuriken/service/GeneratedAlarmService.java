package ly.betime.shuriken.service;

import org.threeten.bp.LocalDate;

import androidx.lifecycle.LiveData;
import ly.betime.shuriken.entities.GeneratedAlarm;

public interface GeneratedAlarmService {

    void cleanUp();

    LiveData<GeneratedAlarm> get(LocalDate date);

    LiveData<GeneratedAlarm> get(int id);

    void snooze(GeneratedAlarm generatedAlarm);

    void disable(GeneratedAlarm generatedAlarm);

    void enable(GeneratedAlarm generatedAlarm);
}
