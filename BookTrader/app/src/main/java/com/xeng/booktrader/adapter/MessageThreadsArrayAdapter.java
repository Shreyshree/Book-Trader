package com.xeng.booktrader.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseUser;
import com.xeng.booktrader.R;
import com.xeng.booktrader.model.MessageThread;
import com.xeng.booktrader.model.User;

import java.util.ArrayList;

/**
 * Created by sahajarora on 2017-10-07.
 */

public class MessageThreadsArrayAdapter extends ArrayAdapter<MessageThread> {

    public MessageThreadsArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public MessageThreadsArrayAdapter(Context context, int resource, ArrayList<MessageThread> messages) {
        super(context, resource, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.message_threads_item, null);
        }

        MessageThread message = getItem(position);

        if (message != null) {
            TextView tvBookName = (TextView) v.findViewById(R.id.tvBookName);
            TextView tvSenderName = (TextView) v.findViewById(R.id.tvSenderName);
            TextView tvMessageText = (TextView) v.findViewById(R.id.tvMessageText);

            // Set Book Name
            tvBookName.setText(message.getPosting().getBook().getName());

            User currentUser = (User) ParseUser.getCurrentUser();
            User fromUser = message.getLastMessage().getFromUser(); // fromUser of last message in this MessageThread

            if (currentUser != null && fromUser != null) {
                if (currentUser.getObjectId().equals(fromUser.getObjectId())) { // If the last message was sent by current user
                    tvSenderName.setText(message.getLastMessage().getToUser().getFirstName()
                            + " " + message.getLastMessage().getToUser().getLastName());
                    tvSenderName.setTypeface(Typeface.DEFAULT);
                    tvMessageText.setText("You: " + message.getLastMessage().getText());
                } else {
                    tvSenderName.setText(message.getLastMessage().getFromUser().getFirstName()
                            + " " + message.getLastMessage().getFromUser().getLastName());
                    tvSenderName.setTypeface(Typeface.DEFAULT_BOLD);
                    tvMessageText.setText(message.getLastMessage().getText());
                }
            }

        }

        return v;
    }


}
