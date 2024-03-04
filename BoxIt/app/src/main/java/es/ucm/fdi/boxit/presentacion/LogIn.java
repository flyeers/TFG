package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class LogIn extends AppCompatActivity {
    TextView registrarse;
    Button entrar;
    EditText nombreUsuario, contraseña;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        registrarse = findViewById(R.id.registrateBtn);
        entrar = findViewById(R.id.entrar);
        nombreUsuario = findViewById(R.id.usuarioLogin);
        contraseña = findViewById(R.id.contrasenyaLogin);
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SAUser saUser = new SAUser();
                if(nombreUsuario.getText().toString().contains("@")){
                    //entrando con correo
                    saUser.loginCorreo(nombreUsuario.getText().toString(), contraseña.getText().toString(), new Callbacks() {
                        @Override
                        public void onCallbackExito(Boolean exito) {
                            if(exito){
                                Intent intent2 = new Intent(LogIn.this, MainActivity.class);
                                startActivity(intent2);
                            }
                            else{
                                Toast.makeText(LogIn.this, R.string.errerLogIn, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    //entrando con nombre de usuario
                    saUser.getUsuarioByUsername(nombreUsuario.getText().toString(), new Callbacks() {
                        @Override
                        public void onCallback(UserInfo u) {

                            if(u != null){
                                saUser.loginCorreo(u.getCorreo().toString(), contraseña.getText().toString(), new Callbacks() {
                                    @Override
                                    public void onCallbackExito(Boolean exito) {
                                        if(exito){
                                            Intent intent2 = new Intent(LogIn.this, MainActivity.class);
                                            startActivity(intent2);
                                        }
                                        else{
                                            Toast.makeText(LogIn.this, R.string.errerLogIn, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(LogIn.this, R.string.errerLogIn, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }

            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, Registro.class);
                startActivity(intent);
            }
        });


    }

}