package ly.betime.shuriken.adapters.data;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ly.betime.shuriken.apis.CalendarEvent;
import ly.betime.shuriken.entities.Alarm;

public class ShurikenData {
    private LocalDate tomorrow;
    private List<Alarm> alarms;
    private List<CalendarEvent> events;
    private final ArrayList<Object> data = new ArrayList<>();

    public ShurikenData() {
        this(false);
    }

    /**
     * Fill with empty lists so it can be rendered if initialized is true
     * @param initialized If  true fill with empty lists
     */
    public ShurikenData(boolean initialized) {
        if (initialized) {
            alarms = Collections.emptyList();
            events = Collections.emptyList();
        }
    }

    public boolean isPrepared() {
        return alarms != null && events != null;
    }

    public void refreshData() {
        data.clear();

        if (tomorrow != null) {
            data.add(tomorrow);
        }

        if (events.size() > 0) {
            data.addAll(events);
        }
        data.addAll(alarms);
    }

    public List<Object> getData() {
        return data;
    }

    public List<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<Alarm> alarms) {
        Collections.sort(alarms, (a, b) -> a.getTime().compareTo(b.getTime()));
        this.alarms = alarms;
    }

    public List<CalendarEvent> getEvents() {
        return events;
    }

    public void setEvents(List<CalendarEvent> events) {
        this.events = events;
    }

    public LocalDate getTomorrow() {
        return tomorrow;
    }

    public void setTomorrow(LocalDate tomorrow) {
        this.tomorrow = tomorrow;
    }

    public void clean() {
        alarms = null;
        tomorrow = null;
        events = null;
    }
}
