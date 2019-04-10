package ly.betime.shuriken.adapters.views;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ly.betime.shuriken.adapters.ShurikenAdapter;

public abstract class ShurikenViewHolder extends RecyclerView.ViewHolder {
    protected final ShurikenAdapter adapter;

    public ShurikenViewHolder(@NonNull View itemView, ShurikenAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
    }

    public abstract void bind(int position);
}
