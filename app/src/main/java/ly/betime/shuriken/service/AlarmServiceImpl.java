package ly.betime.shuriken.service;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
        new UpdateTask(alarmDao).execute(alarm);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void createAlarm(Alarm alarm, boolean enabled) {
        Log.i(LOG_TAG, "Creating alarm.");
        //TODO(slivka): try to find a nicer solution maybe
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
            new UpdateTask(alarmDao).execute(alarm);
            alarmManagerApi.setAlarm(alarm.getId(), now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        } else if (action == AlarmAction.DISABLE) {
            alarm.setRinging(null);
            new UpdateTask(alarmDao).execute(alarm);
            alarmManagerApi.cancelAlarm(alarm.getId());
        } else if (action == AlarmAction.SNOOZE) {
            alarm.setRinging(LocalDateTime.now().plusMinutes(sharedPreferences.getInt(Preferences.SNOOZE_TIME, 10)));
            new UpdateTask(alarmDao).execute(alarm);
            alarmManagerApi.cancelAlarm(alarm.getId());
            alarmManagerApi.setAlarm(alarm.getId(), alarm.getRinging().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
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
