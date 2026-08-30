package net.kdt.pojavlaunch;

import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ModAdapter extends RecyclerView.Adapter<ModAdapter.ModViewHolder> {

    public interface OnCheckListener { void onChecked(ModItem item); }

    private List<ModItem> mods;
    private OnCheckListener listener;

    public ModAdapter(List<ModItem> mods, OnCheckListener listener) {
        this.mods = mods;
        this.listener = listener;
    }

    public void updateList(List<ModItem> newList) {
        this.mods = newList;
        notifyDataSetChanged();
    }

    @Override
    public ModViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_mod, parent, false);
        return new ModViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ModViewHolder holder, int position) {
        ModItem item = mods.get(position);
        holder.name.setText(item.name);
        holder.desc.setText(item.description);
        holder.downloads.setText(item.downloads);
        holder.check.setChecked(item.selected);
        holder.check.setOnCheckedChangeListener((btn, checked) -> {
            item.selected = checked;
            if (listener != null) listener.onChecked(item);
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
