package es.ucm.fdi.boxit.integracion;

import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.UserInfo;

public class DAOUsuario {

    private FirebaseAuth mAuth;
    private final String NOMBRE_USU = "username";
    private final String NOMBRE = "nombre";
    private final String CORREO = "correo";
    private final String COL_USERS = "users";
    private final String TOKEN = "token";
    private final String CAJAS_PROPIAS = "boxes";
    private final String CAJAS_COMPARTIDAS = "boxes_shared";
    private final String CAPSULAS_PROPIAS = "capsules";
    private final String CAPSULAS_COMPARTIDAS = "capsules_shared";
    private final String LISTA_AMIGOS = "amigos";
    private final String LISTA_SOLICITUDES = "solicitudes";




    public DAOUsuario(){
        this.mAuth = FirebaseAuth.getInstance();
    }


    public void createAccount(UserInfo usuarioInsertar, Callbacks cb)  {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    String token = task.getResult();

                    mAuth.createUserWithEmailAndPassword(usuarioInsertar.getCorreo(), usuarioInsertar.getContraseña())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)  {

                                    if (task.isSuccessful()) {

                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("USUARIO", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        new ArrayList<String>();

                                        //metemos en la bd la informacion del usuario
                                        Map<String, Object> data = new HashMap<>();
                                        data.put(NOMBRE_USU, usuarioInsertar.getNombreUsuario().toLowerCase());//username en minusculas
                                        data.put(CORREO, usuarioInsertar.getCorreo());
                                        data.put(NOMBRE, usuarioInsertar.getNombre());
                                        data.put(TOKEN, token);

                                        //instanciamos y metemos las listas de cajas
                                        ArrayList<String> boxProp = new ArrayList<>();
                                        ArrayList<String> boxComp = new ArrayList<>();
                                        ArrayList<String> capProp = new ArrayList<>();
                                        ArrayList<String> capComp = new ArrayList<>();
                                        data.put(CAJAS_PROPIAS, boxProp);
                                        data.put(CAJAS_COMPARTIDAS, boxComp);
                                        data.put(CAPSULAS_PROPIAS, capProp);
                                        data.put(CAPSULAS_COMPARTIDAS, capComp);

                                        //instanciamos y metemos las listas de amigos
                                        ArrayList<String> amigos = new ArrayList<>();
                                        ArrayList<String> solicitudes = new ArrayList<>();
                                        data.put(LISTA_AMIGOS, amigos);
                                        data.put(LISTA_SOLICITUDES, solicitudes);



                                        //añadir la foto de perfil:





                                        //getUID() me devuelve el user id de la tabla de usuarios para emparejarlo con el usuario correspondiente
                                        SingletonDataBase.getInstance().getDB().collection(COL_USERS).document(user.getUid()).set(data);
                                        cb.onCallbackExito(true);


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("USUARIO", "createUserWithEmail:failure", task.getException());
                                        cb.onCallbackExito(false);
                                    }
                                }
                            });
                } else {
                    cb.onCallbackExito(false);
                }
            }
        });


    }

    public void logOut(){
        FirebaseAuth.getInstance().signOut();
    }

    public void getUsuario(String email, Callbacks cb){
        UserInfo userInfo = new UserInfo();
        FirebaseUser user = mAuth.getCurrentUser();
        SingletonDataBase.getInstance().getDB().collection(COL_USERS).whereEqualTo(CORREO,
                email).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot d: task.getResult()){

                    userInfo.setNombre(d.get(NOMBRE).toString());
                    userInfo.setNombreUsuario(d.get(NOMBRE_USU).toString());

                }

                cb.onCallback(userInfo);
            }
        });

    }

    public void getUsuarioByUserName(String username, Callbacks cb){
        UserInfo userInfo = new UserInfo();
        FirebaseUser user = mAuth.getCurrentUser();

        username = username.toLowerCase();
        SingletonDataBase.getInstance().getDB().collection(COL_USERS).whereEqualTo(NOMBRE_USU,
                username).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                if(!task.getResult().isEmpty()){
                    Log.d("CLAU", "ELUSUARIOEXISTE");
                    for (QueryDocumentSnapshot d: task.getResult()){

                        userInfo.setNombre(d.get(NOMBRE).toString());
                        userInfo.setCorreo(d.get(CORREO).toString());

                    }

                    cb.onCallback(userInfo);
                    cb.onCallbackExito(true);
                }
                else{
                    cb.onCallbackExito(false);
                }

            }
            else{
                cb.onCallbackExito(false);
            }
        });

    }

    public void loginCorreo(String correo, String contraseña, Callbacks cb){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    String newToken = task.getResult();

                    mAuth.signInWithEmailAndPassword(correo, contraseña)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("USUARIO", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("token", newToken);
                                        SingletonDataBase.getInstance().getDB()
                                                .collection(COL_USERS).document(user.getUid()).update(updates);

                                        cb.onCallbackExito(true);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("USUARIO", "signInWithEmail:failure", task.getException());
                                        cb.onCallbackExito(false);

                                    }
                                }
                            });
                }
            }
        });
    }

    /*
    public void insertBox(String correo, String idBox, boolean propia){
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);
        usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot d : task.getResult()) {
                    String userID = d.getId();
                    if(propia){
                        usersCollection.document(userID).update(CAJAS_PROPIAS, FieldValue.arrayUnion(idBox));
                    }
                    else{
                        usersCollection.document(userID).update(CAJAS_COMPARTIDAS, FieldValue.arrayUnion(idBox));
                    }
                }
            }
        });
    }*/


    public void getUserBoxes(String correo, Callbacks cb){
        ArrayList<BoxInfo> boxes = new ArrayList<>();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        DAOBox daoBox = new DAOBox();
        usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot d : task.getResult()) {
                    // Obtenesmos el array el documento del usuario
                    List<String> idCajas = (List<String>) d.get(CAJAS_PROPIAS);
                    AtomicInteger count = new AtomicInteger(idCajas.size());
                    for(String caja: idCajas){

                        daoBox.getBoxById(caja, new Callbacks() {
                            @Override
                            public void onCallbackBox(BoxInfo b) {
                                if( b != null){
                                    boxes.add(b);
                                }
                                if (count.decrementAndGet() == 0) {
                                    // Todas lñas cajas se han cargado, llamar al callback
                                    cb.onCallbackBoxes(boxes);
                                }
                            }
                        });

                    }
                    //ninguna caja
                    cb.onCallbackBoxes(boxes);

                }
            }
        });
    }

    public void getAmigos(String correo, Callbacks cb) {
        ArrayList<UserInfo> users = new ArrayList<>();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot d : task.getResult()) {
                    // Obtenesmos el array de amigos del usuario
                    List<String> amigos = (List<String>) d.get(LISTA_AMIGOS);
                    AtomicInteger count = new AtomicInteger(amigos.size());
                    for(String email: amigos){
                        getUsuario(email, new Callbacks() {
                            @Override
                            public void onCallback(UserInfo u) {
                                if( u != null){
                                    users.add(u);
                                }
                                if (count.decrementAndGet() == 0) {
                                    //cargados los amigos
                                    cb.onCallbackUsers(users);
                                }
                            }
                        });
                    }
                    //ningun amigo
                    cb.onCallbackUsers(users);
                }
            }
        });
    }

    public void getSolicitudes(String correo, Callbacks cb) {
        ArrayList<UserInfo> users = new ArrayList<>();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot d : task.getResult()) {
                    // Obtenesmos el array de solicitudes del usuario
                    List<String> amigos = (List<String>) d.get(LISTA_SOLICITUDES);
                    AtomicInteger count = new AtomicInteger(amigos.size());
                    for(String email: amigos){
                        getUsuario(email, new Callbacks() {
                            @Override
                            public void onCallback(UserInfo u) {
                                if( u != null){
                                    users.add(u);
                                }
                                if (count.decrementAndGet() == 0) {
                                    //cargadas las solicitudes
                                    cb.onCallbackUsers(users);
                                }
                            }
                        });
                    }
                    //ninguna solicitud
                    cb.onCallbackUsers(users);
                }
            }
        });
    }

    //Se llama al aceptar la solicitud
    public void addAmigo(String correo, Callbacks cb) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        try {
            //Añadimos a la lista de amigos del usuario activo que acepta la solicitud
            usersCollection.document(currentUser.getUid()).update(LISTA_AMIGOS, FieldValue.arrayUnion(correo));
            //Quito de la lista de solicitudes
            usersCollection.document(currentUser.getUid()).update(LISTA_SOLICITUDES, FieldValue.arrayRemove(correo));

            //Añadimos a la lista de amigos del otro usuario
            usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        String userId = d.getId();
                        usersCollection.document(userId).update(LISTA_AMIGOS, FieldValue.arrayUnion(currentUser.getEmail()));
                    }
                }
            });
            cb.onCallbackExito(true);
        } catch (Exception e) {
            cb.onCallbackExito(false);
        }

    }

    public void removeAmigo(String correo, Callbacks cb) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        try {
            //Quitar a la lista de amigos del usuario activo
            usersCollection.document(currentUser.getUid()).update(LISTA_AMIGOS, FieldValue.arrayRemove(correo));
            //Quitar a la lista de amigos del otro usuario
            usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        String userId = d.getId();
                        usersCollection.document(userId).update(LISTA_AMIGOS, FieldValue.arrayRemove(currentUser.getEmail()));
                    }
                }
            });
            cb.onCallbackExito(true);
        } catch (Exception e) {
            cb.onCallbackExito(false);
        }

    }

    public void sendSolicitud(String correo, Callbacks cb) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        try {
            //Añadimos a la lista de solicitudes del al que se le solicita seguir
            usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        String userId = d.getId();
                        usersCollection.document(userId).update(LISTA_SOLICITUDES, FieldValue.arrayUnion(currentUser.getEmail()));
                    }
                }
            });
            cb.onCallbackExito(true);
        } catch (Exception e) {
            cb.onCallbackExito(false);
        }
    }

    public void removeSolicitud(String correo, Callbacks cb) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        try {
            //Quitamos de la lista de solicitudes
            usersCollection.document(currentUser.getUid()).update(LISTA_SOLICITUDES, FieldValue.arrayRemove(correo));
            cb.onCallbackExito(true);
        } catch (Exception e) {
            cb.onCallbackExito(false);
        }
    }

    public void searchUsuario(String username, Callbacks cb) {
        ArrayList<UserInfo> users = new ArrayList<>();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        String finalUsername = username.toLowerCase();
        usersCollection.whereEqualTo(NOMBRE_USU, finalUsername).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                AtomicInteger count = new AtomicInteger(task.getResult().size());
                for (QueryDocumentSnapshot d: task.getResult()){
                    getUsuarioByUserName(finalUsername, new Callbacks() {
                        @Override
                        public void onCallback(UserInfo u) {
                            if( u != null){
                                users.add(u);
                            }
                            if (count.decrementAndGet() == 0) {
                                //cargadas los usuarios que coinciden con la busqueda
                                cb.onCallbackUsers(users);
                            }
                        }
                    });
                }
                //la busqueda no ha dado resultados
                cb.onCallbackUsers(users);
            }
        });


    }

    //TODO ver si se quiere usar -> puede suponer muchas llamadas
    /*
    public void esAmigo(String correo, Callbacks cb) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot d : task.getResult()) {
                    String userId = d.getId();
                    ArrayList<String> amigos = (ArrayList<String>) d.get(LISTA_AMIGOS);
                    cb.onCallbackExito(amigos.contains(correo));
                }
            }
        });
    }*/

}
