package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.auth.User;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;
import es.ucm.fdi.boxit.negocio.ValidarFormulario;

public class Registro extends AppCompatActivity {

    EditText email, username, password, passwordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);



        Button r = findViewById(R.id.buttonRegistrarse);
        email = findViewById(R.id.reg_email);
        username = findViewById(R.id.reg_nombre);
        password = findViewById(R.id.reg_crear_contrasenya);
        passwordConfirm = findViewById(R.id.reg_confirmar_contrasenya);

        r.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(validar()){
                    //Registro valido
                    SAUser saUser = new SAUser();
                    UserInfo userInfo = new UserInfo(username.getText().toString(), email.getText().toString(), password.getText().toString() );

                    try{
                        saUser.crearUsuario(userInfo);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Intent intent2 = new Intent(Registro.this, MainActivity.class);
                                startActivity(intent2);
                            }
                        }, 2000);
                    }catch (Exception e){
                        Toast.makeText(Registro.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                    //Intent intent = new Intent(Registro.this, MainActivity.class);
                    //startActivity(intent);
                }
                //Registro no valido
            }
        });
    }

    private boolean validar(){

        //LO HE PASADO  A EDITTEXT PARA AHORRARNOS ESTO
        /*TextInputEditText e = findViewById(R.id.reg_email);
        TextInputEditText n = findViewById(R.id.reg_nombre);
        TextInputEditText c1 = findViewById(R.id.reg_crear_contrasenya);
        TextInputEditText c2 = findViewById(R.id.reg_confirmar_contrasenya);

        Editable editable = e.getText();
        email = editable.toString();
        editable = n.getText();
        username = editable.toString();
        editable = c1.getText();
        password = editable.toString();
        editable = c2.getText();
        passwordConfirm = editable.toString();*/

        TextInputLayout textInputLayoutEmail = findViewById(R.id.reg_email_lay);
        TextInputLayout textInputLayoutName = findViewById(R.id.reg_nombre_lay);
        TextInputLayout textInputLayoutPassword = findViewById(R.id.reg_contrasenya_lay);
        TextInputLayout textInputLayoutConfirmPassword = findViewById(R.id.reg_confirmar_contrasenya_lay);

        boolean ok = true;

        //TODO COMPROBAR QUE ESTO FUNCIONA PORQUE NO LO HE PROBADO y meterlo en otro lado !!!!
        ValidarFormulario valid = new ValidarFormulario(this, email.getText().toString(), username.getText().toString(),password.getText().toString());

        if(!valid.isValidEmail()){
            //TODO DA ERROR mirar logs
            textInputLayoutEmail.setError(getString(R.string.emailIncorrecto));
            ok = false;
        }
        else{
            textInputLayoutEmail.setError(null);
        }

        if(!valid.isValidName()){
            textInputLayoutName.setError(getString(R.string.userNameIncorrecto));
            ok = false;
        }
        else{
            textInputLayoutName.setError(null);
        }

        if(!valid.isValidPassword()){
            textInputLayoutPassword.setError(getString(R.string.passwordIncorrecto));
            ok = false;
        }
        else{
            textInputLayoutPassword.setError(null);
            //si la contraseña es valida miramos el confirmar contraseña, si no no haria falta
            if(!passwordConfirm.equals(password)){
                textInputLayoutConfirmPassword.setError(getString(R.string.contraseñasDiferentes));
                ok = false;
            }
            else{
                textInputLayoutConfirmPassword.setError(null);
            }
        }
        return ok;
    }


}


