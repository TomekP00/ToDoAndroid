package com.example.pum_todo;

import static androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
    private List<String> titles;
    private List<String> subtitles;

    public Adapter(List<String> titles, List<String> subtitles) {
        this.titles = titles;
        this.subtitles = subtitles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = titles.get(position);
        String subtitle = subtitles.get(position);

        holder.titleTextView.setText(title);
        holder.subtitleTextView.setText(subtitle);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MaterialTextView titleTextView;
        public MaterialTextView subtitleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            subtitleTextView = itemView.findViewById(R.id.subtitleTextView);
        }
    }

    public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = ItemTouchHelper.RIGHT;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            titles.remove(position);
            subtitles.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void deleteItem(int position) {
        titles.remove(position);
        subtitles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, titles.size());
    }

}