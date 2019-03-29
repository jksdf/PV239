package ly.betime.shuriken.service;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

import java.util.EnumSet;

import static com.google.common.base.Preconditions.checkNotNull;

public class AlarmEntity {
    private Integer id;
    private String name;
    private LocalTime time;
    private boolean enabled;
    private EnumSet<DayOfWeek> repeating;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkNotNull(name);
        this.name = name;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        checkNotNull(time);
        LocalTime t = LocalTime.now();
        this.time = time;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public EnumSet<DayOfWeek> getRepeating() {
        return repeating;
    }

    public void setRepeating(EnumSet<DayOfWeek> repeating) {
        checkNotNull(repeating);
        this.repeating = repeating;
    }
}
