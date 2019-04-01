package ly.betime.shuriken.activities;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

import java.util.EnumSet;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.room.Room;
import dagger.android.AndroidInjection;
import ly.betime.shuriken.App;
import ly.betime.shuriken.R;
import ly.betime.shuriken.dialogs.AlarmRepeatDialog;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.helpers.LanguageTextHelper;
import ly.betime.shuriken.persistance.AppDatabase;
import ly.betime.shuriken.service.AlarmManagerApi;
import ly.betime.shuriken.service.AlarmService;
import ly.betime.shuriken.service.AlarmServiceImpl;

public class AlarmFormActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AlarmFormActivity";

    public final static String ALARM_ID_MESSAGE = "alarmId";

    private static final String REPEAT_DIALOG_TAG = "repeatDialog";

    @Inject
    public AlarmService alarmService;
    @Inject
    public LanguageTextHelper languageTextHelper;

    private Alarm alarm;

    private TimePicker timePicker;
    private View labelContainer;
    private EditText labelEditText;
    private View repeatContainer;
    private TextView repeatValueText;

    private AlarmRepeatDialog repeatDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getComponent().inject(this);
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "alarm service" + alarmService);
        setContentView(R.layout.activity_alarm_form);

        labelEditText = findViewById(R.id.labelText);
        timePicker = findViewById(R.id.timePicker);

        labelContainer = findViewById(R.id.labelContainer);
        labelContainer.setOnClickListener((v) -> labelEditText.requestFocus());

        repeatContainer = findViewById(R.id.repeatContainer);
        repeatContainer.setOnClickListener((v) -> showRepeatDialog());
        repeatValueText = findViewById(R.id.repeatValue);
    }

    private void showRepeatDialog() {
        repeatDialog = new AlarmRepeatDialog();
        if (alarm != null) {
            repeatDialog.setSelectedDays(alarm.getRepeating());
        }
        repeatDialog.setOnSavedListener(() -> {
            repeatValueText.setText(
                    languageTextHelper.getAlarmRepeatText(
                            EnumSet.copyOf(repeatDialog.getSelectedDays())
                    )
            );
        });
        repeatDialog.show(getSupportFragmentManager(), REPEAT_DIALOG_TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        int alarmId = intent.getIntExtra(ALARM_ID_MESSAGE, 0);
        alarm = null;
        if (alarmId != 0) {
            alarm = alarmService.getAlarm(alarmId);
        }
        setFormValues(alarm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.alarm_form_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_alarm:
                saveAlarm();
                returnToAlarmsList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void returnToAlarmsList() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void saveAlarm() {
        if (alarm == null) {
            alarm = new Alarm();
            alarm.setEnabled(true);
            setAlarmValues(alarm);
            alarmService.createAlarm(alarm);
        } else {
            setAlarmValues(alarm);
            alarmService.updateAlarm(alarm);
        }
    }

    /**
     * Set values from the form to the Alarm
     */
    private void setAlarmValues(Alarm alarm) {
        alarm.setName(labelEditText.getText().toString());
        alarm.setTime(LocalTime.of(timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
        alarm.setRepeating(repeatDialog != null ?
                EnumSet.copyOf(repeatDialog.getSelectedDays()) : EnumSet.noneOf(DayOfWeek.class));
    }

    /**
     * Set values from the Alarm to the form
     */
    private void setFormValues(Alarm alarm) {
        labelEditText.setText(alarm != null ? alarm.getName() : "");
        LocalTime time = alarm != null ? alarm.getTime() : LocalTime.now();
        timePicker.setCurrentHour(time.getHour());
        timePicker.setCurrentMinute(time.getMinute());
        timePicker.setIs24HourView(true); // TODO: Add to settings
        repeatValueText.setText(languageTextHelper.getAlarmRepeatText(
                alarm != null ? alarm.getRepeating() : EnumSet.noneOf(DayOfWeek.class)
        ));
    }
}
