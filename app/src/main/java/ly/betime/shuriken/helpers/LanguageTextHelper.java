package ly.betime.shuriken.helpers;

import android.content.Context;

import androidx.annotation.NonNull;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;

import java.util.EnumSet;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;

/**
 * Helper used for working rendering texts that should be translated
 */
public class LanguageTextHelper {
    private Context context;
    private DateTimeFormatter alarmTimeFormatter;

    public LanguageTextHelper(@NonNull Context context) {
        setContext(context);
    }

    public void setContext(@NonNull Context context) {
        this.context = context;
        alarmTimeFormatter = DateTimeFormatter.ofPattern(context.getString(R.string.alarm_time_format));
    }


    public DateTimeFormatter getAlarmTimeFormatter() {
        return alarmTimeFormatter;
    }


    public String getAlarmRepeatText(EnumSet<DayOfWeek> days) {
        if (days.size() >= 7) {
            return context.getString(R.string.repeat_everyday);
        }
        if (days.isEmpty()) {
            return context.getString(R.string.repeat_non);
        }
        StringBuilder sb = new StringBuilder();

        for (DayOfWeek day : days) {
            if (sb.length() > 0) {
                sb.append(context.getString(R.string.repeat_delimiter));
            }
            sb.append(day.getDisplayName(
                    TextStyle.SHORT,
                    // TODO: Probably will need to change if we add manual way of changing locale
                    context.getResources().getConfiguration().getLocales().get(0)));
        }

        return sb.toString();
    }
}
