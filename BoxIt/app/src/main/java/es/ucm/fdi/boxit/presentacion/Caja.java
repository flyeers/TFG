package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.SABox;

public class Caja extends AppCompatActivity {

    private Button add, borrarFoto;
    private TextView nombre, fotos, musica, notas, documentos, audio, textoFotos1, textoFotos2, textoInicio, verTodo;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int PICK_PDF_REQUEST_CODE = 2;
    private static final int RESULT_OK = -1;
    private Context ctx;

    private BoxInfo boxInfo;
    private String imagePath;
    private ElementsAdapter photoAdapter, docAdapter, noteAdapter;

    private List<String> documents_b, photos_b, notes_b;

    private android.net.Uri selectedItem = null;

    private int numfotos = 0;

    private boolean fotoPulsado, docPulsado, notasPulsado;
    private ImageView home, delete, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja);

        ctx = this;
        add = findViewById(R.id.buttonAdd2);
        add.setBackgroundColor(getResources().getColor(R.color.rosaBoton));

        fotoPulsado = false;
        docPulsado = false;
        notasPulsado = false;

        photos_b = new ArrayList<>();
        documents_b = new ArrayList<>();
        notes_b = new ArrayList<>();

        boxInfo = getIntent().getParcelableExtra("boxInfo");

        nombre = findViewById(R.id.nombre_caja);
        nombre.setText(boxInfo.getTitle());

        fotos = findViewById(R.id.fotosCaja);
        musica = findViewById(R.id.musicaCaja);
        notas = findViewById(R.id.notasCaja);
        textoFotos2 = findViewById(R.id.fdelacaja);
        textoFotos1 = findViewById(R.id.fotosdelacaja);
        textoInicio = findViewById(R.id.todoElContenidoCaja);
        verTodo = findViewById(R.id.vertodo);
        documentos = findViewById(R.id.documentosCaja);

        home = findViewById(R.id.homeBtn);
        delete = findViewById(R.id.deleteBox);
        exit = findViewById(R.id.exit);


        docAdapter = new ElementsAdapter();
        photoAdapter = new ElementsAdapter();
        noteAdapter = new ElementsAdapter();


        textoInicio.setText(getResources().getString(R.string.tododelacaja));
        getAll();


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(ctx, MainActivity.class);
                ctx.startActivity(intent1);


            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
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

                    }
                });

                confirmar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SABox saBox = new SABox();
                        saBox.deleteBox(boxInfo.getId(), boxInfo.getTitle(), true, new Callbacks() {
                            @Override
                            public void onCallbackExito(Boolean exito) {
                                if(exito){
                                    Intent intent1 = new Intent(ctx, MainActivity.class);
                                    ctx.startActivity(intent1);
                                    Toast.makeText(ctx,R.string.deleteBien , Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    dialogConfirm.dismiss();
                                    Toast.makeText(ctx,R.string.deleteMal , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });

                dialogConfirm.show();
            }
        });

        if(!boxInfo.getColaborators().isEmpty()){
            exit.setVisibility(View.VISIBLE);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialogConfirm = new Dialog(ctx);
                    dialogConfirm.setContentView(R.layout.exit_confirm);
                    TextView text = dialogConfirm.findViewById(R.id.textAbandono);
                    text.setText(getResources().getString(R.string.abandonarBox));
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
                            saBox.exitBox(boxInfo.getId(), new Callbacks() {
                                @Override
                                public void onCallbackExito(Boolean exito) {
                                    if(exito){
                                        Intent intent1 = new Intent(ctx, MainActivity.class);
                                        ctx.startActivity(intent1);
                                        Toast.makeText(ctx,R.string.abandonoBien , Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        dialogConfirm.dismiss();
                                        Toast.makeText(ctx,R.string.abandonoMal , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                    dialogConfirm.show();
                }
            });
        }

        verTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fotoPulsado){
                    fotoPulsado = false;
                    fotos.setBackgroundResource(android.R.color.transparent);
                    fotos.setTextColor(getResources().getColor(R.color.rosaBoton));
                    textoFotos1.setText("");
                    textoFotos2.setText("");
                }
                if (docPulsado){
                    docPulsado = false;
                    documentos.setBackgroundResource(android.R.color.transparent);
                    documentos.setTextColor(getResources().getColor(R.color.rosaBoton));
                    textoFotos2.setText("");
                    textoFotos1.setText("");
                }
                if(notasPulsado){
                    notasPulsado = false;
                    notas.setBackgroundResource(android.R.color.transparent);
                    notas.setTextColor(getResources().getColor(R.color.rosaBoton));
                    textoFotos2.setText("");
                    textoFotos1.setText("");
                }

                findViewById(R.id.recyclerdocsCaja).setVisibility(View.VISIBLE);
                findViewById(R.id.recyclerfotosCaja).setVisibility(View.VISIBLE);
                findViewById(R.id.recyclernotasCaja).setVisibility(View.VISIBLE);
            }
        });

        fotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!fotoPulsado){
                    fotoPulsado = true;

                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setCornerRadius(20);
                    drawable.setColor(getResources().getColor(R.color.rosaBoton));

                    fotos.setBackground(drawable);


                    fotos.setTextColor(getResources().getColor(R.color.fondoClaro));
                    findViewById(R.id.recyclerfotosCaja).setVisibility(View.VISIBLE);
                    textoFotos1.setText(getResources().getString(R.string.galeria));
                    textoFotos2.setText(getResources().getString(R.string.delacaja));
                    photoAdapter.setElementsData(photos_b, true, false, false, ctx, boxInfo);
                    RecyclerView recyclerView = findViewById(R.id.recyclerfotosCaja);
                    recyclerView.setAdapter(photoAdapter);
                }
                else{
                    fotoPulsado = false;
                    fotos.setBackgroundResource(android.R.color.transparent);
                    fotos.setTextColor(getResources().getColor(R.color.rosaBoton));
                    findViewById(R.id.recyclerfotosCaja).setVisibility(View.GONE);
                    textoFotos1.setText("");
                    textoFotos2.setText("");

                }

                if(docPulsado){
                    docPulsado = false;
                    documentos.setBackgroundResource(android.R.color.transparent);
                    documentos.setTextColor(getResources().getColor(R.color.rosaBoton));

                }
                if(notasPulsado){
                    notasPulsado = false;
                    notas.setBackgroundResource(android.R.color.transparent);
                    notas.setTextColor(getResources().getColor(R.color.rosaBoton));

                }

                findViewById(R.id.recyclernotasCaja).setVisibility(View.GONE);
                findViewById(R.id.recyclerdocsCaja).setVisibility(View.GONE);

            }
        });

        documentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!docPulsado){
                    docPulsado = true;
                    findViewById(R.id.recyclerdocsCaja).setVisibility(View.VISIBLE);


                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setCornerRadius(20);
                    drawable.setColor(getResources().getColor(R.color.rosaBoton));

                    documentos.setBackground(drawable);
                    documentos.setTextColor(getResources().getColor(R.color.fondoClaro));

                    textoFotos1.setText(getResources().getString(R.string.docs));
                    textoFotos2.setText(getResources().getString(R.string.delacaja));

                    docAdapter.setElementsData(documents_b, false, true, false, ctx, boxInfo);
                    RecyclerView recyclerView = findViewById(R.id.recyclerdocsCaja);
                    recyclerView.setAdapter(docAdapter);
                }
                else{
                    docPulsado = false;
                    documentos.setBackgroundResource(android.R.color.transparent);
                    documentos.setTextColor(getResources().getColor(R.color.rosaBoton));

                    findViewById(R.id.recyclerdocsCaja).setVisibility(View.GONE);
                    textoFotos2.setText("");
                    textoFotos1.setText("");

                }

                if(fotoPulsado){
                    fotoPulsado = false;
                    fotos.setBackgroundResource(android.R.color.transparent);
                    fotos.setTextColor(getResources().getColor(R.color.rosaBoton));

                }
                if(notasPulsado){
                    notasPulsado = false;
                    notas.setBackgroundResource(android.R.color.transparent);
                    notas.setTextColor(getResources().getColor(R.color.rosaBoton));

                }

                findViewById(R.id.recyclernotasCaja).setVisibility(View.GONE);
                findViewById(R.id.recyclerfotosCaja).setVisibility(View.GONE);


            }
        });


        notas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!notasPulsado){
                    notasPulsado = true;
                    findViewById(R.id.recyclernotasCaja).setVisibility(View.VISIBLE);

                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setCornerRadius(20);
                    drawable.setColor(getResources().getColor(R.color.rosaBoton));

                    notas.setBackground(drawable);
                    notas.setTextColor(getResources().getColor(R.color.fondoClaro));

                    textoFotos1.setText(getResources().getString(R.string.notas));
                    textoFotos2.setText(getResources().getString(R.string.delacaja));

                    //noteAdapter.setElementsData(notes_b, false, false, true, ctx, boxInfo);
                    RecyclerView recyclerView = findViewById(R.id.recyclernotasCaja);
                    recyclerView.setAdapter(noteAdapter);
                }
                else{
                    notasPulsado = false;
                    notas.setBackgroundResource(android.R.color.transparent);
                    notas.setTextColor(getResources().getColor(R.color.rosaBoton));

                    findViewById(R.id.recyclernotasCaja).setVisibility(View.GONE);
                    textoFotos2.setText("");
                    textoFotos1.setText("");

                }

                if(fotoPulsado){
                    fotoPulsado = false;
                    fotos.setBackgroundResource(android.R.color.transparent);
                    fotos.setTextColor(getResources().getColor(R.color.rosaBoton));

                }
                if(docPulsado){
                    docPulsado = false;
                    documentos.setBackgroundResource(android.R.color.transparent);
                    documentos.setTextColor(getResources().getColor(R.color.rosaBoton));

                }

                findViewById(R.id.recyclerdocsCaja).setVisibility(View.GONE);
                findViewById(R.id.recyclerfotosCaja).setVisibility(View.GONE);


            }
        });



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(ctx, R.style.EstiloMenu);
                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.add_menu_elementos, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int id = item.getItemId();
                        if(id == R.id.addCamara){
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);


                            return true;
                        }
                        else if(id == R.id.addGaleria){
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);


                            return true;
                        }
                        else if(id == R.id.addMusic){


                            return true;
                        }
                        else if(id == R.id.addDoc){

                            Intent docsIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            docsIntent.setType("application/pdf"); // Seleccionar solo archivos de tipo PDF
                            docsIntent.addCategory(Intent.CATEGORY_OPENABLE);


                            startActivityForResult(docsIntent, PICK_PDF_REQUEST_CODE);
                            return true;
                        }
                        else if(id == R.id.addAudio){ //TODO CAMBIAR (lo dejo asi por si luego cambia músiaca)

                            Dialog dialogNote = new Dialog(ctx);
                            dialogNote.setContentView(R.layout.note_preview);
                            Button cancelar = dialogNote.findViewById(R.id.buttonCancelar);
                            Button confirmar = dialogNote.findViewById(R.id.buttonAceptar);
                            EditText textNote = dialogNote.findViewById(R.id.textNote);

                            cancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogNote.dismiss();
                                }
                            });

                            confirmar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String note = String.valueOf(textNote.getText());

                                    /*SABox saBox = new SABox();
                                    saBox.addNote(boxInfo.getId(), new Callbacks() {
                                        @Override
                                        public void onCallbackExito(Boolean exito) {
                                            if(exito){

                                                //TODO METER EN EL ADAPTER
                                                noteAdapter.addElem(note, null);
                                                noteAdapter.notifyDataSetChanged();
                                                dialogNote.dismiss();
                                                Toast.makeText(ctx,R.string.addBien , Toast.LENGTH_SHORT).show();

                                            }
                                            else{
                                                dialogNote.dismiss();
                                                Toast.makeText(ctx,R.string.addMal , Toast.LENGTH_SHORT).show();                                            }
                                        }
                                    });*/
                                    noteAdapter.addElem(note, null);
                                    noteAdapter.notifyDataSetChanged();
                                    dialogNote.dismiss();
                                }
                            });
                            dialogNote.show();

                            return true;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        SABox saBox = new SABox();

        //si lo añadido es una foto, ya sea por camara o por galeria:
        if(requestCode == PICK_IMAGE_REQUEST || requestCode == CAMERA_REQUEST_CODE ){
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data.getData() != null) {

                selectedItem = data.getData();
                saBox.addPhotos(boxInfo, selectedItem.toString(), true, new Callbacks() {
                    @Override
                    public void onCallbackExito(Boolean exito) {
                        if(exito){

                            photoAdapter.addElem(selectedItem.toString(), null);
                            photoAdapter.notifyDataSetChanged();
                            Toast.makeText(ctx,R.string.addBien , Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(ctx,R.string.addMal , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");



                saBox.addPhotosFromCamera(boxInfo, imageBitmap, true, new Callbacks() {
                    @Override
                    public void onCallbackExito(Boolean exito) {
                        if(exito){

                            saBox.getPhotos(boxInfo.getId(), true, new Callbacks() {
                                @Override
                                public void onCallbackItems(ArrayList<String> photos) {
                                    photoAdapter.setElementsData(photos, true, false, false, ctx, boxInfo);
                                    RecyclerView recyclerView = findViewById(R.id.recyclerfotosCaja);
                                    recyclerView.setAdapter(photoAdapter);

                                }
                            });

                            Toast.makeText(ctx,R.string.addBien , Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ctx,R.string.addMal , Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }


        }
        //si lo añadido es un doc
         else if (requestCode == PICK_PDF_REQUEST_CODE && resultCode == RESULT_OK) {

            selectedItem = data.getData();
            String fileName = getFileName(selectedItem);

            saBox.addDocs(boxInfo, selectedItem.toString(), fileName, true, new Callbacks() {
                @Override
                public void onCallbackExito(Boolean exito) {
                    if(exito){

                        saBox.getDocs(boxInfo.getId(), true, new Callbacks() {
                            @Override
                            public void onCallbackItems(ArrayList<String> items) {
                                docAdapter.setElementsData(items, false, true, false, ctx, boxInfo);
                                RecyclerView recyclerView = findViewById(R.id.recyclerdocsCaja);
                                recyclerView.setAdapter(docAdapter);
                            }
                        });
                        Toast.makeText(ctx,R.string.addBien , Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ctx,R.string.addMal , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    public void getAll(){
        SABox saBox = new SABox();

        saBox.getDocs(boxInfo.getId(), true, new Callbacks() {
            @Override
            public void onCallbackItems(ArrayList<String> docs) {
                documents_b = docs;
                docAdapter.setElementsData(documents_b, false, true, false, ctx, boxInfo);
                RecyclerView recyclerView = findViewById(R.id.recyclerdocsCaja);
                recyclerView.setAdapter(docAdapter);
            }
        });
       saBox.getPhotos(boxInfo.getId(), true, new Callbacks() {
            @Override
            public void onCallbackItems(ArrayList<String> photos) {

                photos_b = photos;
                photoAdapter.setElementsData(photos_b, true, false, false, ctx, boxInfo);
                RecyclerView recyclerView = findViewById(R.id.recyclerfotosCaja);
                recyclerView.setAdapter(photoAdapter);

            }
        });

       notes_b.add("NOTA 1");
       notes_b.add("aipsdjfao s9ausdf90uasdf90uaoiusf doiuas auf9ua0s9fu ufa0we uf9oaipufaiohji");
        noteAdapter.setElementsData(notes_b, false, false, true, ctx, boxInfo);
        RecyclerView recyclerView = findViewById(R.id.recyclernotasCaja);
        recyclerView.setAdapter(noteAdapter);

       /*saBox.getNotes(boxInfo.getId(), new Callbacks() {
            @Override
            public void onCallbackItems(ArrayList<String> notes) {
                notes_b = notes;
                noteAdapter.setElementsData(notes_b, false, false, true, ctx, boxInfo);
                RecyclerView recyclerView = findViewById(R.id.recyclernotasCaja);
                recyclerView.setAdapter(noteAdapter);
            }
       });*/

    }



    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndexOrThrow("_display_name");
                    result = cursor.getString(index);
                }
            }
        } else if (uri.getScheme().equals("file")) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}