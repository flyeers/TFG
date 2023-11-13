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
import com.bumptech.glide.load.model.Model;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.negocio.BoxInfo;

public class BoxAdapter extends RecyclerView.Adapter{
    private static final int NORMAL_CARD = 1;
    private static final int ADD_CARD = 0;
    private ArrayList<BoxInfo> boxesData;
    private boolean small;

    public void setBoxData(List<BoxInfo> data, boolean small){
        this.boxesData = (ArrayList<BoxInfo>) data;
        this.small = small;
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

    public static class AddViewHolder extends RecyclerView.ViewHolder{

        private CardView addCard;
        public  AddViewHolder(View view){
            super(view);
            addCard = view.findViewById(R.id.cardViewAdd);
        }
    }


    @Override
    public int getItemViewType(int position) {
        // Devuelve el tipo de vista según la posición

        return (position == 0) ? ADD_CARD : NORMAL_CARD;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Crea una nueva vista
        //viewType podemos cargar distintos tipos -> con getitemviewtipe
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType){
            case ADD_CARD:
                v = inflater.inflate(R.layout.add_view,parent,false);
                    return new AddViewHolder(v);
            case NORMAL_CARD:
                v = inflater.inflate(R.layout.box_view, parent, false);
                return new ViewHolder(v);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case ADD_CARD:
                AddViewHolder h2 = (AddViewHolder) holder;
                  if(small){
                    ViewGroup.LayoutParams layoutParams = h2.addCard.getLayoutParams();
                    layoutParams.height = (int) (layoutParams.height * 0.8);
                    layoutParams.width = (int) (layoutParams.width * 0.8);
                    h2.addCard.setLayoutParams(layoutParams);
                  }
                  h2.addCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("YOOOOOOOO", "SOY ADD");
                        }
                    });
                    break;
            case NORMAL_CARD:
                ViewHolder h1 = (ViewHolder) holder;
                if (small) {
                    ViewGroup.LayoutParams layoutParams = h1.cardView.getLayoutParams();
                    layoutParams.height = (int) (layoutParams.height * 0.8);
                    layoutParams.width = (int) (layoutParams.width * 0.8);
                    h1.cardView.setLayoutParams(layoutParams);

                    ViewGroup.LayoutParams layoutParams2 = h1.imagen.getLayoutParams();
                    layoutParams2.height = (int) (layoutParams2.height * 0.8);
                    h1.imagen.setLayoutParams(layoutParams2);
                }

                BoxInfo box = boxesData.get(position);
                h1.titulo.setText(box.getTitle());
                Glide.with(h1.cardView)
                        .load(box.getImg())
                        .placeholder(R.drawable.img_asturias)
                        .into(h1.imagen);

                h1.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("YOOOOOOOO", "SOY CARDDD");
                    }
                });
                break;


        }
    }



    @Override
    public int getItemCount() {
        //numero de elementos que tiene el adapter
        return boxesData.size();
    }

}
