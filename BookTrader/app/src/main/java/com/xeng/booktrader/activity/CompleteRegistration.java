package com.xeng.booktrader.activity;

import android.app.ProgressDialog;
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

import com.parse.SaveCallback;
import com.xeng.booktrader.R;
import com.xeng.booktrader.model.User;
import com.xeng.booktrader.service.ParseService;
import com.xeng.booktrader.service.UserService;

public class CompleteRegistration extends AppCompatActivity {

    Context self = this;

    EditText etPhone;
    EditText etSecretQuestion;
    EditText etSecretQuestionAnswer;

    String firstName, lastName, email, password, address;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);

        etPhone = (EditText) findViewById(R.id.etPhone);
        etSecretQuestion = (EditText) findViewById(R.id.etSecretQuestion);
        etSecretQuestionAnswer = (EditText) findViewById(R.id.etSecretQuestionAnswer);

        // Get Intent extras
        Intent intent = getIntent();
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        address = intent.getStringExtra("address");
    }

    public void registerUser(View view){

        if (validate()){
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Signing up...");
            pDialog.setCancelable(false);
            pDialog.show();

            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setUsername(email);
            user.setPassword(password);
            user.setAddress(address);
            user.setPhone(etPhone.getText().toString());
            user.setSecretQuestion(etSecretQuestion.getText().toString());
            user.setSecretQuestionAnswer(etSecretQuestionAnswer.getText().toString());
            user.setRating(5); // Set user rating to 5 initially. This value can be changed later as desired.


            UserService.register(user, new ParseService() {
                @Override
                public void onParseCallSuccess(Object result) {
                    ParseUser registeredUser = (ParseUser) result;
                    Toast.makeText(self, "Registration Successful", Toast.LENGTH_SHORT).show();

                    Intent eventIntent = new Intent(getString(R.string.event_logged_in));
                    eventIntent.putExtra("firstName", registeredUser.get("firstName").toString());
                    eventIntent.putExtra("lastName", registeredUser.get("lastName").toString());
                    LocalBroadcastManager.getInstance(self).sendBroadcast(eventIntent);

                    Intent intent = new Intent( CompleteRegistration.this, MainActivity.class );
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    pDialog.dismiss();
                    self.startActivity( intent );

                }

                @Override
                public void onParseCallError(ParseException e) {
                    pDialog.dismiss();
                    Toast.makeText(self, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public Boolean validate() {

        if (etSecretQuestion.getText().toString().length() < 5) {
            etSecretQuestion.setError("Question too short. Should be at-least 5 characters.");
            return false;
        }

        if (etSecretQuestionAnswer.getText().toString().length() < 2) {
            etSecretQuestionAnswer.setError("Answer too short. Should be at-least 2 characters.");
            return false;
        }

        return true;

    }


}
