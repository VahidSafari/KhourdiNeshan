package com.safari.khourdineshan;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class KhoordiNeshanService extends Service {

    public static final String SERVICE_CONTENT_TITLE = "نشان خوردی";
    public static final String SERVICE_CONTENT_TEXT = "در حال دریافت لوکیشن";

    // Binder given to clients.
    private static final int NOTIFICATION_ID = 54832;
    private static final String CHANNEL_ID = "KhoordiNeshanService54832";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, getNotification());
        return START_STICKY;
    }

    public Notification getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(SERVICE_CONTENT_TITLE)
                .setContentText(SERVICE_CONTENT_TEXT)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder.build();
    }

    private final IBinder binder = new LocalBinder(this);

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the service and remove the notification
        stopForeground(true);
    }

}
