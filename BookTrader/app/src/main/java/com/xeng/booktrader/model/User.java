package com.xeng.booktrader.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by sahajarora1286 on 30-09-2017.
 */

@ParseClassName("_User")
public class User extends ParseUser {
    public User() {
        super();
    }

    public String getFirstName(){
        return getString("firstName");
    }

    public void setFirstName(String firstName) {
        put("firstName", firstName);
    }

    public String getLastName(){
        return getString("lastName");
    }

    public void setLastName(String lastName) {
        put("lastName", lastName);
    }

    public void setRating(int rating) {
        put("rating", rating);
    }

    public int getRating(){
        return getInt("rating");
    }

    public void setAddress(String address) {
        put("address", address);
    }

    public String getAddress(){
        return getString("address");
    }

    public void setPhone(String phone) {
        put("phone", phone);
    }

    public String getPhone() {
        return getString("phone");
    }

    public void setSecretQuestion(String secretQuestion) {
        put("secretQuestion", secretQuestion);
    }

    public String getSecretQuestion() {
        return getString("secretQuestion");
    }

    public void setSecretQuestionAnswer(String secretQuestionAnswer) {
        put("secretQuestionAnswer", secretQuestionAnswer);
    }

    public String getSecretQuestionAnswer() {
        return getString("secretQuestionAnswer");
    }
}
