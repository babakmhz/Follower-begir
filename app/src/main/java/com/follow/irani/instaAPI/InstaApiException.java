package com.follow.irani.instaAPI;

public class InstaApiException extends Exception {

    public final static int REASON_NOTLOGGEDIN = 1;

    private int reason;

    public InstaApiException(String Message, int reason) {
        super(Message);
        this.reason = reason;
    }

    public int getReason() {
        return reason;
    }
}