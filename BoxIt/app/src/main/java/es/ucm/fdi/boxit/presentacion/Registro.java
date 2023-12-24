package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.CountDownLatch;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;
import es.ucm.fdi.boxit.negocio.ValidarFormulario;

public class Registro extends AppCompatActivity {

    EditText email, username, password, passwordConfirm, name;
    boolean ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);



        Button r = findViewById(R.id.buttonRegistrarse);
        email = findViewById(R.id.reg_email);
        username = findViewById(R.id.reg_nombre);
        password = findViewById(R.id.reg_crear_contrasenya);
        passwordConfirm = findViewById(R.id.reg_confirmar_contrasenya);
        name = findViewById(R.id.reg_nombrePers);

        r.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                validar(new Callbacks() {
                    @Override
                    public void onCallbackExito(Boolean exito1) {
                        if(exito1){
                            //Registro valido
                            SAUser saUser = new SAUser();
                            UserInfo userInfo = new UserInfo(username.getText().toString(), email.getText().toString(), password.getText().toString() , name.getText().toString());

                            saUser.crearUsuario(userInfo, new Callbacks() {
                                @Override
                                public void onCallbackExito(Boolean exito2) {
                                    if(exito2){
                                        Intent intent2 = new Intent(Registro.this, MainActivity.class);
                                        startActivity(intent2);
                                    }
                                    else{
                                        Toast.makeText(Registro.this, "ERROR", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });

                // TODO Registro no valido
            }
        });
    }

    private void validar(Callbacks cb){


        TextInputLayout textInputLayoutEmail = findViewById(R.id.reg_email_lay);
        TextInputLayout textInputLayoutName = findViewById(R.id.reg_nombre_lay);
        TextInputLayout textInputLayoutPassword = findViewById(R.id.reg_contrasenya_lay);
        TextInputLayout textInputLayoutConfirmPassword = findViewById(R.id.reg_confirmar_contrasenya_lay);
        TextInputLayout textInputLayoutNamePersona = findViewById(R.id.reg_nombrePers_lay);
        CountDownLatch latch = new CountDownLatch(1);

        ok = true;


        ValidarFormulario valid = new ValidarFormulario(this, email.getText().toString(), username.getText().toString(),password.getText().toString(), name.getText().toString());

        if(!valid.isValidUserName()){
            textInputLayoutName.setError(getString(R.string.userNameIncorrecto));
            //ok = false;
            cb.onCallbackExito(false);
        }
        else{
            SAUser saUser = new SAUser();
            saUser.getUsuarioByUsername(username.getText().toString(), new Callbacks() {
                @Override
                public void onCallbackExito(Boolean exito) {
                    if(exito){
                        Log.d("CLAU", "nombre ya se usa");
                        textInputLayoutName.setError(getString(R.string.userNameDuplicado));
                        //ok = false;
                        cb.onCallbackExito(false);
                    }
                    else{
                        textInputLayoutName.setError(null);
                        if(!valid.isValidEmail()){
                            //TODO DA ERROR mirar logs
                            textInputLayoutEmail.setError(getString(R.string.emailIncorrecto));
                            ok = false;
                        }
                        else{
                            textInputLayoutEmail.setError(null);
                        }

                        if(!valid.isValidName()){
                            textInputLayoutNamePersona.setError(getString(R.string.nameIncorrecto));
                            ok = false;
                        }
                        else{
                            textInputLayoutNamePersona.setError(null);
                        }

                        if(!valid.isValidPassword()){
                            textInputLayoutPassword.setError(getString(R.string.passwordIncorrecto));
                            ok = false;
                        }
                        else{
                            textInputLayoutPassword.setError(null);
                            //si la contraseña es valida miramos el confirmar contraseña, si no no haria falta
                            if(!passwordConfirm.getText().toString().equals(password.getText().toString())){
                                textInputLayoutConfirmPassword.setError(getString(R.string.contraseñasDiferentes));
                                ok = false;
                            }
                            else{
                                textInputLayoutConfirmPassword.setError(null);
                            }
                        }

                        if(ok){ cb.onCallbackExito(true);}
                        else{ cb.onCallbackExito(false);}
                    }

                }
            });


        }


        //return ok;
    }


}


