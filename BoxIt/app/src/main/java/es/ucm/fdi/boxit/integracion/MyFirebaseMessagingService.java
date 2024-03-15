package es.ucm.fdi.boxit.integracion;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import es.ucm.fdi.boxit.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            String title = notification.getTitle();
            String body = notification.getBody()+ " " + getString(R.string.solicitudNoti);
            //Log.d("Notifrecibida", "Title: " + title + ", Body: " + body);
            showNotification(title, body);
        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "my_channel_id";
        String channelName = "My Channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(body);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.btn_star_big_on)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(bigTextStyle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        /* El id debería ser un número aleatorio, de momento lo dejo en 0 */
        notificationManager.notify(0, builder.build());
    }


}