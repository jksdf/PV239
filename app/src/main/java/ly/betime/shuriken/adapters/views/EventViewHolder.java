package ly.betime.shuriken.adapters.views;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.ShurikenAdapter;
import ly.betime.shuriken.apis.CalendarEvent;

public class EventViewHolder extends ShurikenViewHolder  {
    public final static int VIEW = R.layout.alarm_item;

    private final TextView alarmTime;

    public EventViewHolder(@NonNull View itemView, ShurikenAdapter adapter) {
        super(itemView, adapter);
        alarmTime = itemView.findViewById(R.id.alarmTimeTextView);
    }

    @Override
    public void bind(int position) {
        CalendarEvent event = (CalendarEvent) adapter.getShurikens().get(position);
        alarmTime.setText(event.getName());
    }
}
