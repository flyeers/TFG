package es.ucm.fdi.boxit.presentacion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.CapsuleInfo;
import es.ucm.fdi.boxit.negocio.SABox;
import es.ucm.fdi.boxit.negocio.SACapsule;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class VerTodo extends AppCompatActivity {

    private RecyclerView recyclerBox, recyclerCap;
    private TextView cap, box;
    private Button plus;
    private LinearLayout layBox, layCap;
    private SearchView sBox, sCap;
    private BoxAdapter b;
    private CapAdapter c;
    private ArrayList<BoxInfo> allBoxes;
    private List<BoxInfo> resBoxes;
    private ArrayList<CapsuleInfo> allCapsules;
    private List<CapsuleInfo> resCapsules;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertodo);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        SAUser saUser = new SAUser();

        cap = findViewById(R.id.capsulaName);
        box = findViewById(R.id.cajaName);

        allBoxes = new ArrayList<>();
        allCapsules = new ArrayList<>();
        resBoxes = new ArrayList<>();
        resCapsules = new ArrayList<>();

        recyclerBox = findViewById(R.id.reclyclerViewBox);
        recyclerCap = findViewById(R.id.reclyclerViewCap);
        layBox = findViewById(R.id.layBox);
        layCap = findViewById(R.id.layCap);

        b = new BoxAdapter();
        c = new CapAdapter();


        sBox = findViewById(R.id.search_box);
        sCap = findViewById(R.id.search_cap);

        boolean isBox = getIntent().getBooleanExtra("isBox", false);

        //Preparacion inicial
        if(isBox){
            layCap.setVisibility(View.GONE);
            box.setTextColor(getResources().getColor(R.color.rosaBoton));
            cap.setTextColor(getResources().getColor(R.color.rosaBotonClaro));
        }
        else{
            layBox.setVisibility(View.GONE);
            cap.setTextColor(getResources().getColor(R.color.rosaBoton));
            box.setTextColor(getResources().getColor(R.color.rosaBotonClaro));
        }

        //Botones cambio
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layBox.getVisibility() == View.GONE){
                    layCap.setVisibility(View.GONE);
                    layBox.setVisibility(View.VISIBLE);
                    box.setTextColor(getResources().getColor(R.color.rosaBoton));
                    cap.setTextColor(getResources().getColor(R.color.rosaBotonClaro));
                }
            }
        });
        cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layCap.getVisibility() == View.GONE){
                    layBox.setVisibility(View.GONE);
                    layCap.setVisibility(View.VISIBLE);
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


        Context ctx = this;
        saUser.infoUsuario(currentUser.getEmail().toString(), new Callbacks() {
            @Override
            public void onCallback(UserInfo u) {

                String f = u.getImgPerfil().toString();
                if (f != ""){

                    Glide.with(ctx)
                            .asBitmap()
                            .load(u.getImgPerfil())
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                    roundedDrawable.setCircular(true);

                                    perfil.setImageDrawable(roundedDrawable);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }


                            });

                }

            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerTodo.this, Perfil.class);
                startActivity(intent);
            }
        });

        plus = findViewById(R.id.button2);
        plus.setBackgroundColor(getResources().getColor(R.color.rosaBoton));

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

        //Busquedas
        sBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                b = new BoxAdapter();
                if(newText.equals("")){ //toda la lista
                    b.setBoxData(allBoxes, true, false);
                    recyclerBox.setAdapter(b);
                }
                else{
                    resBoxes =  allBoxes.stream()
                            .filter(boxInfo -> boxInfo.getTitle().toLowerCase().contains(newText.toLowerCase()))
                            .collect(Collectors.toList());

                    b.setBoxData(resBoxes, true, false);
                    recyclerBox.setAdapter(b);
                }
                return false;
            }
        });

        sCap.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                c = new CapAdapter();
                if(newText.equals("")){ //toda la lista
                    c.setCapData(allCapsules, true, false, VerTodo.this);
                    recyclerCap.setAdapter(c);
                }
                else{
                    resCapsules =  allCapsules.stream()
                            .filter(capInfo -> capInfo.getTitle().toLowerCase().contains(newText.toLowerCase()))
                            .collect(Collectors.toList());
                    c.setCapData(resCapsules, true, false, VerTodo.this);
                    recyclerCap.setAdapter(c);
                }

                return false;
            }
        });

        //Carga info

        saUser.getBoxes(currentUser.getEmail(), new Callbacks() {
            @Override
            public void onCallbackBoxes(ArrayList<BoxInfo> boxes) {
                allBoxes.addAll(boxes);
            }
        });
        saUser.getBoxesCompartidas(currentUser.getEmail(), new Callbacks() {
            @Override
            public void onCallbackBoxes(ArrayList<BoxInfo> boxes) {
                allBoxes.addAll(boxes);
            }
        });

        saUser.getCapsules(currentUser.getEmail(), new Callbacks() {
            @Override
            public void onCallbackCapsules(ArrayList<CapsuleInfo> capsules) {
                allCapsules.addAll(capsules);
            }
        });
        saUser.getCapsulesCompartidas(currentUser.getEmail(), new Callbacks() {
            @Override
            public void onCallbackCapsules(ArrayList<CapsuleInfo> capsules) {
                allCapsules.addAll(capsules);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Collections.sort(allBoxes, Comparator.comparing(BoxInfo::getTitle));
                b.setBoxData(allBoxes, true, false);
                recyclerBox.setAdapter(b);

                Collections.sort(allCapsules, Comparator.comparing(CapsuleInfo::getTitle));
                c.setCapData(allCapsules, true, false,VerTodo.this);
                recyclerCap.setAdapter(c);            }
        }, 1000);

    }
}
