package com.xeng.booktrader.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.xeng.booktrader.R;
import com.xeng.booktrader.service.ParseService;
import com.xeng.booktrader.service.UserService;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    Context self = this;

    EditText etEmail;
    EditText etPassword;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get reference to email and password EditText
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(View view) {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Logging in...");
        pDialog.setCancelable(false);
        pDialog.show();

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        UserService.login(email, password, new ParseService() {
            @Override
            public void onParseCallSuccess(Object result) {
                ParseUser user = (ParseUser) result;
                Toast.makeText(self, "Login Successful", Toast.LENGTH_SHORT).show();

                Intent eventIntent = new Intent(getString(R.string.event_logged_in));
                eventIntent.putExtra("firstName", user.get("firstName").toString());
                eventIntent.putExtra("lastName", user.get("lastName").toString());
                LocalBroadcastManager.getInstance(self).sendBroadcast(eventIntent);

                Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                pDialog.dismiss();
                self.startActivity( intent );
            }

            @Override
            public void onParseCallError(ParseException e) {
                System.out.println("Error: " + e.toString());
                pDialog.dismiss();
                Toast.makeText(self, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    public void openSignUpActivity(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }


}

