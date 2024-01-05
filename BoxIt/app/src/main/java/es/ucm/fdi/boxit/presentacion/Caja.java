package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.negocio.BoxInfo;

public class Caja extends AppCompatActivity {

    private Button add;
    private TextView nombre;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 123;

    private BoxInfo boxInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja);

        add = findViewById(R.id.buttonAdd2);
        add.setBackgroundColor(getResources().getColor(R.color.rosaBoton));


       // boxInfo = (BoxInfo) getIntent().getSerializableExtra("boxInfo");
       //nombre.findViewById(R.id.nombre_caja);
       // nombre.setText(boxInfo.getTitle());

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
}