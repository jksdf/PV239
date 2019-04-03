package ly.betime.shuriken.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ly.betime.shuriken.adapters.views.PreferenceBooleanViewHolder;
import ly.betime.shuriken.adapters.views.PreferenceIntegerViewHolder;
import ly.betime.shuriken.adapters.views.PreferenceViewHolder;
import ly.betime.shuriken.preferences.Preference;

public class PreferencesAdapter extends RecyclerView.Adapter<PreferenceViewHolder> {

    private final static int INTEGER_VIEW = 1, BOOLEAN_VIEW = 2;

    private final List<Preference> preferenceList;

    public PreferencesAdapter(List<Preference> preferenceList) {
        this.preferenceList = preferenceList;
    }

    @Override
    public int getItemViewType(int position) {
        Preference preference = preferenceList.get(position);
        if (preference.getType().equals(Boolean.class)) {
            return BOOLEAN_VIEW;
        }
        if (preference.getType().equals(Integer.class)) {
            return INTEGER_VIEW;
        }
        return -1;
    }

    @NonNull
    @Override
    public PreferenceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView;
        switch (viewType) {
            case INTEGER_VIEW:
                contactView = inflater.inflate(PreferenceIntegerViewHolder.LAYOUT, viewGroup, false);
                return new PreferenceIntegerViewHolder(contactView, this);
            case BOOLEAN_VIEW:
                contactView = inflater.inflate(PreferenceBooleanViewHolder.LAYOUT, viewGroup, false);
                return new PreferenceBooleanViewHolder(contactView, this);
            default:
                throw new IllegalStateException("Unknown view type inside preferecnes adapter");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder viewHolder, int position) {
        Preference preference = preferenceList.get(position);
        viewHolder.setValues(preference);
    }

    public Preference get(int position) {
        return preferenceList.get(position);
    }

    @Override
    public int getItemCount() {
        return preferenceList.size();
    }
}
