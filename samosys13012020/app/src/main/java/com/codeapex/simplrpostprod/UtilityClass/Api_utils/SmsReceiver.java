package com.codeapex.simplrpostprod.UtilityClass.Api_utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;
    Boolean b;
    String abcd;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            Log.e("SMS sender",""+sender);
             b=sender.endsWith("WNRCRP");  //Just to fetch otp sent from WNRCRP
            String messageBody = smsMessage.getMessageBody();
            Log.e("OTP messageBody:::",messageBody);
            abcd=messageBody.replaceAll("[^0-9]","");   // here abcd contains otp

            //Pass on the text to our listener.
            if (abcd!=null&&!abcd.isEmpty()) {
                mListener.messageReceived(abcd);  // attach value to interface
            }
        }
    }
    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
