package es.ucm.fdi.boxit.presentacion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.CapsuleInfo;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class PredesignAdapter extends RecyclerView.Adapter<PredesignAdapter.DesingViewHolder>{
    private ArrayList<CapsuleInfo> data;
    private boolean isBox;
    private static final int IMAGE_WIDTH_KEY = 1;
    private static final int IMAGE_HEIGHT_KEY = 2;

    public void setData(ArrayList<CapsuleInfo> data, boolean isBox){
        this.data = data;
        this.isBox = isBox;
    }

    @NonNull
    @Override
    public PredesignAdapter.DesingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.predesigned_box_view, parent, false);
        return new PredesignAdapter.DesingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PredesignAdapter.DesingViewHolder holder, int position) {

        //En cualquier caso ponemos foto y nombre de usuario
        CapsuleInfo current = data.get(position);
        holder.nombre.setText(current.getTitle());

        Glide.with(holder.card)
                .load(current.getImg())
                .transform(new CenterCrop(), new RoundedCorners(20))
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            holder.imagen.setTag(IMAGE_WIDTH_KEY, resource.getIntrinsicWidth());
                            holder.imagen.setTag(IMAGE_HEIGHT_KEY, resource.getIntrinsicHeight());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                })
                .placeholder(R.drawable.default_image)
                .into(holder.imagen);

        if(isBox){
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    BoxInfo b = new BoxInfo("", current.getTitle(), current.getImg());

                    Intent intent = new Intent(ctx, CrearCajaForm.class);
                    intent.putExtra("DisingData", b);
                    ctx.startActivity(intent);

                }
            });
        }
        else {
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    Intent intent = new Intent(ctx, CrearCapsulaForm.class);
                    intent.putExtra("DisingData", current);
                    ctx.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class DesingViewHolder extends RecyclerView.ViewHolder{

        public ImageView imagen;
        public TextView nombre;
        public CardView card;
        public DesingViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imgPred);
            nombre = itemView.findViewById(R.id.namePred);
            card = itemView.findViewById(R.id.cardView);
        }
    }
}
