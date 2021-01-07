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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StoriesActivity extends AppCompatActivity {

    private StoriesViewModel storiesViewModel;
    private BottomNavigationView bottomNavigationView;
    private ViewStoriesFragment  viewStoriesFragment;
    private CategoriesFragment categoriesFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        storiesViewModel = new ViewModelProvider(this)
                .get(StoriesViewModel.class);
        initUI();
    }

    private void initUI() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        viewStoriesFragment = ViewStoriesFragment.newInstance(null, null);
        categoriesFragment = CategoriesFragment.newInstance(null, null);
        settingsFragment = SettingsFragment.newInstance(null, null);
        getSupportFragmentManager().beginTransaction()
                .add(viewStoriesFragment, "viewStoriesFragment")
                .setReorderingAllowed(true)
                .commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.action_view_stories) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, viewStoriesFragment)
                        .setReorderingAllowed(true)
                        .commit();
            } else if(menuItem.getItemId() == R.id.action_view_categories) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, categoriesFragment)
                        .setReorderingAllowed(true)
                        .commit();
            } else if(menuItem.getItemId() == R.id.action_view_settings) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, settingsFragment)
                        .setReorderingAllowed(true)
                        .commit();
            }
            return true;
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}