package com.deals.jeetodeals;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSBroadcastReceiver";
    public SmsBroadcastReceiverListener smsBroadcastReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Status smsRetreiverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                if (smsRetreiverStatus != null) {
                    switch (smsRetreiverStatus.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            Intent messageIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                            if (smsBroadcastReceiverListener != null) {
                                smsBroadcastReceiverListener.onSuccess(messageIntent);
                            } else {
                                Log.e(TAG, "Listener is null, cannot deliver SMS consent intent");
                            }
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            if (smsBroadcastReceiverListener != null) {
                                smsBroadcastReceiverListener.onFailure();
                            } else {
                                Log.e(TAG, "Listener is null, cannot deliver timeout notification");
                            }
                            break;
                    }
                }
            }
        }
    }

    public interface SmsBroadcastReceiverListener {
        void onSuccess(Intent intent);
        void onFailure();
    }
}