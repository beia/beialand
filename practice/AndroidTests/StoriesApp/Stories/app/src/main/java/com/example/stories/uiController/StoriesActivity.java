package com.example.stories.uiController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.stories.R;
import com.example.stories.adapters.StoriesRecyclerViewAdapter;
import com.example.stories.viewModel.StoriesViewModel;

public class StoriesActivity extends AppCompatActivity {

    private StoriesViewModel storiesViewModel;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        storiesViewModel = new ViewModelProvider(this)
                .get(StoriesViewModel.class);

        initUI();
        loadData();
    }

    private void loadData() {
        storiesViewModel.getStories().observe(this, stories -> {
            if(recyclerView.getAdapter() == null) {
                recyclerView.setAdapter(new StoriesRecyclerViewAdapter(stories));
            } else {
                if(recyclerView.getAdapter() instanceof StoriesRecyclerViewAdapter) {
                    StoriesRecyclerViewAdapter adapter = (StoriesRecyclerViewAdapter) recyclerView.getAdapter();
                    int previousStoriesNr = adapter.getItemCount();
                    int currentStoriesNr = stories.size();
                    adapter.setStories(stories);
                    adapter.notifyItemRangeInserted(previousStoriesNr, currentStoriesNr);
                }
            }
        });
    }

    private void initUI() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int previousPosition = -1;
        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            previousPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();
        }

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        } else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        }

        if(previousPosition != -1) {
            recyclerView.scrollToPosition(previousPosition);
        }
    }
}