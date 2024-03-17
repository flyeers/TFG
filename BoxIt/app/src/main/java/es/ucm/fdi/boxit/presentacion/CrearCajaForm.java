package es.ucm.fdi.boxit.presentacion;

import androidx.annotation.NonNull;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.SABox;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CrearCajaForm extends AppCompatActivity {

    private EditText nombreCajaInput;
    private TextView nombreCajaTitulo;

    private Button btnCrear, btnCancelar;
    private LinearLayout btnAddImg, btnAddColaborator;

    private ImageView ellipse, home;
    private android.net.Uri selectedImage = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ArrayList<UserInfo> amigos;
    private ArrayList<String> colaboradores = new ArrayList<>();
    private UsersAdapter adapter;
    private RecyclerView recyclerView;
    private static final String BEARER_TOKEN = "Bearer AAAAmrxyPYg:APA91bFQnxoyzAqjk2LKtwC6CuEDd3qx37QtldiuPvrl8XV0kSi5sgxTmdriwnVH64bhKvkQjQAw0XEgUymTB0DP_h821tsddqFKpoQyCDGR2qBtcAqksjmzz1dC9H6FbXoy-sslF8NB";
    private String boxName, username_actual;

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


        //Si viene con datos de diseño
        BoxInfo boxDising = getIntent().getParcelableExtra("DisingData");
        if(boxDising != null) setData(boxDising);

        //Si es un update
        boolean isCrear = getIntent().getBooleanExtra("Crear", true);

        //COLABORADORES
        recyclerView = findViewById(R.id.recycler_view_friends);
        recyclerView.setVisibility(View.GONE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        SAUser saUser = new SAUser();

        //cogemos la lista de amigos
        amigos = new ArrayList<>();
        saUser.getAmigos(currentUser.getEmail(), new Callbacks() {
            @Override
            public void onCallbackUsers(ArrayList<UserInfo> users) {
                amigos = users;
                adapter = new UsersAdapter();
                adapter.setUserData(amigos);
                recyclerView.setAdapter(adapter);

                btnAddColaborator = findViewById(R.id.btnAddCol);
                btnAddColaborator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(amigos.isEmpty()){
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

                if(boxDising != null && !boxDising.getColaborators().isEmpty()){
                    //recargamos el adapter para q salgan esoso colaboradores
                    adapter = new UsersAdapter();
                    adapter.setPreData(boxDising.getColaborators());
                    adapter.setUserData(amigos);//los amigos cargados antes
                    recyclerView.setAdapter(adapter);
                }
            }
        });

        //CREAR / ACTUALIZAR
        btnCrear = findViewById(R.id.CrearCajaBTN);
        if(!isCrear){
            btnCrear.setText(getString(R.string.guardar));
            btnCancelar = findViewById(R.id.cancelarBtn);
            btnCancelar.setVisibility(View.VISIBLE);
            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
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
                        colaboradores.add(currentUser.getEmail());
                        box.setCollaborators(colaboradores);
                    }

                    if(isCrear){
                        saBox.createBox(box, new Callbacks() {
                            @Override
                            public void onCallbackExito(Boolean exito) {
                                if(exito){
                                    boxName = nombreCajaInput.getText().toString();
                                    //notificamos a los colaboradores
                                    for (String colab: colaboradores) {
                                        saUser.getToken(colab, new Callbacks() {
                                            @Override
                                            public void onCallbackData(String data) {


                                                saUser.infoUsuario(currentUser.getEmail(), new Callbacks() {
                                                    @Override
                                                    public void onCallback(UserInfo u) {
                                                        username_actual = u.getNombreUsuario();
                                                        realizar_Https_SendAviso(data);
                                                    }
                                                });
                                            }
                                        });
                                    }

                                    //
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
                    else{
                        box.setId(boxDising.getId());
                        saBox.updateBox(box, new Callbacks() {
                            @Override
                            public void onCallbackExito(Boolean exito) {
                                if(exito){
                                    Context ctx = v.getContext();
                                    Intent intent = new Intent(ctx, Caja.class);
                                    intent.putExtra("boxInfo", box);
                                    ctx.startActivity(intent);
                                }
                                else{
                                    Toast.makeText(CrearCajaForm.this, R.string.errUpdate, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
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
            Glide.with(this)
                    .load(selectedImageUri)
                    .transform(new CenterCrop(), new RoundedCorners(5000))
                    .into(ellipse);
            selectedImage = data.getData();
        }
    }

    private void setData(BoxInfo boxDising) {
        nombreCajaTitulo.setText(boxDising.getTitle());
        nombreCajaInput.setText(boxDising.getTitle());

        if (boxDising.getImg() != null) {
            ellipse = findViewById(R.id.ellipse_13);
            Glide.with(this)
                    .load(boxDising.getImg())
                    .transform(new CenterCrop(), new RoundedCorners(5000))
                    .into(ellipse);
            selectedImage = boxDising.getImg();
        }
    }

    public void realizar_Https_SendAviso (String USER_TOKEN){
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonObject = null;

        try{


            jsonObject  = new JSONObject();

            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", boxName);
            notificationObj.put("body", username_actual);
            notificationObj.put("tag", "3");
            jsonObject.put("notification",notificationObj);
            jsonObject.put("to", USER_TOKEN);

        }catch (Exception e){
            Log.d("error", e.toString());
        }


        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), mediaType);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", BEARER_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("CLAU", "notificacion mal");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Toast.makeText(this, R.string.exito_noti, Toast.LENGTH_SHORT).show();
                Log.d("CLAU", "notificacion bien");
            }
        });



    }
}