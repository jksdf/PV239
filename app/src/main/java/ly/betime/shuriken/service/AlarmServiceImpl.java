package ly.betime.shuriken.service;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import ly.betime.shuriken.apis.AlarmManagerApi;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.persistance.AlarmDAO;
import ly.betime.shuriken.preferences.Preferences;

import static com.google.common.base.Preconditions.checkNotNull;

public class AlarmServiceImpl implements AlarmService {

    private static final String LOG_TAG = "AlarmServiceImpl";

    private final AlarmDAO alarmDao;
    private final AlarmManagerApi alarmManagerApi;
    private final SharedPreferences sharedPreferences;

    @Inject
    public AlarmServiceImpl(AlarmDAO alarmDao, AlarmManagerApi alarmManagerApi, SharedPreferences sharedPreferences) {
        this.alarmDao = checkNotNull(alarmDao);
        this.alarmManagerApi = checkNotNull(alarmManagerApi);
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public LiveData<List<Alarm>> listAlarms() {
        Log.i(LOG_TAG, "Listing alarms.");
        return alarmDao.list();
    }

    @Override
    public List<Alarm> listAlarmsSync() {
        Log.i(LOG_TAG, "Listing alarms.");
        return alarmDao.listSync();
    }

    @Override
    public LiveData<Alarm> getAlarm(int id) {
        Log.i(LOG_TAG, "Getting alarm " + id);
        return alarmDao.get(id);
    }

    @Override
    public Alarm getAlarmSync(int id) {
        Log.i(LOG_TAG, "Getting alarm sync " + id);
        return alarmDao.getSync(id);
    }

    @Override
    public void updateAlarm(Alarm alarm) {
        Log.i(LOG_TAG, "Updating alarm " + alarm.getId());
        if (alarm.isEnabled()) {
            LocalDateTime nextRinging = calculateNextRinging(alarm);
            alarm.setRinging(nextRinging);
            alarmManagerApi.cancelAlarm(alarm.getId(), AlarmManagerApi.AlarmType.NORMAL);
            alarmManagerApi.setAlarm(alarm.getId(), AlarmManagerApi.AlarmType.NORMAL, nextRinging.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        } else {
            alarm.setRinging(null);
        }
        new UpdateTask(alarmDao).execute(alarm);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void createAlarm(Alarm alarm, boolean enabled) {
        Log.i(LOG_TAG, "Creating alarm.");
        new AsyncTask<Alarm, Void, Alarm>() {
            @Override
            protected Alarm doInBackground(Alarm... alarms) {
                if (alarms.length != 1) {
                    throw new IllegalArgumentException("Call with one alarm.");
                }
                alarms[0].setId((int) alarmDao.insert(alarms[0]));
                return alarms[0];
            }

            @Override
            protected void onPostExecute(Alarm alarm) {
                if (enabled) {
                    setAlarm(alarm, AlarmAction.ENABLE);
                }
            }
        }.execute(alarm);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void removeAlarm(Alarm alarm) {
        new AsyncTask<Alarm, Void, Alarm>() {
            @Override
            protected Alarm doInBackground(Alarm... alarms) {
                alarmDao.delete(alarm);
                return alarm;
            }

            @Override
            protected void onPostExecute(Alarm alarm) {
                if (alarm.isEnabled()) {
                    setAlarm(alarm, AlarmAction.DISABLE);
                }
                Log.i(LOG_TAG, String.format("Alarm %d removed.", alarm.getId()));
            }
        }.execute(alarm);
    }



    @Override
    public void setAlarm(Alarm alarm, AlarmAction action) {
        Log.i(LOG_TAG, String.format("Alarm %d set to %s", alarm.getId(), action));
        if (alarm.isEnabled() && action == AlarmAction.ENABLE) {
            Log.w(LOG_TAG, String.format("Alarm %d was already set, skipping.", alarm.getId()));
            return;
        }
        switch (action) {
            case ENABLE:
                LocalDateTime nextRinging = calculateNextRinging(alarm);
                new UpdateTask(alarmDao).execute(alarm);
                alarmManagerApi.setAlarm(alarm.getId(), AlarmManagerApi.AlarmType.NORMAL, nextRinging.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                return;
            case DISABLE:
                alarm.setRinging(null);
                new UpdateTask(alarmDao).execute(alarm);
                alarmManagerApi.cancelAlarm(alarm.getId(), AlarmManagerApi.AlarmType.NORMAL);
                return;
            case SNOOZE:
                alarm.setRinging(LocalDateTime.now().plusMinutes(sharedPreferences.getInt(Preferences.SNOOZE_TIME, Preferences.SNOOZE_TIME_DEFAULT)));
                new UpdateTask(alarmDao).execute(alarm);
                alarmManagerApi.cancelAlarm(alarm.getId(), AlarmManagerApi.AlarmType.NORMAL);
                alarmManagerApi.setAlarm(alarm.getId(), AlarmManagerApi.AlarmType.NORMAL, alarm.getRinging().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                return;
            default:
                throw new AssertionError();
        }
    }

    private LocalDateTime calculateNextRinging(Alarm alarm) {
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
        return now;
    }

    private static class UpdateTask extends AsyncTask<Alarm, Void, Void> {

        private final AlarmDAO alarmDao;

        private UpdateTask(AlarmDAO alarmDao) {
            this.alarmDao = alarmDao;
        }

        @Override
        protected Void doInBackground(Alarm... alarms) {
            if (alarms.length != 1) {
                throw new IllegalArgumentException("Call with one alarm.");
            }
            alarmDao.update(alarms[0]);
            return null;
        }
    }

}
