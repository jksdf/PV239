package ly.betime.shuriken.adapters.views;

import android.view.View;

import androidx.annotation.NonNull;
import ly.betime.shuriken.R;
import ly.betime.shuriken.adapters.ShurikenAdapter;

public class EventViewHolder extends ShurikenViewHolder  {
    public final static int VIEW = R.layout.alarm_item;

    public EventViewHolder(@NonNull View itemView, ShurikenAdapter adapter) {
        super(itemView, adapter);
    }
}
