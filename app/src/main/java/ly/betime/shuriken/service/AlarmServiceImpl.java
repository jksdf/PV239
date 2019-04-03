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
    public void createAlarm(Alarm alarm, boolean enabled) {
        Log.i(LOG_TAG, "Creating alarm.");
        alarm.setId((int) alarmDao.insert(alarm));
        if (enabled) {
            setAlarm(alarm, AlarmAction.ENABLE);
        }
    }

    @Override
    public void removeAlarm(Alarm alarm) {
        Log.i(LOG_TAG, String.format("Alarm %d removed.", alarm.getId()));
        if (alarm.isEnabled()) {
            this.setAlarm(alarm, AlarmAction.DISABLE);
        }
        alarmDao.delete(alarm);
    }



    @Override
    public void setAlarm(Alarm alarm, AlarmAction action) {
        Log.i(LOG_TAG, String.format("Alarm %d set to %s", alarm.getId(), action));
        if (alarm.isEnabled() && action == AlarmAction.ENABLE) {
            Log.w(LOG_TAG, String.format("Alarm %d was already set, skipping.", alarm.getId()));
            return;
        }
        if (action == AlarmAction.ENABLE) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime adjusted = now.with(alarm.getTime());
            if (adjusted.isBefore(now)) {
                adjusted = adjusted.plusDays(1);
            }
            now = adjusted;
            if (!alarm.getRepeating().isEmpty()) {
                while (!alarm.getRepeating().contains(now.getDayOfWeek())) {
                    now = now.plusDays(1);
                }
                now = now.with(alarm.getTime());
            }
            alarm.setRinging(now);
            alarmDao.update(alarm);
            alarmManagerApi.setAlarm(alarm.getId(), now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        } else if (action == AlarmAction.DISABLE) {
            alarm.setRinging(null);
            alarmDao.update(alarm);
            alarmManagerApi.cancelAlarm(alarm.getId());
        } else if (action == AlarmAction.SNOOZE) {
            //TODO(slivka): since snooze or since start of alarm (how to handle alarm ringing for 10+ mins)
            alarm.setRinging(LocalDateTime.now().plusMinutes(10));
            alarmDao.update(alarm);
            alarmManagerApi.cancelAlarm(alarm.getId());
            alarmManagerApi.setAlarm(alarm.getId(), alarm.getRinging().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }
}
