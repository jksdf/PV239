package ly.betime.shuriken.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ly.betime.shuriken.R;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.helpers.LanguageTextHelper;

/**
 * Adapter for rendering {@link ly.betime.shuriken.entities.Alarm} in {@link RecyclerView}
 */
public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.AlarmViewHolder> {

    private int contextMenuPosition;
    private final List<Alarm> alarms;
    private final LanguageTextHelper languageTextHelper;
    private AlarmSwitchListener alarmSwitchListener;

    public AlarmsAdapter(List<Alarm> alarms, LanguageTextHelper languageTextHelper) {
        this.alarms = alarms;
        this.languageTextHelper = languageTextHelper;
    }

    public void setAlarmSwitchListener(AlarmSwitchListener alarmSwitchListener) {
        this.alarmSwitchListener = alarmSwitchListener;
    }

    public int getContextMenuPosition() {
        return contextMenuPosition;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.alarm_item, viewGroup, false);

        // Return a new holder instance
        return new AlarmViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder viewHolder, int position) {
        // Get the data model based on position
        Alarm alarm = alarms.get(position);

        viewHolder.alarmTime.setText(alarm.getTime().format(languageTextHelper.getAlarmTimeFormatter()));
        viewHolder.alarmTimePeriod.setText(alarm.getTime().format(languageTextHelper.getAlarmPeriodFormatter()));
        viewHolder.alarmRepeat.setText(languageTextHelper.getAlarmRepeatText(alarm.getRepeating()));
        viewHolder.switchButton.setChecked(alarm.isEnabled());

        viewHolder.setColors(alarm.isEnabled());
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder {
        final TextView alarmTime;
        final TextView alarmTimePeriod;
        final TextView alarmRepeat;
        final Switch switchButton;

        AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.alarmTimeTextView);
            alarmTimePeriod = itemView.findViewById(R.id.alarmTimePeriodTextView);
            alarmRepeat = itemView.findViewById(R.id.alarmRepeatTextView);
            switchButton = itemView.findViewById(R.id.alarmSwitch);

            setListeners();
        }

        private void setListeners() {
            switchButton.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                Alarm alarm = alarms.get(getAdapterPosition());
                if (alarm.isEnabled() != isChecked) {
                    if (AlarmsAdapter.this.alarmSwitchListener != null) {
                        AlarmsAdapter.this.alarmSwitchListener.alarmEnabledChanged(alarm, isChecked);
                    }
                    itemView.post(() -> AlarmsAdapter.this.notifyItemChanged(getAdapterPosition()));
                }
            }));

            itemView.findViewById(R.id.alarmContainer).setOnLongClickListener((View v) -> {
                AlarmsAdapter.this.contextMenuPosition = getAdapterPosition();
                itemView.showContextMenu();
                return true;
            });
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
    }

    @FunctionalInterface
    public interface AlarmSwitchListener {
        void alarmEnabledChanged(Alarm alarmEntity, boolean enabled);
    }
}
