package ly.betime.shuriken;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import ly.betime.shuriken.adapter.AlarmsAdapter;
import ly.betime.shuriken.helpers.LanguageTextHelper;
import ly.betime.shuriken.service.AlarmEntity;
import ly.betime.shuriken.service.AlarmService;
import ly.betime.shuriken.service.DummyAlarmService;

public class AlarmsActivity extends AppCompatActivity {

    private final static Logger LOGGER = Logger.getLogger(AlarmsActivity.class.getName());

    // TODO: DI by Nororok
    private AlarmService alarmService;
    private LanguageTextHelper languageTextHelper;

    private List<AlarmEntity> alarms;
    private AlarmsAdapter alarmsAdapter;

    private RecyclerView alarmsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidThreeTen.init(this);
        alarmService = new DummyAlarmService(25);
        languageTextHelper = new LanguageTextHelper(this);

        setContentView(R.layout.activity_alarms);
        alarmsView = findViewById(R.id.alarmsContainer);

        renderAlarmList();
    }

    /**
     * Gets all alarms from service and render them. Should be only used if all items have to be rendered.
     * Use notify functions of {@link RecyclerView.Adapter} if only one item changed.
     */
    private void renderAlarmList() {
        if (alarmsAdapter == null) {
            alarms = Lists.newArrayList(alarmService.listAlarms());
            Collections.sort(alarms, (a, b) -> a.getTime().compareTo(b.getTime()));

            alarmsAdapter = new AlarmsAdapter(alarms, languageTextHelper);
            alarmsAdapter.setAlarmSwitchListener((alarm) -> alarmService.updateAlarm(alarm));

            alarmsView.setAdapter(alarmsAdapter);
            alarmsView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            alarmsAdapter.notifyDataSetChanged();
        }
    }
}
