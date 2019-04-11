package ly.betime.shuriken.adapters.views;

import android.view.View;
import android.widget.TextView;

import org.threeten.bp.LocalDate;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.ShurikenAdapter;

public class EventListTitleViewHolder extends ShurikenViewHolder {
    public static final int VIEW = R.layout.event_list_title;

    private LocalDate date;

    private final TextView titleView;
    private final TextView dateView;

    public EventListTitleViewHolder(@NonNull View itemView, ShurikenAdapter adapter) {
        super(itemView, adapter);
        titleView = itemView.findViewById(R.id.eventListTitle);
        dateView = itemView.findViewById(R.id.eventListDate);
    }

    @Override
    public void bind(int position) {
        super.bind(position);
        date = (LocalDate) adapter.getShurikens().get(position);
        dateView.setText(adapter.getLanguageTextHelper().getEventDateFormatter().format(date));
    }
}
