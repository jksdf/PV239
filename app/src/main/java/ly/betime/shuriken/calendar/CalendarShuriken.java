package ly.betime.shuriken.calendar;

import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

/**
 * Used as identifier for Calendar in {@link ly.betime.shuriken.adapters.ShurikenAdapter}
 */
public class CalendarShuriken {
    private OnMonthChangedListener onMonthChangedListener;
    private OnDateSelectedListener onDateSelectedListener;

    public CalendarShuriken() {
    }

    public OnMonthChangedListener getOnMonthChangedListener() {
        return onMonthChangedListener;
    }

    public void setOnMonthChangedListener(OnMonthChangedListener onMonthChangedListener) {
        this.onMonthChangedListener = onMonthChangedListener;
    }

    public OnDateSelectedListener getOnDateSelectedListener() {
        return onDateSelectedListener;
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }
}
