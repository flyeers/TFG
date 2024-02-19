package es.ucm.fdi.boxit.presentacion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.CapsuleInfo;
import es.ucm.fdi.boxit.negocio.SABox;
import es.ucm.fdi.boxit.negocio.SACapsule;
import es.ucm.fdi.boxit.negocio.SAUser;

public class VerTodo extends AppCompatActivity {

    private RecyclerView recyclerBox, recyclerCap;
    private TextView cap, box;
    private ArrayList<BoxInfo> allBoxes;
    private ArrayList<CapsuleInfo> allCapsules;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertodo);

        cap = findViewById(R.id.capsulaName);
        box = findViewById(R.id.cajaName);

        allBoxes = new ArrayList<>();
        allCapsules = new ArrayList<>();

        recyclerBox = findViewById(R.id.reclyclerViewBox);
        recyclerCap = findViewById(R.id.reclyclerViewCap);
        boolean isBox = getIntent().getBooleanExtra("isBox", false);

        //Preparacion inicial
        if(isBox){
            recyclerCap.setVisibility(View.GONE);
            box.setTextColor(getResources().getColor(R.color.rosaBoton));
            cap.setTextColor(getResources().getColor(R.color.rosaBotonClaro));
        }
        else{
            recyclerBox.setVisibility(View.GONE);
            cap.setTextColor(getResources().getColor(R.color.rosaBoton));
            box.setTextColor(getResources().getColor(R.color.rosaBotonClaro));
        }

        //Botones cambio
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerBox.getVisibility() == View.GONE){
                    recyclerCap.setVisibility(View.GONE);
                    recyclerBox.setVisibility(View.VISIBLE);
                    box.setTextColor(getResources().getColor(R.color.rosaBoton));
                    cap.setTextColor(getResources().getColor(R.color.rosaBotonClaro));
                }
            }
        });
        cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerCap.getVisibility() == View.GONE){
                    recyclerBox.setVisibility(View.GONE);
                    recyclerCap.setVisibility(View.VISIBLE);
                    cap.setTextColor(getResources().getColor(R.color.rosaBoton));
                    box.setTextColor(getResources().getColor(R.color.rosaBotonClaro));
                }
            }
        });

        //Botones adicionales
        ImageView home = findViewById(R.id.homeBtn);
        ImageView perfil = findViewById(R.id.perfil);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerTodo.this, MainActivity.class);
                startActivity(intent);
            }
        });
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerTodo.this, Perfil.class);
                startActivity(intent);
            }
        });

        /*
        //Carga info
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        SAUser saUser = new SAUser();
        saUser.getBoxes(currentUser.getEmail(), new Callbacks() {
            @Override
            public void onCallbackBoxes(ArrayList<BoxInfo> boxes) {
                allBoxes.addAll(boxes);
                saUser.getBoxesCompartidas(currentUser.getEmail(), new Callbacks() {
                    @Override
                    public void onCallbackBoxes(ArrayList<BoxInfo> boxes) {
                        allBoxes.addAll(boxes);
                        Collections.sort(allBoxes, Comparator.comparing(BoxInfo::getTitle));
                        BoxAdapter b = new BoxAdapter();
                        b.setBoxData(allBoxes, true, false);
                        recyclerBox.setAdapter(b);

                    }
                });
            }
        });

        saUser.getCapsules(currentUser.getEmail(), new Callbacks() {
            @Override
            public void onCallbackCapsules(ArrayList<CapsuleInfo> capsules) {
                allCapsules.addAll(capsules);
                saUser.getCapsulesCompartidas(currentUser.getEmail(), new Callbacks() {
                    @Override
                    public void onCallbackCapsules(ArrayList<CapsuleInfo> capsules) {
                        allCapsules.addAll(capsules);
                        Collections.sort(allCapsules, Comparator.comparing(CapsuleInfo::getTitle));
                        CapAdapter c = new CapAdapter();
                        c.setCapData(allCapsules, true, false);
                        recyclerCap.setAdapter(c);

                    }
                });
            }
        });*/

    }
}
