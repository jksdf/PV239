package ly.betime.shuriken.apis;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import androidx.core.content.ContextCompat;


public class CalendarApiImpl implements CalendarApi {
    private final Context context;
    private final ZoneId zoneId;

    @Inject
    public CalendarApiImpl(@Named("application") Context context, ZoneId zoneId) {
        this.context = context;
        this.zoneId = zoneId;
    }

    @Override
    public List<CalendarEvent> getEvents(long from, long to) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED) {
            throw new RuntimeException("Do not have the permission.");
        }
        List<CalendarEvent> events = new ArrayList<>();

        try (Cursor c = CalendarContract.Instances.query(context.getContentResolver(), new String[]{CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.EVENT_ID}, from, to)) {
            if (c == null) {
                throw new RuntimeException("Can not load calendar.");
            }
            int beginIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN);
            int endIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.END);
            int eventIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID);
            for (c.moveToFirst(); c.isAfterLast(); c.moveToNext()) {
                if (c.isAfterLast()) {
                    break;
                }
                CalendarEvent event = new CalendarEvent();

                event.setFrom(Instant.ofEpochMilli(c.getLong(beginIdx)).atZone(zoneId).toLocalDateTime());
                event.setFrom(Instant.ofEpochMilli(c.getLong(endIdx)).atZone(zoneId).toLocalDateTime());
                long eventId = c.getLong(eventIdx);
                event.setEventId(eventId);
                String selection = "(" + CalendarContract.Events._ID + " = ?)";
                try (Cursor ec = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.STATUS}, selection, new String[]{eventId + ""}, null)) {
                    if (ec == null) {
                        throw new RuntimeException("Event not found.");
                    }
                    ec.moveToFirst();
                    if (!(ec.isFirst() && ec.isLast())) {
                        throw new RuntimeException("Too many or few rows.");
                    }
                    event.setName(ec.getString(c.getColumnIndex(CalendarContract.Events.TITLE)));
                    event.setName(ec.getString(c.getColumnIndex(CalendarContract.Events.STATUS)));
                }
                events.add(event);
            }
        }
        return events;
    }
}
