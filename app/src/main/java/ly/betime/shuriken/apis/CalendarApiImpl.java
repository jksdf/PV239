package ly.betime.shuriken.apis;

import android.Manifest;
import android.app.Activity;
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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class CalendarApiImpl implements CalendarApi {
    private final Context context;

    @Inject
    public CalendarApiImpl(@Named("application") Context context) {
        this.context = context;
    }

    @Override
    public List<CalendarEvent> getEvents(Activity activity, long from, long to) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CALENDAR)) {
                // TODO(kurick): show explanation
                return null;
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, 42);
            }
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

                event.setFrom(Instant.ofEpochMilli(c.getLong(beginIdx)).atZone(ZoneId.systemDefault()).toLocalDateTime());
                event.setFrom(Instant.ofEpochMilli(c.getLong(endIdx)).atZone(ZoneId.systemDefault()).toLocalDateTime());
                long eventId = c.getLong(eventIdx);
                event.setEventId(eventId);
                String selection = "(" + CalendarContract.Events._ID + " = ?)";
                try (Cursor ec = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, new String[]{CalendarContract.Events.TITLE}, selection, new String[]{eventId + ""}, null)) {
                    if (ec == null) {
                        throw new RuntimeException("Event not found.");
                    }
                    ec.moveToFirst();
                    if (!(ec.isFirst() && ec.isLast())) {
                        throw new RuntimeException("Too many rows.");
                    }
                    event.setName(ec.getString(c.getColumnIndex(CalendarContract.Events.TITLE)));
                }
                events.add(event);
            }
        }
        return events;
    }
}
