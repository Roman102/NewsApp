package com.example.android.newsapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class GuardianNewsViewAdapter extends RecyclerView.Adapter<GuardianNewsViewAdapter.GuardianNewsViewHolder> {

    private final String[] dataset;

    public GuardianNewsViewAdapter(String[] dataset_) {
        dataset = dataset_;
    }

    // The layout manager calls the adapter's onCreateViewHolder() method.
    @NonNull
    @Override
    public GuardianNewsViewAdapter.GuardianNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view ...
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.guardian_new_view, parent, false);

        // ... and a view holder for that view.
        return new GuardianNewsViewHolder(v);
    }

    // The layout manager binds the view holder to its data.
    @Override
    public void onBindViewHolder(@NonNull GuardianNewsViewAdapter.GuardianNewsViewHolder guardianNewsViewHolder, int position) {
        guardianNewsViewHolder.textView.setText(dataset[position]);
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }

    public static class GuardianNewsViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public GuardianNewsViewHolder(@NonNull TextView textView_) {
            super(textView_);

            textView = textView_;
        }

    }

}
