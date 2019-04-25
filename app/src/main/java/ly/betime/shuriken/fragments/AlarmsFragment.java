package ly.betime.shuriken.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.threeten.bp.LocalDate;

import javax.inject.Inject;

import ly.betime.shuriken.App;
import ly.betime.shuriken.R;
import ly.betime.shuriken.activities.AlarmFormActivity;
import ly.betime.shuriken.adapters.ShurikenAdapter;
import ly.betime.shuriken.adapters.data.GeneratedAlarmShuriken;
import ly.betime.shuriken.adapters.data.ShurikenData;
import ly.betime.shuriken.apis.CalendarApi;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.helpers.LanguageTextHelper;
import ly.betime.shuriken.service.AlarmService;
import ly.betime.shuriken.service.GeneratedAlarmService;

public class AlarmsFragment extends Fragment {

    private static final String LOG_TAG = "AlarmsFragment";

    @Inject
    public AlarmService alarmService;
    @Inject
    public LanguageTextHelper languageTextHelper;
    @Inject
    public CalendarApi calendarApi;
    @Inject
    public GeneratedAlarmService generatedAlarmService;

    private ShurikenData shurikenData;
    private ShurikenAdapter shurikenAdapter;

    private RecyclerView alarmsView;
    private FloatingActionButton addButton;

    public AlarmsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_alarms, container, false);

        alarmsView = view.findViewById(R.id.alarmsContainer);
        registerForContextMenu(alarmsView);

        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener((View v) -> startAlarmFormActivity(null));

        shurikenData = new ShurikenData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        shurikenData.clean();
        refreshAlarms();
        if (calendarApi.getPermission(getActivity())) {
            refreshEvents();
            refreshGeneratedAlarm();
        }
    }

    /**
     * Starts AlarmFormActivity
     *
     * @param alarm Alarm for editing if any is needed
     */
    protected void startAlarmFormActivity(@javax.annotation.Nullable Alarm alarm) {
        Intent intent = new Intent(getContext(), AlarmFormActivity.class);
        if (alarm != null) {
            intent.putExtra(AlarmFormActivity.ALARM_ID_MESSAGE, alarm.getId());
        }
        startActivity(intent);
    }

    /**
     * Gets all alarms from service and render them. Should be only used if all items have to be rendered.
     * Use notify functions of {@link RecyclerView.Adapter} if only one item changed.
     */
    public void renderShurikenList() {
        if (!shurikenData.isPrepared())
            return;

        shurikenData.refreshData();

        if (alarmsView.getAdapter() == null || shurikenAdapter == null) {
            shurikenAdapter = new ShurikenAdapter(shurikenData.getData(), languageTextHelper);
            shurikenAdapter.setAlarmSwitchListener(
                    (alarm, state) -> alarmService.setAlarm(
                            alarm,
                            state ? AlarmService.AlarmAction.ENABLE : AlarmService.AlarmAction.DISABLE
                    )
            );
            shurikenAdapter.setGeneratedAlarmSwitchListener(
                    (alarm, state) -> {
                        if (state) {
                            generatedAlarmService.enable(alarm);
                        } else {
                            generatedAlarmService.disable(alarm);
                        }
                    }
            );

            alarmsView.setAdapter(shurikenAdapter);
            alarmsView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            shurikenAdapter.notifyDataSetChanged();
        }

        Log.i(LOG_TAG, alarmService.getNextAlarm());
    }

    private void refreshAlarms() {
        alarmService.listAlarms().observe(this, x->{
            shurikenData.setAlarms(x);
            renderShurikenList();
        });
    }

    private void refreshEvents() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        shurikenData.setTomorrow(tomorrow);
        shurikenData.setEvents(calendarApi.getEvents(tomorrow, tomorrow));
        renderShurikenList();
    }

    private void refreshGeneratedAlarm() {
        generatedAlarmService.get(LocalDate.now()).observe(this, alarm -> {
            Log.w(LOG_TAG, "Setting the generated alarm " + alarm);
            shurikenData.setGeneratedAlarmShuriken(new GeneratedAlarmShuriken(alarm));
            renderShurikenList();
        });
    }

    /**
     * Delete alarm
     *
     * @param alarmEntity AlarmEntity
     * @param position    Position of alarm in the ShurikenAdapter, if now know use negative number
     */
    public void deleteAlarm(Alarm alarmEntity, int position) {
        shurikenData.getData().remove(alarmEntity);
        alarmService.removeAlarm(alarmEntity);
        alarmsView.post(() -> {
            if (position >= 0) {
                shurikenAdapter.notifyItemRemoved(position);
            } else {
                shurikenAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.alarm_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Alarm alarm = (Alarm) shurikenData.getData().get(shurikenAdapter.getContextMenuPosition());
        switch (item.getItemId()) {
            case R.id.editAlarm:
                startAlarmFormActivity(alarm);
                return true;
            case R.id.deleteAlarm:
                deleteAlarm(alarm, shurikenAdapter.getContextMenuPosition());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
