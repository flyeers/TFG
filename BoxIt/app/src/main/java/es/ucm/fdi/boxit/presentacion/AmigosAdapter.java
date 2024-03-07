package es.ucm.fdi.boxit.presentacion;

import static androidx.core.content.res.TypedArrayUtils.getString;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AmigosAdapter extends RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder>{
    private ArrayList<UserInfo> users;
    private static final String BEARER_TOKEN = "Bearer AAAAabD_4J8:APA91bHFi2tlM3CUqssG1jqw0HqSsDbWyeCZHUWwLxQDGO_BKYHwlbUC2bISS6zEJ38P3cxVfWiNVWbU_XrXKU0RF2Z4nw0AwQBaxgHLrlajhWnyRk6bNzjwU-wlQf-WmWcEkWZc5oK1";

    private int typeCard;
    private String username_actual;
    private FirebaseUser currentuser = null;

    public void setUsersData(ArrayList<UserInfo> users, int typeCard){
        this.users = users;
        this.typeCard = typeCard;  //1 amigo, 2 solicitudes, 3 busqueda
    }


    public void notifyData(){
        notifyDataSetChanged();
    };
    @NonNull
    @Override
    public AmigosAdapter.AmigoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.amigos_view, parent, false);
        return new AmigoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AmigosAdapter.AmigoViewHolder holder, int position) {

        //En cualquier caso ponemos foto y nombre de usuario
        UserInfo u_current = users.get(position);
        holder.nombre.setText(u_current.getNombreUsuario());

        Glide.with(holder.card)
                .asBitmap()
                .load(u_current.getImgPerfil())
                .placeholder(R.drawable.user)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(holder.perfil.getResources(), resource);
                        roundedDrawable.setCircular(true);
                        holder.perfil.setImageDrawable(roundedDrawable);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        if(typeCard == 1) { //lista amigos

            holder.btn1.setVisibility(View.GONE);
            holder.btn2.setImageResource(R.drawable.cross);
            holder.btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfo selectedUser = users.get(holder.getAdapterPosition());
                    SAUser saUser = new SAUser();
                    saUser.removeAmigo(selectedUser.getCorreo(), new Callbacks() { //eliminar amigo
                        @Override
                        public void onCallbackExito(Boolean exito) {
                            if(exito){
                                // Eliminar el elemento de la lista de datos
                                users.remove(holder.getAdapterPosition());
                                // Notificar al adaptador que los datos han cambiado
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
                            }
                        }
                    });

                }
            });
        }
        else if(typeCard == 2){ //solicitudes

            holder.btn1.setOnClickListener(new View.OnClickListener() { //rechazar solicitud
                @Override
                public void onClick(View v) {
                    UserInfo selectedUser = users.get(holder.getAdapterPosition());
                    SAUser saUser = new SAUser();
                    saUser.removeSolicitud(selectedUser.getCorreo(), new Callbacks() {
                        @Override
                        public void onCallbackExito(Boolean exito) {
                            if(exito){
                                // Eliminar el elemento de la lista de datos
                                users.remove(holder.getAdapterPosition());
                                // Notificar al adaptador que los datos han cambiado
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
                            }
                        }
                    });
                }
            });
            holder.btn2.setOnClickListener(new View.OnClickListener() {//aceptar solicitud
                @Override
                public void onClick(View v) {
                    UserInfo selectedUser = users.get(holder.getAdapterPosition());
                    SAUser saUser = new SAUser();
                    saUser.addAmigo(selectedUser.getCorreo(), new Callbacks() {
                        @Override
                        public void onCallbackExito(Boolean exito) {
                            if(exito){
                                // Eliminar el elemento de la lista de datos
                                users.remove(holder.getAdapterPosition());
                                // Notificar al adaptador que los datos han cambiado
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());

                                //GENERAMOS LAS NOTIFICACIONES

                                saUser.getToken(selectedUser.getCorreo(), new Callbacks() {
                                    @Override
                                    public void onCallbackData(String data) {
                                        if(!data.equals("")){

                                            //aqui se podria hacer una llamada para obtener el nombre de usuario y que se envie en la notificacion
                                            currentuser = FirebaseAuth.getInstance().getCurrentUser();
                                            saUser.infoUsuario(currentuser.getEmail(), new Callbacks() {
                                                @Override
                                                public void onCallback(UserInfo u) {
                                                    username_actual = u.getNombreUsuario();
                                                    realizar_Https(data);
                                                }
                                            });

                                        }
                                    }
                                });
                            }
                        }
                    });

                }
            });
        }
        else{ //lista busqueda
            holder.btn1.setVisibility(View.GONE);
            holder.btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfo selectedUser = users.get(holder.getAdapterPosition());
                    SAUser saUser = new SAUser();
                    saUser.sendSolicitud(selectedUser.getCorreo(), new Callbacks() { //enviar solicitud
                        @Override
                        public void onCallbackExito(Boolean exito) {
                            if(exito){
                                holder.btn2.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
       return users.size();
    }

    public static class AmigoViewHolder extends RecyclerView.ViewHolder{

        public ImageView perfil;
        public TextView nombre;
        public ImageButton btn1;
        public ImageButton btn2;
        public CardView card;
        public AmigoViewHolder(@NonNull View itemView) {
            super(itemView);
            perfil = itemView.findViewById(R.id.img_perfil);
            nombre = itemView.findViewById(R.id.text_nombre);
            btn1 = itemView.findViewById(R.id.btn_1);
            btn2 = itemView.findViewById(R.id.btn_2);
            card = itemView.findViewById(R.id.card_amigo);

        }
    }

    /* Para realizar la comunicación device-to-device utilizamos peticiones https de tipo POST a la
     * api de Firebase Cloud Messaging. Se pasa como parámetro el token del dispositivo al que se va
     * a mandar la notificación. También, para realizar la llamada es necesario auntenticarse por eso
     * el Bearer, que he sacado del proyecto de la web de Firebase.*/
    public void realizar_Https (String USER_TOKEN){
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonObject = null;

        try{

            UserInfo yo = new UserInfo();
            jsonObject  = new JSONObject();

            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", R.string.noti_solicitud);
            String bodyNotification = username_actual + R.string.solicitudNoti;
            notificationObj.put("texto", bodyNotification);
            notificationObj.put("tag",  currentuser.getEmail());
            jsonObject.put("notification",notificationObj);
            jsonObject.put("to", USER_TOKEN);

        }catch (Exception e){
            Log.d("error", e.toString());
        }


        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", BEARER_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("CLAU", "notificacion mal");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Toast.makeText(this, R.string.exito_noti, Toast.LENGTH_SHORT).show();
                Log.d("CLAU", "notificacion bien");
            }
        });



    }
}


