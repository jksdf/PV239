package ly.betime.shuriken.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import javax.inject.Inject;
import javax.inject.Named;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import ly.betime.shuriken.R;


public class NotificationService {
    private final Context context;
    private final NotificationManagerCompat notificationManager;

    @Inject
    public NotificationService(@Named("application") Context context) {
        this.context = context;
        notificationManager = NotificationManagerCompat.from(context);
    }

    public void notify(int id, String chanelId, String title, String text) {
        createNotificationChannel(chanelId);
        notificationManager.notify(id,
                new NotificationCompat.Builder(context, chanelId)
                        .setSmallIcon(R.drawable.ic_alarm_48)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .build());
    }

    private void createNotificationChannel(String chanelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(chanelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
