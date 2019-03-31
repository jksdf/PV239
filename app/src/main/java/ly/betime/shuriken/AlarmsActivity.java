package ly.betime.shuriken;

import android.app.AlarmManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Lists;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import androidx.room.Room;
import ly.betime.shuriken.adapter.AlarmsAdapter;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.helpers.LanguageTextHelper;
import ly.betime.shuriken.persistance.AppDatabase;
import ly.betime.shuriken.service.AlarmManagerApi;
import ly.betime.shuriken.service.AlarmService;
import ly.betime.shuriken.service.AlarmServiceImpl;
import ly.betime.shuriken.service.DummyFiller;

public class AlarmsActivity extends AppCompatActivity {

    private final static Logger LOGGER = Logger.getLogger(AlarmsActivity.class.getName());

    // TODO: DI by Nororok
    private AlarmService alarmService;
    private LanguageTextHelper languageTextHelper;

    private List<Alarm> alarms;
    private AlarmsAdapter alarmsAdapter;

    private RecyclerView alarmsView;
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidThreeTen.init(getApplicationContext());
        // Hardcoded DI is the best DI
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").allowMainThreadQueries().build();
        alarmService = new AlarmServiceImpl(db.alarmDAO(), new AlarmManagerApi((AlarmManager) getSystemService(ALARM_SERVICE), getApplicationContext(), AlarmsActivity.class));
        if (alarmService.listAlarms().size() == 0) {
            for (Alarm alarm : DummyFiller.generate(5)) {
                alarmService.createAlarm(alarm);
            }
        }
        languageTextHelper = new LanguageTextHelper(this);

        setContentView(R.layout.activity_alarms);
        alarmsView = findViewById(R.id.alarmsContainer);
        registerForContextMenu(alarmsView);

        addButton = findViewById(R.id.addButton);

        renderAlarmList();
    }

    /**
     * Gets all alarms from service and render them. Should be only used if all items have to be rendered.
     * Use notify functions of {@link RecyclerView.Adapter} if only one item changed.
     */
    public void renderAlarmList() {
        if (alarmsAdapter == null) {
            alarms = Lists.newArrayList(alarmService.listAlarms());
            Collections.sort(alarms, (a, b) -> a.getTime().compareTo(b.getTime()));

            alarmsAdapter = new AlarmsAdapter(alarms, languageTextHelper);
            alarmsAdapter.setAlarmSwitchListener((alarm, state) -> alarmService.setAlarm(alarm, state));

            alarmsView.setAdapter(alarmsAdapter);
            alarmsView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            alarmsAdapter.notifyDataSetChanged();
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
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Alarm alarm = alarms.get(alarmsAdapter.getContextMenuPosition());
        switch (item.getItemId()) {
            case R.id.editAlarm:
                LOGGER.info("Edit alarm " + alarm);
                return true;
            case R.id.deleteAlarm:
                deleteAlarm(alarm, alarmsAdapter.getContextMenuPosition());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
