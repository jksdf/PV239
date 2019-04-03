package ly.betime.shuriken.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.PreferencesAdapter;
import ly.betime.shuriken.preferences.Preference;
import ly.betime.shuriken.preferences.Preferences;
import ly.betime.shuriken.preferences.Sound;

public class SettingsActivity extends AMenuActivity {

    private static final int SOUND_PICKER_ACTIVITY = 64;

    private final List<Preference> preferenceList = Arrays.asList(
            new Preference(Preferences.HOUR_FORMAT_24, R.string.hour_format_24, Boolean.class),
            new Preference(Preferences.ALARM_SOUND, R.string.alarm_sound, Sound.class),
            new Preference(Preferences.SNOOZE_TIME, R.string.snooze_settings, Integer.class)
    );

    private RecyclerView preferencesListView;
    private PreferencesAdapter preferencesAdapter;

    private OnSoundPicked soundPickedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferencesListView = findViewById(R.id.preferencesList);
        preferencesAdapter = new PreferencesAdapter(this, preferenceList);
        preferencesListView.setAdapter(preferencesAdapter);
        preferencesListView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void showSoundPicker(OnSoundPicked callback, @Nullable String  defaultValue) {
        soundPickedCallback = callback;
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.sound_pick_title));
        if (defaultValue != null) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(defaultValue));
        } else {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        }
        this.startActivityForResult(intent, SOUND_PICKER_ACTIVITY);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == SOUND_PICKER_ACTIVITY) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (soundPickedCallback != null) {
                soundPickedCallback.call(uri);
            }
        }
    }

    @FunctionalInterface
    public interface OnSoundPicked {
        void call(Uri uri);
    }
}
