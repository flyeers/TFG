package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.SABox;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class CrearCajaForm extends AppCompatActivity {

    private EditText nombreCajaInput;
    private TextView nombreCajaTitulo;

    private Button btnCrear;
    private LinearLayout btnAddImg, btnAddColaborator;

    private ImageView ellipse, home;
    private android.net.Uri selectedImage = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ArrayList<UserInfo> amigos;
    private ArrayList<String> colaboradores = new ArrayList<>();
    private UsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_caja_form);

        home = findViewById(R.id.homeBtn);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();
                Intent intent = new Intent(ctx, MainActivity.class);
                intent.putExtra("nueva", true);
                ctx.startActivity(intent);
            }
        });

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

        //COLABORADORES
        RecyclerView recyclerView = findViewById(R.id.recycler_view_friends);
        recyclerView.setVisibility(View.GONE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        SAUser saUser = new SAUser();

        //cogemos la lista de amigos
        amigos = new ArrayList<>();
        saUser.getAmigos(currentUser.getEmail(), new Callbacks() {
            @Override
            public void onCallbackUsers(ArrayList<UserInfo> users) {
                amigos = users;
            }
        });

        /*ArrayList<UserInfo> users = new ArrayList<>();
        UserInfo usu = new UserInfo();
        usu.setCorreo("correo");
        usu.setNombreUsuario("nombre");
        users.add(usu);
        UserInfo usu2 = new UserInfo();
        usu2.setCorreo("correo2");
        usu2.setNombreUsuario("nombre2");
        users.add(usu2);*/
        adapter = new UsersAdapter();
        //adapter.setUserData(users);
        adapter.setUserData(amigos);//TODO CAMBIAR
        recyclerView.setAdapter(adapter);

        btnAddColaborator = findViewById(R.id.btnAddCol);
        btnAddColaborator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setUserData(amigos);
                adapter.notifyDataSetChanged();
                if(amigos.isEmpty()){ //TODO CAMBIAR
                    Toast.makeText(CrearCajaForm.this, R.string.noAmigos , Toast.LENGTH_SHORT).show();
                }
                else{
                    if(recyclerView.getVisibility() == View.GONE)
                        recyclerView.setVisibility(View.VISIBLE);
                    else
                        recyclerView.setVisibility(View.GONE);
                }
            }
        });


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

                    SABox saBox = new SABox();
                    if(selectedImage == null){
                        String packageName = getApplicationContext().getPackageName();
                        selectedImage = Uri.parse("android.resource://" + packageName + "/drawable/default_image");
                    }


                    BoxInfo box = new BoxInfo("a",nombreCajaInput.getText().toString(), selectedImage);

                    //cogemos los colaboradores si los hay
                    colaboradores = adapter.getData();
                    if(!colaboradores.isEmpty()) {
                        colaboradores.add(currentUser.getEmail());//TODO ver si lo queremos hacer asi
                        box.setCollaborators(colaboradores);
                    }

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