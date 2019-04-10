package ly.betime.shuriken.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ly.betime.shuriken.adapters.views.AlarmViewHolder;
import ly.betime.shuriken.adapters.views.EventViewHolder;
import ly.betime.shuriken.adapters.views.ShurikenViewHolder;
import ly.betime.shuriken.apis.CalendarEvent;
import ly.betime.shuriken.entities.Alarm;
import ly.betime.shuriken.helpers.LanguageTextHelper;

/**
 * Adapter for rendering {@link ly.betime.shuriken.entities.Alarm} in {@link RecyclerView}
 */
public class ShurikenAdapter extends RecyclerView.Adapter<ShurikenViewHolder> {

    private final static int ALARM_VIEW = 1, EVENT_VIEW = 2;

    private int contextMenuPosition;
    private final List<Object> shurikens;
    private final LanguageTextHelper languageTextHelper;
    private AlarmSwitchListener alarmSwitchListener;

    public ShurikenAdapter(List<Object> shurikens, LanguageTextHelper languageTextHelper) {
        this.shurikens = shurikens;
        this.languageTextHelper = languageTextHelper;
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = shurikens.get(position);
        if (obj instanceof Alarm) {
            return ALARM_VIEW;
        }
        if (obj instanceof CalendarEvent) {
            return EVENT_VIEW;
        }
        return -1;
    }

    @NonNull
    @Override
    public ShurikenViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView;
        switch (viewType) {
            case ALARM_VIEW:
                contactView = inflater.inflate(AlarmViewHolder.VIEW, viewGroup, false);
                return new AlarmViewHolder(contactView, this);
            case EVENT_VIEW:
                contactView = inflater.inflate(EventViewHolder.VIEW, viewGroup, false);
                return new EventViewHolder(contactView, this);
            default:
                throw new IllegalStateException("Unknown view type inside preferecnes adapter");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ShurikenViewHolder viewHolder, int position) {
        if (viewHolder instanceof AlarmViewHolder) {
            ((AlarmViewHolder) viewHolder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return shurikens.size();
    }

    public List<Object> getShurikens() {
        return shurikens;
    }

    public int getContextMenuPosition() {
        return contextMenuPosition;
    }

    public void setContextMenuPosition(int contextMenuPosition) {
        this.contextMenuPosition = contextMenuPosition;
    }

    public AlarmSwitchListener getAlarmSwitchListener() {
        return alarmSwitchListener;
    }

    public void setAlarmSwitchListener(AlarmSwitchListener alarmSwitchListener) {
        this.alarmSwitchListener = alarmSwitchListener;
    }

    public LanguageTextHelper getLanguageTextHelper() {
        return languageTextHelper;
    }

    @FunctionalInterface
    public interface AlarmSwitchListener {
        void alarmEnabledChanged(Alarm alarmEntity, boolean enabled);
    }
}
