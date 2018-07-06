package com.xeng.booktrader.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.xeng.booktrader.R;
import com.xeng.booktrader.model.User;
import com.xeng.booktrader.service.ParseService;
import com.xeng.booktrader.service.UserService;

public class RegisterActivity extends AppCompatActivity {
    Context self = this;

    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;
    EditText confirmPassword;
    EditText address;

    Boolean isValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //  Initialize input EditTexts
        firstName = (EditText) findViewById(R.id.etFirstName);
        lastName = (EditText) findViewById(R.id.etLastName);
        email = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPassword);
        confirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        address = (EditText) findViewById(R.id.etAddress);
    }

    // Called when user taps SignUp button
    public void attemptSignUp(View view) {

        if (validate()) {
            Intent intent = new Intent(RegisterActivity.this, CompleteRegistration.class);
            intent.putExtra("firstName", firstName.getText().toString());
            intent.putExtra("lastName", lastName.getText().toString());
            intent.putExtra("email", email.getText().toString());
            intent.putExtra("password", password.getText().toString());
            intent.putExtra("address", address.getText().toString());
            startActivity(intent);
        }
    }

    public Boolean validate() {
        isValid = true;

        if (firstName.getText().length() < 3) {
            isValid = false;
            firstName.setError("First Name should be atleast 3 characters long.");
        }

        if (lastName.getText().length() < 2) {
            isValid = false;
            lastName.setError("First Name should be atleast 3 characters long.");
        }

        if (email.getText().length() < 6 || (!email.getText().toString().contains("@") && !email.getText().toString().contains("."))) {
            isValid = false;
            email.setError("Email format is invalid.");
        }

        if (password.getText().length() < 3) {
            isValid = false;
            password.setError("Password should be atleast 3 characters long.");
        }

        if (!confirmPassword.getText().toString().equals(password.getText().toString())) {
            isValid = false;
            password.setError("Password do not match.");
        }

        return isValid;
    }


}
