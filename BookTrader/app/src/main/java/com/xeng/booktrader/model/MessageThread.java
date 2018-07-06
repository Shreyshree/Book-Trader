package com.xeng.booktrader.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by sahajarora on 2017-11-11.
 */

@ParseClassName("MessageThread")
public class MessageThread extends ParseObject {
    public Message getLastMessage() {
        return (Message) get("lastMessage");
    }

    public void setLastMessage(Message message) {
        put("lastMessage", message);
    }
    public User getUser1() {
        return (User) get("user1");
    }

    public User getUser2() {
        return (User) get("user2");
    }

    public Posting getPosting() {
        return (Posting) get("posting");
    }

}
