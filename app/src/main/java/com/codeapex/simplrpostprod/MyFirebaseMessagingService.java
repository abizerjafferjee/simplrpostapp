package com.codeapex.simplrpostprod;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.codeapex.simplrpostprod.Activity.HomeActivity;
import com.codeapex.simplrpostprod.Activity.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";
    String CHANNEL_ID = "my_channel_id";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
//        String icon = remoteMessage.getData().get("icon");
        String type = remoteMessage.getData().get("type");


     //   String url = remoteMessage.getNotification().getIcon();

        Log.d(TAG, "onMessageReceived: "+title);
        Log.d(TAG, "onMessageReceived: "+body);
//        Log.d(TAG, "onMessageReceived: "+icon);
        Log.d(TAG,"type" +type);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        //then here we can use the title and body to build a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.roundicom)
                .setContentTitle(title)
                .setContentText(body).setDefaults(Notification.DEFAULT_VIBRATE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());


        Map<String, String> params = remoteMessage.getData();


        sendNotification(params,title,body, type);



    }


    private void sendNotification(Map<String, String> params, String title, String message,String type) {
        Intent intent;

        intent = new Intent(getApplicationContext(), SplashActivity.class);
        intent.putExtra("notificationData",true);
        intent.putExtra("type",type);
        if (!type.equals("admin")) {
            intent.putExtra("addressId",params.get("addressId"));
            intent.putExtra("isAddressPublic",params.get("isAddressPublic"));
            if (type.equals("businessShare")) {
                intent.putExtra("recipientBusinessId",params.get("recipientBusinessId"));
            }
        }
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.roundicom)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, notificationBuilder.build());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//        }
//        else {
            createNotificationChannel();
//        }


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            //String description = getString(R.string.description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            //channel.setDescription(description);
            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
    }
}
