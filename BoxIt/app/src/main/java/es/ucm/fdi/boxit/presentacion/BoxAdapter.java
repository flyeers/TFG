package es.ucm.fdi.boxit.presentacion;



import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.negocio.BoxInfo;

public class BoxAdapter extends RecyclerView.Adapter{
    private static final int NORMAL_CARD = 1;
    private static final int ADD_CARD = 0;
    private ArrayList<BoxInfo> boxesData;
    private boolean small, addCard, isBox;
    private static final int IMAGE_WIDTH_KEY = 1;
    private static final int IMAGE_HEIGHT_KEY = 2;



    public void setBoxData(List<BoxInfo> data, boolean small, boolean addCard){
        this.boxesData = (ArrayList<BoxInfo>) data;
        this.small = small;
        this.addCard = addCard;
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

        if(addCard) return (position == 0) ? ADD_CARD : NORMAL_CARD;
        else return NORMAL_CARD;
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
                  if(!small){

                      ViewGroup.LayoutParams layoutParams = h2.addCard.getLayoutParams();
                      layoutParams.height = (int) (layoutParams.height / 0.8);
                      layoutParams.width = (int) (layoutParams.width / 0.8);
                      h2.addCard.setLayoutParams(layoutParams);

                      h2.addCard.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              isBox = true;
                              Context ctx = v.getContext();
                              Intent intent = new Intent(ctx, Crear.class);
                              intent.putExtra("TIPO", isBox);
                              ctx.startActivity(intent);

                          }
                      });
                  }
                  else{

                      h2.addCard.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              isBox = true;
                              Context ctx = v.getContext();
                              Intent intent = new Intent(ctx, Crear.class);
                              intent.putExtra("TIPO", isBox);
                              ctx.startActivity(intent);

                          }
                      });
                  }

                    break;
            case NORMAL_CARD:
                ViewHolder h1 = (ViewHolder) holder;
                if (!small) {
                    ViewGroup.LayoutParams layoutParams = h1.cardView.getLayoutParams();
                    layoutParams.height = (int) (layoutParams.height / 0.8);
                    layoutParams.width = (int) (layoutParams.width / 0.8);
                    h1.cardView.setLayoutParams(layoutParams);

                    ViewGroup.LayoutParams layoutParams2 = h1.imagen.getLayoutParams();
                    layoutParams2.height = (int) (layoutParams2.height / 0.8);
                    h1.imagen.setLayoutParams(layoutParams2);
                }

                BoxInfo box = boxesData.get(position);
                h1.titulo.setText(box.getTitle());
                Glide.with(h1.cardView)
                        .load(box.getImg())
                        .transform(new CenterCrop(), new RoundedCorners(20))
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }


                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                try {
                                    h1.imagen.setTag(IMAGE_WIDTH_KEY, resource.getIntrinsicWidth());
                                    h1.imagen.setTag(IMAGE_HEIGHT_KEY, resource.getIntrinsicHeight());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        })
                        .placeholder(R.drawable.default_image)
                        .into(h1.imagen);

                h1.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BoxInfo selectedBox = boxesData.get(holder.getAdapterPosition());
                        Context ctx = v.getContext();
                        Intent intent = new Intent(ctx, Caja.class);
                        intent.putExtra("boxInfo", selectedBox);
                        ctx.startActivity(intent);

                    }
                });

                if(!box.getColaborators().isEmpty()) //hay colaboradores cambio color
                    h1.cardView.setCardBackgroundColor(ContextCompat.getColor( h1.cardView.getContext(), R.color.amarilloColab));


                break;
        }
    }



    @Override
    public int getItemCount() {
        //numero de elementos que tiene el adapter
        return boxesData.size();
    }

}
