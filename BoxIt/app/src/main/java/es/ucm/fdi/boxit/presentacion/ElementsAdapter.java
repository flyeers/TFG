package es.ucm.fdi.boxit.presentacion;

import android.net.Uri;
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

public class ElementsAdapter extends RecyclerView.Adapter {

    private ArrayList<String> itemsData;
    private boolean photo;

    public void setElementsData(List<String> data, boolean photo){
        this.itemsData = (ArrayList<String>) data;
        this.photo = photo;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView imagen;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);


            imagen = view.findViewById(R.id.photoBox);
            cardView=  view.findViewById(R.id.cardViewPhoto);

        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Crea una nueva vista
        //viewType podemos cargar distintos tipos -> con getitemviewtipe
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        v = inflater.inflate(R.layout.photo_view,parent,false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        ElementsAdapter.ViewHolder h1 = (ElementsAdapter.ViewHolder) holder;
        if(photo){
            Uri img = Uri.parse(itemsData.get(position));
            Glide.with(h1.cardView)
                    .load(img)
                    .placeholder(R.drawable.default_image)
                    .into(h1.imagen);
        }


    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}

