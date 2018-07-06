package com.xeng.booktrader.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;
import com.xeng.booktrader.R;
import com.xeng.booktrader.adapter.MessagesAdapter;
import com.xeng.booktrader.model.Message;
import com.xeng.booktrader.model.MessageThread;
import com.xeng.booktrader.service.ParseAPIService;
import com.xeng.booktrader.service.ParseResponseHandler;
import com.xeng.booktrader.utility.Common;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    MessageThread messageThread;
    String messageThreadId; // Optional
    ListView lvMessages;
    EditText etMessage;
    String otherUserId;
    String otherUserName;
    String postingId; // Optional

    ArrayList<Message> messages;
    MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageThreadId = getIntent().getStringExtra("messageThreadId");
        postingId = getIntent().getStringExtra("postingId");

        otherUserId = getIntent().getStringExtra("otherUserId");
        otherUserName = getIntent().getStringExtra("otherUserName");

        setTitle(otherUserName);

        lvMessages = (ListView) findViewById(R.id.lvMessages);
        etMessage = (EditText) findViewById(R.id.etMessage);

        messages = new ArrayList<>();

        adapter = new MessagesAdapter(getApplicationContext(), R.layout.messages_item, messages);
        lvMessages.setAdapter(adapter);

        if (messageThreadId!=null) {
            // The message Thread exists. Fetch messages.
            getMessages(messageThreadId);
        }

        // Handle event when Message received event fired from Parse Live Query Client
        Common.getSharedInstance().getMessageReceivedSubscription().handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Message>() {
                @Override
                public void onEvent(ParseQuery<Message> query, final Message message) {
                    // HANDLING message received event
                    if (ParseUser.getCurrentUser() != null) {
                        message.getFromUser().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                message.getToUser().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        adapter.insert(message, messages.size());
                                    }
                                });
                            }
                        });
                    }

                }
            });

        // Handle event when Message sent event fired from Parse Live Query Client
        Common.getSharedInstance().getMessageSentSubscription().handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Message>() {
            @Override
            public void onEvent(ParseQuery<Message> query, final Message message) {
                // HANDLING message sent event
                if (ParseUser.getCurrentUser() != null) {
                    message.getFromUser().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            message.getToUser().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                        adapter.insert(message, messages.size());
                                }
                            });
                        }
                    });
                }
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(onLogOut,
                new IntentFilter(getString(R.string.event_logged_out)));

    }

    // Get Messages in the MessageThread
    public void getMessages(String id) {
        // if user is logged in, get messages
        if (ParseUser.getCurrentUser() != null) {
            ParseAPIService.getUserMessages(id, new ParseResponseHandler() {
                @Override
                public void onParseCallSuccess(Object results) {
                    messages = (ArrayList<Message>) results;

                    adapter = new MessagesAdapter(getApplicationContext(), R.layout.messages_item, messages);
                    lvMessages.setAdapter(adapter);

                    lvMessages.setSelection(adapter.getCount() - 1);

                }

                @Override
                public void onParseCallError(ParseException e) {

                }
            });
        } else {
            Toast.makeText(this, "Please log in to view messages.", Toast.LENGTH_SHORT).show();
        }
        
    }

    // Send Message
    public void sendMessage(View view) {
        if (etMessage.getText().length() > 0) {

            ParseAPIService.sendMessage(messageThreadId, postingId, ParseUser.getCurrentUser().getObjectId(),
                    otherUserId, etMessage.getText().toString(), new ParseResponseHandler() {
                        @Override
                        public void onParseCallSuccess(Object result) {
                            etMessage.clearFocus();
                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                           // Hide keyboard
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                            etMessage.setText("");

                            messageThreadId = ((MessageThread) result).getObjectId();

                        }

                        @Override
                        public void onParseCallError(ParseException e) {
                            Toast.makeText(ChatActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "loggedOut" is broadcasted.
    private BroadcastReceiver onLogOut = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Clear the list
            adapter.clear();
            
        }
    };
}
