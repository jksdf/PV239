package ly.betime.shuriken.service;

import android.content.SharedPreferences;
import android.provider.CalendarContract;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.junit.Test;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.List;

import ly.betime.shuriken.SharedPreferencesFake;
import ly.betime.shuriken.apis.CalendarApi;
import ly.betime.shuriken.apis.CalendarEvent;
import ly.betime.shuriken.entities.GeneratedAlarm;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlarmGeneratorTest {

    private static final LocalDate TODAY = LocalDate.of(2019, 1, 5);

    private static final CalendarApi fakeCalendar = mock(CalendarApi.class);

    private static final LocalTime DEFAULT_ALARM =
            LocalTime.of(8, 0);
    private static final SharedPreferences sharedPreferences =
            new SharedPreferencesFake(
                    ImmutableMap.of(
                            "DefaultAlarmHour", DEFAULT_ALARM.getHour(),
                            "DefaultAlarmMinute", DEFAULT_ALARM.getMinute()));

    @Test
    public void noEvent() throws Exception {
        AlarmGenerator alarmGenerator = alarmGenerator(ImmutableList.of());
        GeneratedAlarm alarm = alarmGenerator.generateAlarm(TODAY).get();
        assertThat(alarm.getTime()).isEqualTo(DEFAULT_ALARM);
    }

    @Test
    public void eventsAfter() throws Exception {
        AlarmGenerator alarmGenerator =
                alarmGenerator(
                        ImmutableList.of(
                                calendarEvent(TODAY, 12, 0, CalendarContract.Events.STATUS_CONFIRMED)));
        assertThat(alarmGenerator.generateAlarm(TODAY).get().getTime()).isEqualTo(DEFAULT_ALARM);
    }

    @Test
    public void eventsBeforeDenied() throws Exception {
        AlarmGenerator alarmGenerator =
                alarmGenerator(ImmutableList.of(
                        calendarEvent(TODAY, 7, 0, CalendarContract.Events.STATUS_CANCELED),
                        calendarEvent(TODAY, 12, 0, CalendarContract.Events.STATUS_CONFIRMED)));
        assertThat(alarmGenerator.generateAlarm(TODAY).get().getTime()).isEqualTo(DEFAULT_ALARM);
    }

    @Test
    public void eventsBefore() throws Exception {
        AlarmGenerator alarmGenerator =
                alarmGenerator(ImmutableList.of(
                        calendarEvent(TODAY, 9, 0, CalendarContract.Events.STATUS_CONFIRMED),
                        calendarEvent(TODAY, 7, 0, CalendarContract.Events.STATUS_CONFIRMED),
                        calendarEvent(TODAY, 12, 0, CalendarContract.Events.STATUS_CONFIRMED)));
        assertThat(alarmGenerator.generateAlarm(TODAY).get().getTime()).isEqualTo(LocalTime.of(5, 30));
    }

    @Test
    public void eventsRightAfterAlarm() throws Exception {
        AlarmGenerator alarmGenerator =
                alarmGenerator(ImmutableList.of(
                        calendarEvent(TODAY, 9, 0, CalendarContract.Events.STATUS_CONFIRMED),
                        calendarEvent(TODAY, 12, 0, CalendarContract.Events.STATUS_CONFIRMED)));
        assertThat(alarmGenerator.generateAlarm(TODAY).get().getTime()).isEqualTo(LocalTime.of(7, 30));
    }

    @Test
    public void ignoresNightEvent() throws Exception {
        AlarmGenerator alarmGenerator =
                alarmGenerator(ImmutableList.of(
                        calendarEvent(TODAY, 0, 10, CalendarContract.Events.STATUS_CONFIRMED),
                        calendarEvent(TODAY.plusDays(1), 0, 10, CalendarContract.Events.STATUS_CONFIRMED),
                        calendarEvent(TODAY, 12, 0, CalendarContract.Events.STATUS_CONFIRMED)));
        GeneratedAlarm alarm = alarmGenerator.generateAlarm(TODAY).get();
        assertThat(alarm.getTime()).isEqualTo(DEFAULT_ALARM);
    }

    private static AlarmGenerator alarmGenerator(List<CalendarEvent> events) {
        when(fakeCalendar.getEvents((LocalDate) any(), any(), eq(ImmutableSet.of()))).thenReturn(events);
        when(fakeCalendar.getEvents((LocalDateTime) any(), any(), eq(ImmutableSet.of()))).thenReturn(events);
        return new AlarmGenerator(
                fakeCalendar,
                sharedPreferences,
                new AlarmGenerator.EventPrepEstimate());
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