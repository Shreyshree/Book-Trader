package com.xeng.booktrader.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseUser;
import com.xeng.booktrader.R;
import com.xeng.booktrader.model.Message;
import com.xeng.booktrader.model.User;

import java.util.ArrayList;

/**
 * Created by sahajarora on 2017-10-07.
 */

public class MessagesAdapter extends ArrayAdapter<Message> {

    TextView tvMessageText;
    LinearLayout contentWithBackground;
    LinearLayout content;
    TextView txtInfo;

    public MessagesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public MessagesAdapter(Context context, int resource, ArrayList<Message> messages) {
        super(context, resource, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.messages_item, null);
        }

        Message message = getItem(position);

        tvMessageText = (TextView) v.findViewById(R.id.tvMessageText);
        contentWithBackground = (LinearLayout) v.findViewById((R.id.contentWithBackground));
        content = (LinearLayout) v.findViewById((R.id.content));
        txtInfo = (TextView) v.findViewById((R.id.txtInfo));

        if (message != null) {

            User currentUser = (User) ParseUser.getCurrentUser();
            User fromUser = message.getFromUser();

            tvMessageText.setText(message.getText());
            txtInfo.setText(message.getCreatedAt().toString());

            if (currentUser.getObjectId().equals(fromUser.getObjectId())) {
                setAlignment(false);
            } else {
                setAlignment(true);
            }

        }

        return v;
    }


    private void setAlignment(boolean isMe) {
        if (!isMe) {
            contentWithBackground.setBackgroundResource(R.drawable.in_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) contentWithBackground.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            contentWithBackground.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) tvMessageText.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            tvMessageText.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            txtInfo.setLayoutParams(layoutParams);
        } else {
            contentWithBackground.setBackgroundResource(R.drawable.out_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) contentWithBackground.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            contentWithBackground.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) tvMessageText.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            tvMessageText.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            txtInfo.setLayoutParams(layoutParams);
        }
    }

}
