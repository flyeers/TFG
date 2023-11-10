package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.negocio.ValidarFormulario;

public class Registro extends AppCompatActivity {

    String email;
    String username;
    String password;
    String passwordConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        email = findViewById(R.id.reg_email).toString();
        username = findViewById(R.id.reg_nombre).toString();
        password = findViewById(R.id.reg_crear_contrasenya).toString();
        passwordConfirm = findViewById(R.id.reg_confirmar_contrasenya).toString();

        TextInputLayout textInputLayoutEmail = findViewById(R.id.reg_email_lay);
        TextInputLayout textInputLayoutName = findViewById(R.id.reg_nombre_lay);
        TextInputLayout textInputLayoutPassword = findViewById(R.id.reg_contrasenya_lay);
        TextInputLayout textInputLayoutConfirmPassword = findViewById(R.id.reg_confirmar_contrasenya_lay);


        //TODO COMPROBAR QUE ESTO FUNCIONA PORQUE NO LO HE PROBADO y meterlo en otro lado !!!!
        ValidarFormulario valid = new ValidarFormulario(this, email, username,password);

        if(passwordConfirm != password){
            textInputLayoutConfirmPassword.setError(getString(R.string.contraseñasDiferentes));
        }
        else{
            textInputLayoutConfirmPassword.setError(null);
        }

        if(!valid.isValidEmail()){
            textInputLayoutEmail.setError(getString(R.string.emailIncorrecto));
        }
        else{
            textInputLayoutEmail.setError(null);
        }

        if(!valid.isValidName()){
            textInputLayoutName.setError(getString(R.string.userNameIncorrecto));
        }
        else{
            textInputLayoutName.setError(null);
        }

        if(!valid.isValidPassword()){
            textInputLayoutPassword.setError(getString(R.string.passwordIncorrecto));
        }
        else{
            textInputLayoutPassword.setError(null);
        }
    }


}


