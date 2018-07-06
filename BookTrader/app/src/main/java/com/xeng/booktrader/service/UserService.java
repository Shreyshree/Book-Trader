package com.xeng.booktrader.service;

import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.Map;


/**
 * This class contains Parse Cloud API calls.
 * Created by sahajarora1286 on 04-10-2017.
 */

public class UserService {


    public static void login(String email, String password, final ParseService listener) {

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    listener.onParseCallSuccess(user);
                } else {
                    // Login failed. Look at the ParseException to see what happened.
                    listener.onParseCallError(e);
                }
            }
        });
    }

    public static void register(final ParseUser user, final ParseService listener) {
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    listener.onParseCallSuccess(user);
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    listener.onParseCallError(e);
                }
            }
        });
    }


}
