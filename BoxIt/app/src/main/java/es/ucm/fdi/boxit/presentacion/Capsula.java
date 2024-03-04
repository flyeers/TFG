package es.ucm.fdi.boxit.presentacion;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telecom.Call;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Track;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.CapsuleInfo;
import es.ucm.fdi.boxit.negocio.MusicInfo;
import es.ucm.fdi.boxit.negocio.SABox;
import es.ucm.fdi.boxit.negocio.SACapsule;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class Capsula extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Button add, borrarFoto;
    private TextView nombre, tiempo, fotos, musica, notas, documentos, audio, textoFotos1, textoFotos2, textoInicio, verTodo;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int PICK_PDF_REQUEST_CODE = 2;
    private static final int RESULT_OK = -1;
    private Context ctx;
    private CapsuleInfo capsuleInfo;
    private String imagePath;
    private ElementsAdapter photoAdapter, docAdapter, noteAdapter;
    private MusicAdapter musicAdapter;
    private List<String> documents_b, photos_b, notes_b;
    private List<MusicInfo> music_b;
    private android.net.Uri selectedItem = null;
    private int numfotos = 0;
    private boolean fotoPulsado, docPulsado, notasPulsado, musicaPulsado;
    private ImageView home, delete, exit, colab, ellipse;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final String CLIENT_ID = "84e06632856840c38d929188d2bfd919";
    private static final String REDIRECT_URI = "com.spotify.boxit://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private Track track;
    private String songTitle, artist, songUri;
    private ImageUri songImage;
    private MusicInfo musicInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capsula);

        ctx = this;
        add = findViewById(R.id.buttonAdd2);
        add.setBackgroundColor(getResources().getColor(R.color.rosaBoton));

        fotoPulsado = false;
        docPulsado = false;
        notasPulsado = false;
        musicaPulsado = false;

        photos_b = new ArrayList<>();
        documents_b = new ArrayList<>();
        notes_b = new ArrayList<>();
        music_b = new ArrayList<>();

        capsuleInfo = getIntent().getParcelableExtra("capsuleInfo");

        nombre = findViewById(R.id.nombre_caja);
        nombre.setText(capsuleInfo.getTitle());

        fotos = findViewById(R.id.fotosCaja);
        musica = findViewById(R.id.musicaCaja);
        notas = findViewById(R.id.notasCaja);
        textoFotos2 = findViewById(R.id.fdelacaja);
        textoFotos1 = findViewById(R.id.fotosdelacaja);
        textoInicio = findViewById(R.id.todoElContenidoCaja);
        verTodo = findViewById(R.id.vertodo);
        tiempo = findViewById(R.id.textTime);
        documentos = findViewById(R.id.documentosCaja);

        home = findViewById(R.id.homeBtn);
        delete = findViewById(R.id.delete);
        exit = findViewById(R.id.exit);
        colab = findViewById(R.id.colab);

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        docAdapter = new ElementsAdapter();
        photoAdapter = new ElementsAdapter();
        noteAdapter = new ElementsAdapter();
        musicAdapter = new MusicAdapter();


        textoInicio.setText(getResources().getString(R.string.tododelacapsula));
        getAll();
        Date cd = Calendar.getInstance().getTime();
        if(cd.before(capsuleInfo.getCierre())){//antes del cierre

            Long d = capsuleInfo.getApertura().getTime() - cd.getTime();
            long dias = TimeUnit.MILLISECONDS.toDays(d);
            long horas = TimeUnit.MILLISECONDS.toHours(d) % 24;
            long minutos = TimeUnit.MILLISECONDS.toMinutes(d) % 60;
            tiempo.setText(dias+"D. "+horas+"H. "+minutos+"M. ");

        }else{
            tiempo.setText(getResources().getString(R.string.abiertaPermanente));
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(ctx, MainActivity.class);
                ctx.startActivity(intent1);

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(ctx, R.style.EstiloMenu);
                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_menu, popup.getMenu());
                // Forzamos los iconos
                try {
                    Field field = popup.getClass().getDeclaredField("mPopup");
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> cls = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = cls.getDeclaredMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int id = item.getItemId();
                        if(id == R.id.editElem){
                            Context ctx = v.getContext();
                            Intent intent = new Intent(ctx, CrearCapsulaForm.class);
                            intent.putExtra("DisingData", capsuleInfo);
                            intent.putExtra("Crear", false);
                            ctx.startActivity(intent);
                            return true;
                        }
                        else if(id == R.id.deleteElem){
                            Dialog dialogConfirm = new Dialog(ctx);
                            dialogConfirm.setContentView(R.layout.eliminar_confirm);
                            Button cancelar = dialogConfirm.findViewById(R.id.buttonCancelar);
                            Button confirmar = dialogConfirm.findViewById(R.id.buttonEliminar);

                            cancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogConfirm.dismiss();

                                }
                            });

                            confirmar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SABox saBox = new SABox();
                                    saBox.deleteBox(capsuleInfo, false, new Callbacks() {
                                        @Override
                                        public void onCallbackExito(Boolean exito) {
                                            if(exito){
                                                Intent intent1 = new Intent(ctx, MainActivity.class);
                                                ctx.startActivity(intent1);
                                                Toast.makeText(ctx,R.string.deleteBien , Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                dialogConfirm.dismiss();
                                                Toast.makeText(ctx,R.string.deleteMal , Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });

                            dialogConfirm.show();
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });



        if(!capsuleInfo.getColaborators().isEmpty()){
            exit.setVisibility(View.VISIBLE);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialogConfirm = new Dialog(ctx);
                    dialogConfirm.setContentView(R.layout.exit_confirm);
                    TextView text = dialogConfirm.findViewById(R.id.textAbandono);
                    text.setText(getResources().getString(R.string.abandonarCap));
                    Button cancelar = dialogConfirm.findViewById(R.id.buttonCancelar);
                    Button confirmar = dialogConfirm.findViewById(R.id.buttonEliminar);

                    cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirm.dismiss();
                        }
                    });

                    confirmar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SACapsule saCapsule = new SACapsule();
                            saCapsule.exitCapsule(capsuleInfo.getId(), new Callbacks() {
                                @Override
                                public void onCallbackExito(Boolean exito) {
                                    if(exito){
                                        Intent intent1 = new Intent(ctx, MainActivity.class);
                                        ctx.startActivity(intent1);
                                        Toast.makeText(ctx,R.string.abandonoBien , Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        dialogConfirm.dismiss();
                                        Toast.makeText(ctx,R.string.abandonoMal , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                    dialogConfirm.show();
                }
            });

            colab.setVisibility(View.VISIBLE);
            colab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialogColab = new Dialog(ctx);
                    dialogColab.setContentView(R.layout.collaborators_dialog);
                    Button cancelar = dialogColab.findViewById(R.id.buttonCancelar);
                    RecyclerView colab = dialogColab.findViewById(R.id.recyclerColab);

                    SABox saBox = new SABox();
                    saBox.getCollaborators(capsuleInfo.getId(), false, new Callbacks() {
                        @Override
                        public void onCallbackUsers(ArrayList<UserInfo> users) {
                            UsersAdapter usersAdapter = new UsersAdapter();
                            usersAdapter.setReadOnly(true);
                            usersAdapter.setUserData(users);
                            colab.setAdapter(usersAdapter);
                        }
                    });

                    cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogColab.dismiss();
                        }
                    });
                    dialogColab.show();
                }
            });
        }


        verTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fotoPulsado){
                    fotoPulsado = false;
                    fotos.setBackgroundResource(android.R.color.transparent);
                    fotos.setTextColor(getResources().getColor(R.color.rosaBoton));
                    textoFotos1.setText("");
                    textoFotos2.setText("");
                }
                if (docPulsado){
                    docPulsado = false;
                    documentos.setBackgroundResource(android.R.color.transparent);
                    documentos.setTextColor(getResources().getColor(R.color.rosaBoton));
                    textoFotos2.setText("");
                    textoFotos1.setText("");
                }
                if(notasPulsado){
                    notasPulsado = false;
                    notas.setBackgroundResource(android.R.color.transparent);
                    notas.setTextColor(getResources().getColor(R.color.rosaBoton));
                    textoFotos2.setText("");
                    textoFotos1.setText("");
                }
                if(musicaPulsado){
                    musicaPulsado = false;
                    musica.setBackgroundResource(android.R.color.transparent);
                    musica.setTextColor(getResources().getColor(R.color.rosaBoton));
                    textoFotos2.setText("");
                    textoFotos1.setText("");

                }

                findViewById(R.id.recyclerdocsCaja).setVisibility(View.VISIBLE);
                findViewById(R.id.recyclerfotosCaja).setVisibility(View.VISIBLE);
                findViewById(R.id.recyclernotasCaja).setVisibility(View.VISIBLE);
                findViewById(R.id.recyclermusicaCaja).setVisibility(View.VISIBLE);

            }
        });

        fotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!fotoPulsado){
                    fotoPulsado = true;

                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setCornerRadius(20);
                    drawable.setColor(getResources().getColor(R.color.rosaBoton)); // Cambia el color según lo desees

                    fotos.setBackground(drawable);


                    fotos.setTextColor(getResources().getColor(R.color.fondoClaro));
                    findViewById(R.id.recyclerfotosCaja).setVisibility(View.VISIBLE);
                    textoFotos1.setText(getResources().getString(R.string.galeria));
                    textoFotos2.setText(getResources().getString(R.string.delacapsula));
                    photoAdapter.setElementsData(photos_b, true, false, false, ctx, capsuleInfo);
                    photoAdapter.setType(false);
                    RecyclerView recyclerView = findViewById(R.id.recyclerfotosCaja);
                    recyclerView.setAdapter(photoAdapter);
                }
                else{
                    fotoPulsado = false;
                    fotos.setBackgroundResource(android.R.color.transparent);
                    fotos.setTextColor(getResources().getColor(R.color.rosaBoton));
                    findViewById(R.id.recyclerfotosCaja).setVisibility(View.GONE);
                    textoFotos1.setText("");
                    textoFotos2.setText("");

                }

                if(docPulsado){
                    docPulsado = false;
                    documentos.setBackgroundResource(android.R.color.transparent);
                    documentos.setTextColor(getResources().getColor(R.color.rosaBoton));

                }
                if(notasPulsado){
                    notasPulsado = false;
                    notas.setBackgroundResource(android.R.color.transparent);
                    notas.setTextColor(getResources().getColor(R.color.rosaBoton));

                }
                if(musicaPulsado){
                    musicaPulsado = false;
                    musica.setBackgroundResource(android.R.color.transparent);
                    musica.setTextColor(getResources().getColor(R.color.rosaBoton));
                }

                findViewById(R.id.recyclernotasCaja).setVisibility(View.GONE);
                findViewById(R.id.recyclerdocsCaja).setVisibility(View.GONE);
                findViewById(R.id.recyclermusicaCaja).setVisibility(View.GONE);

            }
        });

        documentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!docPulsado){
                    docPulsado = true;
                    findViewById(R.id.recyclerdocsCaja).setVisibility(View.VISIBLE);


                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setCornerRadius(20);
                    drawable.setColor(getResources().getColor(R.color.rosaBoton)); // Cambia el color según lo desees

                    documentos.setBackground(drawable);
                    documentos.setTextColor(getResources().getColor(R.color.fondoClaro));

                    textoFotos1.setText(getResources().getString(R.string.docs));
                    textoFotos2.setText(getResources().getString(R.string.delacapsula));

                    docAdapter.setElementsData(documents_b, false, true, false, ctx, capsuleInfo);
                    RecyclerView recyclerView = findViewById(R.id.recyclerdocsCaja);
                    recyclerView.setAdapter(docAdapter);
                }
                else{
                    docPulsado = false;
                    documentos.setBackgroundResource(android.R.color.transparent);
                    documentos.setTextColor(getResources().getColor(R.color.rosaBoton));

                    findViewById(R.id.recyclerdocsCaja).setVisibility(View.GONE);
                    textoFotos2.setText("");
                    textoFotos1.setText("");

                }

                if(fotoPulsado){
                    fotoPulsado = false;
                    fotos.setBackgroundResource(android.R.color.transparent);
                    fotos.setTextColor(getResources().getColor(R.color.rosaBoton));

                }
                if(notasPulsado){
                    notasPulsado = false;
                    notas.setBackgroundResource(android.R.color.transparent);
                    notas.setTextColor(getResources().getColor(R.color.rosaBoton));

                }
                if(musicaPulsado){
                    musicaPulsado = false;
                    musica.setBackgroundResource(android.R.color.transparent);
                    musica.setTextColor(getResources().getColor(R.color.rosaBoton));
                }


                findViewById(R.id.recyclernotasCaja).setVisibility(View.GONE);
                findViewById(R.id.recyclerfotosCaja).setVisibility(View.GONE);
                findViewById(R.id.recyclermusicaCaja).setVisibility(View.GONE);

            }
        });

        notas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!notasPulsado){
                    notasPulsado = true;
                    findViewById(R.id.recyclernotasCaja).setVisibility(View.VISIBLE);

                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setCornerRadius(20);
                    drawable.setColor(getResources().getColor(R.color.rosaBoton));

                    notas.setBackground(drawable);
                    notas.setTextColor(getResources().getColor(R.color.fondoClaro));

                    textoFotos1.setText(getResources().getString(R.string.notas));
                    textoFotos2.setText(getResources().getString(R.string.delacapsula));

                    noteAdapter.setElementsData(notes_b, false, false, true,  ctx, capsuleInfo);
                    RecyclerView recyclerView = findViewById(R.id.recyclernotasCaja);
                    recyclerView.setAdapter(noteAdapter);
                }
                else{
                    notasPulsado = false;
                    notas.setBackgroundResource(android.R.color.transparent);
                    notas.setTextColor(getResources().getColor(R.color.rosaBoton));

                    findViewById(R.id.recyclernotasCaja).setVisibility(View.GONE);
                    textoFotos2.setText("");
                    textoFotos1.setText("");

                }

                if(fotoPulsado){
                    fotoPulsado = false;
                    fotos.setBackgroundResource(android.R.color.transparent);
                    fotos.setTextColor(getResources().getColor(R.color.rosaBoton));

                }
                if(docPulsado){
                    docPulsado = false;
                    documentos.setBackgroundResource(android.R.color.transparent);
                    documentos.setTextColor(getResources().getColor(R.color.rosaBoton));

                }
                if(musicaPulsado){
                    musicaPulsado = false;
                    musica.setBackgroundResource(android.R.color.transparent);
                    musica.setTextColor(getResources().getColor(R.color.rosaBoton));
                }

                findViewById(R.id.recyclerdocsCaja).setVisibility(View.GONE);
                findViewById(R.id.recyclerfotosCaja).setVisibility(View.GONE);
                findViewById(R.id.recyclermusicaCaja).setVisibility(View.GONE);


            }
        });

        musica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!musicaPulsado){
                    musicaPulsado = true;
                    findViewById(R.id.recyclermusicaCaja).setVisibility(View.VISIBLE);

                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setCornerRadius(20);
                    drawable.setColor(getResources().getColor(R.color.rosaBoton));

                    musica.setBackground(drawable);
                    musica.setTextColor(getResources().getColor(R.color.fondoClaro));

                    textoFotos1.setText(getResources().getString(R.string.musica));
                    textoFotos2.setText(getResources().getString(R.string.delacapsula));

                    musicAdapter.setData(music_b, mSpotifyAppRemote, capsuleInfo.getId(), ctx);
                    musicAdapter.setCapsula();
                    RecyclerView recyclerView = findViewById(R.id.recyclermusicaCaja);
                    recyclerView.setAdapter(musicAdapter);
                }
                else{
                    musicaPulsado = false;
                    musica.setBackgroundResource(android.R.color.transparent);
                    musica.setTextColor(getResources().getColor(R.color.rosaBoton));

                    findViewById(R.id.recyclermusicaCaja).setVisibility(View.GONE);
                    textoFotos2.setText("");
                    textoFotos1.setText("");

                }

                if(fotoPulsado){
                    fotoPulsado = false;
                    fotos.setBackgroundResource(android.R.color.transparent);
                    fotos.setTextColor(getResources().getColor(R.color.rosaBoton));

                }
                if(docPulsado){
                    docPulsado = false;
                    documentos.setBackgroundResource(android.R.color.transparent);
                    documentos.setTextColor(getResources().getColor(R.color.rosaBoton));

                }
                if(notasPulsado){
                    notasPulsado = false;
                    notas.setBackgroundResource(android.R.color.transparent);
                    notas.setTextColor(getResources().getColor(R.color.rosaBoton));
                }

                findViewById(R.id.recyclerdocsCaja).setVisibility(View.GONE);
                findViewById(R.id.recyclerfotosCaja).setVisibility(View.GONE);
                findViewById(R.id.recyclernotasCaja).setVisibility(View.GONE);

            }
        });

        //cover
        ellipse = findViewById(R.id.ellipse_13);
        Glide.with(this)
                .load(capsuleInfo.getImg())
                .transform(new CenterCrop(), new RoundedCorners(5000))
                .into(ellipse);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(ctx, R.style.EstiloMenu);
                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.add_menu_elementos, popup.getMenu());
                // Forzamos los iconos
                try {
                    Field field = popup.getClass().getDeclaredField("mPopup");
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> cls = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = cls.getDeclaredMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int id = item.getItemId();
                        if(id == R.id.addCamara){
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);


                            return true;
                        }
                        else if(id == R.id.addGaleria){
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);


                            return true;
                        }
                        else if(id == R.id.addMusic){



                            conectarSpotify(true, new Callbacks() {
                            });



                            return true;
                        }
                        else if(id == R.id.addDoc){

                            Intent docsIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            docsIntent.setType("application/pdf"); // Seleccionar solo archivos de tipo PDF
                            docsIntent.addCategory(Intent.CATEGORY_OPENABLE);


                            startActivityForResult(docsIntent, PICK_PDF_REQUEST_CODE);
                            return true;
                        }
                        else if(id == R.id.addNota){

                            Dialog dialogNote = new Dialog(ctx);
                            dialogNote.setContentView(R.layout.note_preview);
                            Button cancelar = dialogNote.findViewById(R.id.buttonCancelar);
                            Button confirmar = dialogNote.findViewById(R.id.buttonAceptar);
                            EditText textNote = dialogNote.findViewById(R.id.textNote);

                            cancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogNote.dismiss();
                                }
                            });

                            confirmar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String note = String.valueOf(textNote.getText());

                                    SABox saBox = new SABox();
                                    saBox.addNote(capsuleInfo , note, false, new Callbacks() {
                                        @Override
                                        public void onCallbackData(String data) {
                                            if(!data.equals("")){
                                                noteAdapter.addElem(data, null);
                                                noteAdapter.notifyDataSetChanged();
                                                dialogNote.dismiss();
                                                Toast.makeText(ctx,R.string.addBien , Toast.LENGTH_SHORT).show();

                                            }
                                            else{
                                                dialogNote.dismiss();
                                                Toast.makeText(ctx,R.string.addMal , Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            dialogNote.show();

                            return true;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        this.recreate();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        SABox saBox = new SABox();

        //si lo añadido es una foto, ya sea por camara o por galeria:
        if(requestCode == PICK_IMAGE_REQUEST || requestCode == CAMERA_REQUEST_CODE ){
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data.getData() != null) {

                selectedItem = data.getData();
                saBox.addPhotos(capsuleInfo, selectedItem.toString(), false, new Callbacks() {
                    @Override
                    public void onCallbackData(String data) {
                        if(!data.equals("")){
                            photoAdapter.addElem(data, null);
                            photoAdapter.notifyDataSetChanged();
                            Toast.makeText(ctx,R.string.addBienCap , Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ctx,R.string.addMal , Toast.LENGTH_SHORT).show();
                        }
                    }


                });
            }
            else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");



                saBox.addPhotosFromCamera(capsuleInfo, imageBitmap, false, new Callbacks() {
                    @Override
                    public void onCallbackExito(Boolean exito) {
                        if(exito){

                            saBox.getPhotos(capsuleInfo.getId(), false, new Callbacks() {
                                @Override
                                public void onCallbackItems(ArrayList<String> photos) {
                                    photoAdapter.setElementsData(photos, true, false, false, ctx, capsuleInfo);
                                    photoAdapter.setType(false);
                                    RecyclerView recyclerView = findViewById(R.id.recyclerfotosCaja);
                                    recyclerView.setAdapter(photoAdapter);

                                }
                            });

                            Toast.makeText(ctx,R.string.addBienCap , Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ctx,R.string.addMal , Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }


        }
        //si lo añadido es un doc
        else if (requestCode == PICK_PDF_REQUEST_CODE && resultCode == RESULT_OK) {

            selectedItem = data.getData();
            String fileName = getFileName(selectedItem);

            saBox.addDocs(capsuleInfo, selectedItem.toString(), fileName, false, new Callbacks() {
                @Override
                public void onCallbackExito(Boolean exito) {
                    if(exito){

                        saBox.getDocs(capsuleInfo.getId(), false, new Callbacks() {
                            @Override
                            public void onCallbackItems(ArrayList<String> items) {

                                docAdapter.setElementsData(items, false, true, false, ctx, capsuleInfo);
                                RecyclerView recyclerView = findViewById(R.id.recyclerdocsCaja);
                                recyclerView.setAdapter(docAdapter);
                            }
                        });
                        Toast.makeText(ctx,R.string.addBienCap , Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ctx,R.string.addMal , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    public void getAll(){
        SABox saBox = new SABox();
        saBox.getDocs(capsuleInfo.getId(), false, new Callbacks() {
            @Override
            public void onCallbackItems(ArrayList<String> docs) {
                documents_b = docs;
                docAdapter.setElementsData(documents_b, false, true, false, ctx, capsuleInfo);
                RecyclerView recyclerView = findViewById(R.id.recyclerdocsCaja);
                recyclerView.setAdapter(docAdapter);
            }
        });
        saBox.getPhotos(capsuleInfo.getId(), false, new Callbacks() {
            @Override
            public void onCallbackItems(ArrayList<String> photos) {

                photos_b = photos;
                photoAdapter.setElementsData(photos_b, true, false, false, ctx, capsuleInfo);
                photoAdapter.setType(false);
                RecyclerView recyclerView = findViewById(R.id.recyclerfotosCaja);
                recyclerView.setAdapter(photoAdapter);

            }
        });

        saBox.getNotes(capsuleInfo.getId(), false, new Callbacks() {
            @Override
            public void onCallbackItems(ArrayList<String> notes) {
                notes_b = notes;
                noteAdapter.setElementsData(notes_b, false, false, true, ctx, capsuleInfo);
                RecyclerView recyclerView = findViewById(R.id.recyclernotasCaja);
                recyclerView.setAdapter(noteAdapter);
            }
        });


        conectarSpotify(false, new Callbacks() {
            @Override
            public void onCallbackExito(Boolean exito) {
                if(exito){
                    saBox.getSongs(capsuleInfo.getId(), false, new Callbacks() {
                        @Override
                        public void onCallbackMusicData(ArrayList<MusicInfo> data) {
                            music_b = data;

                            musicAdapter.setData( music_b, mSpotifyAppRemote, capsuleInfo.getId(), ctx);
                            musicAdapter.setCapsula();
                            RecyclerView recyclerView = findViewById(R.id.recyclermusicaCaja);
                            recyclerView.setAdapter(musicAdapter);

                        }
                    });
                }
            }
        });

    }



    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndexOrThrow("_display_name");
                    result = cursor.getString(index);
                }
            }
        } else if (uri.getScheme().equals("file")) {
            result = uri.getLastPathSegment();
        }
        return result;
    }



    private void conectarSpotify(boolean add, Callbacks cb){

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        try {
            SpotifyAppRemote.connect(getApplicationContext(), connectionParams, new Connector.ConnectionListener() {

                @Override
                public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote;
                    // Now you can start interacting with App Remote

                    if (add) {
                        addLastSong();

                    }
                    cb.onCallbackExito(true);

                }

                @Override
                public void onFailure(Throwable throwable) {
                   // Toast.makeText(ctx, R.string.errorConect, Toast.LENGTH_SHORT).show();
                    cb.onCallbackExito(false);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Log.e("CLAU", e.getMessage());

        }
    }

    private void addLastSong(){


        Dialog dialogAddMusic = new Dialog(ctx);
        dialogAddMusic.setContentView(R.layout.add_cancion_dialog);
        Button cancelar = dialogAddMusic.findViewById(R.id.buttonCancelar);
        Button add = dialogAddMusic.findViewById(R.id.buttonAñadir);

        TextView nombreCancion = dialogAddMusic.findViewById(R.id.ultimaCancion);
        ImageView cover = dialogAddMusic.findViewById(R.id.coverSong);

        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
            // Extrae la información de la canción actual del objeto PlayerState
            track = playerState.track;
            songTitle = track.name; // Título de la canción
            artist = track.artist.name; // Nombre del artista
            songImage = track.imageUri;
            songUri = track.uri;

            mSpotifyAppRemote.getImagesApi().getImage(songImage).setResultCallback(
                    bitmap -> {
                        cover.setImageBitmap(bitmap);

                    });


            musicInfo = new MusicInfo(songTitle,artist,songUri, songImage.toString(), "a");

            nombreCancion.setText(songTitle + " - " + artist);

        });


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddMusic.dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                SABox saBox = new SABox();
                saBox.addSong(capsuleInfo.getId(), musicInfo, false, new Callbacks() {
                    @Override
                    public void onCallbackExito(Boolean exito) {
                        if(exito){
                            saBox.getSongs(capsuleInfo.getId(), false, new Callbacks() {
                                @Override
                                public void onCallbackMusicData(ArrayList<MusicInfo> data) {
                                    music_b = data;

                                    musicAdapter.setData( music_b, mSpotifyAppRemote, capsuleInfo.getId(), ctx);
                                    musicAdapter.setCapsula();
                                    RecyclerView recyclerView = findViewById(R.id.recyclermusicaCaja);
                                    recyclerView.setAdapter(musicAdapter);

                                }
                            });
                            Toast.makeText(ctx,R.string.addBienCap , Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ctx,R.string.addMal , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialogAddMusic.dismiss();

            }
        });

        dialogAddMusic.show();

    }

}
