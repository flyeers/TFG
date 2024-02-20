package es.ucm.fdi.boxit.presentacion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.SABox;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class Perfil extends AppCompatActivity {

    private TextView nombreUsuario, correo;
    private ImageButton opt, home;
    private ImageView foto, nuevaFoto;
    private Context ctx;
    private String nom, userN;
    private Uri fPerfil = null;
    private Uri nuevafPerfil = null;
    private Boolean hayFoto = false;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        ctx = this;
        nombreUsuario = findViewById(R.id.nombreUsuarioPerfil);
        correo = findViewById(R.id.correoUsuarioPerfil);
        home = findViewById(R.id.buttonHome);
        opt = findViewById(R.id.perfilOpciones);
        foto = findViewById(R.id.perfilfoto);

        Context ctx = this;

        SAUser saUser = new SAUser();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        saUser.infoUsuario(currentUser.getEmail().toString(), new Callbacks() {
            @Override
            public void onCallback(UserInfo u) {
                nombreUsuario.setText(u.getNombreUsuario());
                userN = u.getNombreUsuario();
                nom = u.getNombre();
                correo.setText(currentUser.getEmail().toString());

                String f = u.getImgPerfil().toString();
                if (f != ""){

                    hayFoto = true;
                    fPerfil = u.getImgPerfil();
                    Glide.with(ctx)
                            .asBitmap()
                            .load(u.getImgPerfil())
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                    roundedDrawable.setCircular(true);

                                    foto.setImageDrawable(roundedDrawable);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }


                            });

                }

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //no puede ir a la activity anterior porque si se ha modificado la foto de perfil aparece mal cargada, hay que invocar un intent nuevo
                Intent intent = new Intent(ctx, MainActivity.class);
                startActivity(intent);
            }

        });

        opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(ctx, R.style.EstiloMenu);
                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.perfil_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int id = item.getItemId();
                        if(id == R.id.editarPerfilMenu){
                            mostrarDialog();
                            return true;
                        }
                        else if(id == R.id.cerrarSesionMenu){
                            SAUser saUser = new SAUser();
                            saUser.cerrarSesion();
                            Intent intent = new Intent(ctx, MainActivity.class);
                            startActivity(intent);

                            return true;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });

        //Amigos fragments
        //CARGAMOS LA BIBLIOTECA DE PRIMERAS
        this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, (Fragment) new AmigosFragment()).commit();

        //Navbar
        BottomNavigationView navbar = (BottomNavigationView) this.findViewById(R.id.navigationView_amigos);
        navbar.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener)(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_amigos){
                    Perfil.this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, (Fragment) new AmigosFragment()).commit();//remplazo el blanco por el fragmento nuevo
                    return true;
                }
                else if(item.getItemId() == R.id.nav_solicitudes){
                    Perfil.this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, (Fragment) new AmigosSolicitudesFragment()).commit();
                    return true;
                }
                else if(item.getItemId() == R.id.nav_buscar){
                    Perfil.this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, (Fragment) new AmigosBuscarFragment()).commit();
                    return true;
                }
                return true;
            }
        }));


    }


    private void mostrarDialog(){
        Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.edit_profile_dialog);
        EditText nuevoNombre = dialog.findViewById(R.id.nuevoNombre);
        nuevoNombre.setText(nom);
        Button cancelar = dialog.findViewById(R.id.buttonCancelar);
        Button editar = dialog.findViewById(R.id.buttonEditar);


        nuevaFoto = dialog.findViewById(R.id.nuevaFoto);

        if (hayFoto){
            Glide.with(ctx)
                    .asBitmap()
                    .load(fPerfil)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                            roundedDrawable.setCircular(true);

                            nuevaFoto.setImageDrawable(roundedDrawable);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }


                    });
        }


        nuevaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);

            }
        });






        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SAUser saUser = new SAUser();

                saUser.updateUser(userN, nuevoNombre.getText().toString(), nuevafPerfil, hayFoto, new Callbacks() {
                    @Override
                    public void onCallbackExito(Boolean exito) {
                        if(exito){
                            Intent intent1 = new Intent(ctx, Perfil.class);
                            ctx.startActivity(intent1);
                            Toast.makeText(ctx,R.string.editarBien , Toast.LENGTH_SHORT).show();
                        }
                        else{
                            dialog.dismiss();
                            Toast.makeText(ctx,R.string.editarMal , Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();

            }
        });

        dialog.show();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data.getData() != null) {

            nuevafPerfil = data.getData();

            Glide.with(ctx)
                    .asBitmap()
                    .load(nuevafPerfil)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                            roundedDrawable.setCircular(true);

                            nuevaFoto.setImageDrawable(roundedDrawable);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }


                    });


        }




    }
}