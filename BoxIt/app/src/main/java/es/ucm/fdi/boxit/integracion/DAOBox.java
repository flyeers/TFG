package es.ucm.fdi.boxit.integracion;

import android.graphics.Bitmap;
import android.net.Uri;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.SAUser;

public class DAOBox {

    private FirebaseAuth mAuth;
    private final String NOMBRE = "nombre";
    private final String PORTADA = "cover";
    private final String COLABORADORES = "colaborators";
    private final String COL_BOX = "boxes";
    private final String COL_USERS = "users";

    private final String CAJAS_PROPIAS = "boxes";
    private final String CAJAS_COMPARTIDAS = "boxes_shared";
    private final String CAPSULAS_PROPIAS = "capsules";
    private final String CAPSULAS_COMPARTIDAS = "capsules_shared";
    private final String FOTOS = "box_photos";
    private final String DOCS = "box_documents";
    private final String MUSICA = "box_music";
    private final String CORREO = "correo";



    private BoxInfo boxInfo;



    public DAOBox(){
        this.mAuth = FirebaseAuth.getInstance();
    }


    public void createBox(BoxInfo b, Callbacks cb){
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            CollectionReference boxCollection = SingletonDataBase.getInstance().getDB().collection(COL_BOX);
            CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

            ArrayList<String> fotos = new ArrayList<>();
            ArrayList<String> docs = new ArrayList<>();
            Map<String, Object> data = new HashMap<>();
            data.put(NOMBRE, b.getTitle());
            data.put(COLABORADORES, b.getColaborators());
            data.put(FOTOS, fotos);
            data.put(DOCS, docs);

            //IMG
            int random = new Random().nextInt(61) + 20;//generamos un numero para asegurarnos de no crear dos ids iguales
            String idImg = String.format("%s-%s-%s", b.getTitle(), random, user.getEmail().toString()).replace("", "");

            FirebaseStorage imageStorage = new FirebaseStorage();
            StorageReference fileReference = imageStorage.getStorageRef().child(idImg + ".png");

            fileReference.putFile(b.getImg())
                    .addOnSuccessListener(taskSnapshot -> {

                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                            //guardamos la de la img ruta en la propia caja
                            data.put(PORTADA, uri.toString());

                            //añadimos la caja a la colleccion de cajas
                            Task<Void> boxesCollectionTask = boxCollection.add(data).continueWithTask(new Continuation<DocumentReference, Task<Void>>() {

                                //Añadimos la caja a su propietario y/o colaboradores
                                @Override
                                public Task<Void> then(@NonNull Task<DocumentReference> task) throws Exception {
                                    String newBoxId = task.getResult().getId(); //id de la caja creada

                                    //añadimos al boxInfo el id para posteriormente tener acceso a qué caja en la BD se refiere ese boxinfo
                                    b.setId(newBoxId);
                                    if(b.getColaborators().isEmpty()){ //UNICO PROPIETARIO
                                        return usersCollection.document(user.getUid()).update(CAJAS_PROPIAS, FieldValue.arrayUnion(newBoxId));
                                    }
                                    else{
                                        try{
                                            for(String correo: b.getColaborators()){ //COLABORADORES
                                                //TODO
                                                //usersCollection.document(id).update(CAJAS_COMPARTIDAS, FieldValue.arrayUnion(newBoxId));
                                                usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task2 -> {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot d : task2.getResult()) {
                                                            String userId = d.getId();
                                                            usersCollection.document(userId).update(CAJAS_COMPARTIDAS, FieldValue.arrayUnion(newBoxId));
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



    public void getBoxById(String id, Callbacks callBacks){


        try{
            DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL_BOX).document(id);
            boxDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot ds = task.getResult();
                        if(ds.exists()){
                            boxInfo = new BoxInfo(id, ds.getData().get(NOMBRE).toString(), Uri.parse(ds.getData().get(PORTADA).toString()));
                            ArrayList<String> colab = new ArrayList<>();
                            colab = (ArrayList<String>) ds.getData().get(COLABORADORES);
                            boxInfo.setCollaborators(colab);
                        }
                    }
                    callBacks.onCallbackBox(boxInfo);

                }

            });


        }catch (Exception e){
            callBacks.onCallbackExito(false);
        }


    }

    public void addDocs(BoxInfo b, String d, String fileName, Callbacks cb){

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL_BOX).document(b.getId());

        //IMG
        int random = new Random().nextInt(61) + 20;//generamos un numero para asegurarnos de no crear dos ids iguales


        FirebaseStorage storage = new FirebaseStorage();
        StorageReference fileReference = storage.getStorageRef().child(fileName);

        fileReference.putFile(Uri.parse(d))
                .addOnSuccessListener(taskSnapshot -> {

                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {


                        boxDocument.update(DOCS, FieldValue.arrayUnion(uri.toString())).addOnSuccessListener(aVoid -> {
                                    cb.onCallbackExito(true);
                                })
                                .addOnFailureListener(e -> {
                                    cb.onCallbackExito(false);
                                });



                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            cb.onCallbackExito(false);
                        }
                    });

                });
    }

    public void addPhotos(BoxInfo b, String img, Callbacks cb){
        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL_BOX).document(b.getId());

        //IMG
        int random = new Random().nextInt(61) + 20;//generamos un numero para asegurarnos de no crear dos ids iguales
        //TODO GENERAR UN ID SIMILAR AL RESTO


        FirebaseUser user = mAuth.getCurrentUser();
        String idImg = String.format("%s-%s-%s", b.getTitle(), random, user.getEmail().toString()).replace("", "");


        FirebaseStorage imageStorage = new FirebaseStorage();
        StorageReference fileReference = imageStorage.getStorageRef().child(idImg + ".png");

        fileReference.putFile(Uri.parse(img))
                .addOnSuccessListener(taskSnapshot -> {

                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                        //guardamos la de la img ruta en la propia caja

                        boxDocument.update(FOTOS, FieldValue.arrayUnion(uri.toString())).addOnSuccessListener(aVoid -> {
                                    cb.onCallbackExito(true);
                                })
                                .addOnFailureListener(e -> {
                                    cb.onCallbackExito(false);
                                });



                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            cb.onCallbackExito(false);
                        }
                    });

                });


    }

    public void addPhotoFromCamera(BoxInfo b, Bitmap img, Callbacks cb){

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL_BOX).document(b.getId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();


        int random = new Random().nextInt(61) + 20;//generamos un numero para asegurarnos de no crear dos ids iguales
        FirebaseUser user = mAuth.getCurrentUser();
        String idImg = String.format("%s-%s-%s", b.getTitle(), random, user.getEmail().toString()).replace("", "");


        FirebaseStorage imageStorage = new FirebaseStorage();
        StorageReference fileReference = imageStorage.getStorageRef().child(idImg + ".png");

        fileReference.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {

                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                        boxDocument.update(FOTOS, FieldValue.arrayUnion(uri.toString())).addOnSuccessListener(aVoid -> {
                                    cb.onCallbackExito(true);
                                })
                                .addOnFailureListener(e -> {
                                    cb.onCallbackExito(false);
                                });

                    }).addOnFailureListener(e -> {
                        cb.onCallbackExito(false);
                    });
                })
                .addOnFailureListener(e -> {

                    cb.onCallbackExito(false);
                });
    }

    public void getPhotos(String id, Callbacks cb){

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL_BOX).document(id);
        boxDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object arrayPhotos = document.get(FOTOS);
                        if (arrayPhotos != null) {

                            cb.onCallbackItems((ArrayList<String>) arrayPhotos);
                        }
                    } else {
                        cb.onCallbackExito(false);
                    }
                } else {
                    cb.onCallbackExito(false);
                }
            }
        });

    }

    public void getDocs(String id, Callbacks cb) {
        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL_BOX).document(id);
        boxDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object arrayDocs = document.get(DOCS);
                        if (arrayDocs != null) {

                            cb.onCallbackItems((ArrayList<String>) arrayDocs);
                        }
                    } else {
                        cb.onCallbackExito(false);
                    }
                } else {
                    cb.onCallbackExito(false);
                }
            }
        });

    }


    public void borrarFoto(String id, String imagenB){


        //TODO NO SE BORRA DEL STORAGE!!!

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL_BOX).document(id);
        boxDocument.update(FOTOS, FieldValue.arrayRemove(imagenB))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Referencia de imagen en la colección eliminada exitosamente
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al eliminar la referencia de imagen en la colección
                    }
                });

        /*

        FirebaseStorage imageStorage = new FirebaseStorage();
        StorageReference reference = imageStorage.getStorageRef().child(imagenB);

        // Eliminar el archivo
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // El archivo se eliminó exitosamente


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error al intentar eliminar el archivo

            }
        });*/
    }



}
