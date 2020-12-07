package com.example.stories.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stories.R;
import com.example.stories.model.Story;

import java.util.List;

public class StoriesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Story> stories;

    public StoriesRecyclerViewAdapter(List<Story> stories) {
        this.stories = stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoryViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.story_view,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StoryViewHolder storyViewHolder = (StoryViewHolder) holder;
        Story story = stories.get(position);

        Glide.with(storyViewHolder.image.getContext())
                .load(story.getPictureUrl())
                .into(storyViewHolder.image);
        storyViewHolder.title
                .setText(story.getTitle());
        storyViewHolder.author
                .setText(String.format("Author: %s",
                        story.getAuthor().getName()));
        storyViewHolder.description
                .setText(String.format("Description: %s",
                        story.getDescription()));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }



    private static class StoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title;
        private TextView author;
        private Button authorInfo;
        private TextView description;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.titleText);
            author = itemView.findViewById(R.id.authorText);
            authorInfo = itemView.findViewById(R.id.authorInfoButton);
            description = itemView.findViewById(R.id.descriptionText);
        }
    }
}
