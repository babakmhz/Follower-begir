package com.follow.irani.Interface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ValueUpdaterBroadCast extends BroadcastReceiver {
    private static ValueChangerInterface mListener;

    public static void bindListener(ValueChangerInterface listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.journaldev.broadcastreceiver.Update")) {
            mListener.update();
        }

    }
}
