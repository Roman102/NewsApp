package com.example.android.newsapp;

import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

public class MainActivity extends AppCompatActivity {

    private VerticalGridView guardianNewsView;
    private LinearLayoutManager linearLayoutManager;
    private GuardianNewsViewAdapter guardianNewsViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // A VerticalGridView fills itself with views provided by a layout manager.
        // Each view is represented by a view holder object.
        linearLayoutManager = new LinearLayoutManager(this);

        String[] data = new String[1000];

        for (int i = 0; i < data.length; i++) {
            data[i] = "Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum End" + i;
        }


        // The view holder objects are managed by a custom adapter by extending
        // RecyclerView.Adapter. The adapter creates view holders as needed. The adapter also
        // binds the view holders to their data.
        guardianNewsViewAdapter = new GuardianNewsViewAdapter(data);

        guardianNewsView = findViewById(R.id.guardian_news_view);

        guardianNewsView.setLayoutManager(linearLayoutManager);
        guardianNewsView.setAdapter(guardianNewsViewAdapter);

    }

}
