package com.example.sivaprasad.trackme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.example.sivaprasad.trackme.SmsListener;

/**
 * Created by mohan on 1/22/2018.
 */


public class SmsReceiver extends BroadcastReceiver {

    //interface
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();


        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();


            String messageBody = smsMessage.getMessageBody().toString();
            //Pass the message text to interface
            mListener.messageReceived(messageBody,sender);
        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}