package ly.betime.shuriken.calendar;

import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import org.threeten.bp.LocalDate;

import androidx.annotation.ColorInt;

/**
 * Make sure that today is always visibly different
 */
public class TodayDecorator implements DayViewDecorator {

    private final CalendarDay today;
    private final int color;

    public TodayDecorator(LocalDate date, @ColorInt int color) {
        this(CalendarDay.from(date), color);
    }

    public TodayDecorator(CalendarDay today, @ColorInt int color) {
        this.today = today;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(today);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new ForegroundColorSpan(color));
    }
}
