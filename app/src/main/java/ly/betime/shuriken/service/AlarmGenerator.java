package ly.betime.shuriken.service;

import android.content.SharedPreferences;
import android.provider.CalendarContract;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.List;

import javax.inject.Inject;

import ly.betime.shuriken.apis.CalendarApi;
import ly.betime.shuriken.apis.CalendarEvent;

public class AlarmGenerator {
    static final int DEFAULT_ALARM_HOUR = 8;
    static final int DEFAULT_ALARM_MINUTE = 0;

    private final CalendarApi calendarApi;
    private final SharedPreferences sharedPreferences;
    private final EventPrepEstimate eventPrepEstimate;
    private final ZoneId zoneId;

    @Inject
    public AlarmGenerator(CalendarApi calendarApi, SharedPreferences sharedPreferences, EventPrepEstimate eventPrepEstimate, ZoneId zoneId) {
        this.calendarApi = calendarApi;
        this.sharedPreferences = sharedPreferences;
        this.eventPrepEstimate = eventPrepEstimate;
        this.zoneId = zoneId;
    }

    public LocalTime estimateTime(LocalDate date) {
        int hour = sharedPreferences.getInt("DefaultAlarmHour", DEFAULT_ALARM_HOUR);
        int minute = sharedPreferences.getInt("DefaultAlarmMinute", DEFAULT_ALARM_MINUTE);
        LocalTime time = LocalTime.of(hour, minute);
        long start = date.atStartOfDay().atZone(zoneId).toInstant().toEpochMilli();
        List<CalendarEvent> events = calendarApi.getEvents(date, date.plusDays(1));
        CalendarEvent nextEvent = null;
        for (CalendarEvent event : events) {
            if (event.getStatus() == CalendarContract.Events.STATUS_CONFIRMED) {
                if (nextEvent == null || event.getFrom().isBefore(nextEvent.getFrom())) {
                    nextEvent = event;
                }
            }
        }
        if (nextEvent == null) {
            return time;
        }
        LocalDateTime preppedDateTime = nextEvent.getFrom().minus(eventPrepEstimate.timeToPrep(nextEvent), ChronoUnit.MILLIS);
        if (nextEvent.getFrom().getDayOfMonth() != preppedDateTime.getDayOfMonth()) {
            return time;
        }

        LocalTime preppedTime = preppedDateTime.toLocalTime();

        if (preppedTime.isBefore(time)) {
            return preppedTime;
        }
        return time;
    }


    public static class EventPrepEstimate {

        @Inject
        public EventPrepEstimate() {
        }

        private long timeToPrep(CalendarEvent event) {
            return 1000L * 60 * 90;
        }
    }
}
