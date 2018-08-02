package com.example.android.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class GuardianNewsViewAdapter extends RecyclerView.Adapter<GuardianNewsViewAdapter.GuardianNewsViewHolder> {

    private final ArrayList<GuardianNewsItem> dataset;

    GuardianNewsViewAdapter(ArrayList<GuardianNewsItem> dataset) {
        this.dataset = dataset;
    }

    // The layout manager calls the adapter's onCreateViewHolder() method.
    @NonNull
    @Override
    public GuardianNewsViewAdapter.GuardianNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view and a view holder for that view.
        if ((dataset.size() > 0) && (dataset.get(0).errorText != null)) {
            return new GuardianNewsViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.error_view, parent, false));
        }

        return new GuardianNewsViewHolder((ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.guardian_new_view, parent, false));
    }

    // The layout manager binds the view holder to its data.
    @Override
    public void onBindViewHolder(@NonNull final GuardianNewsViewAdapter.GuardianNewsViewHolder guardianNewsViewHolder, int position) {
        if (dataset.size() > 0) {
            if (dataset.get(0).errorText == null) {
                if (position % 2 == 0) {
                    guardianNewsViewHolder.guardianNewView.setBackgroundColor(guardianNewsViewHolder.guardianNewView.getResources().getColor(R.color.lb_action_text_color));
                } else {
                    guardianNewsViewHolder.guardianNewView.setBackgroundColor(guardianNewsViewHolder.guardianNewView.getResources().getColor(android.R.color.white));
                }

                TextView titleOfTheArticle = guardianNewsViewHolder.guardianNewView.findViewById(R.id.title_of_the_article);

                titleOfTheArticle.setText(dataset.get(position).title);

                titleOfTheArticle.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Uri webpage = Uri.parse(dataset.get(guardianNewsViewHolder.getAdapterPosition()).url);

                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

                        if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
                            view.getContext().startActivity(intent);
                        }
                    }

                });

                ((TextView) guardianNewsViewHolder.guardianNewView.findViewById(R.id.name_of_the_section_that_it_belongs_to)).setText(dataset.get(position).sectionName);
                ((TextView) guardianNewsViewHolder.guardianNewView.findViewById(R.id.author_name)).setText(dataset.get(position).authors);
                ((TextView) guardianNewsViewHolder.guardianNewView.findViewById(R.id.article_date)).setText(dataset.get(position).articleDate);
            } else {
                ((TextView) guardianNewsViewHolder.guardianNewView.findViewById(R.id.error_text_view)).setText(dataset.get(0).errorText);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    static class GuardianNewsViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout guardianNewView;

        GuardianNewsViewHolder(@NonNull ConstraintLayout guardianNewView) {
            super(guardianNewView);

            this.guardianNewView = guardianNewView;
        }

    }

}
