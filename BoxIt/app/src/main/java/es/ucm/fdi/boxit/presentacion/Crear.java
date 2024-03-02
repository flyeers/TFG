package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.negocio.CapsuleInfo;

public class Crear extends AppCompatActivity {

    private ImageButton desde0;
    private TextView titulo;

    private ImageView home;
    private boolean box;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear);

        desde0 = findViewById(R.id.addCaja);
        titulo = findViewById(R.id.tituloCrear);
        home = findViewById(R.id.homeBtn);
        box = getIntent().getBooleanExtra("TIPO", false);

        if(box){
            titulo.setText(getString(R.string.crearCaja));
        }
        else{
            titulo.setText(getString(R.string.crearCapsula));
        }
        desde0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(box){
                    Intent intent = new Intent(Crear.this, CrearCajaForm.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(Crear.this, CrearCapsulaForm.class);
                    startActivity(intent);
                }

            }
        });

        setPremadeDisings();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setPremadeDisings(){
        ArrayList<CapsuleInfo> capDisings = new ArrayList<>();
        String packageName = getApplicationContext().getPackageName();

        GregorianCalendar cd = new GregorianCalendar();
        int year = cd.get(Calendar.YEAR);
        cd.add(Calendar.YEAR, 1);
        int year2 = cd.get(Calendar.YEAR);

        //Verano
        Calendar calendar = new GregorianCalendar(year, Calendar.AUGUST, 30, 0, 0);
        Calendar calendar2 = new GregorianCalendar(year2, Calendar.JULY, 1, 0, 0);
        Uri img = Uri.parse("android.resource://" + packageName + "/drawable/dis_verano");
        CapsuleInfo cap1 = new CapsuleInfo("", getString(R.string.disVerano), img);
        cap1.setCierre(calendar.getTime());
        cap1.setApertura(calendar2.getTime());
        capDisings.add(cap1);

        //Navidad
        calendar = new GregorianCalendar(year, Calendar.DECEMBER, 26, 0, 0);
        calendar2 = new GregorianCalendar(year2, Calendar.DECEMBER, 1, 0, 0);
        img = Uri.parse("android.resource://" + packageName + "/drawable/dis_navidad");
        CapsuleInfo cap2 = new CapsuleInfo("", getString(R.string.disNavidad), img);
        cap2.setCierre(calendar.getTime());
        cap2.setApertura(calendar2.getTime());
        capDisings.add(cap2);

        //TFG
        calendar = new GregorianCalendar(year, Calendar.MAY, 27, 0, 0);
        calendar2 = new GregorianCalendar(year, Calendar.JUNE, 6, 0, 0);
        img = Uri.parse("android.resource://" + packageName + "/drawable/dis_tfg");
        CapsuleInfo cap3 = new CapsuleInfo("", getString(R.string.disTFG), img);
        cap3.setCierre(calendar.getTime());
        cap3.setApertura(calendar2.getTime());
        capDisings.add(cap3);

        //Primaver-otoño




        PredesignAdapter p = new PredesignAdapter();
        p.setData(capDisings, box);
        RecyclerView recyclerView2 = findViewById(R.id.reclyclerViewPred);
        recyclerView2.setAdapter(p);

    }
}