package ly.betime.shuriken.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import ly.betime.shuriken.App;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.ShurikenAdapter;
import ly.betime.shuriken.adapters.data.GeneratedAlarmShuriken;
import ly.betime.shuriken.apis.CalendarApi;
import ly.betime.shuriken.apis.CalendarEvent;
import ly.betime.shuriken.calendar.CalendarShuriken;
import ly.betime.shuriken.calendar.EventDecorator;
import ly.betime.shuriken.helpers.LanguageTextHelper;
import ly.betime.shuriken.service.GeneratedAlarmService;

public class CalendarFragment extends Fragment implements OnMonthChangedListener, OnDateSelectedListener {

    @Inject
    public CalendarApi calendarApi;

    @Inject
    public GeneratedAlarmService generatedAlarmService;

    @Inject
    public LanguageTextHelper languageTextHelper;

    private final HashSet<LocalDate> fetchedEvents = new HashSet<>();
    private CalendarDay selectedDate;

    private RecyclerView recyclerView;
    private List<Object> adapterList = new ArrayList<>();
    private ShurikenAdapter shurikenAdapter;
    private CalendarShuriken calendarShuriken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        App.getComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_calendar, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (calendarApi.getPermission(getActivity())) {
            if (calendarShuriken == null) {
                createCalendar();
            }
            renderRecyclerView();
        }
    }

    private void createCalendar() {
        calendarShuriken = new CalendarShuriken();
        calendarShuriken.setOnDateSelectedListener(this);
        calendarShuriken.setOnMonthChangedListener(this);
        adapterList.add(calendarShuriken);
    }

    private void renderRecyclerView() {
        if (recyclerView.getAdapter() == null || shurikenAdapter == null) {
            shurikenAdapter = new ShurikenAdapter(adapterList, languageTextHelper);

            shurikenAdapter.setGeneratedAlarmSwitchListener(
                    (alarm, state) -> {
                        if (state) {
                            generatedAlarmService.enable(alarm);
                        } else {
                            generatedAlarmService.disable(alarm);
                        }
                    }
            );

            recyclerView.setAdapter(shurikenAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            shurikenAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMonthChanged(MaterialCalendarView calendarView, CalendarDay date) {
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

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        if (selectedDate == null || !selectedDate.equals(date)) {
            selectedDate = date;
            LocalDate localDate = date.getDate();

            generatedAlarmService.get(localDate).observe(this, alarm -> {
                int oldEvents = adapterList.size() - 1;
                adapterList.clear();
                adapterList.add(calendarShuriken);
                adapterList.add(new GeneratedAlarmShuriken(alarm, true));
                adapterList.addAll(calendarApi.getEvents(localDate, localDate));
                if (!recyclerView.isComputingLayout()) {
                    shurikenAdapter.notifyItemRangeChanged(1, Math.max(oldEvents, adapterList.size() - 1));
                }
            });
        }
    }

    private HashSet<CalendarDay> eventsToDays(Collection<CalendarEvent> events) {
        HashSet<CalendarDay> days = new HashSet<>();
        for (CalendarEvent event : events) {
            days.add(CalendarDay.from(event.getFrom().toLocalDate()));
        }
        return days;
    }
}