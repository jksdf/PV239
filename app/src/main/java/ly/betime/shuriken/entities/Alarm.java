package ly.betime.shuriken.entities;

import com.google.common.base.MoreObjects;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.EnumSet;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String name;

    @TypeConverters(LocalDateTimeConverter.class)
    private LocalDateTime ringing;

    @TypeConverters(TimeConverter.class)
    private LocalTime time;

    @TypeConverters(DayOfWeekConverter.class)
    private EnumSet<DayOfWeek> repeating;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public EnumSet<DayOfWeek> getRepeating() {
        return repeating;
    }

    public void setRepeating(EnumSet<DayOfWeek> repeating) {
        this.repeating = repeating;
    }

    public LocalDateTime getRinging() {
        return ringing;
    }

    public void setRinging(LocalDateTime ringing) {
        this.ringing = ringing;
    }

    public boolean isEnabled() {
        return getRinging() != null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(Alarm.class)
                .add("id", getId())
                .add("name", getName())
                .add("time", getTime())
                .add("ringing", getRinging())
                .add("repeating", getRepeating())
                .toString();
    }
}
