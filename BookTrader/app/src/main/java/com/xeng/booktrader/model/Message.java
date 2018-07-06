package com.xeng.booktrader.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by sahajarora on 2017-11-11.
 */

@ParseClassName("Message")
public class Message extends ParseObject {
    public User getFromUser() {
        return (User) get("from");
    }

    public User getToUser() {
        return (User) get("to");
    }

    public String getText() {
        return getString("text");
    }

}
