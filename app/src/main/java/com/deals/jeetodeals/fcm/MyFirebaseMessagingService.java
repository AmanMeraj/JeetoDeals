package com.deals.jeetodeals.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";
    //String channelid;

    @Override

    public void onMessageReceived(RemoteMessage remoteMessage) {
//        afication().getBody());
//        Log.e(TAG, "From4: " + remoteMessage.getData());

        //channelid = remoteMessage.getNotification().getChannelId();

        remoteMessage.getData();
        Log.d(TAG, "onMessageReceived: messageeeeeeeeeeeee" + remoteMessage.getNotification().getTitle());
        Log.e(TAG, "title: " + remoteMessage.getNotification().getTitle());
        Log.e(TAG, "body: " + remoteMessage.getNotification().getBody());

            /*Intent intent = new Intent("msg");    //action: "msg"
            intent.setPackage(getPackageName());
            intent.putExtra("badge", badgeCount);
            getApplicationContext().sendBroadcast(intent);*/

        if (remoteMessage.getData().isEmpty()) {
            String getTitle = remoteMessage.getNotification().getTitle();
            String getBody = remoteMessage.getNotification().getBody();
            sendNotification("",getTitle, getBody);
        } else{
            String type = remoteMessage.getData().get("type");
            String bazar_name = remoteMessage.getData().get("bazar_name");
            String close_result = remoteMessage.getData().get("close_result");

            sendNotification("",bazar_name, close_result);

        }
    }

    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotification(String type, String title, String messageBody) {
        Log.e(TAG, "sendNotification: " );
        Intent intent = new Intent(this, ContainerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_IMMUTABLE);
        String channelId = "PB12345";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(com.deals.jeetodeals.R.drawable.logo_jd)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 , notificationBuilder.build());
        //notificationBuilder.setContentIntent(pendingIntent);
        //playSound();
    }

    /*private void playSound(){
        MediaPlayer ring= MediaPlayer.create(this,R.raw.swiftly);
        ring.start();
    }*/
}
