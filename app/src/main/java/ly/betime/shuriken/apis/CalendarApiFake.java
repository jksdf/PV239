package ly.betime.shuriken.apis;

import com.google.common.collect.ImmutableList;

import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CalendarApiFake implements CalendarApi {

    private final ImmutableList<CalendarEvent> events;
    private final ZoneId zoneId;

    @Inject
    public CalendarApiFake(List<CalendarEvent> events, ZoneId zoneId) {
        this.events = ImmutableList.copyOf(events);
        this.zoneId = zoneId;
    }

    @Override
    public List<CalendarEvent> getEvents(long from, long to) {
        List<CalendarEvent> newEvents = new ArrayList<>();
        for (CalendarEvent event : events) {
            long time = event.getFrom().atZone(zoneId).toInstant().toEpochMilli();
            if (from <= time && time <= to) {
                newEvents.add(event);
            }
        }
        return newEvents;
    }
}
