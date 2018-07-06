package com.xeng.booktrader.model;

import android.os.Parcel;

import com.parse.*;


/**
 * Created by sahajarora1286 on 30-09-2017.
 */

@ParseClassName("Book")
public class Book extends ParseObject {

    public Book() {
        super();
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public ParseUser getOwner() {
        return getParseUser("owner");
    }

    public void setOwner(ParseUser user) {
        put("owner", user);
    }

    public String getAuthor(){
        return getString("author");
    }

    public void setAuthor(String author) {
        put ("author", author);
    }

    public String getEdition() {
        return getString("edition");
    }

    public void setEdition(String edition) {
        put("edition", edition);
    }

    public String getPublisher() {
        return getString("publisher");
    }

    public String getISBN() {
        return getString("isbn");
    }

    public String getSummary() {
        return getString("summary");
    }

    public String getLongName() {
        return getString("nameLong");
    }

}
