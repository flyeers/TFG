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
            String tag = notification.getTag();
            String title ="", body="";
            switch (tag) {
                case "1":
                    title = getString(R.string.noti_solicitud);
                    body = notification.getBody() + " " + getString(R.string.solicitudNoti);
                    break;
                case "2":
                    title = getString(R.string.noti_solicitud);
                    body = notification.getBody() + " " + getString(R.string.solicitudAceptaNoti);
                    break;
                case "3":
                    title = getString(R.string.titleAddCaja);
                    body = notification.getBody() + " " + getString(R.string.bodyAddCaja) + " " +notification.getTitle();
                    break;
                case "4":
                    title = getString(R.string.titleAddCapsula);
                    body = notification.getBody() + " " + getString(R.string.bodyAddCapsula) + " " +notification.getTitle();
                    break;
                case "5":
                    title = getString(R.string.titleCajaUpdate);
                    body = notification.getBody() + " " + getString(R.string.bodyAddCajaUpdate) + " " +notification.getTitle();
                    break;
                case "6":
                    title = getString(R.string.titleCapsUpdate);
                    body = notification.getBody() + " " + getString(R.string.bodyAddCapsUpdate) + " " +notification.getTitle();
                    break;
                default:
                    break;
            }
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
                .setSmallIcon(R.drawable.icon_boxit)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(bigTextStyle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        /* El id debería ser un número aleatorio, de momento lo dejo en 0 */
        notificationManager.notify(0, builder.build());
    }


}