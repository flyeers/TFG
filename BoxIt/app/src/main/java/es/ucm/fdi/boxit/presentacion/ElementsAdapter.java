package es.ucm.fdi.boxit.presentacion;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import es.ucm.fdi.boxit.negocio.SABox;

public class ElementsAdapter extends RecyclerView.Adapter {

    private ArrayList<String> itemsData;
    private boolean photo, doc;
    private String boxId;


    private static final int IMAGE_WIDTH_KEY = 1;
    private static final int IMAGE_HEIGHT_KEY = 2;


    private Context ctx;

    public void setElementsData(List<String> data, boolean photo, boolean doc, Context ctx, String boxId){
        this.itemsData = (ArrayList<String>) data;
        this.photo = photo;
        this.doc = doc;
        this.ctx = ctx;
        this.boxId = boxId;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView imagen;
        private CardView cardView;
        private TextView fileName;

        public ViewHolder(View view) {
            super(view);


            imagen = view.findViewById(R.id.photoBox);
            cardView=  view.findViewById(R.id.card_amigo);
            fileName = view.findViewById(R.id.nombreDoc);

        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Crea una nueva vista
        //viewType podemos cargar distintos tipos -> con getitemviewtipe
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = null;

        if(photo){
            v = inflater.inflate(R.layout.photo_view,parent,false);
        } else if (doc) {
            v = inflater.inflate(R.layout.document_view,parent,false);
        }

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        ElementsAdapter.ViewHolder h1 = (ElementsAdapter.ViewHolder) holder;
        if(photo){
            Uri img = Uri.parse(itemsData.get(position));

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

            h1.imagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarImagen(img.toString());
                }
            });
        }
        else{

            String name = itemsData.get(position);

            h1.fileName.setText(getNombre(name));

            h1.fileName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PDFView.abrirVisorPDF(ctx, name);
                }
            });
        }


    }

    public String getNombre(String name){
        int indiceUltimaBarra = name.lastIndexOf("/");

        int indiceUltimoPunto = name.lastIndexOf(".");


        if(indiceUltimaBarra != -1 && indiceUltimoPunto != -1 && indiceUltimoPunto > indiceUltimaBarra) {

            String nombre = name.substring(indiceUltimaBarra + 1, indiceUltimoPunto);
            return nombre;
        }
        else {

            return "Desconocido";
        }
    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    private void mostrarImagen(String i) {

        Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.photo_preview);

        Button borrarFoto = dialog.findViewById(R.id.eliminarFoto);

        borrarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialogConfirm = new Dialog(ctx);
                dialogConfirm.setContentView(R.layout.eliminar_confirm);
                Button cancelar = dialogConfirm.findViewById(R.id.buttonCancelar);
                Button confirmar = dialogConfirm.findViewById(R.id.buttonEliminar);

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConfirm.dismiss();
                        dialog.dismiss();
                    }
                });

                confirmar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SABox saBox = new SABox();
                        saBox.deletePhoto(boxId, i);
                        dialogConfirm.dismiss();
                        dialog.dismiss();

                    }
                });

                dialogConfirm.show();
            }
        });

        ImageView dialogImageView = dialog.findViewById(R.id.modalImageView);

        Glide.with(ctx)
                .load(i)
                .into(dialogImageView);


        dialog.show();
    }

    public void addElem(String newElem, String filename) {

        itemsData.add(newElem);

        notifyDataSetChanged();
    }


}

