package ly.betime.shuriken.calendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.HashSet;

import androidx.annotation.ColorInt;

public class EventDecorator implements DayViewDecorator {

    private final HashSet<CalendarDay> dates;
    private final int color;

    public EventDecorator(HashSet<CalendarDay> dates, @ColorInt int color) {
        this.color = color;
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(8, color));
    }
}
