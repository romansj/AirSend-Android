package com.cherrydev.airsend.app.service.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Icon;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.MyApplication;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationUtils {


    public static void initNotificationChannels() {
        createChannel(CHANNEL_ID, "Messages", "Allows you to receive new message notifications", NotificationManager.IMPORTANCE_DEFAULT);
        createChannel(CHANNEL_ID_ONGOING, "Service Notification", "Notification is visible when AirSend is active and allows you to control its function", NotificationManager.IMPORTANCE_LOW);
    }


    private static void createChannel(String channelID, String name, String description, int importance) {
        NotificationChannel channel = new NotificationChannel(channelID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = MyApplication.getInstance().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }


    public static void showNotification(String title, String description) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.getInstance().getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.message_outline)
                .setContentTitle(title)
                .setContentText(description)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setChannelId(CHANNEL_ID);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyApplication.getInstance());

        // notificationId is a unique int for each notification that you must define
        NOTIFICATION_ID++;
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    public static void dismissNotifications() {
        //todo can have filter for which notifications to dismiss -- regular or ongoing (permanent)
        NotificationManager notificationManager = (NotificationManager) MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        for (int i = 0; i <= NOTIFICATION_ID; i++) notificationManager.cancel(i);
    }


    public static final String DEFAULT_TITLE = "getText(R.string.notification_title)";
    public static final String DEFAULT_TEXT = "getText(R.string.notification_message)";
    public static final int DEFAULT_DRAWABLE_ID = R.drawable.arrow_up;


    public static String CHANNEL_ID = "0";
    public static String CHANNEL_ID_ONGOING = "1";

    public static final int ONGOING_NOTIFICATION_ID = 1;
    private static int NOTIFICATION_ID = 2;

    public static Notification getNotification(PendingIntent pendingIntent, String channelID) {
        return getNotification(pendingIntent, channelID, DEFAULT_TITLE, DEFAULT_TEXT, DEFAULT_DRAWABLE_ID);
    }

    public static <T extends NotificationAction> Notification getNotification(PendingIntent pendingIntent, String channelID, List<T> actionList) {
        return getNotification(pendingIntent, channelID, DEFAULT_TITLE, DEFAULT_TEXT, DEFAULT_DRAWABLE_ID, actionList);
    }

    public static Notification getNotification(PendingIntent pendingIntent, String channelID, String title, String text, int drawable) {
        Notification notification = getNotification(pendingIntent, channelID, title, text, drawable, Collections.emptyList());
        return notification;
    }

    public static <T extends NotificationAction> Notification getNotification(PendingIntent pendingIntent, String channelID, String title, String text, int drawable, List<T> actionList) {
        List<Notification.Action> collectedActions = actionList.stream().map(t -> {
            int icon = t.getIcon();
            String actionText = t.getActionText();
            PendingIntent pendingIntentAction = t.getPendingIntent();

            Notification.Action.Builder builder = new Notification.Action.Builder(Icon.createWithResource("", icon), actionText, pendingIntentAction);
            Notification.Action notificationAction = builder.build();
            return notificationAction;

        }).collect(Collectors.toList());


        Notification.Builder builder = new Notification.Builder(MyApplication.getInstance().getApplicationContext(), channelID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(drawable)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID_ONGOING)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);

        collectedActions.forEach(notificationAction -> builder.addAction(notificationAction));


        Notification notification = builder.build();
        return notification;
    }
}
