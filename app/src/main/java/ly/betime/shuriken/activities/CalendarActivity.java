package ly.betime.shuriken.activities;

import android.os.Bundle;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.threeten.bp.LocalDate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.inject.Inject;

import ly.betime.shuriken.App;
import ly.betime.shuriken.R;
import ly.betime.shuriken.apis.CalendarApi;
import ly.betime.shuriken.apis.CalendarEvent;
import ly.betime.shuriken.calendar.EventDecorator;
import ly.betime.shuriken.calendar.TodayDecorator;

public class CalendarActivity extends AMenuActivity implements OnMonthChangedListener {

    @Inject
    public CalendarApi calendarApi;

    private MaterialCalendarView calendarView;
    private HashSet<LocalDate> fetchedEvents = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        LocalDate today = LocalDate.now();
        calendarView.setCurrentDate(today);
        calendarView.setSelectedDate(today);

        calendarView.addDecorators(
                new TodayDecorator(today, getResources().getColor(R.color.colorText))
        );

        onMonthChanged(calendarView, CalendarDay.from(today));
        calendarView.setOnMonthChangedListener(this);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        LocalDate start = date.getDate().withDayOfMonth(1);
        if (fetchedEvents.contains(start))
            return;
        fetchedEvents.add(start);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        calendarView.addDecorators(new EventDecorator(
                eventsToDays(calendarApi.getEvents(start, end)),
                getResources().getColor(R.color.colorPrimary)
        ));
    }

    private HashSet<CalendarDay> eventsToDays(Collection<CalendarEvent> events) {
        HashSet<CalendarDay> days = new HashSet<>();
        for (CalendarEvent event : events) {
            days.add(CalendarDay.from(event.getFrom().toLocalDate()));
        }
        return days;
    }
}
