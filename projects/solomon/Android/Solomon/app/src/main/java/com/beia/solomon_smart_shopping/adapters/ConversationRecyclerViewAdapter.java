package com.beia.solomon_smart_shopping.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beia.solomon_smart_shopping.R;
import com.beia.solomon_smart_shopping.model.Conversation;
import com.beia.solomon_smart_shopping.model.Message;
import com.beia.solomon_smart_shopping.model.User;

import java.util.Base64;

public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Conversation conversation;
    private long userId;

    public ConversationRecyclerViewAdapter(Conversation conversation, long userId) {
        this.conversation = conversation;
        this.userId = userId;
    }

    private static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profilePicture;
        private final TextView username;
        private final TextView text;
        private final TextView date;

        private MessageViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePictureMessage);
            username = itemView.findViewById(R.id.usernameMessage);
            text = itemView.findViewById(R.id.messageText);
            date = itemView.findViewById(R.id.messageDate);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewType == 0
                ? new MessageViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.message_view, parent, false))
                : new MessageViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.message_view_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        Message message = conversation.getMessages().get(position);
        initProfilePicture(messageViewHolder, message);
        initUsername(messageViewHolder, message);
        initText(messageViewHolder, message);
        initDate(messageViewHolder, message);
    }

    private void initProfilePicture(MessageViewHolder holder, Message message) {
        Bitmap image = getSenderImageAsBitmap(message);
        if(image != null) {
            holder.profilePicture
                    .setImageBitmap(getSenderImageAsBitmap(message));
        }
    }

    private void initUsername(MessageViewHolder holder, Message message) {
        User sender = getSender(message);
        holder.username
                .setText(String.format("%s %s",
                        sender.getFirstName(),
                        sender.getLastName()));
    }

    private void initText(MessageViewHolder holder, Message message) {
        holder.text.setText(message.getText());
    }

    private void initDate(MessageViewHolder holder, Message message) {
        holder.date.setText(message.getDate());
    }

    private Bitmap getSenderImageAsBitmap(Message message) {
        User sender = getSender(message);
        if(sender.getImage() != null) {
            byte[] image = Base64
                    .getDecoder()
                    .decode(getSender(message).getImage());
            return BitmapFactory
                    .decodeByteArray(image, 0, image.length);
        }
        return null;
    }

    private User getSender(Message message) {
        return message.getSenderId() == conversation.getUser1().getId()
                ? conversation.getUser1()
                : conversation.getUser2();
    }

    @Override
    public int getItemCount() {
        return conversation.getMessages().size();
    }

    @Override
    public int getItemViewType(int position) {
        return conversation.getMessages().get(position).getSenderId() == userId
                ? 1
                : 0;
    }
}
