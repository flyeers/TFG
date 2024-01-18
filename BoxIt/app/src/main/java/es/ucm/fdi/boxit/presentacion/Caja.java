package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.SABox;

public class Caja extends AppCompatActivity {

    private Button add;
    private TextView nombre, fotos, musica, notas, audio, textoFotos1, textoFotos2;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int RESULT_OK = -1;

    private BoxInfo boxInfo;
    private String imagePath;

    private android.net.Uri selectedImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja);

        add = findViewById(R.id.buttonAdd2);
        add.setBackgroundColor(getResources().getColor(R.color.rosaBoton));


        boxInfo = getIntent().getParcelableExtra("boxInfo");


        nombre = findViewById(R.id.nombre_caja);
        nombre.setText(boxInfo.getTitle());

        fotos = findViewById(R.id.fotosCaja);
        musica = findViewById(R.id.musicaCaja);
        textoFotos2 = findViewById(R.id.fdelacaja);
        textoFotos1 = findViewById(R.id.fotosdelacaja);


        SABox saBox = new SABox();
        ElementsAdapter elementsAdapter = new ElementsAdapter();


        fotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                fotos.setBackgroundColor(getResources().getColor(R.color.rosaBoton));
                fotos.setTextColor(getResources().getColor(R.color.fondoClaro));
                textoFotos1.setText(getResources().getString(R.string.galeria));
                textoFotos2.setText(getResources().getString(R.string.delacaja));
                saBox.getPhotos(boxInfo.getId(), new Callbacks() {
                    @Override
                    public void onCallbackPhotos(ArrayList<String> photos) {
                        elementsAdapter.setElementsData(photos, true);
                        RecyclerView recyclerView = findViewById(R.id.recyclerfotosCaja);
                        recyclerView.setAdapter(elementsAdapter);

                    }
                });
            }
        });
        saBox.getPhotos(boxInfo.getId(), new Callbacks() {
            @Override
            public void onCallbackPhotos(ArrayList<String> photos) {
                elementsAdapter.setElementsData(photos, true);
                RecyclerView recyclerView = findViewById(R.id.recyclerfotosCaja);
                recyclerView.setAdapter(elementsAdapter);

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
                        else if(id == R.id.addNote){


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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data.getData() != null) {

            selectedImage = data.getData();
        }
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            //TODO quedarse con la imagen sacada con la camara

        }

        SABox saBox = new SABox();


        saBox.addPhotos(boxInfo.getId(), selectedImage.toString(), new Callbacks() {
            @Override
            public void onCallbackExito(Boolean exito) {
                if(exito){
                    //TODO Actualizar el recycler
                    Log.d("CLAU", "todo bien");
                }
                else{
                    //TODO poner un toas
                    Log.d("CLAU", "todo mal");
                }
            }
        });
    }
}