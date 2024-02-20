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

        //TODO cambiar
        Calendar calendar = new GregorianCalendar(2024, Calendar.JANUARY, 16, 12, 30, 0);
        Date date1 = calendar.getTime();

        Calendar calendar2 = new GregorianCalendar(2024, Calendar.MAY, 16, 12, 30, 0);
        Date date2 = calendar2.getTime();

        ArrayList<CapsuleInfo> c = new ArrayList<>();

        CapsuleInfo cap1 = new CapsuleInfo("","prueba", null);
        cap1.setApertura(date2); //cerrada
        cap1.setCierre(date1);
        c.add(cap1);

        Uri img = Uri.parse("https://books.google.com/books/content?id=e8DwuncELaoC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api");
        CapsuleInfo cap2 = new CapsuleInfo("","AHHHHHHHHHH", img);
        cap2.setApertura(date2); //cerrada
        cap2.setCierre(date1);
        c.add(cap2);

        PredesignAdapter p = new PredesignAdapter();
        p.setData(c, box);
        RecyclerView recyclerView2 = findViewById(R.id.reclyclerViewPred);
        recyclerView2.setAdapter(p);

        
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}