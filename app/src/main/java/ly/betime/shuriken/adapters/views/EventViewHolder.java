package ly.betime.shuriken.adapters.views;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.ShurikenAdapter;
import ly.betime.shuriken.apis.CalendarEvent;

public class EventViewHolder extends ShurikenViewHolder {
    public final static int VIEW = R.layout.event_item;

    private CalendarEvent event;

    private final TextView headerNameView;
    private final TextView headerDateView;
    private final View headView;
    private final View bodyView;
    private final ImageButton showMoreButton;
    private final TextView eventStart;
    private final TextView eventEnd;
    private final Button shareButton;

    public EventViewHolder(@NonNull View itemView, ShurikenAdapter adapter) {
        super(itemView, adapter);
        headerNameView = itemView.findViewById(R.id.eventName);
        headerDateView = itemView.findViewById(R.id.eventDate);
        headView = itemView.findViewById(R.id.eventHeader);
        bodyView = itemView.findViewById(R.id.eventBody);
        showMoreButton = itemView.findViewById(R.id.showMoreButton);
        eventStart = itemView.findViewById(R.id.eventStart);
        eventEnd = itemView.findViewById(R.id.eventEnd);
        shareButton = itemView.findViewById(R.id.shareButton);

        headView.setOnClickListener(v -> expand());
        showMoreButton.setOnClickListener(v -> expand());
        shareButton.setOnClickListener(v -> share());
    }

    private void expand() {
        event.setExpanded(!event.isExpanded());
        adapter.notifyItemChanged(position);
    }

    private void share() {
        String shareBody = itemView.getResources().getString(R.string.event_share_title, event.getName()) + "\n" +
                itemView.getResources().getString(R.string.event_start) + formatEventDateTime(event.getFrom()) + "\n" +
                itemView.getResources().getString(R.string.event_end) + formatEventDateTime(event.getTo()) + "\n";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, itemView.getResources().getString(R.string.event_share_title, event.getName()));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        adapter.getFragment().startActivity(Intent.createChooser(sharingIntent, itemView.getResources().getString(R.string.share)));
    }

    @Override
    public void bind(int position) {
        super.bind(position);
        event = (CalendarEvent) adapter.getShurikens().get(position);
        headerNameView.setText(event.getName());
        headerDateView.setText(adapter.getLanguageTextHelper().getEventDateFormatter().format(event.getFrom()));

        eventStart.setText(formatEventDateTime(event.getFrom()));
        eventEnd.setText(formatEventDateTime(event.getTo()));

        bodyView.setVisibility(event.isExpanded() ? View.VISIBLE : View.GONE);
        int showMoreIcon = R.drawable.ic_keyboard_arrow_down_48;
        if (event.isExpanded()) {
            showMoreIcon = R.drawable.ic_keyboard_arrow_up_48;
        }
        showMoreButton.setBackground(itemView.getResources().getDrawable(showMoreIcon));
    }

    private String formatEventDateTime(LocalDateTime from) {
        return adapter.getLanguageTextHelper().getEventDateFormatter().format(from) + " " +
                adapter.getLanguageTextHelper().getAlarmTimeFormatter().format(from) +
                adapter.getLanguageTextHelper().getAlarmPeriodFormatter().format(from);
    }


}
