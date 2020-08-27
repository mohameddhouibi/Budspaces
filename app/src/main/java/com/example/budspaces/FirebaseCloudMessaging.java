package com.example.budspaces;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.budspaces.Views.Activities.SplashScreenActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class FirebaseCloudMessaging extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    public FirebaseCloudMessaging() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("from", "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {
            if (Objects.equals(remoteMessage.getData().get("uid"), Cache.getInstance().getSession("userId")))
                return;

            handleNow(remoteMessage.getNotification(), remoteMessage.getData());
        }
    }

    @Override
    public void onNewToken(@NotNull String token) {
        Log.d("TAG", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    private void sendRegistrationToServer(String token) { }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow(RemoteMessage.Notification notification, Map<String, String> data) {
        Uri uri = notification.getImageUrl();
        Bitmap bitmap = null;

        if (uri != null)
            bitmap = getBitmapfromUrl(uri.toString());

        if (Objects.equals(data.get("type"), "message"))
            sendMessageNotification(notification, data, bitmap);
    }

    /*
     *To get a Bitmap image from the URL received
     * */
    public Bitmap getBitmapfromUrl(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private void sendMessageNotification(RemoteMessage.Notification notification, Map<String, String> data, Bitmap bmp) {
        String body = data.get("name") + ": ";

        if (Objects.equals(data.get("state"), "picture"))
            body += getString(R.string.picture_message);
        else
            body += notification.getBody();

        sendNotification(notification, body, bmp);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param notification FCM message body received.
     */
    private void sendNotification(RemoteMessage.Notification notification, String body, Bitmap bmp) {
        Intent intent = new Intent(this, SplashScreenActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "petApp")
                .setLargeIcon(bmp)
                .setSmallIcon(R.mipmap.logo_notification)
                .setContentTitle(notification.getTitle())
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("petApp",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
