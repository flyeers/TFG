package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
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
import es.ucm.fdi.boxit.negocio.CapsuleInfo;
import es.ucm.fdi.boxit.negocio.SABox;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class CrearCapsulaForm extends AppCompatActivity {

    private ImageView ellipse, home;
    private EditText nombreCapsulaInput;
    private TextView nombreCapsulaTitulo, fechaCierre, fechaApertura;
    private NumberPicker diaCierre, mesCierre, añoCierre, diaApertura, mesApertura, añoApertura;
    private LinearLayout btnAddImg, btnAddColaborator;
    private Button btnCrear;
    private android.net.Uri selectedImage = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ArrayList<UserInfo> amigos;
    private ArrayList<String> colaboradores = new ArrayList<>();
    private UsersAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_capsula_form);

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
        nombreCapsulaTitulo = findViewById(R.id.nombreCapsulaTit);
        nombreCapsulaInput = findViewById(R.id.campo_nombre_capsula);
        nombreCapsulaInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nombreCapsulaTitulo.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //FECHAS
        diaCierre = findViewById(R.id.numberPickerDayClose);
        diaApertura = findViewById(R.id.numberPickerDayOpen);
        mesCierre = findViewById(R.id.numberPickerMonthClose);
        mesApertura = findViewById(R.id.numberPickerMonthOpen);
        añoCierre = findViewById(R.id.numberPickerYearClose);
        añoApertura = findViewById(R.id.numberPickerYearOpen);

        fechaApertura = findViewById(R.id.fechaApertura);
        fechaCierre = findViewById(R.id.fechaCierre);

        String [] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sept","Oct", "Nov", "Dec"};

        //Configuración del picker de fecha de cierre
        diaCierre.setMaxValue(31);
        diaCierre.setMinValue(1);
        diaCierre.setWrapSelectorWheel(true);
        diaCierre.setValue(1);

        añoCierre.setMaxValue(2050);
        añoCierre.setMinValue(2023); //TODO no puede haber fechas pasadas, controlar esto en la logica
        añoCierre.setWrapSelectorWheel(true);
        añoCierre.setValue(2023);

        mesCierre.setMaxValue(meses.length - 1);
        mesCierre.setMinValue(0);
        mesCierre.setWrapSelectorWheel(true);
        mesCierre.setDisplayedValues(meses);

        //Configuración del picker de fecha de apertura
        diaApertura.setMaxValue(31);
        diaApertura.setMinValue(1);
        diaApertura.setWrapSelectorWheel(true);
        diaApertura.setValue(1);

        añoApertura.setMaxValue(2050);
        añoApertura.setMinValue(2023); //TODO no puede haber fechas pasadas, controlar esto en la logica
        añoApertura.setWrapSelectorWheel(true);
        añoApertura.setValue(2023);

        mesApertura.setMaxValue(meses.length - 1);
        mesApertura.setMinValue(0);
        mesApertura.setWrapSelectorWheel(true);
        mesApertura.setDisplayedValues(meses);


        fechaCierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(diaCierre.getVisibility() != View.VISIBLE && diaApertura.getVisibility() != View.VISIBLE){
                    diaCierre.setVisibility(View.VISIBLE);
                    mesCierre.setVisibility(View.VISIBLE);
                    añoCierre.setVisibility(View.VISIBLE);
                }
                else{
                    diaCierre.setVisibility(View.GONE);
                    mesCierre.setVisibility(View.GONE);
                    añoCierre.setVisibility(View.GONE);
                }

            }
        });

        fechaApertura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(diaApertura.getVisibility() != View.VISIBLE && diaCierre.getVisibility() != View.VISIBLE){
                    diaApertura.setVisibility(View.VISIBLE);
                    mesApertura.setVisibility(View.VISIBLE);
                    añoApertura.setVisibility(View.VISIBLE);
                }
                else{
                    diaApertura.setVisibility(View.GONE);
                    mesApertura.setVisibility(View.GONE);
                    añoApertura.setVisibility(View.GONE);
                }
            }
        });

        //IMG
        btnAddImg = findViewById(R.id.btnAddImgCap);
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
                adapter = new UsersAdapter();
                adapter.setUserData(amigos);
                recyclerView.setAdapter(adapter);

                btnAddColaborator = findViewById(R.id.btnAddColCap);
                btnAddColaborator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(amigos.isEmpty()){
                            Toast.makeText(CrearCapsulaForm.this, R.string.noAmigos , Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(recyclerView.getVisibility() == View.GONE)
                                recyclerView.setVisibility(View.VISIBLE);
                            else
                                recyclerView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });


        //CREAR
        btnCrear = findViewById(R.id.CrearCapBTN);
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //el unico parametro a "validar" sera el nombre que no puede ser vacio"
                if(nombreCapsulaInput.getText().toString().equals("")){
                    nombreCapsulaInput.setError(getString(R.string.requerido));
                }
                else{
                    if(selectedImage == null){
                        String packageName = getApplicationContext().getPackageName();
                        selectedImage = Uri.parse("android.resource://" + packageName + "/drawable/default_image");
                    }
                    CapsuleInfo cap = new CapsuleInfo("a",nombreCapsulaInput.getText().toString(), selectedImage);

                    //TODO coger y poner fechas

                    //cogemos los colaboradores si los hay
                    colaboradores = adapter.getData();
                    if(!colaboradores.isEmpty()) {
                        //colaboradores.add(currentUser.getEmail());
                        cap.setCollaborators(colaboradores);
                    }

                    /*SACapsule saCapsule = new SACapsule();
                    saCapsule.createCapsule(cap, new Callbacks() {
                        @Override
                        public void onCallbackExito(Boolean exito) {
                            if(exito){
                                Context ctx = v.getContext();
                                //Intent intent = new Intent(ctx, Caja.class);
                                //intent.putExtra("boxInfo", box);
                                //ctx.startActivity(intent);
                            }
                            else{
                                Toast.makeText(CrearCapsulaForm.this, R.string.errCrearCaja, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/
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
}