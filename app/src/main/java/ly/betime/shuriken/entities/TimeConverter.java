package ly.betime.shuriken.entities;

import org.threeten.bp.LocalTime;

import androidx.room.TypeConverter;

public class TimeConverter {
    @TypeConverter
    public static int persist(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    @TypeConverter
    public static LocalTime load(int time) {
        return LocalTime.of(time / 60, time % 60);
    }
}
