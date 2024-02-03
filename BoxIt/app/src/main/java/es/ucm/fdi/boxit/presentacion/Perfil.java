package es.ucm.fdi.boxit.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class Perfil extends AppCompatActivity {

    private TextView nombreUsuario, correo;
    private ImageButton opt, notificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        nombreUsuario = findViewById(R.id.nombreUsuarioPerfil);
        correo = findViewById(R.id.correoUsuarioPerfil);
        notificaciones = findViewById(R.id.buttonNotification);
        opt = findViewById(R.id.perfilOpciones);

        SAUser saUser = new SAUser();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        saUser.infoUsuario(currentUser.getEmail().toString(), new Callbacks() {
            @Override
            public void onCallback(UserInfo u) {
                nombreUsuario.setText(u.getNombreUsuario());
                correo.setText(currentUser.getEmail().toString());
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
                            //mostrarDialog();
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
}