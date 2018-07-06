package com.xeng.booktrader.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.xeng.booktrader.R;
import com.xeng.booktrader.activity.LoginActivity;
import com.xeng.booktrader.activity.MainActivity;
import com.xeng.booktrader.activity.PostBookActivity;
import com.xeng.booktrader.activity.ViewPostActivity;
import com.xeng.booktrader.adapter.PostingsArrayAdapter;
import com.xeng.booktrader.model.Posting;
import com.xeng.booktrader.service.ParseService;
import com.xeng.booktrader.service.PostingService;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    TextView tvPostBook;
    TextView tvNoPostingsFound;

    ListView lvPostings;
    SwipeRefreshLayout swipeRefresh;

    private ProgressBar spinner;

    Context self;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get reference to Navigation Drawer Header View
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "loggedOut" is broadcasted.
    private BroadcastReceiver onLogOut = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Log Out event triggered.");
            tvPostBook.setText("Please Sign In / Sign Up");
        }
    };

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "loggedIn" is broadcasted.
    private BroadcastReceiver onLogIn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Log In event triggered.");

            // Get extra data included in the Intent
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");

            // Set Text to "Post a Book"
            tvPostBook.setText(R.string.action_post_a_book);

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        self = getActivity().getApplicationContext();


        // Initialize View Elements
        tvPostBook = (TextView) view.findViewById(R.id.tvPostBook) ;
        if (ParseUser.getCurrentUser() != null) {
            // if user is logged in, set text to Post a Book
            tvPostBook.setText(R.string.action_post_a_book);
        } else {
            // else, set text to sign in/ sign up
            tvPostBook.setText("Please Sign In / Sign Up");
        }

        tvPostBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If user is logged in, direct to Post Book Activity
                if (ParseUser.getCurrentUser() != null) {
                    Intent intent = new Intent( getActivity(), PostBookActivity.class );
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    self.startActivity( intent );
                } else {
                    // Else, direct to sign in activity
                    Intent intent = new Intent( getActivity(), LoginActivity.class );
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    self.startActivity( intent );
                }
            }
        });

        tvNoPostingsFound = (TextView) view.findViewById(R.id.tvNoPostingsFound);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        lvPostings = (ListView) view.findViewById(R.id.list);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPostings(false);
            }
        });

        spinner= (ProgressBar) view.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        getPostings(true);

        // Subscribe to Log Out event
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onLogOut,
                new IntentFilter(getString(R.string.event_logged_out)));

        // Subscribe to Log In event
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onLogIn,
                new IntentFilter(getString(R.string.event_logged_in)));


        // Inflate the layout for this fragment
        return view;
    }

    // Get Postings from Backend
    public void getPostings(final boolean displayProgressSpinner) {

        if (displayProgressSpinner) spinner.setVisibility(View.VISIBLE);
        //         Gets All Postings
        PostingService.getPostings(new ParseService() {
            @Override
            public void onParseCallSuccess(Object results) {
                ArrayList<Posting> postings = (ArrayList<Posting>) results;
                if (postings.size()>0) {
                    tvNoPostingsFound.setVisibility(View.GONE);
                    PostingsArrayAdapter postingsAdapter = new PostingsArrayAdapter(self,
                            R.layout.postings_item, postings);
                    lvPostings.setAdapter(postingsAdapter);

                    // Define what happens when a posting is clicked
                    lvPostings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(getActivity(), ViewPostActivity.class);
                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("postingId", ((Posting) lvPostings.getItemAtPosition(i)).getObjectId());
                            self.startActivity( intent );
                        }
                    });
                } else {
                    tvNoPostingsFound.setVisibility(View.VISIBLE);
                }

                if (displayProgressSpinner) spinner.setVisibility(View.GONE);
                else swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onParseCallError(ParseException e) {
                Toast.makeText(self.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                tvNoPostingsFound.setVisibility(View.VISIBLE);

                if (displayProgressSpinner) spinner.setVisibility(View.GONE);
                else swipeRefresh.setRefreshing(false);
            }
        });
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
