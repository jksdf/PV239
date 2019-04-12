package ly.betime.shuriken.adapters.views;

import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.LocalDate;

import java.util.Objects;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.ShurikenAdapter;
import ly.betime.shuriken.calendar.CalendarShuriken;
import ly.betime.shuriken.calendar.TodayDecorator;

public class CalendarViewHolder extends ShurikenViewHolder {
    public final static int VIEW = R.layout.calendar_item;

    private final MaterialCalendarView calendarView;

    private CalendarShuriken calendarShuriken;

    public CalendarViewHolder(@NonNull View itemView, ShurikenAdapter adapter) {
        super(itemView, adapter);

        calendarView = itemView.findViewById(R.id.calendarView);
    }

    @Override
    public void bind(int position) {
        super.bind(position);
        calendarShuriken = (CalendarShuriken) adapter.getShurikens().get(position);

        LocalDate today = LocalDate.now();
        calendarView.setCurrentDate(today);
        calendarView.setSelectedDate(today);

        calendarView.addDecorators(
                new TodayDecorator(today, itemView.getResources().getColor(R.color.colorText))
        );

        calendarView.setOnMonthChangedListener(calendarShuriken.getOnMonthChangedListener());
        calendarView.setOnDateChangedListener(calendarShuriken.getOnDateSelectedListener());

        finishBinding();
    }

    private void finishBinding() {
        CalendarDay actualDay = calendarView.getSelectedDate();
        calendarShuriken.getOnMonthChangedListener().onMonthChanged(calendarView, actualDay);
        calendarShuriken.getOnDateSelectedListener()
                .onDateSelected(calendarView, Objects.requireNonNull(actualDay), true);
    }
}
