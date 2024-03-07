package es.ucm.fdi.boxit.integracion;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.CapsuleInfo;
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
    private final String FOTO_PERFIL = "foto_perfil";




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
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        new ArrayList<String>();

                                        //metemos en la bd la informacion del usuario
                                        Map<String, Object> data = new HashMap<>();
                                        data.put(NOMBRE_USU, usuarioInsertar.getNombreUsuario().toLowerCase());//username en minusculas
                                        data.put(CORREO, usuarioInsertar.getCorreo().toLowerCase());
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

                                        if(usuarioInsertar.getImgPerfil() != null){


                                            String idImg = String.format("%s-%s", usuarioInsertar.getNombreUsuario(), "fotoPerfil").replace("", "");
                                            FirebaseStorage imageStorage = new FirebaseStorage();
                                            StorageReference fileReference = imageStorage.getStorageRef().child(idImg + ".png");

                                            fileReference.putFile(usuarioInsertar.getImgPerfil())
                                                    .addOnSuccessListener(taskSnapshot -> {

                                                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                                                            //guardamos la de la img ruta en la propia caja

                                                            data.put(FOTO_PERFIL, uri.toString());
                                                            SingletonDataBase.getInstance().getDB().collection(COL_USERS).document(user.getUid()).set(data);
                                                            cb.onCallbackExito(true);


                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                cb.onCallbackExito(false);
                                                            }
                                                        });

                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            cb.onCallbackExito(false);
                                                        }
                                                    });
                                        }
                                        else{
                                            data.put(FOTO_PERFIL, "");
                                            SingletonDataBase.getInstance().getDB().collection(COL_USERS).document(user.getUid()).set(data);
                                            cb.onCallbackExito(true);
                                        }


                                    } else {
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
        //FirebaseUser user = mAuth.getCurrentUser();
        SingletonDataBase.getInstance().getDB().collection(COL_USERS).whereEqualTo(CORREO,
                email).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot d: task.getResult()){

                    userInfo.setNombre(d.get(NOMBRE).toString());
                    userInfo.setNombreUsuario(d.get(NOMBRE_USU).toString());
                    userInfo.setCorreo(email);

                    if(d.get(FOTO_PERFIL) != null)
                        userInfo.setImgPerfil(Uri.parse(d.get(FOTO_PERFIL).toString()));
                    else userInfo.setImgPerfil(Uri.parse(""));


                }

                cb.onCallback(userInfo);
            }
        });

    }

    public void getUsuarioByUserName(String username, Callbacks cb){
        UserInfo userInfo = new UserInfo();
        FirebaseUser user = mAuth.getCurrentUser();

        String finalUsername = username.toLowerCase();
        SingletonDataBase.getInstance().getDB().collection(COL_USERS).whereEqualTo(NOMBRE_USU,
                finalUsername).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                if(!task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot d: task.getResult()){

                        userInfo.setNombre(d.get(NOMBRE).toString());
                        userInfo.setCorreo(d.get(CORREO).toString());
                        userInfo.setNombreUsuario(finalUsername);

                        if(d.get(FOTO_PERFIL) != null)
                            userInfo.setImgPerfil(Uri.parse(d.get(FOTO_PERFIL).toString()));
                        else userInfo.setImgPerfil(Uri.parse(""));
                    }

                    cb.onCallback(userInfo);

                }
                else{
                    cb.onCallback(null);
                }

            }
            else{
                cb.onCallback(null);
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
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("token", newToken);
                                        SingletonDataBase.getInstance().getDB()
                                                .collection(COL_USERS).document(user.getUid()).update(updates);

                                        cb.onCallbackExito(true);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        cb.onCallbackExito(false);

                                    }
                                }
                            });
                }
            }
        });
    }


    public void updateperfil(String nuevoNombre, Uri nuevaFoto, String username, Boolean hayFoto, Callbacks cb){
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        Map<String, Object> data = new HashMap<>();
        data.put(NOMBRE, nuevoNombre);



        if (nuevaFoto != null){
            //eliminar foto anterior del storage si la hubiese

            FirebaseStorage imageStorage = new FirebaseStorage();
            String idImg = String.format("%s-%s", username, "fotoPerfil").replace("", "");
            if(hayFoto){


                StorageReference fileReference = imageStorage.getStorageRef().child(idImg + ".png");
                fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //ahora hay que subir la nueva imagen

                        StorageReference fileReference2 = imageStorage.getStorageRef().child(idImg + ".png");

                        fileReference2.putFile(nuevaFoto)
                                .addOnSuccessListener(taskSnapshot -> {

                                    fileReference2.getDownloadUrl().addOnSuccessListener(uri -> {

                                        //guardamos la de la img ruta en la propia caja

                                        data.put(FOTO_PERFIL, uri.toString());
                                        usersCollection.document(currentUser.getUid()).update(data);
                                        cb.onCallbackExito(true);


                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            cb.onCallbackExito(false);
                                        }
                                    });

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        cb.onCallbackExito(false);
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        cb.onCallbackExito(false);
                    }
                });
            }
            else{

                //si no habia foto de perfil, subir la nueva
                StorageReference fileReference2 = imageStorage.getStorageRef().child(idImg + ".png");

                fileReference2.putFile(nuevaFoto)
                        .addOnSuccessListener(taskSnapshot -> {

                            fileReference2.getDownloadUrl().addOnSuccessListener(uri -> {

                                //guardamos la de la img ruta en la propia caja

                                data.put(FOTO_PERFIL, uri.toString());
                                usersCollection.document(currentUser.getUid()).update(data);
                                cb.onCallbackExito(true);


                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    cb.onCallbackExito(false);
                                }
                            });

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                cb.onCallbackExito(false);
                            }
                        });
            }

        }
        else{
            usersCollection.document(currentUser.getUid()).update(data);
            cb.onCallbackExito(true);
        }



    }

    //CAJAS
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

    public void getUserBoxesCompartidas(String correo, Callbacks cb){
        ArrayList<BoxInfo> boxes = new ArrayList<>();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        DAOBox daoBox = new DAOBox();
        usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot d : task.getResult()) {
                    // Obtenesmos el array el documento del usuario
                    List<String> idCajas = (List<String>) d.get(CAJAS_COMPARTIDAS);
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

    //De una caja compartidas a una propia
    public void boxComToProp(String correo, String boxId, Callbacks cb) {
        try{
            CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);
            usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        String userId = d.getId();
                        //Quitamos de la lista compartida
                        usersCollection.document(userId).update(CAJAS_COMPARTIDAS, FieldValue.arrayRemove(boxId));
                        //Añadimos a lista propia
                        usersCollection.document(userId).update(CAJAS_PROPIAS, FieldValue.arrayUnion(boxId));

                    }
                }
            });
            cb.onCallbackExito(true);
        }catch (Exception e) {
            cb.onCallbackExito(false);
        }
    }

    //De una caja propia a una compartida
    public void boxPropToComp(String correo, String boxId, Callbacks cb) {
        try{
            CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);
            usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        String userId = d.getId();
                        //Quitamos de la lista propia
                        usersCollection.document(userId).update(CAJAS_PROPIAS, FieldValue.arrayRemove(boxId));
                        //Añadimos a lista compartida
                        usersCollection.document(userId).update(CAJAS_COMPARTIDAS, FieldValue.arrayUnion(boxId));

                    }
                }
            });
            cb.onCallbackExito(true);
        }catch (Exception e) {
            cb.onCallbackExito(false);
        }
    }



    //CAPSULAS
    public void getUserCapsules(String correo, Callbacks cb){
        ArrayList<CapsuleInfo> capsules = new ArrayList<>();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        DAOCapsule daoCapsule = new DAOCapsule();
        usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot d : task.getResult()) {
                    // Obtenesmos el array el documento del usuario
                    List<String> idCapsulas = (List<String>) d.get(CAPSULAS_PROPIAS);
                    AtomicInteger count = new AtomicInteger(idCapsulas.size());
                    for(String capsula: idCapsulas){

                        daoCapsule.getCapsuleById(capsula, new Callbacks() {
                            @Override
                            public void onCallbackCapsule(CapsuleInfo c) {
                                if( c != null){
                                    capsules.add(c);
                                }
                                if (count.decrementAndGet() == 0) {
                                    // Todas las capsulas se han cargado, llamar al callback
                                    cb.onCallbackCapsules(capsules);
                                }
                            }
                        });

                    }
                    //ninguna capsula
                    cb.onCallbackCapsules(capsules);

                }
            }
        });
    }

    public void getUserCapsulesCompartidas(String correo, Callbacks cb){
        ArrayList<CapsuleInfo> capsules = new ArrayList<>();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        DAOCapsule daoCapsule = new DAOCapsule();
        usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot d : task.getResult()) {
                    // Obtenesmos el array el documento del usuario
                    List<String> idCapsulas = (List<String>) d.get(CAPSULAS_COMPARTIDAS);
                    AtomicInteger count = new AtomicInteger(idCapsulas.size());
                    for(String capsula: idCapsulas){

                        daoCapsule.getCapsuleById(capsula, new Callbacks() {
                            @Override
                            public void onCallbackCapsule(CapsuleInfo c) {
                                if( c != null){
                                    capsules.add(c);
                                }
                                if (count.decrementAndGet() == 0) {
                                    // Todas las capsulas se han cargado, llamar al callback
                                    cb.onCallbackCapsules(capsules);
                                }
                            }
                        });

                    }
                    //ninguna capsulas
                    cb.onCallbackCapsules(capsules);

                }
            }
        });
    }

    //De una capsula compartidas a una propia
    public void capsuleComToProp(String correo, String capId, Callbacks cb) {
        try{
            CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);
            usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        String userId = d.getId();
                        //Quitamos de la lista compartida
                        usersCollection.document(userId).update(CAPSULAS_COMPARTIDAS, FieldValue.arrayRemove(capId));
                        //Añadimos a lista propia
                        usersCollection.document(userId).update(CAPSULAS_PROPIAS, FieldValue.arrayUnion(capId));
                    }
                }
            });
            cb.onCallbackExito(true);
        }catch (Exception e) {
            cb.onCallbackExito(false);
        }
    }
    //De una capsula propia a una compartida
    public void capsulePropToComp(String correo, String capId, Callbacks cb) {
        try{
            CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);
            usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        String userId = d.getId();
                        //Quitamos de la lista propia
                        usersCollection.document(userId).update(CAPSULAS_PROPIAS, FieldValue.arrayRemove(capId));
                        //Añadimos a lista compartida
                        usersCollection.document(userId).update(CAPSULAS_COMPARTIDAS, FieldValue.arrayUnion(capId));
                    }
                }
            });
            cb.onCallbackExito(true);
        }catch (Exception e) {
            cb.onCallbackExito(false);
        }
    }



    //AMIGOS
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

                        //si el usuario tenia tambien una solicitud de current
                        ArrayList<String> s = (ArrayList<String>) d.get(LISTA_SOLICITUDES);
                        if(s.contains(currentUser.getEmail()))
                            usersCollection.document(userId).update(LISTA_SOLICITUDES, FieldValue.arrayRemove(currentUser.getEmail()));
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

        if(!correo.equals(mAuth.getCurrentUser().getEmail())){
            try {
                //Añadimos a la lista de solicitudes del al que se le solicita seguir
                usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot d : task.getResult()) {
                            String userId = d.getId();
                            ArrayList<String> s = (ArrayList<String>) d.get(LISTA_SOLICITUDES);
                            ArrayList<String> a = (ArrayList<String>) d.get(LISTA_AMIGOS);
                            if (!s.contains(currentUser.getEmail()) && !a.contains(currentUser.getEmail())) {//si no esta ya lo silicitud o amigos
                                usersCollection.document(userId).update(LISTA_SOLICITUDES, FieldValue.arrayUnion(currentUser.getEmail()));
                            }
                        }
                    }
                });
                cb.onCallbackExito(true);
            } catch (Exception e) {
                cb.onCallbackExito(false);
            }
        }
        else{
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

    public void searchUsuario(String fragmentoUsername, Callbacks cb) {
        ArrayList<UserInfo> usuarios = new ArrayList<>();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        String fragmentoMin = fragmentoUsername.toLowerCase();
        String fragmentoMax = fragmentoMin + "\uf8ff"; // "\uf8ff" es un carácter Unicode que representa el final del conjunto Unicode

        usersCollection.orderBy(NOMBRE_USU).startAt(fragmentoMin).endAt(fragmentoMax).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(QueryDocumentSnapshot document : task.getResult()) {
                    String nombreUsuario = document.getString(NOMBRE_USU);
                    // Si el nombre de usuario contiene el fragmento buscado
                    if(nombreUsuario.toLowerCase().contains(fragmentoUsername.toLowerCase())) {
                        // Obtener el usuario completo y agregarlo a la lista
                        getUsuarioByUserName(nombreUsuario, new Callbacks() {
                            @Override
                            public void onCallback(UserInfo u) {
                                if(u != null) {
                                        usuarios.add(u);
                                }
                                // Si todos los documentos han sido procesados, llamar al callback
                                if(usuarios.size() == task.getResult().size()) {
                                    cb.onCallbackUsers(usuarios);
                                }
                            }
                        });
                    }
                }
            }
            cb.onCallbackUsers(usuarios);
        });
    }

    public void getToken(String correo, Callbacks cb){

        SingletonDataBase.getInstance().getDB().collection(COL_USERS).whereEqualTo("correo",
               correo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        String userToken = document.getString("token");
                        cb.onCallbackData(userToken);

                    }
                    else{
                        cb.onCallbackData("");
                    }
                }
                else{
                    cb.onCallbackData("");
                }
            }
        });
    }

}
