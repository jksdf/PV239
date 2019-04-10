package ly.betime.shuriken.service;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import org.threeten.bp.LocalDate;

import java.util.List;

import javax.inject.Inject;

import ly.betime.shuriken.entities.GeneratedAlarm;
import ly.betime.shuriken.persistance.GeneratedAlarmDAO;

public class GeneratedAlarmServiceImpl implements GeneratedAlarmService {
    private static final String LOG_TAG = "GeneratedAlarmServiceIm";

    private final AlarmGenerator alarmGenerator;
    private final GeneratedAlarmDAO generatedAlarmDAO;

    @Inject
    public GeneratedAlarmServiceImpl(AlarmGenerator alarmGenerator, GeneratedAlarmDAO generatedAlarmDAO) {
        this.alarmGenerator = alarmGenerator;
        this.generatedAlarmDAO = generatedAlarmDAO;
    }

    @Override
    public void cleanUp() {
        new CleanUp(generatedAlarmDAO).execute();
    }

    @Override
    public ListenableFuture<GeneratedAlarm> get(LocalDate date) {
        ListenableFuture<GeneratedAlarm> suggestedAlarmFuture = alarmGenerator.generateAlarm(date);
        ListenableFuture<GeneratedAlarm> persistedAlarmFuture = generatedAlarmDAO.get(date);
        return Futures.whenAllComplete(ImmutableList.of(suggestedAlarmFuture, persistedAlarmFuture)).callAsync(() -> {
            GeneratedAlarm suggestedAlarm = suggestedAlarmFuture.get();
            GeneratedAlarm persistedAlarm = persistedAlarmFuture.get();
            Log.d(LOG_TAG, "" + suggestedAlarm);
            Log.d(LOG_TAG, "" + persistedAlarm);
            if (persistedAlarm == null) {
                Log.i(LOG_TAG, "Creating alarm " + suggestedAlarm);
                return Futures.transform(generatedAlarmDAO.insert(suggestedAlarm), id -> {
                    suggestedAlarm.setId((int) (long) id);
                    return suggestedAlarm;
                }, MoreExecutors.directExecutor());
            } else {
                suggestedAlarm.setId(persistedAlarm.getId());
                if (!suggestedAlarm.equals(persistedAlarm)) {
                    Log.i(LOG_TAG, "Updating alarm " + persistedAlarm + " to " + suggestedAlarm);
                    new UpdateAlarm(generatedAlarmDAO).doInBackground(suggestedAlarm);
                }
                return Futures.immediateFuture(suggestedAlarm);
            }
        }, MoreExecutors.directExecutor());
    }

    private static class UpdateAlarm extends AsyncTask<GeneratedAlarm, Void, Void> {

        private final GeneratedAlarmDAO generatedAlarmDAO;

        public UpdateAlarm(GeneratedAlarmDAO generatedAlarmDAO) {
            this.generatedAlarmDAO = generatedAlarmDAO;
        }

        @Override
        protected Void doInBackground(GeneratedAlarm... alarms) {
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
}
