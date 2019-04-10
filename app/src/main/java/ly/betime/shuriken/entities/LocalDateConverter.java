package ly.betime.shuriken.entities;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import androidx.room.TypeConverter;

public class LocalDateConverter {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @TypeConverter
    public static String persist(LocalDate time) {
        if (time == null) {
            return null;
        }
        return DATE_FORMATTER.format(time);
    }

    @TypeConverter
    public static LocalDate load(String persisted) {
        if (persisted == null) {
            return null;
        }
        return LocalDate.parse(persisted, DATE_FORMATTER);
    }
}
