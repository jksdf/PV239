package ly.betime.shuriken.apis;

import android.app.Activity;

import java.util.List;

public interface CalendarApi {
    List<CalendarEvent> getEvents(Activity activity, long from, long to);
}
