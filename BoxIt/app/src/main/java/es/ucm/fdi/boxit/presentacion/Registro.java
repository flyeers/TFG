package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;
import es.ucm.fdi.boxit.negocio.ValidarFormulario;

public class Registro extends AppCompatActivity {

    EditText email, username, password, passwordConfirm, name;
    boolean ok;
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView imagen;
    Uri fotoPerfil;

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
        imagen = findViewById(R.id.addfotoperfil);

        r.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                validar(new Callbacks() {
                    @Override
                    public void onCallbackExito(Boolean exito1) {
                        if(exito1){
                            //Registro valido
                            SAUser saUser = new SAUser();
                            UserInfo userInfo = new UserInfo(username.getText().toString(), email.getText().toString(), password.getText().toString() , name.getText().toString(), fotoPerfil);

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

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        SAUser saUser = new SAUser();

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data.getData() != null) {

            fotoPerfil = data.getData();



            try {

                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fotoPerfil);
                Bitmap squareBitmap = getCroppedBitmap(originalBitmap);
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), squareBitmap);

                roundedDrawable.setCornerRadius(Math.max(originalBitmap.getWidth(), originalBitmap.getHeight()) / 1.5f);


                imagen.setImageDrawable(roundedDrawable);
                imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }




    }

    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newSize = Math.min(width, height);

        int startX = (width - newSize) / 2;
        int startY = (height - newSize) / 2;

        return Bitmap.createBitmap(bitmap, startX, startY, newSize, newSize);
    }


}


