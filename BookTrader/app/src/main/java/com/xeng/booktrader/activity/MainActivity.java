package com.xeng.booktrader.activity;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;
import com.xeng.booktrader.R;
import com.xeng.booktrader.fragment.MainFragment;
import com.xeng.booktrader.fragment.MessageThreadsFragment;
import com.xeng.booktrader.model.Book;
import com.xeng.booktrader.model.Message;
import com.xeng.booktrader.model.MessageThread;
import com.xeng.booktrader.model.Posting;
import com.xeng.booktrader.model.User;
import com.xeng.booktrader.utility.Common;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MessageThreadsFragment.OnFragmentInteractionListener,
        MainFragment.OnFragmentInteractionListener {
    Context self = this;

    TextView tvNavUserName;
    TextView tvNavUserRating;

    Menu menu;
    View navHeaderView;
    DrawerLayout drawerLayout;

    ParseLiveQueryClient parseLiveQueryClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Initialize Parse
        Parse.initialize(this);

        // Register Parse subclasses
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Book.class);
        ParseObject.registerSubclass(Posting.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(MessageThread.class);

        // Initialize Parse LiveQuery
        Common.getSharedInstance().initializeParseLiveQueryClient();

        // Subscribe to live messages
        Common.getSharedInstance().subscribeToMessageReceived();
        Common.getSharedInstance().subscribeToMessageSent();

        // Get reference to Navigation Drawer Header View
        navHeaderView = navigationView.getHeaderView(0);

        this.menu = navigationView.getMenu();

        tvNavUserName = (TextView) navHeaderView.findViewById(R.id.tvNavUserName);
        tvNavUserRating =  (TextView) navHeaderView.findViewById(R.id.tvNavUserRating);

        FragmentManager fManager = getFragmentManager();
        fManager.beginTransaction()
                .replace(R.id.content_frame, new MainFragment())
                .commit();

        LocalBroadcastManager.getInstance(self).registerReceiver(onLogOut,
                new IntentFilter(getString(R.string.event_logged_out)));

        LocalBroadcastManager.getInstance(self).registerReceiver(onLogIn,
                new IntentFilter(getString(R.string.event_logged_in)));

        // Handle event when Message received from Parse Live Query Client
//        if (ParseUser.getCurrentUser() != null) {
//            Common.getSharedInstance().getMessageReceivedSubscription().handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Message>() {
//                @Override
//                public void onEvent(ParseQuery<Message> query, final Message message) {
//                    // HANDLING create event
//                    System.out.println("Message received!!!");
//
//                    message.getFromUser().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
//                            @Override
//                            public void done(ParseObject object, ParseException e) {
//                                User user = (User) object;
//
//                                Toast.makeText(self, user.getFirstName() + ": " +
//                                         message.getText(), Toast.LENGTH_SHORT).show();
//                            }
//                    });
//
//                }
//            });
//        }

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void subscribeToLiveQuery() {
        if (ParseUser.getCurrentUser() != null) {
            ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
            parseQuery.whereEqualTo("to", ParseUser.getCurrentUser());

            SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

            subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Message>() {
                @Override
                public void onEvent(ParseQuery<Message> query, Message message) {
                    // HANDLING create event
                    System.out.println("Message received!!!");
                    Intent eventIntent = new Intent(getString(R.string.event_message_received));
                    LocalBroadcastManager.getInstance(self).sendBroadcast(eventIntent);
                }
            });
        }
    }


    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "loggedOut" is broadcasted.
    private BroadcastReceiver onLogOut = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Log Out event triggered.");
            MenuItem item = menu.findItem(R.id.nav_signIn);
            item.setTitle("Sign In");

            // Set current user's name in Navigation Drawer
            tvNavUserName.setText("Welcome to BookTrader");

            // Display current user's rating in Navigation Drawer
            tvNavUserRating.setVisibility(View.INVISIBLE);
        }
    };

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "loggedIn" is broadcasted.
    private BroadcastReceiver onLogIn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Log In event triggered in MainActivity");
            MenuItem item = menu.findItem(R.id.nav_signIn);
            item.setTitle("Sign Out");

            // Get extra data included in the Intent
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");


            // Set current user's name in Navigation Drawer
            tvNavUserName.setText(firstName + " " + lastName);
            System.out.println("tvNavUserName: ");
            System.out.println(tvNavUserName);

            // Display current user's rating in Navigation Drawer
            tvNavUserRating.setVisibility(View.VISIBLE);
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getString(R.string.event_logged_in));
            intent.putExtra("firstName", currentUser.get("firstName").toString());
            intent.putExtra("lastName", currentUser.get("lastName").toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            Intent intent = new Intent(getString(R.string.event_logged_out));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.nav_signIn) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                ParseUser.logOut();

                Intent intent = new Intent(getString(R.string.event_logged_out));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                Toast.makeText(this, "You have successfully signed out!", Toast.LENGTH_SHORT).show();

                // Set Sign Out item to Sign In
                item.setTitle("Sign In");
            } else {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_home) {
            // Handle the home action
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new MainFragment())
                    .commit();
        } else if (id == R.id.nav_messages) {
            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new MessageThreadsFragment())
                    .commit();

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_postings) {

        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_signIn) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                ParseUser.logOut();

                Intent intent = new Intent(getString(R.string.event_logged_out));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                Toast.makeText(this, "You have successfully signed out!", Toast.LENGTH_SHORT).show();

                // Set Sign Out item to Sign In
                item.setTitle("Sign In");

                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } else {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onLogOut);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onLogIn);
        super.onDestroy();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
