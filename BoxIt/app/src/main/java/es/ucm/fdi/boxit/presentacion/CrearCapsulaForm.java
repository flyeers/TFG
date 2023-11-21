package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import es.ucm.fdi.boxit.R;

public class CrearCapsulaForm extends AppCompatActivity {

    private EditText nombreCapsulaInput;
    private TextView nombreCapsulaTitulo, fechaCierre, fechaApertura;
    private NumberPicker dia, mes, año;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_capsula_form);

        nombreCapsulaTitulo = findViewById(R.id.nombreCapsulaTit);
        nombreCapsulaInput = findViewById(R.id.campo_nombre_capsula);
        dia = findViewById(R.id.numberPickerDay);
        mes = findViewById(R.id.numberPickerMonth);
        año = findViewById(R.id.numberPickerYear);
        fechaApertura = findViewById(R.id.fechaApertura);
        fechaCierre = findViewById(R.id.fechaCierre);


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

        dia.setMaxValue(31);
        dia.setMinValue(1);
        dia.setWrapSelectorWheel(true);
        dia.setValue(1);

        año.setMaxValue(2050);
        año.setMinValue(2023); //TODO no puede haber fechas pasadas, controlar esto en la logica
        año.setWrapSelectorWheel(true);
        año.setValue(2023);

        String [] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sept","Oct", "Nov", "Dec"};
        mes.setMaxValue(meses.length - 1);
        mes.setMinValue(0);
        mes.setWrapSelectorWheel(true);
        mes.setDisplayedValues(meses);


        fechaCierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dia.getVisibility() != View.VISIBLE){
                    dia.setVisibility(View.VISIBLE);
                    mes.setVisibility(View.VISIBLE);
                    año.setVisibility(View.VISIBLE);
                }
                else{
                    dia.setVisibility(View.GONE);
                    mes.setVisibility(View.GONE);
                    año.setVisibility(View.GONE);
                }

            }
        });

        if(dia.getVisibility() == View.VISIBLE){

            fechaCierre.setText("hola");
        }

        fechaApertura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dia.getVisibility() != View.VISIBLE){
                    dia.setVisibility(View.VISIBLE);
                    mes.setVisibility(View.VISIBLE);
                    año.setVisibility(View.VISIBLE);
                }
                else{
                    dia.setVisibility(View.GONE);
                    mes.setVisibility(View.GONE);
                    año.setVisibility(View.GONE);
                }
            }
        });


    }
}