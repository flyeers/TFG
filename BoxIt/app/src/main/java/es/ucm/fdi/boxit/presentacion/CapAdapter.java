package es.ucm.fdi.boxit.presentacion;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.CapsuleInfo;

public class CapAdapter extends RecyclerView.Adapter{


    private static final int NORMAL_CARD = 1;
    private static final int ADD_CARD = 0;
    private ArrayList<CapsuleInfo> capsData;
    private boolean small, addCard, isBox;
    private static final int IMAGE_WIDTH_KEY = 1;
    private static final int IMAGE_HEIGHT_KEY = 2;



    public void setCapData(List<CapsuleInfo> data, boolean small, boolean addCard){
        this.capsData = (ArrayList<CapsuleInfo>) data;
        this.small = small;
        this.addCard = addCard;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo, time;
        private ImageView imagen, simbol;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);

            titulo = view.findViewById(R.id.nameCap);
            time = view.findViewById(R.id.timeCap);
            imagen = view.findViewById(R.id.imgCap);
            simbol = view.findViewById(R.id.imgSimbol);
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
        if(addCard) return (position == 0) ? ADD_CARD : NORMAL_CARD;
        else return NORMAL_CARD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType){
            case ADD_CARD:
                v = inflater.inflate(R.layout.add_view,parent,false);
                return new CapAdapter.AddViewHolder(v);
            case NORMAL_CARD:
                v = inflater.inflate(R.layout.cap_view, parent, false);
                return new CapAdapter.ViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ADD_CARD:
                CapAdapter.AddViewHolder h2 = (CapAdapter.AddViewHolder) holder;
                if(small){

                    ViewGroup.LayoutParams layoutParams = h2.addCard.getLayoutParams();
                    layoutParams.height = (int) (layoutParams.height * 0.8);
                    layoutParams.width = (int) (layoutParams.width * 0.8);
                    h2.addCard.setLayoutParams(layoutParams);

                    h2.addCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isBox = false;
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
                            isBox = false;
                            Context ctx = v.getContext();
                            Intent intent = new Intent(ctx, Crear.class);
                            intent.putExtra("TIPO", isBox);
                            ctx.startActivity(intent);

                        }
                    });
                }

                break;
            case NORMAL_CARD:
                CapAdapter.ViewHolder h1 = (CapAdapter.ViewHolder) holder;
                if (small) {
                    ViewGroup.LayoutParams layoutParams = h1.cardView.getLayoutParams();
                    layoutParams.height = (int) (layoutParams.height * 0.8);
                    layoutParams.width = (int) (layoutParams.width * 0.8);
                    h1.cardView.setLayoutParams(layoutParams);

                    ViewGroup.LayoutParams layoutParams2 = h1.imagen.getLayoutParams();
                    layoutParams2.height = (int) (layoutParams2.height * 0.8);
                    h1.imagen.setLayoutParams(layoutParams2);
                }

                CapsuleInfo cap = capsData.get(position);
                h1.titulo.setText(cap.getTitle());
                Glide.with(h1.cardView)
                        .load(cap.getImg())
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

                //Evaluamos las fechas de apertura y cierre para ver el estado de la capsula
                Date cd = Calendar.getInstance().getTime();
                boolean isAbierta = true;
                if(cd.before(cap.getCierre())){//antes del cierre

                    Long d = cap.getApertura().getTime() - cd.getTime();

                    long dias = TimeUnit.MILLISECONDS.toDays(d);
                    long horas = TimeUnit.MILLISECONDS.toHours(d) % 24;
                    long minutos = TimeUnit.MILLISECONDS.toMinutes(d) % 60;
                    h1.time.setText(dias+"D. "+horas+"H. "+minutos+"M. ");

                    h1.simbol.setImageResource(R.drawable.lock_open);

                }else if(cd.after(cap.getApertura())){//tras apertura
                    h1.simbol.setImageResource(R.drawable.lock_open);

                }else{//cerrada
                    isAbierta = false;
                    Long d = cap.getApertura().getTime() - cd.getTime();

                    long dias = TimeUnit.MILLISECONDS.toDays(d);
                    long horas = TimeUnit.MILLISECONDS.toHours(d) % 24;
                    long minutos = TimeUnit.MILLISECONDS.toMinutes(d) % 60;
                    h1.time.setText(dias+"D. "+horas+"H. "+minutos+"M. ");

                    h1.simbol.setImageResource(R.drawable.lock_close);
                }

                if(isAbierta){
                    h1.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CapsuleInfo selectedCap = capsData.get(holder.getAdapterPosition());
                            Context ctx = v.getContext();
                            Intent intent = new Intent(ctx, Capsula.class);
                            intent.putExtra("capsuleInfo", selectedCap);
                            ctx.startActivity(intent);

                        }
                    });
                }

                if(!cap.getColaborators().isEmpty()) //hay colaboradores cambio color
                    h1.cardView.setCardBackgroundColor(ContextCompat.getColor( h1.cardView.getContext(), R.color.amarilloColab));


                break;
        }
    }

    @Override
    public int getItemCount() {
        return capsData.size();
    }
}
