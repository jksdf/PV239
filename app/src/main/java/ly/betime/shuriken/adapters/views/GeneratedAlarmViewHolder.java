package ly.betime.shuriken.adapters.views;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.ShurikenAdapter;
import ly.betime.shuriken.adapters.data.GeneratedAlarmShuriken;
import ly.betime.shuriken.entities.GeneratedAlarm;

public class GeneratedAlarmViewHolder extends ShurikenViewHolder {
    public final static int VIEW = R.layout.generated_alarm_item;

    private GeneratedAlarmShuriken alarmShuriken;
    private GeneratedAlarm alarm;

    private final TextView title;
    private final TextView alarmTime;
    private final TextView alarmTimePeriod;
    private final SwitchCompat switchButton;

    public GeneratedAlarmViewHolder(@NonNull View itemView, ShurikenAdapter adapter) {
        super(itemView, adapter);
        title = itemView.findViewById(R.id.alarmTitle);
        alarmTime = itemView.findViewById(R.id.alarmTimeTextView);
        alarmTimePeriod = itemView.findViewById(R.id.alarmTimePeriodTextView);
        switchButton = itemView.findViewById(R.id.alarmSwitch);

        setListeners();
    }

    private void setListeners() {
        switchButton.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            // TODO(slivka): Uncomment when isEnabled is added
//            if (alarm.isEnabled() != isChecked) {
//                if (adapter.getGeneratedAlarmSwitchListener() != null) {
//                    adapter.getGeneratedAlarmSwitchListener().alarmEnabledChanged(alarm, isChecked);
//                }
//                itemView.post(() -> adapter.notifyItemChanged(position));
//            }
        }));
    }

    @Override
    public void bind(int position) {
        super.bind(position);
        alarmShuriken = (GeneratedAlarmShuriken) adapter.getShurikens().get(position);
        alarm = alarmShuriken.getAlarm();

        if (alarmShuriken.isShowDate()) {
            title.setText(itemView.getContext().getResources().getString(
                    R.string.recommendation_title_with_date,
                adapter.getLanguageTextHelper().getAlarmTitleFormatter().format(alarm.getRinging())
            ));
        } else {
            title.setText(R.string.recommendation_title);
        }

        alarmTime.setText(alarm.getRinging().format(adapter.getLanguageTextHelper().getAlarmTimeFormatter())); // TODO(slivka): getTime
        alarmTimePeriod.setText(alarm.getRinging().format(adapter.getLanguageTextHelper().getAlarmPeriodFormatter())); // TODO(slivka): getTime
        //switchButton.setChecked(alarm.isEnabled()); // TODO(slivka): Uncomment when isEnabled is added
//
        setColors(true); // TODO(slivka): add alarm isEnabled when finished
    }

    /**
     * Change design of view according to its enabled value
     */
    public void setColors(boolean enabled) {
        // WTF: You can't change style of view in code
        if (enabled) {
            title.setTextColor(itemView.getResources().getColor(R.color.colorText));
            alarmTime.setTextColor(itemView.getResources().getColor(R.color.colorText));
            alarmTimePeriod.setTextColor(itemView.getResources().getColor(R.color.colorText));
        } else {
            title.setTextColor(itemView.getResources().getColor(R.color.colorTextDark));
            alarmTime.setTextColor(itemView.getResources().getColor(R.color.colorTextDark));
            alarmTimePeriod.setTextColor(itemView.getResources().getColor(R.color.colorTextDark));
        }
    }
}
