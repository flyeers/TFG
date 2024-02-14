package es.ucm.fdi.boxit.presentacion;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class UsersAdapter extends RecyclerView.Adapter{


    private ArrayList<UserInfo> usersData; //nombreUsuario e imagen de perfil

    public void setUserData(List<UserInfo> data){
        this.usersData = (ArrayList<UserInfo>) data;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.collaborators_view,parent,false);
        return new UsersAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserInfo u = usersData.get(position);
        Log.d("hola", u.getNombreUsuario());
        UsersAdapter.ViewHolder h = (UsersAdapter.ViewHolder) holder;
        h.nombre.setText(u.getNombreUsuario());
        Glide.with(h.cardView)
                .load(u.getImgPerfil())
                .placeholder(R.drawable.user)
                .into(h.imagen);

        h.imgOver.setVisibility(View.GONE);
        h.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(h.imgOver.getVisibility() == View.GONE)
                    h.imgOver.setVisibility(View.VISIBLE);
                else
                    h.imgOver.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public int getItemCount() {
        return usersData != null ? usersData.size() : 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nombre;
        private ImageView imagen;
        private ImageView imgOver;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);

            nombre = view.findViewById(R.id.nombreColaborador);
            imagen = view.findViewById(R.id.imgPerfil);
            imgOver = view.findViewById(R.id.imgOver);
            cardView =  view.findViewById(R.id.cardUser);

        }
    }
}
