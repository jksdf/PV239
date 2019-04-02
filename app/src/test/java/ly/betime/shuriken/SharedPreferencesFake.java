package ly.betime.shuriken;

import android.content.SharedPreferences;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Set;

import androidx.annotation.Nullable;

public class SharedPreferencesFake implements SharedPreferences {

    private final ImmutableMap<String, Object> data;

    public SharedPreferencesFake(Map<String, Object> data) {
        this.data = ImmutableMap.copyOf(data);
    }

    @Override
    public Map<String, ?> getAll() {
        return data;
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return (String) data.getOrDefault(key, defValue);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return (Set<String>) data.getOrDefault(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return (int) data.getOrDefault(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return (long) data.getOrDefault(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return (float) data.getOrDefault(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return (boolean) data.getOrDefault(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return data.containsKey(key);
    }

    @Override
    public Editor edit() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException();
    }
}
