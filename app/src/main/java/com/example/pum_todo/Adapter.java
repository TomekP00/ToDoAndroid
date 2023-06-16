package com.example.pum_todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public void addData(String title, String subtitle) {
        titles.add(title);
        subtitles.add(subtitle);
        notifyDataSetChanged();
    }
}