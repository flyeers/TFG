package es.ucm.fdi.boxit.presentacion;

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

public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.ViewHolder>{

    private ArrayList<BoxInfo> boxesData;
    private boolean small;

    public void setBoxData(List<BoxInfo> data, boolean small){
        this.boxesData = (ArrayList<BoxInfo>) data;
        this.small = small;
    }

    @NonNull
    @Override
    public BoxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Crea una nueva vista
        //viewType podemos cargar distintos tipos -> con getitemviewtipe

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.box_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoxAdapter.ViewHolder holder, int position) {
        if(small){
            ViewGroup.LayoutParams layoutParams = holder.cardView.getLayoutParams();
            layoutParams.height = (int) (layoutParams.height * 0.8);
            layoutParams.width = (int) (layoutParams.width * 0.8);
            holder.cardView.setLayoutParams(layoutParams);

            ViewGroup.LayoutParams layoutParams2 = holder.imagen.getLayoutParams();
            layoutParams2.height = (int) (layoutParams2.height * 0.8);
            holder.imagen.setLayoutParams(layoutParams2);
        }

        BoxInfo box = boxesData.get(position);
        holder.titulo.setText(box.getTitle());
        Glide.with(holder.cardView)
                .load(box.getImg())
                .placeholder(R.drawable.button_shape)
                .into(holder.imagen);    }

    @Override
    public int getItemCount() {
        //numero de elementos que tiene el adapter
        return boxesData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo;
        private ImageView imagen;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);

            titulo = view.findViewById(R.id.nameBox);
            imagen = view.findViewById(R.id.imgBox);
            cardView=  view.findViewById(R.id.cardView);

        }
    }

}
