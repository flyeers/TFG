package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.CapsuleInfo;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class MainActivity extends AppCompatActivity {


    Button verTodoCapsula, plus;
    ImageButton perfil;
    TextView nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
        }
        else{

            setContentView(R.layout.activity_main);

            nombre = findViewById(R.id.nombreUsuario);
            SAUser saUser = new SAUser();
            saUser.infoUsuario(currentUser.getEmail().toString(), new Callbacks() {
                @Override
                public void onCallback(UserInfo u) {
                    nombre.setText(u.getNombre());
                }
            });


            plus = findViewById(R.id.button2);
            plus.setBackgroundColor(getResources().getColor(R.color.rosaBoton));

            //caja especial para el añadir
            BoxInfo boxAdd = new BoxInfo("","ADD", null);
            CapsuleInfo capAdd = new CapsuleInfo("","ADD", null);

            /////////////////////////// CAPSULAS ///////////////////////////////
            //TODO
            Calendar calendar = new GregorianCalendar(2024, Calendar.JANUARY, 16, 12, 30, 0);
            Date date1 = calendar.getTime();

            Calendar calendar2 = new GregorianCalendar(2024, Calendar.MAY, 16, 12, 30, 0);
            Date date2 = calendar2.getTime();

           /* ArrayList<CapsuleInfo> c = new ArrayList<>();

            CapsuleInfo cap1 = new CapsuleInfo("","prueba", null);
            c.add(cap1);//add
            cap1.setApertura(date2); //cerrada
            cap1.setCierre(date1);
            c.add(cap1);


            CapAdapter c1 = new CapAdapter();
            c1.setCapData(c, false, true);
            RecyclerView recyclerView2 = findViewById(R.id.recycler_view_capsule);
            recyclerView2.setAdapter(c1);*/
            CapAdapter c1 = new CapAdapter();
            ArrayList<CapsuleInfo> capsules = new ArrayList<>();

            capsules.add(capAdd);
            saUser.getCapsules(currentUser.getEmail(), new Callbacks() {
                @Override
                public void onCallbackCapsules(ArrayList<CapsuleInfo> cs) {
                    capsules.addAll(cs);
                    c1.setCapData(capsules, true, true);
                    RecyclerView recyclerView = findViewById(R.id.recycler_view_capsule);
                    recyclerView.setAdapter(c1);
                }
            });

            /////////////////////////// CAJAS ///////////////////////////////
            BoxAdapter b2 = new BoxAdapter();
            ArrayList<BoxInfo> boxes = new ArrayList<>();

            boxes.add(boxAdd);

            saUser.getBoxes(currentUser.getEmail(), new Callbacks() {
                @Override
                public void onCallbackBoxes(ArrayList<BoxInfo> bs) {
                    boxes.addAll(bs);
                    b2.setBoxData(boxes, true, true);
                    RecyclerView recyclerView = findViewById(R.id.recycler_view_box);
                    recyclerView.setAdapter(b2);
                }
            });

            /////////////////////////// CAJAS COMPARTIDAS ///////////////////////////////
            BoxAdapter b3 = new BoxAdapter();
            ArrayList<BoxInfo> boxesShared = new ArrayList<>();
            boxesShared.add(boxAdd);

            LinearLayout lShare = findViewById(R.id.layoutShare);
            RecyclerView recyclerView3 = findViewById(R.id.recycler_view_share);

            saUser.getBoxesCompartidas(currentUser.getEmail(), new Callbacks() {
                @Override
                public void onCallbackBoxes(ArrayList<BoxInfo> bs) {
                    if(!bs.isEmpty()){
                        lShare.setVisibility(View.VISIBLE);
                        recyclerView3.setVisibility(View.VISIBLE);

                        boxesShared.addAll(bs);
                        b3.setBoxData(bs, true,true);
                        recyclerView3.setAdapter(b3);
                    }
                    else{
                        //Este Layout no se vera si no hay ninguna caja compartida
                        lShare.setVisibility(View.GONE);
                        recyclerView3.setVisibility(View.GONE);
                    }
                }
            });

            /////////////////////////// CAPSAULAS COMPARTIDAS ///////////////////////////////
            //TODO
            ArrayList<CapsuleInfo> ca = new ArrayList<>();
            CapsuleInfo cap2 = new CapsuleInfo("","prueba", null);
            ca.add(cap2);//add
            cap2.setApertura(date2); //cerrada
            cap2.setCierre(date1);
            ArrayList<String> col = new ArrayList<>();
            col.add("colab");
            cap2.setCollaborators(col);
            ca.add(cap2);

            CapAdapter c2 = new CapAdapter();
            c2.setCapData(ca, true, true);
            RecyclerView recyclerView4 = findViewById(R.id.recycler_view_share_cap);
            recyclerView4.setAdapter(c2);
            //LinearLayout lShareCap = findViewById(R.id.layoutShareCap);
            //lShareCap.setVisibility(View.GONE);
            //recyclerView4.setVisibility(View.GONE);

            /*saUser.getCapsulesCompartidas(currentUser.getEmail(), new Callbacks() {
                @Override
                public void onCallbackBoxes(ArrayList<BoxInfo> bs) {
                    if(!bs.isEmpty()){
                        lShareCap.setVisibility(View.VISIBLE);
                        recyclerView4.setVisibility(View.VISIBLE);

                        boxesShared.addAll(bs);
                        b3.setBoxData(bs, true,true);
                        RecyclerView recyclerView3 = findViewById(R.id.recycler_view_share);
                        recyclerView3.setAdapter(b3);
                    }
                    else{
                        //Este Layout no se vera si no hay ninguna caja compartida
                        lShare.setVisibility(View.GONE);
                        recyclerView4.setVisibility(View.GONE);
                    }
                }
            });*/

            //VER ALL
            //TODO
            Button verTodoCaja = findViewById(R.id.verTodoCaja);
            verTodoCaja.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    Intent intent = new Intent(ctx, VerTodo.class);
                    ctx.startActivity(intent);

                }
            });

            verTodoCapsula = findViewById(R.id.verTodoCapsula);
            verTodoCapsula.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    Intent intent = new Intent(ctx, VerTodo.class);
                    ctx.startActivity(intent);
                }
            });

            perfil = findViewById(R.id.perfil1);
            perfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context ctx = v.getContext();
                    Intent intent = new Intent(ctx, Perfil.class);
                    startActivity(intent);
                }
            });

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(ctx, R.style.EstiloMenu);
                    PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.add_menu, popup.getMenu());
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
}