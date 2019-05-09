package ly.betime.shuriken.service;

import android.content.SharedPreferences;
import android.provider.CalendarContract;
import android.util.Log;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import ly.betime.shuriken.apis.CalendarApi;
import ly.betime.shuriken.apis.CalendarEvent;
import ly.betime.shuriken.dagger.MyApplication;
import ly.betime.shuriken.entities.GeneratedAlarm;
import ly.betime.shuriken.preferences.Preferences;

public class AlarmGenerator {
    static final int DEFAULT_ALARM_HOUR = 10;
    static final int DEFAULT_ALARM_MINUTE = 0;
    private static final String LOG_TAG = "AlarmGenerator";

    private final CalendarApi calendarApi;
    private final SharedPreferences sharedPreferences;
    private final EventPreparationEstimator eventPrepEstimate;
    private final Executor executor;

    @Inject
    public AlarmGenerator(CalendarApi calendarApi, SharedPreferences sharedPreferences, EventPreparationEstimator eventPrepEstimate, @MyApplication Executor executor) {
        this.calendarApi = calendarApi;
        this.sharedPreferences = sharedPreferences;
        this.eventPrepEstimate = eventPrepEstimate;
        this.executor = executor;
    }

    public ListenableFuture<GeneratedAlarm> generateAlarm(LocalDate date) {
        GeneratedAlarm generatedAlarm = new GeneratedAlarm();
        int hour = sharedPreferences.getInt("DefaultAlarmHour", DEFAULT_ALARM_HOUR);
        int minute = sharedPreferences.getInt("DefaultAlarmMinute", DEFAULT_ALARM_MINUTE);
        LocalTime defaultTime = LocalTime.of(hour, minute);
        List<CalendarEvent> events = calendarApi.getEvents(date, date.plusDays(1), Preferences.parseInts(sharedPreferences.getString(Preferences.CALENDARS_SELECTED, Preferences.CALENDARS_SELECTED_DEFAULT)));
        CalendarEvent nextEvent = null;
        for (CalendarEvent event : events) {
            if (event.getStatus() == CalendarContract.Events.STATUS_CONFIRMED) {
                if (nextEvent == null || event.getFrom().isBefore(nextEvent.getFrom())) {
                    nextEvent = event;
                }
            }
        }
        generatedAlarm.setTime(defaultTime);
        if (nextEvent != null) {
            final CalendarEvent nextEventFinal = nextEvent;
            return Futures.transform(eventPrepEstimate.timeToPrep(nextEvent), time -> {
                LocalDateTime preppedDateTime = nextEventFinal.getFrom().minus(time, ChronoUnit.MILLIS);
                if (nextEventFinal.getFrom().getDayOfMonth() == preppedDateTime.getDayOfMonth()) {
                    LocalTime preppedTime = preppedDateTime.toLocalTime();

                    if (preppedTime.isBefore(defaultTime)) {
                        generatedAlarm.setTime(preppedTime);
                        generatedAlarm.setEventId(nextEventFinal.getEventId());
                    }
                }
                generatedAlarm.setForDate(date);
                Log.d(LOG_TAG, "alarm is " + generatedAlarm);
                return generatedAlarm;
            }, executor);

        }
        generatedAlarm.setForDate(date);
        Log.d(LOG_TAG, "alarm is " + generatedAlarm);
        return Futures.immediateFuture(generatedAlarm);
    }
}
