package ly.betime.shuriken.service;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import ly.betime.shuriken.apis.AlarmManagerApi;
import ly.betime.shuriken.entities.GeneratedAlarm;
import ly.betime.shuriken.persistance.GeneratedAlarmDAO;
import ly.betime.shuriken.preferences.Preferences;

public class GeneratedAlarmServiceImpl implements GeneratedAlarmService {
    private static final String LOG_TAG = "GeneratedAlarmServiceIm";

    private final AlarmGenerator alarmGenerator;
    private final GeneratedAlarmDAO generatedAlarmDAO;
    private final AlarmManagerApi alarmManagerApi;
    private final SharedPreferences sharedPreferences;

    @Inject
    public GeneratedAlarmServiceImpl(AlarmGenerator alarmGenerator, GeneratedAlarmDAO generatedAlarmDAO, AlarmManagerApi alarmManagerApi, SharedPreferences sharedPreferences) {
        this.alarmGenerator = alarmGenerator;
        this.generatedAlarmDAO = generatedAlarmDAO;
        this.alarmManagerApi = alarmManagerApi;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void cleanUp() {
        new CleanUp(generatedAlarmDAO).execute();
    }

    @Override
    public LiveData<GeneratedAlarm> get(LocalDate date) {
        ListenableFuture<GeneratedAlarm> suggestedAlarmFuture = alarmGenerator.generateAlarm(date);
        ListenableFuture<GeneratedAlarm> persistedAlarmFuture = generatedAlarmDAO.get(date);
        return futureToLiveData(Futures.whenAllComplete(ImmutableList.of(suggestedAlarmFuture, persistedAlarmFuture)).callAsync(() -> {
            GeneratedAlarm suggestedAlarm = suggestedAlarmFuture.get();
            GeneratedAlarm persistedAlarm = persistedAlarmFuture.get();
            if (persistedAlarm == null) {
                Log.i(LOG_TAG, "Creating alarm " + suggestedAlarm);
                Futures.transform(generatedAlarmDAO.insert(suggestedAlarm), id -> {
                    suggestedAlarm.setId(id.intValue());
                    alarmManagerApi.setAlarm(suggestedAlarm.getId(), AlarmManagerApi.AlarmType.GENERATED, suggestedAlarm.getRinging().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                    return suggestedAlarm;
                }, MoreExecutors.directExecutor());
            } else {
                suggestedAlarm.setId(persistedAlarm.getId());
                if (!suggestedAlarm.equals(persistedAlarm)) {
                    Log.i(LOG_TAG, "Updating alarm " + persistedAlarm + " to " + suggestedAlarm);
                    new UpdateAlarm(generatedAlarmDAO, alarmManagerApi).doInBackground(suggestedAlarm);
                }
                return Futures.immediateFuture(suggestedAlarm);
            }
            return null;
        }, MoreExecutors.directExecutor()));
    }

    @Override
    public LiveData<GeneratedAlarm> get(int id) {
        return generatedAlarmDAO.get(id);
    }

    @Override
    public void snooze(GeneratedAlarm generatedAlarm) {
        generatedAlarm.setRinging(generatedAlarm.getRinging().plusMinutes(sharedPreferences.getInt(Preferences.SNOOZE_TIME, Preferences.SNOOZE_TIME_DEFAULT)));
        long time = generatedAlarm.getRinging().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        alarmManagerApi.setAlarm(generatedAlarm.getId(), AlarmManagerApi.AlarmType.GENERATED, time);
    }

    private static class UpdateAlarm extends AsyncTask<GeneratedAlarm, Void, Void> {

        private final GeneratedAlarmDAO generatedAlarmDAO;
        private final AlarmManagerApi alarmManagerApi;

        public UpdateAlarm(GeneratedAlarmDAO generatedAlarmDAO, AlarmManagerApi alarmManagerApi) {
            this.generatedAlarmDAO = generatedAlarmDAO;
            this.alarmManagerApi = alarmManagerApi;
        }

        @Override
        protected Void doInBackground(GeneratedAlarm... alarms) {
            alarmManagerApi.setAlarm(alarms[0].getId(), AlarmManagerApi.AlarmType.GENERATED, alarms[0].getRinging().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            generatedAlarmDAO.update(alarms[0]);
            return null;
        }
    }

    private static class CleanUp extends AsyncTask<Void, Void, Void> {

        private final GeneratedAlarmDAO generatedAlarmDAO;

        public CleanUp(GeneratedAlarmDAO generatedAlarmDAO) {
            this.generatedAlarmDAO = generatedAlarmDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<GeneratedAlarm> alarms = generatedAlarmDAO.listSync();
            LocalDate now = LocalDate.now();
            for (GeneratedAlarm alarm : alarms) {
                if (alarm.getForDate().isBefore(now)) {
                    generatedAlarmDAO.delete(alarm);
                }
            }
            return null;
        }
    }

    private static <T> LiveData<T> futureToLiveData(ListenableFuture<T> future) {
        MutableLiveData<T> liveData = new MutableLiveData<>();
        future.addListener(() -> {
            liveData.postValue(Futures.getUnchecked(future));
        }, MoreExecutors.directExecutor());
        return liveData;
    }
}