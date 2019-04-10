package ly.betime.shuriken.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Lists;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ly.betime.shuriken.App;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.AlarmsAdapter;
import ly.betime.shuriken.apis.CalendarApi;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.helpers.LanguageTextHelper;
import ly.betime.shuriken.service.AlarmService;

public class AlarmsActivity extends AMenuActivity {

    private static final String LOG_TAG = "AlarmsActivity";

    @Inject
    public AlarmService alarmService;
    @Inject
    public  LanguageTextHelper languageTextHelper;
    @Inject
    public CalendarApi calendarApi;

    private List<Alarm> alarms;
    private AlarmsAdapter alarmsAdapter;

    private RecyclerView alarmsView;
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getComponent().inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarms);
        alarmsView = findViewById(R.id.alarmsContainer);
        registerForContextMenu(alarmsView);

        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener((View v) -> startAlarmFormActivity(null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderAlarmList();
    }

    /**
     * Starts AlarmFormActivity
     *
     * @param alarm Alarm for editing if any is needed
     */
    protected void startAlarmFormActivity(@Nullable Alarm alarm) {
        Intent intent = new Intent(this, AlarmFormActivity.class);
        if (alarm != null) {
            intent.putExtra(AlarmFormActivity.ALARM_ID_MESSAGE, alarm.getId());
        }
        startActivity(intent);
    }

    /**
     * Gets all alarms from service and render them. Should be only used if all items have to be rendered.
     * Use notify functions of {@link RecyclerView.Adapter} if only one item changed.
     */
    public void renderAlarmList() {
        alarmService.listAlarms().observe(this, x->{
            if (alarms == null) {
                alarms = Lists.newArrayList(x);
            } else {
                alarms.clear();
                alarms.addAll(x);
            }

            Collections.sort(alarms, (a, b) -> a.getTime().compareTo(b.getTime()));

            if (alarmsAdapter == null) {
                alarmsAdapter = new AlarmsAdapter(alarms, languageTextHelper);
                alarmsAdapter.setAlarmSwitchListener(
                        (alarm, state) ->
                                alarmService.setAlarm(
                                        alarm,
                                        state ? AlarmService.AlarmAction.ENABLE : AlarmService.AlarmAction.DISABLE));

                alarmsView.setAdapter(alarmsAdapter);
                alarmsView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                alarmsAdapter.notifyDataSetChanged();
            }
        });

        if (calendarApi.getPermission(this)) {
            Log.i(LOG_TAG, "Next week events: " + calendarApi.getEvents(LocalDate.now(), LocalDate.now().plusDays(6)).toString());
        }
    }

    /**
     * Delete alarm
     *
     * @param alarmEntity AlarmEntity
     */
    public void deleteAlarm(Alarm alarmEntity) {
        deleteAlarm(alarmEntity, -1);
    }

    /**
     * Delete alarm
     *
     * @param alarmEntity AlarmEntity
     * @param position    Position of alarm in the AlarmsAdapter, if now know use negative number
     */
    public void deleteAlarm(Alarm alarmEntity, int position) {
        alarms.remove(alarmEntity);
        alarmService.removeAlarm(alarmEntity);
        alarmsView.post(() -> {
            if (position >= 0) {
                alarmsAdapter.notifyItemRemoved(position);
            } else {
                alarmsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alarm_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Alarm alarm = alarms.get(alarmsAdapter.getContextMenuPosition());
        switch (item.getItemId()) {
            case R.id.editAlarm:
                startAlarmFormActivity(alarm);
                return true;
            case R.id.deleteAlarm:
                deleteAlarm(alarm, alarmsAdapter.getContextMenuPosition());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
