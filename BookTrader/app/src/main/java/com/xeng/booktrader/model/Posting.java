package com.xeng.booktrader.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by sahajarora1286 on 30-09-2017.
 */


@ParseClassName("Posting")
public class Posting extends ParseObject {

    public Posting() {
        super();
    }

    public Book getBook() {
        return (Book) get("book");
    }

    public void setBook(Book book) {
        put("book", book);
    }

    public User getPoster() {
        return (User) get("poster");
    }

    public void setPoster(User user) {
        put("poster", user);
    }

    public String getExpiryDateString() {
        return getString("expiryDate");
    }

    public String getCity() {
        return getString("city");
    }

    public void setCity(String city) {
        put("city", city);
    }

    public String getProvince() {
        return getString("province");
    }

    public void setProvince(String province) {
        put("province", province);
    }

    public String getCondition() {
        return getString("condition");
    }

    public void setCondition(String condition) {
        put("condition", condition);
    }

    public Double getPrice() {
        return getDouble("price");
    }

    public void setPrice(Double price){
        put("price", price);
    }

    public String getNotes() {
        return getString("notes");
    }

}
