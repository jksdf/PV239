package ly.betime.shuriken.apis;

import com.google.common.base.MoreObjects;

import org.threeten.bp.LocalDateTime;

import androidx.annotation.NonNull;

public class CalendarEvent {
    private long eventId;
    private LocalDateTime from;
    private LocalDateTime to;
    private String name;
    private int status;

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(CalendarEvent.class)
                .add("eventId", eventId)
                .add("from", from)
                .add("to", to)
                .add("name", name)
                .toString();
    }
}
