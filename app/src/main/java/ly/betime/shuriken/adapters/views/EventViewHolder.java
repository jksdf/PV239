package ly.betime.shuriken.adapters.views;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.ShurikenAdapter;
import ly.betime.shuriken.apis.CalendarEvent;

public class EventViewHolder extends ShurikenViewHolder  {
    public final static int VIEW = R.layout.event_item;

    private final TextView headerNameView;
    private final TextView headerDateView;

    public EventViewHolder(@NonNull View itemView, ShurikenAdapter adapter) {
        super(itemView, adapter);
        headerNameView = itemView.findViewById(R.id.eventName);
        headerDateView = itemView.findViewById(R.id.eventDate);
    }

    @Override
    public void bind(int position) {
        CalendarEvent event = (CalendarEvent) adapter.getShurikens().get(position);
        headerNameView.setText(event.getName());
        headerDateView.setText(adapter.getLanguageTextHelper().getEventDateFormatter().format(event.getFrom()));
    }
}
