package com.obabuji.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.obabuji.R;
import com.obabuji.SplashActivity;
import com.obabuji.session.SessionManager;


/**
 * Created by user on 05-06-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("obabuji","obabuji onMessageReceived called");

        if (remoteMessage.getData().size() > 0) {
            String type = remoteMessage.getData().get("type");
            String body = remoteMessage.getData().get("body");
            String title = remoteMessage.getData().get("title");

            Log.e("obabuji","obabuji title "+title+" type "+type);

            if(type.equalsIgnoreCase("0")){
                String count = SessionManager.getInstance(this).getNotificationCount();
                int countNumber = Integer.parseInt(count)+1;
                SessionManager.getInstance(this).saveNotificationCount(""+countNumber);
            }
            sendNotification(body,title,type);

        }

    }

    private void sendNotification(String messageBody, String messageTitle, String type) {
        Intent intent = new Intent(getApplicationContext(),SplashActivity.class);
        intent.putExtra("type", type);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(type), notificationBuilder.build());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
