package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import es.ucm.fdi.boxit.R;

public class Crear extends AppCompatActivity {

    private ImageButton desde0;
    private TextView titulo;
    private boolean box;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear);

        desde0 = findViewById(R.id.addCaja);
        titulo = findViewById(R.id.tituloCrear);
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
    }
}