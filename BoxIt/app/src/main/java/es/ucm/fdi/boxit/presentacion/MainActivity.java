package es.ucm.fdi.boxit.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
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
        /*
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);*/

        /*
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("AHHH", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("AHHH", "Error getting documents.", task.getException());
                        }
                    }
                });*/

            android.net.Uri img = null;
            BoxInfo boxAdd = new BoxInfo("","ADD", img);
            BoxInfo box1 = new BoxInfo("","prueba", img);
            BoxInfo box2 = new BoxInfo("","ahhhhhhhhhhhh", img);
            BoxInfo box3 = new BoxInfo("","help", img);
            ArrayList<BoxInfo> a = new ArrayList<>();
            a.add(boxAdd);
            a.add(box1);
            a.add(box2);
            a.add(box3);

            /////////////////////////// CAPSULAS ///////////////////////////////
            BoxAdapter b = new BoxAdapter();
            b.setBoxData(a, false, true);
            RecyclerView recyclerView2 = findViewById(R.id.recycler_view_capsule);
            recyclerView2.setAdapter(b);


            /////////////////////////// CAJAS ///////////////////////////////
            BoxAdapter b2 = new BoxAdapter();
            ArrayList<BoxInfo> boxes = new ArrayList<>();


            //card para la card de añadir caja
           // b2.setBoxData(a, true,true);
            //android.net.Uri selectedImage=null;
           // BoxInfo boxAdd = new BoxInfo("", selectedImage);

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


            /*BoxAdapter b2 = new BoxAdapter();
            b2.setBoxData(a, true,true);
            RecyclerView recyclerView = findViewById(R.id.recycler_view_box);
            recyclerView.setAdapter(b2);*/


            /////////////////////////// COMPARTIDO ///////////////////////////////
            BoxAdapter b3 = new BoxAdapter();
            b3.setBoxData(a, true,true);
            RecyclerView recyclerView3 = findViewById(R.id.recycler_view_share);
            recyclerView3.setAdapter(b3);

            //Este Layout no se vera si no hay ninguna caja compartida
        /*LinearLayout lShare = findViewById(R.id.layoutShare);
        lShare.setVisibility(View.GONE);
        recyclerView3.setVisibility(View.GONE);*/

            //VER ALL

            Button verTodoCaja = findViewById(R.id.verTodoCaja);
            verTodoCaja.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    Intent intent = new Intent(ctx, VerTodoCaja.class);
                    ctx.startActivity(intent);

                }
            });

            verTodoCapsula = findViewById(R.id.verTodoCapsula);
            verTodoCapsula.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    Intent intent = new Intent(ctx, VerTodoCapsula.class);
                    ctx.startActivity(intent);

                }
            });

            perfil = findViewById(R.id.perfil1);

            //CIERRA SESION POR AHORA !!!!! ES TEMPORAL *****************************
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