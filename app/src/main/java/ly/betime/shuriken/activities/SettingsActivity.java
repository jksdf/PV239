package ly.betime.shuriken.activities;

import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.PreferencesAdapter;
import ly.betime.shuriken.preferences.Preference;
import ly.betime.shuriken.preferences.Preferences;

public class SettingsActivity extends AMenuActivity {

    private final List<Preference> preferenceList = Arrays.asList(
            new Preference(Preferences.HOUR_FORMAT_24, R.string.hour_format_24, Boolean.class),
            new Preference(Preferences.SNOOZE_TIME, R.string.snooze_settings, Integer.class)
    );

    private RecyclerView preferencesListView;
    private PreferencesAdapter preferencesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferencesListView = findViewById(R.id.preferencesList);
        preferencesAdapter = new PreferencesAdapter(preferenceList);
        preferencesListView.setAdapter(preferencesAdapter);
        preferencesListView.setLayoutManager(new LinearLayoutManager(this));
    }


}
