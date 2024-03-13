package es.ucm.fdi.boxit.integracion;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.ucm.fdi.boxit.presentacion.MainActivity;


/* Esta clase se encarga de gestionar la llegada de notificaciones. No se instancia en ningún sitio.
 * Funciona porque está añadida como un servicio en el Android Manifest. */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /* Listener que espera la llegada de nuevas notificaciones y desempaqueta el mensaje en título de
    la notificación y cuerpo.  */
   /* @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            String title = notification.getTitle();
            String body = notification.getBody();
            String email = notification.getTag();
            showNotification(title, body, email);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String fecha = dateFormat.format(calendar.getTime());

            SAUser sa = new SAUser();
            sa.aniadirNotificacion(body, fecha, email);
        }
    }*/

    /* Esta función crea la vista de la notificación. Ocurre que en las versiones más nuevas es
     * obligatorio crear un canal de comunicación pero en las más antiguas no, por eso el IF. */
    private void showNotification(String title, String body, String email) {
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

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragmentToLoad", "UsuarioBibliotecaFragment");
        intent.putExtra("propietario", email);

        // Creamos PendingIntent para la notificación con el Intent configurado
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);


        /* El id debería ser un número aleatorio, de momento lo dejo en 0 */
        notificationManager.notify(0, builder.build());
    }

    /* Función que se invoca cuando el token del usuario ha cambiado. Existen diversas razones como
     * reinstalar la aplicación o ejecutarla en modo debug. Explico qué es el token del usuario en
     * DAOUser */
    /*
    @Override
    public void onNewToken(String newToken) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String idUsuario = currentUser.getUid();

        Map<String, Object> updates = new HashMap<>();
        updates.put("token", newToken);

        SingletonDataBase.getInstance().getDB()
                .collection("Usuarios").document(idUsuario).update(updates);

    }
    */

}
