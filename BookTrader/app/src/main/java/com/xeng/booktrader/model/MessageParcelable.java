package com.xeng.booktrader.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sahajarora on 2017-11-12.
 */

public class MessageParcelable extends Message implements Parcelable{
    private int mData;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    private Message message;

    public MessageParcelable(Message message) {
        this.message = message;
    }


    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<MessageParcelable> CREATOR = new Parcelable.Creator<MessageParcelable>() {
        public MessageParcelable createFromParcel(Parcel in) {
            return new MessageParcelable(in);
        }

        public MessageParcelable[] newArray(int size) {
            return new MessageParcelable[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private MessageParcelable(Parcel in) {
        mData = in.readInt();
    }
}
