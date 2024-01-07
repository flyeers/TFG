package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.SABox;

public class CrearCajaForm extends AppCompatActivity {

    private EditText nombreCajaInput;
    private TextView nombreCajaTitulo;

    private Button btnCrear;
    private LinearLayout btnAddImg, btnAddColaborator;

    private ImageView ellipse;
    private android.net.Uri selectedImage = null;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_caja_form);

        //NOMBRE
        nombreCajaTitulo = findViewById(R.id.nombre_caja_tit);
        nombreCajaInput = findViewById(R.id.nombre_caja_input);

        nombreCajaInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nombreCajaTitulo.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //IMG
        btnAddImg = findViewById(R.id.btnAddImg);
        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creamos un intent para acceder a la galeria
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
            }
        });

        //COLABORADORES //TODO
        btnAddColaborator = findViewById(R.id.btnAddCol);
        btnAddColaborator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CrearCajaForm.this);
                builder.setTitle("AÑADIR COLABORADOR");
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //AÑADIR A UN ARRAY
                    }
                });
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        //lista de colaboradores

        //TODO CAMBIAR - lo q hay q coger (y en el modal no aqui), es la lista amigos del usuario
        ArrayList<Pair<String, String>> users = new ArrayList<Pair<String, String>>();
        users.add(new Pair<>("Pepe03", ""));
        UsersAdapter u = new UsersAdapter();
        u.setUserData(users);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_friends);
        recyclerView.setAdapter(u);


        //CREAR
        btnCrear = findViewById(R.id.CrearCajaBTN);
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //el unico parametro a "validar" sera el nombre que no puede ser vacio"
                if(nombreCajaInput.getText().toString().equals("")){
                    nombreCajaInput.setError(getString(R.string.requerido));
                }
                else{
                    //TODO coger colaboradores



                    SABox saBox = new SABox();
                    if(selectedImage == null){
                        String packageName = getApplicationContext().getPackageName();
                        selectedImage = Uri.parse("android.resource://" + packageName + "/drawable/default_image");
                    }
                    BoxInfo box = new BoxInfo(nombreCajaInput.getText().toString(), selectedImage);
                    saBox.createBox(box, new Callbacks() {
                        @Override
                        public void onCallbackExito(Boolean exito) {
                            if(exito){
                                Context ctx = v.getContext();
                                Intent intent = new Intent(ctx, Caja.class);
                                intent.putExtra("boxInfo", box);
                                ctx.startActivity(intent);
                            }
                            else{
                                Toast.makeText(CrearCajaForm.this, R.string.errCrearCaja, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //seleccionamos la imagen y la sustituimos
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data.getData() != null) {
            ellipse = findViewById(R.id.ellipse_13);
            Uri selectedImageUri = data.getData();
            /*Glide.with(this)
                    .load(selectedImageUri)
                    .centerCrop()
                    .into(elipse);*/
            Glide.with(this)
                    .load(selectedImageUri)
                    .transform(new CenterCrop(), new RoundedCorners(5000))
                    .into(ellipse);
            selectedImage = data.getData();
        }
    }

}