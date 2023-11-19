package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import es.ucm.fdi.boxit.R;

public class CrearCajaForm extends AppCompatActivity {

    private EditText nombreCajaInput;
    private TextView nombreCajaTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_caja_form);

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
    }
}