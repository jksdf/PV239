package ly.betime.shuriken.service;

import android.content.SharedPreferences;
import android.provider.CalendarContract;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.threeten.bp.LocalDate;

import static com.google.common.truth.Truth.assertThat;

import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import java.util.List;

import ly.betime.shuriken.SharedPreferencesFake;
import ly.betime.shuriken.apis.CalendarApiFake;
import ly.betime.shuriken.apis.CalendarEvent;

public class AlarmGeneratorTest {

    private static final LocalDate TODAY = LocalDate.of(2019, 1, 5);
    private static final ZoneId ZONE_ID = ZoneOffset.UTC;

    private static final LocalTime DEFAULT_ALARM =
            LocalTime.of(8, 0);
    private static final SharedPreferences sharedPreferences =
            new SharedPreferencesFake(
                    ImmutableMap.of(
                            "DefaultAlarmHour", DEFAULT_ALARM.getHour(),
                            "DefaultAlarmMinute", DEFAULT_ALARM.getMinute()));

    @Test
    public void noEvent() {
        AlarmGenerator alarmGenerator = alarmGenerator(ImmutableList.of());
        assertThat(alarmGenerator.estimateTime(TODAY)).isEqualTo(DEFAULT_ALARM);
    }

    @Test
    public void eventsAfter() {
        AlarmGenerator alarmGenerator =
                alarmGenerator(
                        ImmutableList.of(
                                calendarEvent(TODAY, 12, 0, CalendarContract.Events.STATUS_CONFIRMED)));
        assertThat(alarmGenerator.estimateTime(TODAY)).isEqualTo(DEFAULT_ALARM);
    }

    @Test
    public void eventsBeforeDenied() {
        AlarmGenerator alarmGenerator =
                alarmGenerator(ImmutableList.of(
                        calendarEvent(TODAY, 7, 0, CalendarContract.Events.STATUS_CANCELED),
                        calendarEvent(TODAY, 12, 0, CalendarContract.Events.STATUS_CONFIRMED)));
        assertThat(alarmGenerator.estimateTime(TODAY)).isEqualTo(DEFAULT_ALARM);
    }

    @Test
    public void eventsBefore() {
        AlarmGenerator alarmGenerator =
                alarmGenerator(ImmutableList.of(
                        calendarEvent(TODAY, 9, 0, CalendarContract.Events.STATUS_CONFIRMED),
                        calendarEvent(TODAY, 7, 0, CalendarContract.Events.STATUS_CONFIRMED),
                        calendarEvent(TODAY, 12, 0, CalendarContract.Events.STATUS_CONFIRMED)));
        assertThat(alarmGenerator.estimateTime(TODAY)).isEqualTo(LocalTime.of(5, 30));
    }

    @Test
    public void eventsRightAfterAlarm() {
        AlarmGenerator alarmGenerator =
                alarmGenerator(ImmutableList.of(
                        calendarEvent(TODAY, 9, 0, CalendarContract.Events.STATUS_CONFIRMED),
                        calendarEvent(TODAY, 12, 0, CalendarContract.Events.STATUS_CONFIRMED)));
        assertThat(alarmGenerator.estimateTime(TODAY)).isEqualTo(LocalTime.of(7, 30));
    }

    @Test
    public void ignoresNightEvent() {
        AlarmGenerator alarmGenerator =
                alarmGenerator(ImmutableList.of(
                        calendarEvent(TODAY, 0, 10, CalendarContract.Events.STATUS_CONFIRMED),
                        calendarEvent(TODAY.plusDays(1), 0, 10, CalendarContract.Events.STATUS_CONFIRMED),
                        calendarEvent(TODAY, 12, 0, CalendarContract.Events.STATUS_CONFIRMED)));
        assertThat(alarmGenerator.estimateTime(TODAY)).isEqualTo(DEFAULT_ALARM);
    }

    private static AlarmGenerator alarmGenerator(List<CalendarEvent> events) {
        return new AlarmGenerator(
                new CalendarApiFake(events, ZONE_ID),
                sharedPreferences,
                new AlarmGenerator.EventPrepEstimate(),
                ZONE_ID);
    }

    private static CalendarEvent calendarEvent(LocalDate day, int hour, int minute, int status) {
        CalendarEvent event = new CalendarEvent();
        event.setName("Event");
        event.setEventId(42);
        event.setTo(day.atTime(hour + 1, minute));
        event.setStatus(status);
        event.setFrom(day.atTime(hour, minute));
        return event;
    }
}