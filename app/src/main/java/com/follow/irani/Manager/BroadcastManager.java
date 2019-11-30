package com.follow.irani.Manager;

import android.content.Context;
import android.content.Intent;

public class BroadcastManager {

    public static void sendBroadcast(Context context) {
        Intent intent = new Intent("com.journaldev.broadcastreceiver.Update");
        context.sendBroadcast(intent);
    }
}
