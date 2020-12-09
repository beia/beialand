package com.example.stories.uiController;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stories.R;
import com.example.stories.adapters.StoriesRecyclerViewAdapter;
import com.example.stories.viewModel.StoriesViewModel;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewStoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewStoriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private StoriesViewModel storiesViewModel;

    public ViewStoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewStoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewStoriesFragment newInstance(String param1, String param2) {
        ViewStoriesFragment fragment = new ViewStoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_stories, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storiesViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity()))
                .get(StoriesViewModel.class);
        loadData();
        return v;
    }

    private void loadData() {
        storiesViewModel.getStories().observe(Objects.requireNonNull(getActivity()), stories -> {
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

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int previousPosition = -1;
        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            previousPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();
        }

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }

        if(previousPosition != -1) {
            recyclerView.scrollToPosition(previousPosition);
        }
    }
}