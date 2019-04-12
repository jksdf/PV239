package ly.betime.shuriken.adapters.views;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.PreferencesAdapter;
import ly.betime.shuriken.preferences.Preference;

public class PreferenceBooleanViewHolder extends PreferenceViewHolder {

    public static final int LAYOUT = R.layout.preference_boolean;

    private final TextView label;
    private final Switch aSwitch;

    public PreferenceBooleanViewHolder(@NonNull View itemView, @NonNull PreferencesAdapter adapter) {
        super(itemView, adapter);
        label = itemView.findViewById(R.id.labelText);
        aSwitch = itemView.findViewById(R.id.aSwitch);
        setSwitchListener();
    }

    private void setSwitchListener() {
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                applyWithEditor((e) -> e.putBoolean(adapter.get(getAdapterPosition()).getName(), isChecked)));
    }

    @Override
    public void setValues(Preference preference) {
        label.setText(preference.getLabelStringId());
        aSwitch.setChecked(sharedPref.getBoolean(preference.getName(), (Boolean) preference.getDefaultValue()));
    }
}
