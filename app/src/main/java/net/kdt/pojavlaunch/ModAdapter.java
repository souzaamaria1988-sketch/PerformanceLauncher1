package net.kdt.pojavlaunch;

import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ModAdapter extends RecyclerView.Adapter<ModAdapter.ModViewHolder> {

    public interface OnCheckListener { void onChecked(ModrinthApi.ModResult mod, boolean selected); }

    private List<ModrinthApi.ModResult> mods;
    private OnCheckListener listener;

    public ModAdapter(List<ModrinthApi.ModResult> mods, OnCheckListener listener) {
        this.mods = mods;
        this.listener = listener;
    }

    @Override
    public ModViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_mod, parent, false);
        return new ModViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ModViewHolder holder, int position) {
        ModrinthApi.ModResult item = mods.get(position);
        holder.name.setText(item.title);
        holder.desc.setText(item.description);
        holder.downloads.setText(item.downloads);
        holder.check.setOnCheckedChangeListener(null);
        holder.check.setChecked(false);
        holder.check.setOnCheckedChangeListener((btn, checked) -> {
            if (listener != null) listener.onChecked(item, checked);
        });
    }

    @Override
    public int getItemCount() { return mods.size(); }

    static class ModViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, downloads;
        CheckBox check;
        ModViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.textModName);
            desc = v.findViewById(R.id.textModDesc);
            downloads = v.findViewById(R.id.textModDownloads);
            check = v.findViewById(R.id.checkMod);
        }
    }
}
