package service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.pj.app_update.R;

/**
 * Created by pj on 2016/3/28.
 */
public class NormalService extends Service {
    private static final int NOTIF_ID = 1234;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private Notification mNotification;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        // setup the notification first
        setUpNotification();
        return START_STICKY;
    }

    private void setUpNotification() {

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // we need to build a basic notification first, then update it

        // notification's layout
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.normal_notification);
        // notification's icon
        mRemoteViews.setImageViewResource(R.id.notif_icon, R.drawable.default_icon);
        // notification's title
        mRemoteViews.setTextViewText(R.id.notif_title, getResources().getString(R.string.app_name));
        // notification's content
        mRemoteViews.setTextViewText(R.id.notif_content, "content");

        mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.drawable.default_icon)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContent(mRemoteViews)
                .setTicker("Ticker");

        // starting service with notification in foreground mode
        startForeground(NOTIF_ID, mBuilder.build());
    }

    // use this method to update the Notification's UI
    private void updateNotification() {

        int api = Build.VERSION.SDK_INT;
        // update the icon
        mRemoteViews.setImageViewResource(R.id.notif_icon, R.drawable.rabbit);
        // update the title
        mRemoteViews.setTextViewText(R.id.notif_title, "兔子");
        // update the content
        mRemoteViews.setTextViewText(R.id.notif_content, "我是一只兔子");

        // update the notification
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
    }
}
