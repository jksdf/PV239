package ly.betime.shuriken.helpers;

import android.content.Context;
import android.text.format.DateFormat;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;

import java.util.EnumSet;

import javax.inject.Inject;
import javax.inject.Named;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;

/**
 * Helper used for working rendering texts that should be translated
 */
public class LanguageTextHelper {
    private Context context;
    private DateTimeFormatter alarmTimeFormatter24;
    private DateTimeFormatter alarmTimeFormatter12;
    private DateTimeFormatter alarmPeriod;
    private static final DateTimeFormatter ALARM_PERIOD_NONE = DateTimeFormatter.ofPattern("");
    private DateTimeFormatter eventDate;
    private DateTimeFormatter alarmTitle;

    @Inject
    public LanguageTextHelper(@Named("application") Context context) {
        setContext(context);
    }

    public void setContext(@NonNull Context context) {
        this.context = context;
        alarmTimeFormatter24 = DateTimeFormatter.ofPattern(context.getString(R.string.alarm_time_format_24));
        alarmTimeFormatter12 = DateTimeFormatter.ofPattern(context.getString(R.string.alarm_time_format_12));
        alarmPeriod = DateTimeFormatter.ofPattern(context.getString(R.string.alarm_period_format));
        eventDate = DateTimeFormatter.ofPattern(context.getString(R.string.event_date_format));
        alarmTitle = DateTimeFormatter.ofPattern(context.getString(R.string.alarm_title_format));
    }

    public DateTimeFormatter getAlarmTimeFormatter() {
        return DateFormat.is24HourFormat(context) ? alarmTimeFormatter24 : alarmTimeFormatter12;
    }

    public DateTimeFormatter getAlarmPeriodFormatter() {
        return DateFormat.is24HourFormat(context) ? ALARM_PERIOD_NONE : alarmPeriod;
    }

    public DateTimeFormatter getEventDateFormatter() {
        return eventDate;
    }

    public DateTimeFormatter getAlarmTitleFormatter() {
        return alarmTitle;
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
                    context.getResources().getConfiguration().locale));
        }

        return sb.toString();
    }
}
