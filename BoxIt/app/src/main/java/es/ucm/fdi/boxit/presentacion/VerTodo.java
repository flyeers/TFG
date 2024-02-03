package es.ucm.fdi.boxit.presentacion;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import es.ucm.fdi.boxit.R;

public class VerTodo extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertodo);


        //CARGAMOS LA BIBLIOTECA DE PRIMERAS
        ///TODO cambiar
        this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, (Fragment) new VerTodoCajaFragment()).commit();

        //Navbar
        BottomNavigationView navbar = (BottomNavigationView) this.findViewById(R.id.navigationViewVerTodo);
        navbar.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener)(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_cajas){
                    VerTodo.this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, (Fragment) new VerTodoCajaFragment()).commit();//remplazo el blanco por el fragmento nuevo
                    return true;
                }
                else if(item.getItemId() == R.id.nav_capsulas){
                    VerTodo.this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, (Fragment) new VerTodoCapsulaFragment()).commit();
                    return true;
                }
                return true;
            }
        }));
    }
}
