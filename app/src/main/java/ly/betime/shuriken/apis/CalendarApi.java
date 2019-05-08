package ly.betime.shuriken.apis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.google.common.base.Preconditions.checkNotNull;


public class CalendarApi {
    private static final Uri EVENTS_URI = Uri.parse("content://com.android.calendar/events");

    private final Context context;

    @Inject
    public CalendarApi(@Named("application") Context context) {
        this.context = context;
    }

    public boolean getPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, 42);
            return false;
        }
        return true;
    }

    public List<CalendarEvent> getEvents(LocalDate from, LocalDate to, Set<Integer> calendarIds) {
        return getEvents(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), calendarIds);
    }

    public List<CalendarEvent> getEvents(LocalDateTime from, LocalDateTime to, Set<Integer> calendarIds) {
        checkNotNull(calendarIds);
        checkNotNull(from);
        checkNotNull(to);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED) {
            throw new RuntimeException("Do not have the permission.");
        }
        List<CalendarEvent> events = new ArrayList<>();

        try (Cursor c = CalendarContract.Instances.query(context.getContentResolver(),
                new String[]{CalendarContract.Instances.BEGIN,
                        CalendarContract.Instances.END,
                        CalendarContract.Instances.EVENT_ID,
                        CalendarContract.Instances.ALL_DAY},
                from.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), to.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())) {
            if (c == null) {
                throw new RuntimeException("Can not load calendar.");
            }
            int beginIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN);
            int endIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.END);
            int eventIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID);
            int allDayIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                CalendarEvent event = new CalendarEvent();
                long eventId = c.getLong(eventIdx);
                event.setEventId(eventId);
                event.setAllDay(c.getInt(allDayIdx) == 1);
                ZoneId timeZone;
                String selection = "(" + CalendarContract.Events._ID + " = ?)";
                int calendarId;
                try (Cursor ec =
                             context
                                     .getContentResolver()
                                     .query(EVENTS_URI,
                                             new String[]{CalendarContract.Events.TITLE,
                                                     CalendarContract.Events.STATUS,
                                                     CalendarContract.Events.EVENT_TIMEZONE,
                                                     CalendarContract.Events.CALENDAR_ID},
                                             selection,
                                             new String[]{eventId + ""}, null)) {
                    if (ec == null) {
                        throw new RuntimeException("Event not found.");
                    }
                    ec.moveToFirst();

                    if (ec.getCount() != 1) {
                        throw new RuntimeException("Too many or few rows.");
                    }
                    event.setName(ec.getString(ec.getColumnIndexOrThrow(CalendarContract.Events.TITLE)));
                    event.setStatus(ec.getInt(ec.getColumnIndexOrThrow(CalendarContract.Events.STATUS)));
                    timeZone = ZoneId.of(ec.getString(ec.getColumnIndexOrThrow(CalendarContract.Events.EVENT_TIMEZONE)));
                    calendarId = ec.getInt(ec.getColumnIndexOrThrow(CalendarContract.Events.CALENDAR_ID));
                }
                event.setFrom(Instant.ofEpochMilli(c.getLong(beginIdx)).atZone(timeZone).toLocalDateTime());
                event.setTo(Instant.ofEpochMilli(c.getLong(endIdx)).atZone(timeZone).toLocalDateTime());
                if (!event.getTo().equals(from)
                        && (calendarIds.isEmpty() || calendarIds.contains(calendarId))) {
                    events.add(event);
                }
            }
        }
        Collections.sort(events, (a, b) -> a.getFrom().compareTo(b.getFrom()));
        return events;
    }

    public CalendarEvent getEvent(Long id) {
        if (id == null) {
            return null;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED) {
            throw new RuntimeException("Do not have the permission.");
        }
        String selection = "(" + CalendarContract.Events._ID + " = ?)";
        try (Cursor ec =
                     context
                             .getContentResolver()
                             .query(EVENTS_URI,
                                     new String[]{CalendarContract.Events.TITLE,
                                             CalendarContract.Events.STATUS,
                                             CalendarContract.Events.DTSTART,
                                             CalendarContract.Events.DTEND,
                                             CalendarContract.Events.EVENT_TIMEZONE,
                                             CalendarContract.Events.ALL_DAY},
                                     selection,
                                     new String[]{id + ""}, null)) {
            if (ec == null) {
                throw new RuntimeException("Event not found.");
            }
            if (ec.getCount() != 1) {
                throw new RuntimeException("Too many or few rows.");
            }
            ec.moveToFirst();

            CalendarEvent event = new CalendarEvent();
            event.setName(ec.getString(ec.getColumnIndexOrThrow(CalendarContract.Events.TITLE)));
            event.setStatus(ec.getInt(ec.getColumnIndexOrThrow(CalendarContract.Events.STATUS)));
            ZoneId timeZone = ZoneId.of(ec.getString(ec.getColumnIndexOrThrow(CalendarContract.Events.EVENT_TIMEZONE)));
            event.setFrom(
                    Instant
                            .ofEpochMilli(ec.getLong(ec.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)))
                            .atZone(timeZone)
                            .toLocalDateTime());
            event.setTo(
                    Instant
                            .ofEpochMilli(ec.getLong(ec.getColumnIndexOrThrow(CalendarContract.Events.DTEND)))
                            .atZone(timeZone)
                            .toLocalDateTime());
            event.setAllDay(ec.getInt(ec.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)) == 1);
            event.setEventId(id);
            return event;
        }
    }
}
