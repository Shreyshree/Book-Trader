package com.xeng.booktrader.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sahajarora on 2017-11-15.
 */

public class SearchFilter {
    private String name;
    private String value;
    private String displayName;

    public SearchFilter(String name, String value, String displayName) {
        this.name = name;
        this.value = value;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
