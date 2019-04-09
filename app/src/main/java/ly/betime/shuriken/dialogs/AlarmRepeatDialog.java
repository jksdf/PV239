package ly.betime.shuriken.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.TextStyle;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import ly.betime.shuriken.R;

public class AlarmRepeatDialog extends DialogFragment {

    private final Map<DayOfWeek, Boolean> selectedDays = new HashMap<>();
    private OnSavedListener onSavedListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.repeat).setMultiChoiceItems(getDays(), getCheckedDays(),
                (DialogInterface dialog, int which, boolean isChecked) -> selectedDays.put(DayOfWeek.values()[which], isChecked)).setPositiveButton(R.string.ok, (dialog, id) -> {
                    if (onSavedListener != null) {
                        onSavedListener.onSaved();
                    }
        });

        return builder.create();
    }

    private String[] getDays() {
        String[] days = new String[DayOfWeek.values().length];
        int i = 0;
        for (DayOfWeek day : EnumSet.allOf(DayOfWeek.class)) {
            days[i++] = day.getDisplayName(TextStyle.FULL, getContext().getResources().getConfiguration().locale);
        }
        return days;
    }

    private boolean[] getCheckedDays() {
        boolean[] days = new boolean[DayOfWeek.values().length];
        int i = 0;
        for (DayOfWeek day : EnumSet.allOf(DayOfWeek.class)) {
            days[i++] = getDayValue(day);
        }
        return days;
    }

    public boolean getDayValue(DayOfWeek day) {
        if (selectedDays.containsKey(day)) {
            return selectedDays.get(day);
        }
        return false;
    }

    public void setSelectedDays(Iterable<DayOfWeek> selected) {
        selectedDays.clear();
        for (DayOfWeek d : selected) {
            selectedDays.put(d, true);
        }
    }

    public EnumSet<DayOfWeek> getSelectedDays() {
        EnumSet<DayOfWeek> selected = EnumSet.noneOf(DayOfWeek.class);
        for (Map.Entry<DayOfWeek, Boolean> day : selectedDays.entrySet()) {
            if (day.getValue()) {
                selected.add(day.getKey());
            }
        }
        return selected;
    }

    public void setOnSavedListener(OnSavedListener onSavedListener) {
        this.onSavedListener = onSavedListener;
    }

    @FunctionalInterface
    public interface OnSavedListener {
        void onSaved();
    }
}
