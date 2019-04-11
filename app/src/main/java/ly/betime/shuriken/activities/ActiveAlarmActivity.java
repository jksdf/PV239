package ly.betime.shuriken.activities;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import ly.betime.shuriken.App;
import ly.betime.shuriken.R;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.helpers.LanguageTextHelper;
import ly.betime.shuriken.preferences.Preferences;
import ly.betime.shuriken.service.AlarmService;

public class ActiveAlarmActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ActiveAlarmActivity";

    public static final String ALARM_ID_EXTRA_NAME = "alarm_id";
    public static final String ALARM_ID_EXTRA_TYPE = "alarm_type";

    @Inject
    public AlarmService alarmService;

    @Inject
    public LanguageTextHelper languageTextHelper;

    private TextView alarmName;
    private TextView alarmTime;
    private TextView alarmPeriod;
    private View snoozeButton;
    private View stopButton;

    private Alarm alarm;

    private Ringtone beep;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getComponent().inject(this);
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "Activated alarm");

        setContentView(R.layout.activity_active_alarm);

        alarmName = findViewById(R.id.alarmName);
        alarmTime = findViewById(R.id.alarmTime);
        alarmPeriod = findViewById(R.id.alarmTimePeriod);
        snoozeButton = findViewById(R.id.snoozeButton);
        stopButton = findViewById(R.id.stopButton);

        playSound();
        addListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAlarmValues();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        beep.stop();
    }

    private void playSound() {
        String sound = getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE).getString(Preferences.ALARM_SOUND, null);
        beep = RingtoneManager.getRingtone(getApplicationContext(), sound != null ? Uri.parse(sound) : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            beep.setLooping(true);
        } else {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (!beep.isPlaying()) {
                        beep.play();
                    }
                }
            }, 1000, 1000);
        }
        beep.play();
    }

    private void setAlarmValues() {
        int alarmId = getIntent().getIntExtra(ALARM_ID_EXTRA_NAME, -1);
        alarmService.getAlarm(alarmId).observe(this, newAlarm -> {
            alarm = newAlarm;
            if (alarm == null) {
                throw new IllegalStateException("In " + this.getClass().getName() + " without valid alarm id");
            }
            alarmName.setText(alarm.getName());
            alarmTime.setText(alarm.getTime().format(languageTextHelper.getAlarmTimeFormatter()));
            alarmPeriod.setText(alarm.getTime().format(languageTextHelper.getAlarmPeriodFormatter()));
        });
    }

    private void addListeners() {
        snoozeButton.setOnClickListener((v) -> {
            Log.i(LOG_TAG, "Snoozed");
            alarmService.setAlarm(alarm, AlarmService.AlarmAction.SNOOZE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                finish();
            }
        });

        stopButton.setOnLongClickListener((v) -> {
            Log.i(LOG_TAG, "Stoped");
            alarmService.setAlarm(alarm, AlarmService.AlarmAction.DISABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                finish();
            }
            return true;
        });
    }
}
