package com.xeng.booktrader.service;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.xeng.booktrader.model.MessageThread;
import com.xeng.booktrader.model.Posting;
import com.xeng.booktrader.model.SearchFilter;
import com.xeng.booktrader.model.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Define all Parse cloud function calls here.
 * Created by sahajarora on 2017-10-07.
 */

public class ParseAPIService {

    // ** Postings **

    // Create a Posting
    public static void createPosting(String isbn, String edition, String notes, String condition, String city, String province,
                                     String price, String posterId, final ParseResponseHandler listener) {

        HashMap<String,Object> params = new HashMap<>();

        //placing params in hashmap
        params.put("isbn", isbn);
        params.put("edition", edition);
        params.put("condition", condition);
        params.put("city", city);
        params.put("province", province);
        params.put("price", price);
        params.put("notes", notes);
        params.put("posterId", posterId);

        ParseCloud.callFunctionInBackground("createPosting", params, new FunctionCallback<Posting>() {

            @Override
            public void done(Posting posting, ParseException e) {
                if (e == null) {
                    // Everything went alright
                    listener.onParseCallSuccess(posting);
                }
                else {
                    // Something went wrong
                    listener.onParseCallError(e);
                }
            }
        });

    }

    // Get Postings
    public static void getPostings(final ParseResponseHandler listener) {

        ParseCloud.callFunctionInBackground("getPostings", new HashMap<String, String>(), new FunctionCallback<ArrayList<Posting>>() {

            @Override
            public void done(ArrayList<Posting> objects, ParseException e) {
                if (e == null) {
                    // Everything went alright
                    listener.onParseCallSuccess(objects);
                }
                else {
                    // Something went wrong
                    listener.onParseCallError(e);
                }
            }
        });
    }

    // Get Posting by objectId
    public static void getPostingById(String postingId, final ParseResponseHandler listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("postingId", postingId);

        ParseCloud.callFunctionInBackground("getPostingById", params, new FunctionCallback<Posting>() {

            @Override
            public void done(Posting objects, ParseException e) {
                if (e == null) {
                    // Everything went alright
                    listener.onParseCallSuccess(objects);
                }
                else {
                    // Something went wrong
                    listener.onParseCallError(e);
                }
            }
        });
    }


    // ** Search **

    // Search
    public static void search(String keywords, ArrayList<SearchFilter> filters, final ParseResponseHandler listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("keywords", keywords);

        for (int i = 0; i<filters.size(); i++) {
            params.put(filters.get(i).getName(), filters.get(i).getValue());
        }

        ParseCloud.callFunctionInBackground("search", params, new FunctionCallback<ArrayList<Posting>>() {

            @Override
            public void done(ArrayList<Posting> objects, ParseException e) {
                if (e == null) {
                    // Everything went alright
                    listener.onParseCallSuccess(objects);
                }
                else {
                    // Something went wrong
                    listener.onParseCallError(e);
                }
            }
        });

    }


    // ** Messages **

    // Get current user's messages
    public static void getUserMessageThreads(String userId, final ParseResponseHandler listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", ParseUser.getCurrentUser().getObjectId());

        ParseCloud.callFunctionInBackground("getUserMessageThreads", params, new FunctionCallback<ArrayList<MessageThread>>() {

            @Override
            public void done(ArrayList<MessageThread> objects, ParseException e) {
                if (e == null) {
                    // Everything went alright
                    listener.onParseCallSuccess(objects);
                }
                else {
                    // Something went wrong
                    listener.onParseCallError(e);
                }
            }
        });
    }

    // Get messages by MessageThread ID
    public static void getUserMessages(String messageThreadId, final ParseResponseHandler listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("messageThreadId", messageThreadId);

        ParseCloud.callFunctionInBackground("getUserMessages", params, new FunctionCallback<ArrayList<MessageThread>>() {

            @Override
            public void done(ArrayList<MessageThread> objects, ParseException e) {
                if (e == null) {
                    // Everything went alright
                    listener.onParseCallSuccess(objects);
                }
                else {
                    // Something went wrong
                    listener.onParseCallError(e);
                }
            }
        });
    }

    // Message seller
    public static void getMessageThreadByPostingId(String postingId, String userId, final ParseResponseHandler listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("postingId", postingId);
        params.put("userId", userId);

        ParseCloud.callFunctionInBackground("getMessageThreadByPostingId", params, new FunctionCallback<MessageThread>() {

            @Override
            public void done(MessageThread result, ParseException e) {
                if (e == null) {
                    // Everything went alright
                    listener.onParseCallSuccess(result);
                }
                else {
                    // Something went wrong
                    listener.onParseCallError(e);
                }
            }
        });
    }

    // Send message
    public static void sendMessage(String messageThreadId, String postingId, String fromUserId, String toUserId, String text, final ParseResponseHandler listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("messageThreadId", messageThreadId);
        params.put("postingId", postingId);
        params.put("fromUserId", fromUserId);
        params.put("toUserId", toUserId);
        params.put("text", text);

        ParseCloud.callFunctionInBackground("sendMessage", params, new FunctionCallback<MessageThread>() {

            @Override
            public void done(MessageThread result, ParseException e) {
                if (e == null) {
                    // Everything went alright
                    listener.onParseCallSuccess(result);
                }
                else {
                    // Something went wrong
                    listener.onParseCallError(e);
                }
            }
        });
    }

    // Get other user in MessageThread
    public static void getOtherUserInMessageThread(String messageThreadId, String knownUserId, final ParseResponseHandler listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("messageThreadId", messageThreadId);
        params.put("knownUserId", knownUserId);

        ParseCloud.callFunctionInBackground("getOtherUserInMessageThread", params, new FunctionCallback<User>() {

            @Override
            public void done(User user, ParseException e) {
                if (e == null) {
                    // Everything went alright
                    listener.onParseCallSuccess(user);
                }
                else {
                    // Something went wrong
                    listener.onParseCallError(e);
                }
            }
        });
    }

}
