package es.ucm.fdi.boxit.integracion;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import es.ucm.fdi.boxit.negocio.CapsuleInfo;

public class DAOCapsule {
    private FirebaseAuth mAuth;
    private final String NOMBRE = "nombre";
    private final String PORTADA = "cover";
    private final String COLABORADORES = "colaborators";
    private final String CIERRE = "cierre";
    private final String APERTURA = "apertura";
    private final String COL_CAP = "capsules";
    private final String COL_USERS = "users";
    private final String CAPSULAS_PROPIAS = "capsules";
    private final String CAPSULAS_COMPARTIDAS = "capsules_shared";
    private final String FOTOS = "capsule_photos";
    private final String DOCS = "capsule_documents";
    private final String MUSICA = "capsule_music";
    private final String NOTAS = "capsule_notes";

    private final String CORREO = "correo";

    private CapsuleInfo capsuleInfo;

    public DAOCapsule(){
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void createCapsule(CapsuleInfo c, Callbacks cb) {
        //TODO
        //docData.put("dateExample", new Timestamp(new Date()));

        try {
            FirebaseUser user = mAuth.getCurrentUser();
            CollectionReference capsuleCollection = SingletonDataBase.getInstance().getDB().collection(COL_CAP);
            CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

            ArrayList<String> fotos = new ArrayList<>();
            ArrayList<String> docs = new ArrayList<>();
            Map<String, Object> data = new HashMap<>();
            ArrayList<String> notes = new ArrayList<>();
            data.put(NOMBRE, c.getTitle());
            data.put(COLABORADORES, c.getColaborators());
            data.put(FOTOS, fotos);
            data.put(DOCS, docs);
            data.put(CIERRE, c.getCierre());
            data.put(APERTURA, c.getApertura());
            data.put(NOTAS, notes);


            //IMG
            int random = new Random().nextInt(61) + 20;//generamos un numero para asegurarnos de no crear dos ids iguales
            String idImg = String.format("%s-%s-%s", c.getTitle(), random, user.getEmail().toString()).replace("", "");

            FirebaseStorage imageStorage = new FirebaseStorage();
            StorageReference fileReference = imageStorage.getStorageRef().child(idImg + ".png");

            fileReference.putFile(c.getImg())
                    .addOnSuccessListener(taskSnapshot -> {

                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                            //guardamos la de la img ruta en la propia caja
                            data.put(PORTADA, uri.toString());

                            //añadimos la caja a la colleccion de cajas
                            Task<Void> capsulesCollectionTask = capsuleCollection.add(data).continueWithTask(new Continuation<DocumentReference, Task<Void>>() {

                                //Añadimos la caja a su propietario y/o colaboradores
                                @Override
                                public Task<Void> then(@NonNull Task<DocumentReference> task) throws Exception {
                                    String newCapId = task.getResult().getId(); //id de la caja creada

                                    //añadimos al capsuleInfo el id para posteriormente tener acceso a qué caja en la BD se refiere ese capsulesinfo
                                    c.setId(newCapId);
                                    if(c.getColaborators().isEmpty()){ //UNICO PROPIETARIO
                                        return usersCollection.document(user.getUid()).update(CAPSULAS_PROPIAS, FieldValue.arrayUnion(newCapId));
                                    }
                                    else{
                                        try{
                                            for(String correo: c.getColaborators()){ //COLABORADORES
                                                usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task2 -> {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot d : task2.getResult()) {
                                                            String userId = d.getId();
                                                            usersCollection.document(userId).update(CAPSULAS_COMPARTIDAS, FieldValue.arrayUnion(newCapId));
                                                        }
                                                    }
                                                });

                                            }
                                            cb.onCallbackExito(true);
                                        }catch (Exception e){
                                            cb.onCallbackExito(false);
                                        }

                                        return null;
                                    }

                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    cb.onCallbackExito(true);

                                }
                            });

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

        } catch (Exception e){
            cb.onCallbackExito(false);
        }

    }

