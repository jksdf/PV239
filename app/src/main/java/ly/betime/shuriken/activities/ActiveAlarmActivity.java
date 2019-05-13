package ly.betime.shuriken.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import ly.betime.shuriken.App;
import ly.betime.shuriken.R;
import ly.betime.shuriken.apis.AlarmManagerApi;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.entities.GeneratedAlarm;
import ly.betime.shuriken.helpers.LanguageTextHelper;
import ly.betime.shuriken.preferences.Preferences;
import ly.betime.shuriken.service.AlarmService;
import ly.betime.shuriken.service.GeneratedAlarmService;

public class ActiveAlarmActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ActiveAlarmActivity";

    private static final String ACTIVE_ALARM_ACTIVITY_NOTIFICATION = "ACTIVE_ALARM_ACTIVITY";

    public static final String ALARM_ID_EXTRA_NAME = "alarm_id";
    public static final String ALARM_ID_EXTRA_TYPE = "alarm_type";

    private static final long[] VIBRATION_THEME = new long[]{0, 500, 110, 500, 110, 450, 110, 200, 110, 170, 40, 450, 110, 200, 110, 170, 40, 500};
    private static final int[] VIBRATION_AMPLITUDES = new int[]{255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255};

    @Inject
    public AlarmService alarmService;

    @Inject
    public GeneratedAlarmService generatedAlarmService;

    @Inject
    public LanguageTextHelper languageTextHelper;

    @Inject
    public SharedPreferences sharedPreferences;

    private TextView alarmName;
    private TextView alarmTime;
    private TextView alarmPeriod;
    private View snoozeButton;
    private View stopButton;

    private AlarmWrapper alarmWrapper;

    private Ringtone beep;
    private Timer timer;
    private Timer isRingingTimer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getComponent().inject(this);
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "Activated alarm");
        keepScreenOn();

        setContentView(R.layout.activity_active_alarm);

        alarmName = findViewById(R.id.alarmName);
        alarmTime = findViewById(R.id.alarmTime);
        alarmPeriod = findViewById(R.id.alarmTimePeriod);
        snoozeButton = findViewById(R.id.snoozeButton);
        stopButton = findViewById(R.id.stopButton);

        playSound();
        vibrate();
        addListeners();
        ringingLengthLimit();
    }

    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }

    private void vibrate() {
        if (sharedPreferences.getBoolean(Preferences.VIBRATE, Preferences.VIBRATE_DEFAULT)) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(VIBRATION_THEME, VIBRATION_AMPLITUDES, 0));
            } else {
                vibrator.vibrate(VIBRATION_THEME, 0);
            }
        }
    }

    private void ringingLengthLimit() {
        isRingingTimer = new Timer();
        isRingingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "Cancelling after inactivity");
                createNotificationChannel();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ActiveAlarmActivity.this);

                notificationManager.notify(alarmWrapper.getId() * 2 + alarmWrapper.getType().getIndex(),
                        new NotificationCompat.Builder(ActiveAlarmActivity.this, ACTIVE_ALARM_ACTIVITY_NOTIFICATION)
                                .setSmallIcon(R.drawable.ic_alarm_48)
                                .setContentTitle(getString(R.string.alarm_rang_too_long))
                                .setContentText(getString(R.string.cancelled_alarm_notif_msg, alarmWrapper.getRinging().atZone(ZoneId.systemDefault()).toLocalTime()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true)
                                .build());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                } else {
                    finish();
                }
            }
        }, sharedPreferences.getInt(Preferences.MAX_RINGING_TIME, 5) * 60L * 1000L);
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
        if (isRingingTimer != null) {
            isRingingTimer.cancel();
        }
        if (vibrator != null) {
            vibrator.cancel();
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
        Observer<AlarmWrapper> observer = alarm -> {
            alarmName.setText(alarm.getName());
            alarmTime.setText(alarm.getTime().format(languageTextHelper.getAlarmTimeFormatter()));
            alarmPeriod.setText(alarm.getTime().format(languageTextHelper.getAlarmPeriodFormatter()));
        };

        int alarmId = getIntent().getIntExtra(ALARM_ID_EXTRA_NAME, -1);

        switch (AlarmManagerApi.AlarmType.fromIndex(getIntent().getIntExtra(ALARM_ID_EXTRA_TYPE, -1))) {
            case NORMAL:
                alarmService.getAlarm(alarmId).observe(this, alarm -> {
                    if (alarm == null) {
                        throw new IllegalStateException("In " + this.getClass().getName() + " without valid alarm id");
                    }
                    alarmWrapper = new AlarmWrapper(alarm);
                    observer.onChanged(alarmWrapper);
                });
                return;
            case GENERATED:
                generatedAlarmService.get(alarmId).observe(this, generatedAlarm -> {
                    if (generatedAlarm == null) {
                        throw new IllegalStateException("In " + this.getClass().getName() + " without valid alarm id");
                    }
                    alarmWrapper = new AlarmWrapper(generatedAlarm);
                    observer.onChanged(alarmWrapper);
                });
            default:
                throw new AssertionError();
        }

    }

    private void addListeners() {
        snoozeButton.setOnClickListener((v) -> {
            Log.i(LOG_TAG, "Snoozed");
            switch (alarmWrapper.getType()) {
                case NORMAL:
                    alarmService.setAlarm(alarmWrapper.alarm, AlarmService.AlarmAction.SNOOZE);
                    break;
                case GENERATED:
                    generatedAlarmService.snooze(alarmWrapper.generatedAlarm);
                    break;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                finish();
            }
        });

        stopButton.setOnLongClickListener((v) -> {
            Log.i(LOG_TAG, "Stopped");
            switch (alarmWrapper.getType()) {
                case NORMAL:
                    alarmService.setAlarm(alarmWrapper.alarm, AlarmService.AlarmAction.DISABLE);
                    break;
                case GENERATED:
                    generatedAlarmService.cleanUp();
                    break;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                finish();
            }
            return true;
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(ACTIVE_ALARM_ACTIVITY_NOTIFICATION, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private class AlarmWrapper {
        private final Alarm alarm;
        private final GeneratedAlarm generatedAlarm;

        private AlarmWrapper(Alarm alarm) {
            this.alarm = alarm;
            this.generatedAlarm = null;
        }

        private AlarmWrapper(GeneratedAlarm generatedAlarm) {
            this.alarm = null;
            this.generatedAlarm = generatedAlarm;
        }

        public Integer getId() {
            return alarm != null ? alarm.getId() : generatedAlarm.getId();
        }

        public LocalDateTime getRinging() {
            return alarm != null ? alarm.getRinging() : generatedAlarm.getRinging();
        }

        public AlarmManagerApi.AlarmType getType() {
            return alarm != null ? AlarmManagerApi.AlarmType.NORMAL : AlarmManagerApi.AlarmType.GENERATED;
        }

        public String getName() {
            if (alarm != null) {
                return alarm.getName();
            }
            return getString(R.string.generated);
        }

        public LocalTime getTime() {
            return alarm != null ? alarm.getTime() : generatedAlarm.getRinging().toLocalTime();
        }

    }
}
