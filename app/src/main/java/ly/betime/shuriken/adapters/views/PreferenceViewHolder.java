package ly.betime.shuriken.adapters.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ly.betime.shuriken.adapters.PreferencesAdapter;
import ly.betime.shuriken.preferences.Preference;
import ly.betime.shuriken.preferences.Preferences;

public abstract class PreferenceViewHolder extends RecyclerView.ViewHolder {

    protected final PreferencesAdapter adapter;
    protected final SharedPreferences sharedPref;

    public PreferenceViewHolder(@NonNull View itemView, @NonNull PreferencesAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        sharedPref = itemView.getContext().getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE);
    }

    abstract public void setValues(Preference preference);

    protected void applyWithEditor(EditorCallback callback) {
        SharedPreferences.Editor editor = sharedPref.edit();
        callback.call(editor);
        editor.apply();
    }

    @FunctionalInterface
    protected interface EditorCallback {
        void call(SharedPreferences.Editor editor);
    }
}
