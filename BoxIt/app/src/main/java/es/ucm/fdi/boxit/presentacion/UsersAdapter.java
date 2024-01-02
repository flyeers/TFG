package es.ucm.fdi.boxit.presentacion;

import android.util.Pair;
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


    private ArrayList<Pair<String,String>> usersData; //nombreUsuario e imagen de perfil

    public void setUserData(List<Pair<String,String>> data){
        this.usersData = (ArrayList<Pair<String,String>>) data;
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
        Pair<String, String> u = usersData.get(position);
        UsersAdapter.ViewHolder h = (UsersAdapter.ViewHolder) holder;
        h.nombre.setText(u.first);
        Glide.with(h.cardView)
                .load(u.second)
                .placeholder(R.drawable.user)
                .into(h.imagen);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nombre;
        private ImageView imagen;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);

            nombre = view.findViewById(R.id.nombreColaborador);
            imagen = view.findViewById(R.id.imgPerfil);
            cardView =  view.findViewById(R.id.cardUser);

        }
    }
}
