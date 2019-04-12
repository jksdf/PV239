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

import javax.inject.Inject;
import javax.inject.Named;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class CalendarApi {
    private static final Uri EVENTS_URI = Uri.parse("content://com.android.calendar/events");

    private final Context context;
    private final ZoneId zoneId;

    @Inject
    public CalendarApi(@Named("application") Context context, ZoneId zoneId) {
        this.context = context;
        this.zoneId = zoneId;
    }

    public boolean getPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, 42);
            return false;
        }
        return true;
    }

    public List<CalendarEvent> getEvents(LocalDate from, LocalDate to) {
        return getEvents(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }

    public List<CalendarEvent> getEvents(LocalDateTime from, LocalDateTime to) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED) {
            throw new RuntimeException("Do not have the permission.");
        }
        List<CalendarEvent> events = new ArrayList<>();

        try (Cursor c = CalendarContract.Instances.query(context.getContentResolver(), new String[]{CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.EVENT_ID}, from.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), to.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())) {
            if (c == null) {
                throw new RuntimeException("Can not load calendar.");
            }
            int beginIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN);
            int endIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.END);
            int eventIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                if (c.isAfterLast()) {
                    break;
                }
                CalendarEvent event = new CalendarEvent();

                event.setFrom(Instant.ofEpochMilli(c.getLong(beginIdx)).atZone(zoneId).toLocalDateTime());
                event.setTo(Instant.ofEpochMilli(c.getLong(endIdx)).atZone(zoneId).toLocalDateTime());
                long eventId = c.getLong(eventIdx);
                event.setEventId(eventId);
                String selection = "(" + CalendarContract.Events._ID + " = ?)";
                try (Cursor ec = context.getContentResolver().query(EVENTS_URI, new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.STATUS}, selection, new String[]{eventId + ""}, null)) {
                    if (ec == null) {
                        throw new RuntimeException("Event not found.");
                    }
                    ec.moveToFirst();

                    if (ec.getCount() != 1) {
                        throw new RuntimeException("Too many or few rows.");
                    }
                    event.setName(ec.getString(ec.getColumnIndexOrThrow(CalendarContract.Events.TITLE)));
                    event.setStatus(ec.getInt(ec.getColumnIndexOrThrow(CalendarContract.Events.STATUS)));
                }
                events.add(event);
                c.moveToNext();
            }
        }
        Collections.sort(events, (a, b) -> a.getFrom().compareTo(b.getFrom()));
        return events;
    }
}
