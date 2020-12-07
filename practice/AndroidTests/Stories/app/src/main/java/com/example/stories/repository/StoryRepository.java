package com.example.stories.repository;

import com.example.stories.model.Story;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StoryRepository {
    @GET("getStories")
    Call<List<Story>> getStories(@Query("receivedStories") int receivedStories);
}
