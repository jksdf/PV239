package ly.betime.shuriken.entities;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import org.threeten.bp.DayOfWeek;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import androidx.room.TypeConverter;

public class DayOfWeekConverter {
    private static final char SEPARATOR = ',';
    private static final Joiner JOINER = Joiner.on(SEPARATOR);
    private static final Splitter SPLITTER = Splitter.on(SEPARATOR).omitEmptyStrings();

    @TypeConverter
    public static String persist(EnumSet<DayOfWeek> days) {
        List<String> dayIndices = new ArrayList<>(days.size());
        for (DayOfWeek day : days) {
            dayIndices.add(Integer.toString(day.getValue()));
        }
        return JOINER.join(dayIndices);
    }

    @TypeConverter
    public static EnumSet<DayOfWeek> load(String persisted) {
        EnumSet<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        for (String dayNum : SPLITTER.split(persisted)) {
            days.add(DayOfWeek.of(Integer.valueOf(dayNum)));
        }
        return days;
    }
}
