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

import javax.inject.Inject;

import ly.betime.shuriken.apis.CalendarApi;
import ly.betime.shuriken.apis.CalendarEvent;
import ly.betime.shuriken.entities.GeneratedAlarm;

public class AlarmGenerator {
    static final int DEFAULT_ALARM_HOUR = 10;
    static final int DEFAULT_ALARM_MINUTE = 0;
    private static final String LOG_TAG = "AlarmGenerator";

    private final CalendarApi calendarApi;
    private final SharedPreferences sharedPreferences;
    private final EventPrepEstimate eventPrepEstimate;

    @Inject
    public AlarmGenerator(CalendarApi calendarApi, SharedPreferences sharedPreferences, EventPrepEstimate eventPrepEstimate) {
        this.calendarApi = calendarApi;
        this.sharedPreferences = sharedPreferences;
        this.eventPrepEstimate = eventPrepEstimate;
    }

    public ListenableFuture<GeneratedAlarm> generateAlarm(LocalDate date) {
        GeneratedAlarm generatedAlarm = new GeneratedAlarm();
        int hour = sharedPreferences.getInt("DefaultAlarmHour", DEFAULT_ALARM_HOUR);
        int minute = sharedPreferences.getInt("DefaultAlarmMinute", DEFAULT_ALARM_MINUTE);
        LocalTime defaultTime = LocalTime.of(hour, minute);
        List<CalendarEvent> events = calendarApi.getEvents(date, date.plusDays(1));
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
            LocalDateTime preppedDateTime = nextEvent.getFrom().minus(eventPrepEstimate.timeToPrep(nextEvent), ChronoUnit.MILLIS);
            if (nextEvent.getFrom().getDayOfMonth() == preppedDateTime.getDayOfMonth()) {
                LocalTime preppedTime = preppedDateTime.toLocalTime();

                if (preppedTime.isBefore(defaultTime)) {
                    generatedAlarm.setTime(preppedTime);
                    generatedAlarm.setEventId(nextEvent.getEventId());
                }
            }
        }
        generatedAlarm.setForDate(date);
        Log.d(LOG_TAG, "alarm is " + generatedAlarm);
        return Futures.immediateFuture(generatedAlarm);
    }

    public static class EventPrepEstimate {

        @Inject
        public EventPrepEstimate() {
        }

        @SuppressWarnings("unused")
        private long timeToPrep(CalendarEvent event) {
            return 1000L * 60 * 90;
        }
    }
}
