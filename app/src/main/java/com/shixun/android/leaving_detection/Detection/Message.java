package com.shixun.android.leaving_detection.Detection;

/**
 * Created by shixunliu on 22/4/17.
 */

public class Message {

    private String message;

    public Message(String str) {
        this.message = str;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
