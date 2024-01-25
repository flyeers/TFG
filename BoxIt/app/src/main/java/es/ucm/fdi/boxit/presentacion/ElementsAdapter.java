package es.ucm.fdi.boxit.presentacion;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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

public class ElementsAdapter extends RecyclerView.Adapter {

    private ArrayList<String> itemsData;
    private boolean photo;

    private static final int IMAGE_WIDTH_KEY = 1;
    private static final int IMAGE_HEIGHT_KEY = 2;

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
           /* Glide.with(h1.cardView)
                    .load(img)
                    .placeholder(R.drawable.default_image)
                    .into(h1.imagen);*/

            Glide.with(h1.cardView)
                    .load(img)
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
        }


    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}

