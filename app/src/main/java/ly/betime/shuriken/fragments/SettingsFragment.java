package ly.betime.shuriken.fragments;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.PreferencesAdapter;
import ly.betime.shuriken.preferences.Preference;
import ly.betime.shuriken.preferences.Preferences;
import ly.betime.shuriken.preferences.Sound;

public class SettingsFragment extends Fragment {

    private static final int SOUND_PICKER_ACTIVITY = 64;

    private final List<Preference> preferenceList = Arrays.asList(
            new Preference(Preferences.ALARM_SOUND, Preferences.ALARM_SOUND_DEFAULT, R.string.alarm_sound, Sound.class),
            new Preference(Preferences.SNOOZE_TIME, Preferences.SNOOZE_TIME_DEFAULT, R.string.snooze_settings, Integer.class),
            new Preference(Preferences.MAX_RINGING_TIME, Preferences.MAX_RINGING_TIME_DEFAULT, R.string.max_ring_settings, Integer.class),
            new Preference(Preferences.VIBRATE, Preferences.VIBRATE_DEFAULT, R.string.vibrate_settings, Boolean.class)
    );

    private RecyclerView preferencesListView;
    private PreferencesAdapter preferencesAdapter;

    private OnSoundPicked soundPickedCallback;


    public SettingsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        preferencesListView = view.findViewById(R.id.preferencesList);
        preferencesAdapter = new PreferencesAdapter(this, preferenceList);
        preferencesListView.setAdapter(preferencesAdapter);
        preferencesListView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    public void showSoundPicker(OnSoundPicked callback, @javax.annotation.Nullable String  defaultValue) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
