package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import es.ucm.fdi.boxit.R;

public class CrearCapsulaForm extends AppCompatActivity {

    private EditText nombreCapsulaInput;
    private TextView nombreCapsulaTitulo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_capsula_form);

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


    }
}