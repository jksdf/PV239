package ly.betime.shuriken.adapter;

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
import ly.betime.shuriken.helpers.LanguageTextHelper;
import ly.betime.shuriken.service.AlarmEntity;

/**
 * Adapter for rendering {@link ly.betime.shuriken.service.AlarmEntity} in {@link RecyclerView}
 */
public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.AlarmViewHolder> {

    private int contextMenuPosition;
    private List<AlarmEntity> alarms;
    private LanguageTextHelper languageTextHelper;
    private AlarmSwitchListener alarmSwitchListener;

    public AlarmsAdapter(List<AlarmEntity> alarms, LanguageTextHelper languageTextHelper) {
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
        AlarmEntity alarm = alarms.get(position);

        viewHolder.alarmTime.setText(alarm.getTime().format(languageTextHelper.getAlarmTimeFormatter()));
        viewHolder.alarmRepeat.setText(languageTextHelper.getAlarmRepeatText(alarm.getRepeating()));
        viewHolder.switchButton.setChecked(alarm.isEnabled());

        viewHolder.setColors(alarm.isEnabled());
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView alarmTime;
        TextView alarmRepeat;
        Switch switchButton;

        AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.alarmTimeTextView);
            alarmRepeat = itemView.findViewById(R.id.alarmRepeatTextView);
            switchButton = itemView.findViewById(R.id.alarmSwitch);

            setListeners();
        }

        private void setListeners() {
            switchButton.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                AlarmEntity alarm = alarms.get(getAdapterPosition());
                if (alarm.isEnabled() != isChecked) {
                    alarm.setEnabled(isChecked);
                    itemView.post(() -> AlarmsAdapter.this.notifyItemChanged(getAdapterPosition()));
                    if (AlarmsAdapter.this.alarmSwitchListener != null) {
                        AlarmsAdapter.this.alarmSwitchListener.alarmEnabledChanged(alarm);
                    }
                }
            }));

            itemView.setOnLongClickListener((View v) -> {
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
                alarmTime.setTextColor(itemView.getResources().getColor(R.color.colorText, null));
                alarmRepeat.setTextColor(itemView.getResources().getColor(R.color.colorText, null));
            } else {
                alarmTime.setTextColor(itemView.getResources().getColor(R.color.colorTextDark, null));
                alarmRepeat.setTextColor(itemView.getResources().getColor(R.color.colorTextDark, null));
            }
        }
    }

    @FunctionalInterface
    public interface AlarmSwitchListener {
        void alarmEnabledChanged(AlarmEntity alarmEntity);
    }
}
