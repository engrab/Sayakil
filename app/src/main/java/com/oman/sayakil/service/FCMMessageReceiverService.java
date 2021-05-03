package com.oman.sayakil.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.oman.sayakil.R;
import com.oman.sayakil.ui.activities.NotificationActivity;

import static com.oman.sayakil.App.FCM_CHANNEL_ID;

public class FCMMessageReceiverService extends FirebaseMessagingService {
    private static final String TAG = "FCMMessageReceiverServi";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);




        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.d(TAG, "onMessageReceived: title and body: "+title+body);

            // Create an Intent for the activity you want to start
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("body", body);

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(this, FCM_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setColor(Color.BLUE)
                    .build();

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(1002, notification);
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "onMessageReceived: Data Size: " + remoteMessage.getData().size());

            for (String key : remoteMessage.getData().keySet()) {
                Log.d(TAG, "onMessageReceived Key: " + key + " Data: " + remoteMessage.getData().get(key));
            }

            Log.d(TAG, "onMessageReceived: Data: " + remoteMessage.getData().toString());
        }

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.d(TAG, "onDeletedMessages: called");
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: called");
        //upload this token on the app server
    }
}