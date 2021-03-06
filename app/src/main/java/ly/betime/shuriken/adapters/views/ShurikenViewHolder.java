package ly.betime.shuriken.adapters.views;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ly.betime.shuriken.adapters.ShurikenAdapter;

public abstract class ShurikenViewHolder extends RecyclerView.ViewHolder {
    protected final ShurikenAdapter adapter;

    protected int position;

    public ShurikenViewHolder(@NonNull View itemView, ShurikenAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
    }

    public void bind(int position) {
        this.position = position;
    }
}
