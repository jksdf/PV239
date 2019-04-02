package ly.betime.shuriken.service;

import android.util.Log;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.List;

import javax.inject.Inject;

import ly.betime.shuriken.apis.AlarmManagerApi;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.persistance.AlarmDAO;

import static com.google.common.base.Preconditions.checkNotNull;

public class AlarmServiceImpl implements AlarmService {

    private static final String LOG_TAG = "AlarmServiceImpl";

    private final AlarmDAO alarmDao;
    private final AlarmManagerApi alarmManagerApi;

    @Inject
    public AlarmServiceImpl(AlarmDAO alarmDao, AlarmManagerApi alarmManagerApi) {
        this.alarmDao = checkNotNull(alarmDao);
        this.alarmManagerApi = checkNotNull(alarmManagerApi);
    }

    @Override
    public List<Alarm> listAlarms() {
        Log.i(LOG_TAG, "Listing alarms.");
        return alarmDao.list();
    }

    @Override
    public Alarm getAlarm(int id) {
        Log.i(LOG_TAG, "Getting alarm " + id);
        return alarmDao.get(id);
    }

    @Override
    public void updateAlarm(Alarm alarm) {
        Log.i(LOG_TAG, "Updating alarm " + alarm.getId());
        alarmDao.update(alarm);
    }

    @Override
    public void createAlarm(Alarm alarm) {
        Log.i(LOG_TAG, "Creating alarm.");
        alarm.setId((int) alarmDao.insert(alarm));
        if (alarm.isEnabled()) {
            alarm.setEnabled(false);
            setAlarm(alarm, true);
        }
    }

    @Override
    public void removeAlarm(Alarm alarm) {
        Log.i(LOG_TAG, String.format("Alarm %d removed.", alarm.getId()));
        if (alarm.isEnabled()) {
            this.setAlarm(alarm, false);
        }
        alarmDao.delete(alarm);
    }

    @Override
    public void setAlarm(Alarm alarm, boolean enable) {
        Log.i(LOG_TAG, String.format("Alarm %d set to %b", alarm.getId(), enable));
        if (enable == alarm.isEnabled()) {
            Log.w(LOG_TAG, String.format("Alarm %d was already set correctly, skipping.", alarm.getId()));
            return;
        }
        alarm.setEnabled(enable);
        alarmDao.update(alarm);
        if (enable) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime adjusted = now.with(alarm.getTime());
            if (adjusted.isBefore(now)) {
                adjusted = adjusted.plusDays(1);
            }
            alarmManagerApi.setAlarm(alarm.getId(), adjusted.atZone(ZoneId.systemDefault()).toEpochSecond());
        } else {
            alarmManagerApi.cancelAlarm(alarm.getId());
        }
    }
}
