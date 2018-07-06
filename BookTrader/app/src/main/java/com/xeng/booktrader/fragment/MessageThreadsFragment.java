package com.xeng.booktrader.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;
import com.xeng.booktrader.R;
import com.xeng.booktrader.activity.ChatActivity;
import com.xeng.booktrader.activity.MainActivity;
import com.xeng.booktrader.adapter.MessageThreadsArrayAdapter;
import com.xeng.booktrader.model.Message;
import com.xeng.booktrader.model.MessageThread;
import com.xeng.booktrader.service.ParseResponseHandler;
import com.xeng.booktrader.service.ParseAPIService;
import com.xeng.booktrader.utility.Common;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageThreadsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MessageThreadsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ListView lvMessages;
    SwipeRefreshLayout swipeRefresh;
    TextView tvNoMessagesFound;

    private ProgressBar spinner;

    ArrayList<MessageThread> messageThreads;
    MessageThreadsArrayAdapter messagesAdapter;

    Context self;

    public MessageThreadsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        self = getActivity().getApplicationContext();

        // Initialize View elements
        tvNoMessagesFound = (TextView) view.findViewById(R.id.tvNoMessagesFound);
        lvMessages = (ListView) view.findViewById(R.id.lvMessages);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMessages(false);
            }
        });
        spinner = (ProgressBar) view.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        getMessages(true);

        // Handle event when Message received event fired from Parse Live Query Client
        Common.getSharedInstance().getMessageReceivedSubscription().handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Message>() {
                @Override
                public void onEvent(ParseQuery<Message> query, final Message message) {
                    // HANDLING message received event
                    if (ParseUser.getCurrentUser() != null) {
                        // Re-fetch user message threads
                        getMessages(false);
                    }
                }
        });

        // Handle event when Message sent event fired from Parse Live Query Client
        Common.getSharedInstance().getMessageSentSubscription().handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Message>() {
            @Override
            public void onEvent(ParseQuery<Message> query, final Message message) {
                // HANDLING message sent event
                if (ParseUser.getCurrentUser() != null) {
                    // Re-fetch user message threads
                    getMessages(false);
                }

            }
        });

        return view;
    }

    public void getMessages(final boolean displayProgressSpinner) {
        
        // Get Messages if user is logged in. If not, ask user to login.
        if (ParseUser.getCurrentUser() != null) {
            if (displayProgressSpinner) spinner.setVisibility(View.VISIBLE);

            // Gets All Postings
            ParseAPIService.getUserMessageThreads(ParseUser.getCurrentUser().getObjectId(), new ParseResponseHandler() {
                @Override
                public void onParseCallSuccess(Object results) {
                    messageThreads = (ArrayList<MessageThread>) results;
                    System.out.println("Messages received: ");
                    System.out.println(results);
                    if (messageThreads.size()>0) {
                        tvNoMessagesFound.setText("You have no messages");
                        tvNoMessagesFound.setVisibility(View.GONE);
                        messagesAdapter = new MessageThreadsArrayAdapter(self,
                                R.layout.message_threads_item, messageThreads);
                        lvMessages.setAdapter(messagesAdapter);

                        // Define what happens when a posting is clicked
                        lvMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("messageThreadId", ((MessageThread) lvMessages.getItemAtPosition(i)).getObjectId());

                                // Attach other user's object id with intent
                                if (ParseUser.getCurrentUser() != null) { // this should never be null
                                    if (ParseUser.getCurrentUser().getObjectId().equals(
                                            ((MessageThread) lvMessages.getItemAtPosition(i)).getUser1().getObjectId())) {
                                        intent.putExtra("otherUserId",
                                                ((MessageThread) lvMessages.getItemAtPosition(i)).getUser2().getObjectId());
                                        intent.putExtra("otherUserName",
                                                ((MessageThread) lvMessages.getItemAtPosition(i)).getUser2().getFirstName() +
                                                       " " + ((MessageThread) lvMessages.getItemAtPosition(i)).getUser2().getLastName());
                                    } else {
                                        intent.putExtra("otherUserId",
                                                ((MessageThread) lvMessages.getItemAtPosition(i)).getUser1().getObjectId());
                                        intent.putExtra("otherUserName",
                                                ((MessageThread) lvMessages.getItemAtPosition(i)).getUser1().getFirstName() +
                                                      " " +  ((MessageThread) lvMessages.getItemAtPosition(i)).getUser1().getLastName());
                                    }
                                }

                            self.startActivity( intent );
                            }
                        });
                    } else {
                        tvNoMessagesFound.setVisibility(View.VISIBLE);
                    }

                    if (displayProgressSpinner) spinner.setVisibility(View.GONE);
                    else swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onParseCallError(ParseException e) {
                    Toast.makeText(self.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    tvNoMessagesFound.setVisibility(View.VISIBLE);

                    if (displayProgressSpinner) spinner.setVisibility(View.GONE);
                    else swipeRefresh.setRefreshing(false);
                }
            });
        } else {
            swipeRefresh.setRefreshing(false);
            tvNoMessagesFound.setText("Please log in to view messages.");
            tvNoMessagesFound.setVisibility(View.VISIBLE);
            Toast.makeText(self, "Please log in to view messages.", Toast.LENGTH_SHORT).show();
        }
        
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "loggedOut" is broadcasted.
    private BroadcastReceiver onLogOut = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Log Out event triggered.");
            tvNoMessagesFound.setText("Please log in to view messages.");
            tvNoMessagesFound.setVisibility(View.VISIBLE);
        }
    };

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "loggedIn" is broadcasted.
    private BroadcastReceiver onLogIn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Log In event triggered.");

            tvNoMessagesFound.setVisibility(View.GONE);
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
