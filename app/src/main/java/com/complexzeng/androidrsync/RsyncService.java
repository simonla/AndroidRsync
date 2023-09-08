package com.complexzeng.androidrsync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

public class RsyncService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private boolean isShowingNotification = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isShowingNotification) {
            bindForeground();
            isShowingNotification = true;
        }
        if (!RsyncStatus.isRunning) {
            AndroidLibraryPatcher.addListener(() -> {
                handler.post(() -> RsyncStatus.isRunning = false);
            });
            RsyncStatus.isRunning = true;
            AndroidLibraryPatcher.start(getApplicationContext());
        }
        return START_STICKY;
    }

    private void bindForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        NotificationChannel channel = new NotificationChannel("default",
                "AndroidRsync", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification notification =
                new Notification.Builder(this, "default")
                        .setContentTitle("AndroidRsync")
                        .setContentText("Running")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setTicker("AndroidRsync")
                        .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}