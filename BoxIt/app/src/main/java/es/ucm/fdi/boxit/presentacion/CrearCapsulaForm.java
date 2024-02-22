package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.CapsuleInfo;
import es.ucm.fdi.boxit.negocio.SACapsule;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class CrearCapsulaForm extends AppCompatActivity {

    private ImageView ellipse, home;
    private EditText nombreCapsulaInput;
    private TextView nombreCapsulaTitulo, textApertura, textCierre, daysApertura, daysCierre, daysCerrado;
    private NumberPicker diaCierre, mesCierre, añoCierre, diaApertura, mesApertura, añoApertura;
    private LinearLayout btnAddImg, btnAddColaborator, layCierre, layApertura, layTextCierre, layTextApertura;
    private Button btnCrear, btnSetCierre, btnSetApertura;
    private android.net.Uri selectedImage = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ArrayList<UserInfo> amigos;
    private ArrayList<String> colaboradores = new ArrayList<>();
    private UsersAdapter adapter;
    private Date apertura, cierre;
    private String [] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sept","Oct", "Nov", "Dec"};

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

        /////////////////FECHAS
        //fechas valores
        diaCierre = findViewById(R.id.numberPickerDayClose);
        diaApertura = findViewById(R.id.numberPickerDayOpen);
        mesCierre = findViewById(R.id.numberPickerMonthClose);
        mesApertura = findViewById(R.id.numberPickerMonthOpen);
        añoCierre = findViewById(R.id.numberPickerYearClose);
        añoApertura = findViewById(R.id.numberPickerYearOpen);

        //testos
        textApertura = findViewById(R.id.fechaApertura_text);
        textCierre = findViewById(R.id.fechaCierre_text);
        daysApertura = findViewById(R.id.daysToOpen);
        daysCierre = findViewById(R.id.daysToClose);
        daysCerrado = findViewById(R.id.daysClose);

        //btn set
        btnSetApertura = findViewById(R.id.buttonSetApertura);
        btnSetCierre = findViewById(R.id.buttonSetCierre);

        //layouts
        layCierre = findViewById(R.id.numberPickerClose);
        layApertura = findViewById(R.id.numberPickerApertura);
        layTextCierre = findViewById(R.id.layTextCierre);
        layTextApertura = findViewById(R.id.layTextApertura);

        //Date date = new Date();
        GregorianCalendar cm = new GregorianCalendar(); //calendario mañana
        cm.add(Calendar.DATE, 1);
        GregorianCalendar cp = new GregorianCalendar();//calendario pasado mañana
        cp.add(Calendar.DATE, 2);


        //Configuración del picker de fecha de cierre - inicializado a hoy
        diaCierre.setMaxValue(31);
        diaCierre.setMinValue(1);
        diaCierre.setWrapSelectorWheel(true);
        diaCierre.setValue(cm.get(Calendar.DAY_OF_MONTH));

        añoCierre.setMaxValue(2050);
        añoCierre.setMinValue(cm.get(Calendar.YEAR));
        añoCierre.setWrapSelectorWheel(true);
        añoCierre.setValue(cm.get(Calendar.YEAR));

        mesCierre.setMaxValue(meses.length - 1);
        mesCierre.setMinValue(0);
        mesCierre.setWrapSelectorWheel(true);
        mesCierre.setDisplayedValues(meses);
        mesCierre.setValue(cm.get(Calendar.MONTH));

        //Configuración del picker de fecha de apertura - inicializado a mañana
        diaApertura.setMaxValue(31);
        diaApertura.setMinValue(1);
        diaApertura.setWrapSelectorWheel(true);
        diaApertura.setValue(cp.get(Calendar.DAY_OF_MONTH));

        añoApertura.setMaxValue(2050);
        añoApertura.setMinValue(cp.get(Calendar.YEAR));
        añoApertura.setWrapSelectorWheel(true);
        añoApertura.setValue(cp.get(Calendar.YEAR));

        mesApertura.setMaxValue(meses.length - 1);
        mesApertura.setMinValue(0);
        mesApertura.setWrapSelectorWheel(true);
        mesApertura.setDisplayedValues(meses);
        mesApertura.setValue(cp.get(Calendar.MONTH));

        layTextCierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layCierre.getVisibility() == View.VISIBLE){
                    layCierre.setVisibility(View.GONE);
                    layTextCierre.setBackgroundColor(Color.TRANSPARENT);
                }
                else{
                    layCierre.setVisibility(View.VISIBLE);
                    layApertura.setVisibility(View.GONE);
                    layTextCierre.setBackgroundColor(getResources().getColor(R.color.rosaBotonClaro));
                    layTextApertura.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });
        layTextApertura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layApertura.getVisibility() == View.VISIBLE){
                    layApertura.setVisibility(View.GONE);
                    layTextApertura.setBackgroundColor(Color.TRANSPARENT);
                }
                else{
                    layCierre.setVisibility(View.GONE);
                    layApertura.setVisibility(View.VISIBLE);
                    layTextApertura.setBackgroundColor(getResources().getColor(R.color.rosaBotonClaro));
                    layTextCierre.setBackgroundColor(Color.TRANSPARENT);
                }

            }
        });

        Date cd = Calendar.getInstance().getTime();
        btnSetCierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = new GregorianCalendar(añoCierre.getValue(), mesCierre.getValue(), diaCierre.getValue(), 0, 0);
                cierre = calendar.getTime();
                textCierre.setText(diaCierre.getValue()+"/"+ meses[mesCierre.getValue()]+"/"+añoCierre.getValue());
                Long d = cierre.getTime() - cd.getTime();
                daysCierre.setText(TimeUnit.MILLISECONDS.toDays(d) + " ");
                if(apertura != null){
                    d = apertura.getTime() -  cierre.getTime();
                    daysCerrado.setText(getString(R.string.tiempoCerrado)+" "+ TimeUnit.MILLISECONDS.toDays(d)+" "+ getString(R.string.dias));
                }
            }
        });

        btnSetApertura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = new GregorianCalendar(añoApertura.getValue(), mesApertura.getValue(), diaApertura.getValue(), 0, 0);
                apertura = calendar.getTime();
                textApertura.setText(diaApertura.getValue()+"/"+ meses[mesApertura.getValue()]+"/"+añoApertura.getValue());
                Long d = apertura.getTime() - cd.getTime();
                daysApertura.setText(TimeUnit.MILLISECONDS.toDays(d) + " ");
                if(cierre != null){
                    d = apertura.getTime() -  cierre.getTime();
                    daysCerrado.setText(getString(R.string.tiempoCerrado)+" "+ TimeUnit.MILLISECONDS.toDays(d) +" "+ getString(R.string.dias));
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

        //Si viene con datos
        CapsuleInfo capDising = getIntent().getParcelableExtra("DisingData");
        if(capDising != null) setData(capDising);

        //Si es un update
        boolean isCrear = getIntent().getBooleanExtra("Crear", true);

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

                if(capDising != null && !capDising.getColaborators().isEmpty()){
                    //recargamos el adapter para q salgan esoso colaboradores
                    adapter = new UsersAdapter();
                    adapter.setPreData(capDising.getColaborators());
                    adapter.setUserData(amigos);//los amigos cargados antes
                    recyclerView.setAdapter(adapter);
                }
            }
        });


        //CREAR / ACTUALIZAR
        btnCrear = findViewById(R.id.CrearCapBTN);
        if(!isCrear) btnCrear.setText(getString(R.string.guardar));
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //el unico parametro a "validar" sera el nombre que no puede ser vacio"
                if(nombreCapsulaInput.getText().toString().equals("")){
                    nombreCapsulaInput.setError(getString(R.string.requerido));
                }
                else if(apertura == null || cierre == null){
                    Toast.makeText(CrearCapsulaForm.this, R.string.dateObligatoria, Toast.LENGTH_SHORT).show();
                }
                else if(apertura.before(cierre)){//El cierre debe ser previo
                    Toast.makeText(CrearCapsulaForm.this, R.string.CierrePreApertura, Toast.LENGTH_LONG).show();;
                }
                else if(apertura.before(cd) || cierre.before(cd)){//El antes de el dia actual
                    Toast.makeText(CrearCapsulaForm.this, R.string.CierrePreApertura, Toast.LENGTH_LONG).show();;
                }
                else{
                    if(selectedImage == null){
                        String packageName = getApplicationContext().getPackageName();
                        selectedImage = Uri.parse("android.resource://" + packageName + "/drawable/default_image");
                    }
                    CapsuleInfo cap = new CapsuleInfo("a",nombreCapsulaInput.getText().toString(), selectedImage);

                    //cogemos las fechas
                    cap.setCierre(cierre);
                    cap.setApertura(apertura);

                    //cogemos los colaboradores si los hay
                    colaboradores = adapter.getData();
                    if(!colaboradores.isEmpty()) {
                        colaboradores.add(currentUser.getEmail());
                        cap.setCollaborators(colaboradores);
                    }

                    SACapsule saCapsule = new SACapsule();
                    if(isCrear){
                        saCapsule.createCapsule(cap, new Callbacks() {
                            @Override
                            public void onCallbackExito(Boolean exito) {
                                if(exito){
                                    Context ctx = v.getContext();
                                    Intent intent = new Intent(ctx, Capsula.class);
                                    intent.putExtra("capsuleInfo", cap);
                                    ctx.startActivity(intent);
                                }
                                else{
                                    Toast.makeText(CrearCapsulaForm.this, R.string.errCrearCap, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        cap.setId(capDising.getId());
                        saCapsule.updateCap(cap, new Callbacks() {
                            @Override
                            public void onCallbackExito(Boolean exito) {
                                if(exito){
                                    Context ctx = v.getContext();
                                    Intent intent = new Intent(ctx, Caja.class);
                                    intent.putExtra("boxInfo", cap);
                                    ctx.startActivity(intent);
                                }
                                else{
                                    Toast.makeText(CrearCapsulaForm.this, R.string.errUpdate, Toast.LENGTH_SHORT).show();
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

    private void setData(CapsuleInfo capDising) {
        nombreCapsulaInput.setText(capDising.getTitle());
        nombreCapsulaTitulo.setText(capDising.getTitle());

        if (capDising.getImg() != null) {
            ellipse = findViewById(R.id.ellipse_13);
            Glide.with(this)
                    .load(capDising.getImg())
                    .transform(new CenterCrop(), new RoundedCorners(5000))
                    .into(ellipse);
            selectedImage = capDising.getImg();
        }

        Date cd = Calendar.getInstance().getTime();
        //Aperura
        apertura =capDising.getApertura();
        Calendar a = new GregorianCalendar();
        a.setTime(apertura);

        textApertura.setText(a.get(Calendar.DAY_OF_MONTH)+"/"+ meses[a.get(Calendar.MONTH)]+"/"+a.get(Calendar.YEAR));
        Long d = apertura.getTime() - cd.getTime();
        daysApertura.setText(TimeUnit.MILLISECONDS.toDays(d) + " ");

        //Cierre
        cierre = capDising.getCierre();
        Calendar c = new GregorianCalendar();
        c.setTime(cierre);

        textCierre.setText(c.get(Calendar.DAY_OF_MONTH)+"/"+ meses[c.get(Calendar.MONTH)]+"/"+c.get(Calendar.YEAR));
        d = cierre.getTime() - cd.getTime();
        daysCierre.setText(TimeUnit.MILLISECONDS.toDays(d) + " ");

        d = apertura.getTime() -  cierre.getTime();
        daysCerrado.setText(getString(R.string.tiempoCerrado)+" "+ TimeUnit.MILLISECONDS.toDays(d) +" "+ getString(R.string.dias));
    }
}