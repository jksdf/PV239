package ly.betime.shuriken.adapters.views;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.PreferencesAdapter;
import ly.betime.shuriken.preferences.Preference;

public class PreferenceIntegerViewHolder extends PreferenceViewHolder {

    public static final int LAYOUT = R.layout.preference_integer;

    private final TextView label;
    private final EditText value;

    public PreferenceIntegerViewHolder(@NonNull View itemView, @NonNull PreferencesAdapter adapter) {
        super(itemView, adapter);
        label = itemView.findViewById(R.id.labelText);
        value = itemView.findViewById(R.id.value);
        itemView.setOnClickListener((v) -> value.requestFocus());
        setValueListener();
    }

    private void setValueListener() {
        value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                applyWithEditor((e) -> {
                    try {
                        e.putInt(adapter.get(getAdapterPosition()).getName(), Integer.valueOf(s.toString()));
                    } catch (NumberFormatException exception) {
                        // Everything is ok, we don't care
                    }
                });
            }
        });
    }

    @Override
    public void setValues(Preference preference) {
        label.setText(preference.getLabelStringId());
        value.setText(String.valueOf(sharedPref.getInt(preference.getName(), (Integer) preference.getDefaultValue())));
    }
}
