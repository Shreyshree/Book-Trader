package com.xeng.booktrader.utility;

import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;
import com.xeng.booktrader.model.Message;
import com.xeng.booktrader.model.SearchFilter;

import java.util.ArrayList;

/**
 * Created by sahajarora1286 on 06-10-2017.
 */

public class Common {

    private static Common sharedInstance;
    private ParseLiveQueryClient parseLiveQueryClient;
    private SubscriptionHandling<Message> messageReceivedSubscription;
    private SubscriptionHandling<Message> messageSentSubscription;
    private ArrayList<SearchFilter> searchFilters;

    public Common() {
        // Initialize Parse LiveQuery
        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        searchFilters = new ArrayList<>();
    }

    public static Common getSharedInstance(){
        if (sharedInstance == null) {
            sharedInstance = new Common();
            return sharedInstance;
        } else {
            return sharedInstance;
        }
    }

    public void initializeParseLiveQueryClient() {
            // Initialize Parse LiveQuery
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
    }

    public void subscribeToMessageReceived() {
        if (ParseUser.getCurrentUser() != null) {
            ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
            parseQuery.whereEqualTo("to", ParseUser.getCurrentUser());
            parseQuery.include("from");

            messageReceivedSubscription = parseLiveQueryClient.subscribe(parseQuery);
        }
    }

    public void subscribeToMessageSent() {
        if (ParseUser.getCurrentUser() != null) {
            ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
            parseQuery.whereEqualTo("from", ParseUser.getCurrentUser());
            parseQuery.include("to");

            messageSentSubscription = parseLiveQueryClient.subscribe(parseQuery);
        }
    }

    public SubscriptionHandling<Message> getMessageReceivedSubscription(){
        return messageReceivedSubscription;
    }

    public SubscriptionHandling<Message> getMessageSentSubscription(){
        return messageSentSubscription;
    }

    public void unsubscribeFromMessageReceived() {
        messageReceivedSubscription = null;
    }

    public void disconnectLiveQueryClient() {
        parseLiveQueryClient.disconnect();
    }

    public void reconnectLiveQueryClient() {
        parseLiveQueryClient.reconnect();
    }

    // ** Search **

    // Set Search Filters
    public void setSearchFilters(ArrayList<SearchFilter> filters) {
        searchFilters = filters;
    }

    // Get Search Filters
    public ArrayList<SearchFilter> getSearchFilters() {
        return searchFilters;
    }

    // Clear Search Filters
    public void clearSearchFilters() {
        searchFilters.clear();
    }

}
