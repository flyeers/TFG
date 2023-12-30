package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import es.ucm.fdi.boxit.R;

public class Caja extends AppCompatActivity {

    private Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja);

        add = findViewById(R.id.buttonAdd2);
        add.setBackgroundColor(getResources().getColor(R.color.rosaBoton));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(ctx, R.style.EstiloMenu);
                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.add_menu_elementos, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int id = item.getItemId();
                        if(id == R.id.nuevaCajaMenu){

                            boolean isBox = true;
                            Intent intent = new Intent(ctx, Crear.class);
                            intent.putExtra("TIPO", isBox);
                            ctx.startActivity(intent);
                            return true;
                        }
                        else if(id == R.id.nuevaCapsulaMenu){
                            boolean isBox = false;
                            Intent intent = new Intent(ctx, Crear.class);
                            intent.putExtra("TIPO", isBox);
                            ctx.startActivity(intent);

                            return true;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });
    }
}