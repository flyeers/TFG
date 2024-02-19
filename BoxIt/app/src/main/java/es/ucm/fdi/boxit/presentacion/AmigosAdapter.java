package es.ucm.fdi.boxit.presentacion;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class AmigosAdapter extends RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder>{
    private ArrayList<UserInfo> users;

    private int typeCard;

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

        /*
        Glide.with(holder.card)
                .load(u_current.getImgPerfil())
                .placeholder(R.drawable.user)
                .into(holder.perfil);*/
        Glide.with(holder.card)
                .asBitmap()
                .load(u_current.getImgPerfil())
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
}


