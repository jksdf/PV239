package ly.betime.shuriken.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import ly.betime.shuriken.App;
import ly.betime.shuriken.R;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.helpers.LanguageTextHelper;
import ly.betime.shuriken.service.AlarmService;

public class ActiveAlarmActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ActiveAlarmActivity";

    public static final String ALARM_ID_EXTRA_NAME = "alarm_id";

    @Inject
    public AlarmService alarmService;

    @Inject
    public LanguageTextHelper languageTextHelper;

    private TextView alarmName;
    private TextView alarmTime;
    private View snoozeButton;
    private View stopButton;

    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getComponent().inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_alarm);

        alarmName = findViewById(R.id.alarmName);
        alarmTime = findViewById(R.id.alarmTime);
        snoozeButton = findViewById(R.id.snoozeButton);
        stopButton = findViewById(R.id.stopButton);

        addListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAlarmValues();
    }

    private void setAlarmValues() {
        int alarmId = getIntent().getIntExtra(ALARM_ID_EXTRA_NAME, -1);
        alarm = alarmService.getAlarm(alarmId);
        if (alarm == null) {
            throw new IllegalStateException("In " + this.getClass().getName() + " without valid alarm id");
        }
        alarmName.setText(alarm.getName());
        alarmTime.setText(alarm.getTime().format(languageTextHelper.getAlarmTimeFormatter()));
    }

    private void addListeners() {
        snoozeButton.setOnClickListener((v) -> {
            Log.i(LOG_TAG, "Snoozed");
            // TODO(slivka): Do something
        });

        stopButton.setOnLongClickListener((v) -> {
            Log.i(LOG_TAG, "Stoped");
            // TODO(slivka): Do something even better
            return true;
        });
    }
}
