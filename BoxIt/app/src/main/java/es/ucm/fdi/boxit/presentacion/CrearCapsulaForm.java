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
    private NumberPicker diaCierre, mesCierre, añoCierre, diaApertura, mesApertura, añoApertura;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_capsula_form);

        nombreCapsulaTitulo = findViewById(R.id.nombreCapsulaTit);
        nombreCapsulaInput = findViewById(R.id.campo_nombre_capsula);

        diaCierre = findViewById(R.id.numberPickerDayClose);
        diaApertura = findViewById(R.id.numberPickerDayOpen);
        mesCierre = findViewById(R.id.numberPickerMonthClose);
        mesApertura = findViewById(R.id.numberPickerMonthOpen);
        añoCierre = findViewById(R.id.numberPickerYearClose);
        añoApertura = findViewById(R.id.numberPickerYearOpen);

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


    }
}