    public void updateCap(CapsuleInfo c, Callbacks cb) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);
        DAOUsuario daoUsuario = new DAOUsuario();

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL_CAP).document(c.getId());
        boxDocument.update(NOMBRE, c.getTitle());
        boxDocument.update(APERTURA, c.getCierre());
        boxDocument.update(CIERRE, c.getApertura());

        //TODO -> AÑADIR EL ACTUALIZAR LA FOTO
        //COLABORADORES
        if(!c.getColaborators().isEmpty()){
            boxDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot ds = task.getResult();
                        if(ds.exists()){
                            ArrayList<String> preColab = (ArrayList<String>) ds.get(COLABORADORES);
                            if(preColab.isEmpty()){
                                //si era vacio cambio la caja de lista en el currentUser
                                daoUsuario.boxPropToComp(currentUser.getEmail(), c.getId(), new Callbacks() {
                                    @Override
                                    public void onCallbackExito(Boolean exito) {
                                        for (String correo: c.getColaborators()) {
                                            if(!preColab.contains(currentUser.getEmail())){//me aseguro de no volve a meter al current
                                                usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot d : task.getResult()) {
                                                            String userId = d.getId();
                                                            usersCollection.document(userId).update(CAPSULAS_COMPARTIDAS, FieldValue.arrayUnion(c.getId()));
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                        //actualizo la lista de colaboradores de la capsula
                                        boxDocument.update(COLABORADORES, c.getColaborators());
                                        cb.onCallbackExito(true);
                                    }
                                });
                            }
                            else if(preColab.size() == c.getColaborators().size()){
                                //Si el size es igual no se han añadido más colaborades
                                cb.onCallbackExito(true);
                            }
                            else{
                                //Meto todos los q no estaban ya
                                for (String correo: c.getColaborators()) {
                                    if(!preColab.contains(correo)){
                                        usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                for (QueryDocumentSnapshot d : task2.getResult()) {
                                                    String userId = d.getId();
                                                    usersCollection.document(userId).update(CAPSULAS_COMPARTIDAS, FieldValue.arrayUnion(c.getId()));
                                                }
                                            }
                                        });
                                    }
                                }
                                //actualizo la lista de colaboradores de la capsula
                                boxDocument.update(COLABORADORES, c.getColaborators());
                                cb.onCallbackExito(true);
                            }
                        }
                        else{
                            cb.onCallbackExito(false);
                        }
                    }
                    else{
                        cb.onCallbackExito(false);
                    }
                }
            });
        }
        else{
            cb.onCallbackExito(true);
        }
    }

    public void getCapsuleById(String id, Callbacks cb) {
        try{
            DocumentReference capDocument = SingletonDataBase.getInstance().getDB().collection(COL_CAP).document(id);
            capDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot ds = task.getResult();
                        if(ds.exists()){
                            capsuleInfo = new CapsuleInfo(id, ds.getData().get(NOMBRE).toString(), Uri.parse(ds.getData().get(PORTADA).toString()));

                            Timestamp timestamp = (Timestamp) ds.getData().get(CIERRE);
                            capsuleInfo.setCierre(timestamp.toDate());
                            Timestamp timestamp2 = (Timestamp) ds.getData().get(APERTURA);
                            capsuleInfo.setApertura(timestamp2.toDate());

                            ArrayList<String> colab = new ArrayList<>();
                            colab = (ArrayList<String>) ds.getData().get(COLABORADORES);
                            capsuleInfo.setCollaborators(colab);
                        }
                    }
                    cb.onCallbackCapsule(capsuleInfo);

                }

            });


        }catch (Exception e){
            cb.onCallbackExito(false);
        }


    }

    public void exitCapsule(String id, Callbacks cb){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);
        CollectionReference capsuleCollection = SingletonDataBase.getInstance().getDB().collection(COL_CAP);

        try {
            //Quitamos de la lista de cajas compartidas del usuario
            usersCollection.document(currentUser.getUid()).update(CAPSULAS_COMPARTIDAS, FieldValue.arrayRemove(id));
            //Quitamos de la lista de colaboradores al usuario
            capsuleCollection.document(id).update(COLABORADORES, FieldValue.arrayRemove(currentUser.getEmail()));
            //vemos si quedan colaboradoes
            capsuleCollection.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Document found in the offline cache
                        DocumentSnapshot ds = task.getResult();

                        ArrayList<String> colab = new ArrayList<>();
                        colab = (ArrayList<String>) ds.getData().get(COLABORADORES);
                        if(colab.size() > 1) cb.onCallbackExito(true); //quedan colaboradores
                        else{//si queda un colaborador -> pasa a ser capsula propia

                            //Quitamos ese ultimo colaborador
                            capsuleCollection.document(id).update(COLABORADORES, FieldValue.arrayRemove(colab.get(0)));

                            //Gestionamos la caja en el usuario
                            DAOUsuario daoUsuario = new DAOUsuario();
                            daoUsuario.capsuleComToProp(colab.get(0), id, new Callbacks() {
                                @Override
                                public void onCallbackExito(Boolean exito) {
                                    if(exito) cb.onCallbackExito(true);
                                    else cb.onCallbackExito(false);
                                }
                            });
                        }
                    }
                    else cb.onCallbackExito(false);
                }
            });

        } catch (Exception e) {
            cb.onCallbackExito(false);
        }
    }

}
