package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.SABox;

public class Caja extends AppCompatActivity {

    private Button add;
    private TextView nombre, fotos, musica, documentos, audio, textoFotos1, textoFotos2, textoDoc1, textoDoc2, textoInicio;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int PICK_PDF_REQUEST_CODE = 2;
    private static final int RESULT_OK = -1;
    private Context ctx;

    private BoxInfo boxInfo;
    private String imagePath;
    private ElementsAdapter elementsAdapter;

    private List<String> documents_b, photos_b;

    private android.net.Uri selectedItem = null;


    private boolean fotoPulsado, docPulsado;
    private ImageView home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja);

        ctx = this;
        add = findViewById(R.id.buttonAdd2);
        add.setBackgroundColor(getResources().getColor(R.color.rosaBoton));


        fotoPulsado = false;
        docPulsado = false;

        photos_b = new ArrayList<>();
        documents_b = new ArrayList<>();

        boxInfo = getIntent().getParcelableExtra("boxInfo");


        nombre = findViewById(R.id.nombre_caja);
        nombre.setText(boxInfo.getTitle());

        fotos = findViewById(R.id.fotosCaja);
        musica = findViewById(R.id.musicaCaja);
        textoFotos2 = findViewById(R.id.fdelacaja);
        textoFotos1 = findViewById(R.id.fotosdelacaja);
        textoInicio = findViewById(R.id.todoElContenidoCaja);

        musica = findViewById(R.id.musicaCaja);
        textoFotos2 = findViewById(R.id.fdelacaja);
        textoFotos1 = findViewById(R.id.fotosdelacaja);

        documentos = findViewById(R.id.documentosCaja);
        textoDoc1 = findViewById(R.id.docsdelacaja);
        textoDoc2 = findViewById(R.id.ddelacaja);
        home = findViewById(R.id.homeBtn);


        SABox saBox = new SABox();
        elementsAdapter = new ElementsAdapter();


        textoInicio.setText(getResources().getString(R.string.tododelacaja));
        getAll();





        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(ctx, MainActivity.class);
                ctx.startActivity(intent1);

                //TODO revisar si cuando viene de la main deberia de ser un onBackPressed() en lugar de crear un intent nuevo

            }
        });

        fotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!fotoPulsado){
                    fotoPulsado = true;
                    fotos.setBackgroundColor(getResources().getColor(R.color.rosaBoton));
                    fotos.setTextColor(getResources().getColor(R.color.fondoClaro));
                    findViewById(R.id.recyclerfotosCaja).setVisibility(View.VISIBLE);
                    textoFotos1.setText(getResources().getString(R.string.galeria));
                    textoFotos2.setText(getResources().getString(R.string.delacaja));
                    elementsAdapter.setElementsData(photos_b, true, false, ctx);
                    RecyclerView recyclerView = findViewById(R.id.recyclerfotosCaja);
                    recyclerView.setAdapter(elementsAdapter);
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

                findViewById(R.id.recyclerdocsCaja).setVisibility(View.GONE);




            }
        });

        documentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(!docPulsado){
                    docPulsado = true;
                    findViewById(R.id.recyclerdocsCaja).setVisibility(View.VISIBLE);
                    documentos.setBackgroundColor(getResources().getColor(R.color.rosaBoton));
                    documentos.setTextColor(getResources().getColor(R.color.fondoClaro));

                    textoFotos1.setText(getResources().getString(R.string.docs));
                    textoFotos2.setText(getResources().getString(R.string.delacaja));

                    elementsAdapter.setElementsData(documents_b, false, true, ctx);
                    RecyclerView recyclerView = findViewById(R.id.recyclerdocsCaja);
                    recyclerView.setAdapter(elementsAdapter);
                }
                else{
                    docPulsado = false;
                    documentos.setBackgroundResource(android.R.color.transparent);
                    documentos.setTextColor(getResources().getColor(R.color.rosaBoton));

                    findViewById(R.id.recyclerdocsCaja).setVisibility(View.GONE);
                    textoFotos2.setText("");
                    textoFotos1.setText("");

                }

                findViewById(R.id.recyclerfotosCaja).setVisibility(View.GONE);

                if(fotoPulsado){
                    fotoPulsado = false;
                    fotos.setBackgroundResource(android.R.color.transparent);
                    fotos.setTextColor(getResources().getColor(R.color.rosaBoton));

                }


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
                        else if(id == R.id.addAudio){


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
                saBox.addPhotos(boxInfo, selectedItem.toString(), new Callbacks() {
                    @Override
                    public void onCallbackExito(Boolean exito) {
                        if(exito){
                            //TODO Realmente no estoy actualizando, estoy volviendo a hacer peticion, ns si se puede hacer de manera mas optima
                            saBox.getPhotos(boxInfo.getId(), new Callbacks() {
                                @Override
                                public void onCallbackItems(ArrayList<String> photos) {
                                    elementsAdapter.setElementsData(photos, true, false, ctx);
                                    RecyclerView recyclerView = findViewById(R.id.recyclerfotosCaja);
                                    recyclerView.setAdapter(elementsAdapter);

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
            else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                saBox.addPhotosFromCamera(boxInfo, imageBitmap, new Callbacks() {
                    @Override
                    public void onCallbackExito(Boolean exito) {
                        if(exito){
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

            saBox.addDocs(boxInfo, selectedItem.toString(), fileName, new Callbacks() {
                @Override
                public void onCallbackExito(Boolean exito) {
                    if(exito){
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




        saBox.getPhotos(boxInfo.getId(), new Callbacks() {
            @Override
            public void onCallbackItems(ArrayList<String> photos) {

                photos_b = photos;
                elementsAdapter.setElementsData(photos, true, false, ctx);
                RecyclerView recyclerView = findViewById(R.id.recyclerfotosCaja);
                recyclerView.setAdapter(elementsAdapter);

                saBox.getDocs(boxInfo.getId(), new Callbacks() {
                    @Override
                    public void onCallbackItems(ArrayList<String> docs) {

                        documents_b = docs;
                        elementsAdapter.setElementsData(docs, false, true, ctx);
                        RecyclerView recyclerView = findViewById(R.id.recyclerdocsCaja);
                        recyclerView.setAdapter(elementsAdapter);

                    }
                });

            }
        });





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