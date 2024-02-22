package es.ucm.fdi.boxit.presentacion;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.SABox;
import es.ucm.fdi.boxit.negocio.SACapsule;

public class ElementsAdapter extends RecyclerView.Adapter {

    private ArrayList<String> itemsData;
    private boolean photo, doc, note, isBox;
    private String boxId;
    private BoxInfo box;


    private static final int IMAGE_WIDTH_KEY = 1;
    private static final int IMAGE_HEIGHT_KEY = 2;

    private final String NOTE_IDENTIFIER ="///noteIdentifier///";

    private Context ctx;

    public void setElementsData(List<String> data, boolean photo, boolean doc, boolean note, Context ctx, BoxInfo box){
        this.itemsData = (ArrayList<String>) data;
        this.photo = photo;
        this.doc = doc;
        this.note = note;
        this.ctx = ctx;
        this.boxId = box.getId();
        this.box = box;
        isBox = true;
    }

    public void setType(boolean notBox){
        this.isBox = notBox;
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
        } else if (note) {
            v = inflater.inflate(R.layout.note_view,parent,false);
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

            h1.imagen.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Dialog dialogConfirm = new Dialog(ctx);
                    dialogConfirm.setContentView(R.layout.eliminar_confirm);
                    Button cancelar = dialogConfirm.findViewById(R.id.buttonCancelar);
                    Button confirmar = dialogConfirm.findViewById(R.id.buttonEliminar);

                    cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirm.dismiss();

                        }
                    });

                    confirmar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //if(isBox){
                                SABox saBox = new SABox();
                                saBox.deletePhoto(boxId, img.toString(), isBox, new Callbacks() {
                                    @Override
                                    public void onCallbackExito(Boolean exito) {
                                        if(exito){

                                            int pos = itemsData.indexOf(img.toString());
                                            if(pos != -1){
                                                itemsData.remove(pos);
                                                notifyDataSetChanged();
                                            }
                                            dialogConfirm.dismiss();

                                            Toast.makeText(ctx,R.string.deleteBien , Toast.LENGTH_SHORT).show();


                                        }
                                        else{
                                            Toast.makeText(ctx,R.string.deleteMal , Toast.LENGTH_SHORT).show();
                                            dialogConfirm.dismiss();

                                        }
                                    }
                                });
                            /*}
                            else{ TODO BORRA
                                SACapsule saCapsule = new SACapsule();
                                saCapsule.deletePhoto(boxId, img.toString(), new Callbacks() {
                                    @Override
                                    public void onCallbackExito(Boolean exito) {
                                        if(exito){
                                            int pos = itemsData.indexOf(img.toString());
                                            if(pos != -1){
                                                itemsData.remove(pos);
                                                notifyDataSetChanged();
                                            }
                                            Toast.makeText(ctx,R.string.deleteBien , Toast.LENGTH_SHORT).show();
                                            dialogConfirm.dismiss();

                                        }
                                        else{
                                            Toast.makeText(ctx,R.string.deleteMal , Toast.LENGTH_SHORT).show();
                                            dialogConfirm.dismiss();

                                        }
                                    }
                                });
                            }*/
                        }
                    });

                    dialogConfirm.show();

                    return false;
                }
            });

        }

        else if(doc){

            String name = itemsData.get(position);

            h1.fileName.setText(getNombre(name));

            h1.fileName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PDFView.abrirVisorPDF(ctx, name);
                }
            });

            h1.fileName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    Dialog dialogConfirm = new Dialog(ctx);
                    dialogConfirm.setContentView(R.layout.eliminar_confirm);
                    Button cancelar = dialogConfirm.findViewById(R.id.buttonCancelar);
                    Button confirmar = dialogConfirm.findViewById(R.id.buttonEliminar);

                    cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirm.dismiss();

                        }
                    });

                    confirmar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SABox saBox = new SABox();
                            saBox.deleteDoc(boxId, name, isBox, new Callbacks() {
                                @Override
                                public void onCallbackExito(Boolean exito) {
                                    if(exito){
                                        Log.d("CLAU", "TODO BIEN");
                                        int pos = itemsData.indexOf(name);
                                        if(pos != -1){
                                            itemsData.remove(pos);
                                            notifyDataSetChanged();
                                        }
                                        Toast.makeText(ctx,R.string.deleteBien , Toast.LENGTH_SHORT).show();
                                        dialogConfirm.dismiss();
                                    }
                                    else{
                                        Log.d("CLAU", "TODO MAL");
                                        Toast.makeText(ctx,R.string.deleteMal , Toast.LENGTH_SHORT).show();
                                        dialogConfirm.dismiss();
                                    }
                                }
                            });
                        }


                    });

                    dialogConfirm.show();
                    return false;
                }
            });
        }
        else if (note) {
            String noteId = itemsData.get(position);

            int endIndex = noteId.indexOf(NOTE_IDENTIFIER) + NOTE_IDENTIFIER.length();
            String noteText = noteId.substring(endIndex);
            h1.fileName.setText(noteText);

            h1.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarNote(noteId);
                }
            });

            h1.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    Dialog dialogConfirm = new Dialog(ctx);
                    dialogConfirm.setContentView(R.layout.eliminar_confirm);
                    Button cancelar = dialogConfirm.findViewById(R.id.buttonCancelar);
                    Button confirmar = dialogConfirm.findViewById(R.id.buttonEliminar);

                    cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirm.dismiss();
                        }
                    });

                    confirmar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SABox saBox = new SABox();
                            saBox.deleteNote(boxId, noteId, isBox, new Callbacks() {
                                @Override
                                public void onCallbackExito(Boolean exito) {
                                    if(exito){
                                        int pos = itemsData.indexOf(noteId);
                                        if(pos != -1){
                                            itemsData.remove(pos);
                                            notifyDataSetChanged();
                                        }
                                        Toast.makeText(ctx,R.string.deleteBien , Toast.LENGTH_SHORT).show();
                                        dialogConfirm.dismiss();
                                    }
                                    else{
                                        Toast.makeText(ctx,R.string.deleteMal , Toast.LENGTH_SHORT).show();
                                        dialogConfirm.dismiss();
                                    }
                                }
                            });
                        }


                    });

                    dialogConfirm.show();
                    return false;
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

    public void addElem(String newElem, String filename) {

        itemsData.add(newElem);
        notifyDataSetChanged();
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
                        //if(isBox){
                            SABox saBox = new SABox();
                            saBox.deletePhoto(boxId, i, isBox, new Callbacks() {
                                @Override
                                public void onCallbackExito(Boolean exito) {
                                    if(exito){

                                        int pos = itemsData.indexOf(i);
                                        if(pos != -1){
                                            itemsData.remove(pos);
                                            notifyDataSetChanged();
                                        }
                                        dialogConfirm.dismiss();
                                        dialog.dismiss();
                                        Toast.makeText(ctx,R.string.deleteBien , Toast.LENGTH_SHORT).show();


                                    }
                                    else{
                                        Toast.makeText(ctx,R.string.deleteMal , Toast.LENGTH_SHORT).show();
                                        dialogConfirm.dismiss();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        /*}else{
                            SACapsule saCapsule = new SACapsule();
                            saCapsule.deletePhoto(boxId, i, new Callbacks() {
                                @Override
                                public void onCallbackExito(Boolean exito) {
                                    if(exito){
                                        int pos = itemsData.indexOf(i);
                                        if(pos != -1){
                                            itemsData.remove(pos);
                                            notifyDataSetChanged();
                                        }
                                        dialogConfirm.dismiss();
                                        dialog.dismiss();
                                        Toast.makeText(ctx,R.string.deleteBien , Toast.LENGTH_SHORT).show();

                                    }
                                    else{
                                        Toast.makeText(ctx,R.string.deleteMal , Toast.LENGTH_SHORT).show();
                                        dialogConfirm.dismiss();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }*/
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

    private void mostrarNote(String noteId){

        int endeIndex = noteId.indexOf(NOTE_IDENTIFIER) + NOTE_IDENTIFIER.length();

        String noteText = noteId.substring(endeIndex);
        String preId = noteId.substring(0, endeIndex);

        Dialog dialogNote = new Dialog(ctx);
        dialogNote.setContentView(R.layout.note_preview);
        Button cancelar = dialogNote.findViewById(R.id.buttonCancelar);
        Button confirmar = dialogNote.findViewById(R.id.buttonAceptar);
        EditText textFile = dialogNote.findViewById(R.id.textNote);
        textFile.setText(noteText);
        confirmar.setText(ctx.getString(R.string.actualizar));

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNote.dismiss();
            }
        });

        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTextNew = String.valueOf(textFile.getText());
                String noteIdNew = String.format("%s%s", preId, noteTextNew);

                SABox saBox = new SABox();
                saBox.updateNote(boxId, noteId, noteIdNew, true, new Callbacks() {
                    @Override
                    public void onCallbackExito(Boolean exito) {
                        if(exito){
                            int pos = itemsData.indexOf(noteId);
                            itemsData.set(pos, noteIdNew);
                            notifyDataSetChanged();
                            dialogNote.dismiss();
                            Toast.makeText(ctx,R.string.editarBien , Toast.LENGTH_SHORT).show();
                        }
                        else{
                            dialogNote.dismiss();
                            Toast.makeText(ctx,R.string.editarMal , Toast.LENGTH_SHORT).show();                                            }
                    }
                });
            }
        });
        dialogNote.show();

    }



}

