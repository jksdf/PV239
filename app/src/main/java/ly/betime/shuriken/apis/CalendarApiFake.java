package ly.betime.shuriken.apis;

import android.app.Activity;

import com.google.common.collect.ImmutableList;

import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.List;

public class CalendarApiFake implements CalendarApi {

    private final ImmutableList<CalendarEvent> events;

    public CalendarApiFake(List<CalendarEvent> events) {
        this.events = ImmutableList.copyOf(events);
    }

    @Override
    public List<CalendarEvent> getEvents(Activity activity, long from, long to) {
        List<CalendarEvent> newEvents = new ArrayList<>();
        for (CalendarEvent event : events) {
            long time = event.getFrom().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            if (from <= time && time <= to) {
                newEvents.add(event);
            }
        }
        return newEvents;
    }
}
