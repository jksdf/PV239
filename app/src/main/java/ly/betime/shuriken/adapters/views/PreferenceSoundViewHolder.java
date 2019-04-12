package ly.betime.shuriken.adapters.views;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.PreferencesAdapter;
import ly.betime.shuriken.preferences.Preference;

public class PreferenceSoundViewHolder extends PreferenceViewHolder {

    public static final int LAYOUT = R.layout.preference_sound;

    private final TextView label;
    private final TextView soundName;

    public PreferenceSoundViewHolder(@NonNull View itemView, @NonNull PreferencesAdapter adapter) {
        super(itemView, adapter);
        label = itemView.findViewById(R.id.labelText);
        soundName = itemView.findViewById(R.id.soundName);
        setListener();
    }

    private void setListener() {
        itemView.setOnClickListener((v) -> {
            adapter.getActivity().showSoundPicker(uri -> {
                applyWithEditor((e) -> {
                    Preference preference = adapter.get(getAdapterPosition());
                    e.putString(
                            preference.getName(),
                            uri != null ? uri.toString() : null
                    );
                    setSoundName(uri);
                });
            }, sharedPref.getString(adapter.get(getAdapterPosition()).getName(), null));
        });
    }

    @Override
    public void setValues(Preference preference) {
        label.setText(preference.getLabelStringId());
        setSoundName(sharedPref.getString(preference.getName(), (String) preference.getDefaultValue()));
    }

    private void setSoundName(String value) {
        setSoundName(value != null ? Uri.parse(value) : null);
    }

    private void setSoundName(Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(itemView.getContext(), uri);
        if (ringtone != null) {
            soundName.setText(ringtone.getTitle(itemView.getContext()));
        } else {
            soundName.setText(""); // Couldn't get any sound in the phone
        }
    }
}
