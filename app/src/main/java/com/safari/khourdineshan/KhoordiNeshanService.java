package com.safari.khourdineshan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.safari.khourdineshan.ui.activity.MainActivity;

public class KhoordiNeshanService extends Service {

    public static final String SERVICE_CONTENT_TITLE = "نشان خوردی";
    public static final String SERVICE_CONTENT_TEXT = "در حال دریافت لوکیشن";

    private static final int NOTIFICATION_ID = 54832;
    private static final String CHANNEL_ID = "KhoordiNeshanService54832";
    private static final String CHANNEL_NAME = "Khoordi";

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(NOTIFICATION_ID, getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public Notification getNotification() {
        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setChannelId(CHANNEL_ID);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        } else {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        }
        builder.setContentTitle(SERVICE_CONTENT_TITLE)
                .setContentText(SERVICE_CONTENT_TEXT)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }

        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
