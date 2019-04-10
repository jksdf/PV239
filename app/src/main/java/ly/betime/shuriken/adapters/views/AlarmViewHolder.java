package ly.betime.shuriken.adapters.views;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.ShurikenAdapter;
import ly.betime.shuriken.entities.Alarm;

public class AlarmViewHolder extends ShurikenViewHolder {
    public final static int VIEW = R.layout.alarm_item;

    private final TextView alarmTime;
    private final TextView alarmTimePeriod;
    private final TextView alarmRepeat;
    private final Switch switchButton;

    public AlarmViewHolder(@NonNull View itemView, ShurikenAdapter adapter) {
        super(itemView, adapter);
        alarmTime = itemView.findViewById(R.id.alarmTimeTextView);
        alarmTimePeriod = itemView.findViewById(R.id.alarmTimePeriodTextView);
        alarmRepeat = itemView.findViewById(R.id.alarmRepeatTextView);
        switchButton = itemView.findViewById(R.id.alarmSwitch);

        setListeners();
    }

    private void setListeners() {
        switchButton.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            Alarm alarm = (Alarm) adapter.getShurikens().get(getAdapterPosition());
            if (alarm.isEnabled() != isChecked) {
                if (adapter.getAlarmSwitchListener() != null) {
                    adapter.getAlarmSwitchListener().alarmEnabledChanged(alarm, isChecked);
                }
                itemView.post(() -> adapter.notifyItemChanged(getAdapterPosition()));
            }
        }));

        itemView.findViewById(R.id.alarmContainer).setOnLongClickListener((View v) -> {
            adapter.setContextMenuPosition(getAdapterPosition());
            itemView.showContextMenu();
            return true;
        });
    }

    public void bind(int position) {
        Alarm alarm = (Alarm) adapter.getShurikens().get(position);

        alarmTime.setText(alarm.getTime().format(adapter.getLanguageTextHelper().getAlarmTimeFormatter()));
        alarmTimePeriod.setText(alarm.getTime().format(adapter.getLanguageTextHelper().getAlarmPeriodFormatter()));
        alarmRepeat.setText(adapter.getLanguageTextHelper().getAlarmRepeatText(alarm.getRepeating()));
        switchButton.setChecked(alarm.isEnabled());

        setColors(alarm.isEnabled());
    }

    /**
     * Change design of view according to its enabled value
     */
    public void setColors(boolean enabled) {
        // WTF: You can't change style of view in code
        if (enabled) {
            alarmTime.setTextColor(itemView.getResources().getColor(R.color.colorText));
            alarmTimePeriod.setTextColor(itemView.getResources().getColor(R.color.colorText));
            alarmRepeat.setTextColor(itemView.getResources().getColor(R.color.colorText));
        } else {
            alarmTime.setTextColor(itemView.getResources().getColor(R.color.colorTextDark));
            alarmTimePeriod.setTextColor(itemView.getResources().getColor(R.color.colorTextDark));
            alarmRepeat.setTextColor(itemView.getResources().getColor(R.color.colorTextDark));
        }
    }

    public TextView getAlarmTime() {
        return alarmTime;
    }

    public TextView getAlarmTimePeriod() {
        return alarmTimePeriod;
    }

    public TextView getAlarmRepeat() {
        return alarmRepeat;
    }

    public Switch getSwitchButton() {
        return switchButton;
    }
}
