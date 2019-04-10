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
    private ArrayList<Object> data = new ArrayList<>();

    public ShurikenData() {
    }

    public boolean isPrepared() {
        return alarms != null && events != null;
    }

    public void refreshData() {
        data.clear();

        if (events.size() > 0) {
            data.add(tomorrow);
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
        Collections.sort(events, (a, b) -> a.getFrom().compareTo(b.getFrom()));
        this.events = events;
    }

    public LocalDate getTomorrow() {
        return tomorrow;
    }

    public void setTomorrow(LocalDate tomorrow) {
        this.tomorrow = tomorrow;
    }
}
