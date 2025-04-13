package com.deals.jeetodeals;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import in.aabhasjindal.otptextview.OtpTextView;

public class OtpReciver extends BroadcastReceiver {

    private static OtpTextView editText_otp;

    public void setEditText_otp(OtpTextView editText){
        OtpReciver.editText_otp = editText;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage smsMessage : smsMessages){
            String message_body = smsMessage.getMessageBody();
            String getOTP = message_body.split(":")[1];
            editText_otp.setOTP(getOTP);
        }
    }
}
