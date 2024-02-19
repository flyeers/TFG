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

import com.bumptech.glide.Glide;
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


    Button verTodoCapsula, verTodoCaja, verTodoCapsulaComp, verTodoCajaComp, plus;
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
            CapAdapter c2 = new CapAdapter();
            ArrayList<CapsuleInfo> capsulesShared = new ArrayList<>();
            capsulesShared.add(capAdd);

            LinearLayout lShareCap = findViewById(R.id.layoutShareCap);
            RecyclerView recyclerView4 = findViewById(R.id.recycler_view_share_cap);

            saUser.getCapsulesCompartidas(currentUser.getEmail(), new Callbacks() {
                @Override
                public void onCallbackCapsules(ArrayList<CapsuleInfo> cs) {
                    if(!cs.isEmpty()){
                        lShareCap.setVisibility(View.VISIBLE);
                        recyclerView4.setVisibility(View.VISIBLE);

                        capsulesShared.addAll(cs);
                        c2.setCapData(cs, true,true);
                        recyclerView4.setAdapter(b3);
                    }
                    else{
                        //Este Layout no se vera si no hay ninguna caja compartida
                        lShareCap.setVisibility(View.GONE);
                        recyclerView4.setVisibility(View.GONE);
                    }
                }
            });

            //Ver tod botones
            verTodoCaja = findViewById(R.id.verTodoCaja);
            verTodoCapsula = findViewById(R.id.verTodoCapsula);
            verTodoCapsulaComp = findViewById(R.id.verTodoCompartidoCap);
            verTodoCajaComp = findViewById(R.id.verTodoCompartido);
            View.OnClickListener onClickListener1 = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    Intent intent = new Intent(ctx, VerTodo.class);
                    intent.putExtra("isBox", true);
                    ctx.startActivity(intent);
                }
            };
            verTodoCaja.setOnClickListener(onClickListener1);
            verTodoCajaComp.setOnClickListener(onClickListener1);

            View.OnClickListener onClickListener2 = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    Intent intent = new Intent(ctx, VerTodo.class);
                    intent.putExtra("isBox", false);
                    ctx.startActivity(intent);
                }
            };
            verTodoCapsula.setOnClickListener(onClickListener2);
            verTodoCapsulaComp.setOnClickListener(onClickListener2);

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