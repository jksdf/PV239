package ly.betime.shuriken.service;

import com.google.common.util.concurrent.ListenableFuture;

import org.threeten.bp.LocalDate;

import androidx.lifecycle.LiveData;
import ly.betime.shuriken.entities.GeneratedAlarm;

public interface GeneratedAlarmService {

    void cleanUp();

    ListenableFuture<GeneratedAlarm> get(LocalDate date);

    LiveData<GeneratedAlarm> get(int id);
}
