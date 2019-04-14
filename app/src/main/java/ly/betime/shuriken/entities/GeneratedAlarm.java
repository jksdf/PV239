package ly.betime.shuriken.entities;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class GeneratedAlarm {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @TypeConverters(LocalDateTimeConverter.class)
    private LocalDateTime ringing;

    @TypeConverters(LocalDateConverter.class)
    private LocalDate forDate;

    private Long eventId;

    @TypeConverters(TimeConverter.class)
    private LocalTime time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getRinging() {
        return ringing;
    }

    public void setRinging(LocalDateTime ringing) {
        this.ringing = ringing;
    }

    public LocalDate getForDate() {
        return forDate;
    }

    public void setForDate(LocalDate forDate) {
        this.forDate = forDate;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneratedAlarm)) return false;
        GeneratedAlarm alarm = (GeneratedAlarm) o;
        return Objects.equal(getId(), alarm.getId()) &&
                Objects.equal(getRinging(), alarm.getRinging()) &&
                Objects.equal(getForDate(), alarm.getForDate()) &&
                Objects.equal(getEventId(), alarm.getEventId()) &&
                Objects.equal(getTime(), alarm.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getRinging(), getForDate(), getEventId(), getTime());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(GeneratedAlarm.class)
                .add("id", getId())
                .add("ringing", getRinging())
                .add("forDate", getForDate())
                .add("eventId", getEventId())
                .add("time", getTime())
                .toString();
    }
}
