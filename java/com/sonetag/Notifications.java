package com.sonetag;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * This class manages the notification system
 * @version 1
 * @author Benjamin BOURG
 */
public class Notifications extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationManager notif = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String channelID = "com.sonetag";
        //Build the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notifChannel = new NotificationChannel(channelID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notifChannel.setDescription("Sonetag");
            notifChannel.enableLights(true);
            notifChannel.setLightColor(Color.BLUE);
            notifChannel.setVibrationPattern(new long []{0, 1000, 500, 1000});
            notif.createNotificationChannel(notifChannel);
        }
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(SingletonUserData.getInstance().context, channelID);
        notifBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentInfo("Info");
        notif.notify(new Random().nextInt(), notifBuilder.build());
    }
}
