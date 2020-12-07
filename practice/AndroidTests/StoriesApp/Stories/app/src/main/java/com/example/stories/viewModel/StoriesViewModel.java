package com.example.stories.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.stories.model.Story;
import com.example.stories.repository.StoryRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoriesViewModel extends ViewModel {
    private final StoryRepository storyRepository;
    private MutableLiveData<List<Story>> stories;

    public StoriesViewModel() {
        super();
        this.storyRepository = new Retrofit.Builder()
                .baseUrl("http://192.168.0.45:8000/mobileApp/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StoryRepository.class);
    }

    public LiveData<List<Story>> getStories() {
        if(stories == null) {
            stories = new MutableLiveData<>();
            loadStories();
        }
        return stories;
    }

    private void loadStories() {
        int receivedStories = stories.getValue() != null ? stories.getValue().size() : 0;
        storyRepository
                .getStories(receivedStories)
                .enqueue(new Callback<List<Story>>() {
                    @Override
                    public void onResponse(Call<List<Story>> call, Response<List<Story>> response) {
                        if(response.isSuccessful()) {
                            if(stories.getValue() != null && !stories.getValue().isEmpty()) {
                                List<Story> updatedStories = stories.getValue();
                                updatedStories.addAll(response.body());
                                stories.setValue(updatedStories);
                            } else {
                                stories.setValue(response.body());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Story>> call, Throwable t) {

                    }
                });
    }
}
