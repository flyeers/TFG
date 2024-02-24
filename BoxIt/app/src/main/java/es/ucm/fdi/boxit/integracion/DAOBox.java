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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.internal.StorageReferenceUri;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private final String FOTOS_CAP = "capsule_photos";
    private final String DOCS_CAP = "capsule_documents";
    private final String MUSICA_CAP = "capsule_music";
    private final String NOTAS_CAP = "capsule_notes";
    private final String NOTAS = "box_notes";
    private final String COL_CAP = "capsules";


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
            ArrayList<String> notes = new ArrayList<>();
            Map<String, Object> data = new HashMap<>();
            data.put(NOMBRE, b.getTitle());
            data.put(COLABORADORES, b.getColaborators());
            data.put(FOTOS, fotos);
            data.put(DOCS, docs);
            data.put(NOTAS, notes);


            //IMG

            String idImg = String.format("%s-%s", b.getTitle(), "cover").replace("", "");

            FirebaseStorage imageStorage = new FirebaseStorage();
            StorageReference fileReference = imageStorage.getStorageRef().child(idImg + ".png");

            fileReference.putFile(b.getImg())
                    .addOnSuccessListener(taskSnapshot -> {

                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                            //guardamos la de la img ruta en la propia caja
                            data.put(PORTADA, uri.toString());
                            b.setImg(uri);

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

    public void updateBox(BoxInfo b, Callbacks cb){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);
        DAOUsuario daoUsuario = new DAOUsuario();

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL_BOX).document(b.getId());
        boxDocument.update(NOMBRE, b.getTitle());


        //TODO -> AÑADIR EL ACTUALIZAR LA FOTO
        //COLABORADORES
        if(!b.getColaborators().isEmpty()){
            boxDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot ds = task.getResult();
                        if(ds.exists()){
                            ArrayList<String> preColab = (ArrayList<String>) ds.get(COLABORADORES);
                            if(preColab.isEmpty()){
                                //si era vacio cambio la caja de lista en el currentUser
                                daoUsuario.boxPropToComp(currentUser.getEmail(), b.getId(), new Callbacks() {
                                    @Override
                                    public void onCallbackExito(Boolean exito) {
                                        for (String correo: b.getColaborators()) {
                                            if(!preColab.contains(currentUser.getEmail())){//me aseguro de no volve a meter al current
                                                usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot d : task.getResult()) {
                                                            String userId = d.getId();
                                                            usersCollection.document(userId).update(CAJAS_COMPARTIDAS, FieldValue.arrayUnion(b.getId()));
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                        //actualizo la lista de colaboradores de la caja
                                        boxDocument.update(COLABORADORES, b.getColaborators());
                                        cb.onCallbackExito(true);
                                    }
                                });
                            }
                            else if(preColab.size() == b.getColaborators().size()){
                                //Si el size es igual no se han añadido más colaborades
                                cb.onCallbackExito(true);
                            }
                            else{
                                //Meto todos los q no estaban ya
                                for (String correo: b.getColaborators()) {
                                    if(!preColab.contains(correo)){
                                        usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                for (QueryDocumentSnapshot d : task2.getResult()) {
                                                    String userId = d.getId();
                                                    usersCollection.document(userId).update(CAJAS_COMPARTIDAS, FieldValue.arrayUnion(b.getId()));
                                                }
                                            }
                                        });
                                    }
                                }
                                //actualizo la lista de colaboradores de la caja
                                boxDocument.update(COLABORADORES, b.getColaborators());
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

    public void addDocs(BoxInfo b, String d, String fileName, boolean isBox, Callbacks cb){

        String COL = isBox ? COL_BOX : COL_CAP;
        String DOC = isBox ? DOCS : DOCS_CAP;

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL).document(b.getId());

        //IMG
        int random = new Random().nextInt(61) + 20;//generamos un numero para asegurarnos de no crear dos ids iguales


        FirebaseStorage storage = new FirebaseStorage();
        StorageReference fileReference = storage.getStorageRef().child(fileName);

        fileReference.putFile(Uri.parse(d))
                .addOnSuccessListener(taskSnapshot -> {

                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {


                        boxDocument.update(DOC, FieldValue.arrayUnion(uri.toString())).addOnSuccessListener(aVoid -> {
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

    public void addPhotos(BoxInfo b, String img, boolean isBox, Callbacks cb){

        String COL = isBox ? COL_BOX : COL_CAP;
        String FOT = isBox ? FOTOS : FOTOS_CAP;

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL).document(b.getId());

        //IMG
        int random = new Random().nextInt(61) + 20;//generamos un numero para asegurarnos de no crear dos ids iguales
        //TODO GENERAR UN ID SIMILAR AL RESTO

        FirebaseUser user = mAuth.getCurrentUser();
        String idImg = String.format("%s-%s-%s", b.getTitle(), random, "foto").replace("", "");


        FirebaseStorage imageStorage = new FirebaseStorage();
        StorageReference fileReference = imageStorage.getStorageRef().child(idImg + ".png");

        fileReference.putFile(Uri.parse(img))
                .addOnSuccessListener(taskSnapshot -> {

                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                        //guardamos la de la img ruta en la propia caja

                        boxDocument.update(FOT, FieldValue.arrayUnion(uri.toString())).addOnSuccessListener(aVoid -> {
                                   // cb.onCallbackExito(true);
                                    cb.onCallbackData(uri.toString());
                                })
                                .addOnFailureListener(e -> {
                                    cb.onCallbackData("");
                                });



                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            cb.onCallbackData("");
                        }
                    });

                });


    }

    public void addPhotoFromCamera(BoxInfo b, Bitmap img, boolean isBox, Callbacks cb){

        String COL = isBox ? COL_BOX : COL_CAP;
        String FOT = isBox ? FOTOS : FOTOS_CAP;

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL).document(b.getId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();


        int random = new Random().nextInt(61) + 20;//generamos un numero para asegurarnos de no crear dos ids iguales
        FirebaseUser user = mAuth.getCurrentUser();
        String idImg = String.format("%s-%s-%s", b.getTitle(), random, "foto").replace("", "");


        FirebaseStorage imageStorage = new FirebaseStorage();
        StorageReference fileReference = imageStorage.getStorageRef().child(idImg + ".png");

        fileReference.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {

                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                        boxDocument.update(FOT, FieldValue.arrayUnion(uri.toString())).addOnSuccessListener(aVoid -> {
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

    public void getPhotos(String id, boolean isBox, Callbacks cb){

        String COL = isBox ? COL_BOX : COL_CAP;
        String FOT = isBox ? FOTOS : FOTOS_CAP;

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL).document(id);
        boxDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object arrayPhotos = document.get(FOT);
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

    public void getDocs(String id, boolean isBox, Callbacks cb) {

        String COL = isBox ? COL_BOX : COL_CAP;
        String DOC = isBox ? DOCS : DOCS_CAP;

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL).document(id);
        boxDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object arrayDocs = document.get(DOC);
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


    public void borrarFoto(String id, String imagenB, boolean isBox, Callbacks cb){

        String COL = isBox ? COL_BOX : COL_CAP;
        String FOT = isBox ? FOTOS : FOTOS_CAP;

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL).document(id);
        boxDocument.update(FOT, FieldValue.arrayRemove(imagenB))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Referencia de imagen en la colección eliminada exitosamente
                        Log.d("CLAU", "Borrado de lista");
                        FirebaseStorage imageStorage = new FirebaseStorage();

                        //manipuilamos la referencia de la imagen para quedarnos con el id para borrarlo del storage:
                        int startIndex = imagenB.indexOf("/o/") + 3; // Sumamos 3 para avanzar hasta después de "/o/"
                        int endIndex = imagenB.indexOf(".png");
                        String res = imagenB.substring(startIndex, endIndex);

                        //String idImagen = res.replace("%40", "@");


                        StorageReference fileReference = imageStorage.getStorageRef().child(res + ".png");

                        // Delete the file
                        fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("CLAU", "Borrado del storage");
                                cb.onCallbackExito(true);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                Log.d("CLAU", "Borrado del storage MAL");
                                cb.onCallbackExito(false);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al eliminar la referencia de imagen en la colección
                        cb.onCallbackExito(false);
                    }
                });





    }

    public void deleteDocument(String id, String file, boolean isBox, Callbacks cb){

        String COL = isBox ? COL_BOX : COL_CAP;
        String DOC = isBox ? DOCS : DOCS_CAP;

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL).document(id);
        boxDocument.update(DOC, FieldValue.arrayRemove(file))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Referencia de documento en la colección eliminada exitosamente
                        Log.d("CLAU", "Borrado de lista");
                        FirebaseStorage imageStorage = new FirebaseStorage();

                        int startIndex = file.indexOf("/o/") + 3; // Sumamos 3 para avanzar hasta después de "/o/"
                        int endIndex = file.indexOf(".pdf");
                        String res = file.substring(startIndex, endIndex);

                        StorageReference fileReference = imageStorage.getStorageRef().child(res + ".pdf");

                        // Delete the file
                        fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("CLAU", "Borrado del storage");
                                cb.onCallbackExito(true);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                Log.d("CLAU", "Borrado del storage MAL");
                                cb.onCallbackExito(false);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al eliminar la referencia de imagen en la colección
                        cb.onCallbackExito(false);
                    }
                });
    }


    public void getNotes(String id, boolean isBox ,Callbacks cb){

        String COL = isBox ? COL_BOX : COL_CAP;
        String NOT = isBox ? NOTAS : NOTAS_CAP;

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL).document(id);
        boxDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object arrayNotes = document.get(NOT);
                        if (arrayNotes != null) {
                            cb.onCallbackItems((ArrayList<String>) arrayNotes);
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
    private final String NOTE_IDENTIFIER ="///noteIdentifier///";
    public void addNote(BoxInfo b, String note, boolean isBox, Callbacks cb){

        String COL = isBox ? COL_BOX : COL_CAP;
        String NOT = isBox ? NOTAS : NOTAS_CAP;

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL).document(b.getId());

        int random = new Random().nextInt(61) + 20;//generamos un numero para asegurarnos de no crear dos ids iguales
        FirebaseUser user = mAuth.getCurrentUser();
        String idNote = String.format("%s-%s-%s-%s%s", b.getTitle(), user.getEmail(), random, NOTE_IDENTIFIER, note);

        try {
            boxDocument.update(NOT, FieldValue.arrayUnion(idNote));
            cb.onCallbackData(idNote);
        }
        catch (Exception e){
            cb.onCallbackData("");
        }
    }

    public void deleteNote(String id, String idNote, boolean isBox, Callbacks cb){

        String COL = isBox ? COL_BOX : COL_CAP;
        String NOT = isBox ? NOTAS : NOTAS_CAP;

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL).document(id);
        boxDocument.update(NOT, FieldValue.arrayRemove(idNote)).addOnSuccessListener(aVoid -> {
                    cb.onCallbackExito(true);
                })
                .addOnFailureListener(e -> {
                    cb.onCallbackExito(false);
                });

    }

    public void updateNote(String id,String idNoteOld, String idNoteNew,  boolean isBox, Callbacks cb){

        String COL = isBox ? COL_BOX : COL_CAP;
        String NOT = isBox ? NOTAS : NOTAS_CAP;

        DocumentReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL).document(id);
        boxDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> arrayNotes = (ArrayList<String>) document.get(NOT);
                        if (arrayNotes != null) {
                            int pos = arrayNotes.indexOf(idNoteOld);
                            arrayNotes.set(pos, idNoteNew);
                            boxDocument.update(NOT, arrayNotes);

                            cb.onCallbackExito(true);
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

    public void deleteBox(BoxInfo boxInfo, boolean isBox, Callbacks cb){

        String COL = isBox ? COL_BOX : COL_CAP;
        String DOC = isBox ? DOCS : DOCS_CAP;
        String FOT = isBox ? FOTOS : FOTOS_CAP;
        String ELEM_PROP = isBox ? CAJAS_PROPIAS : CAPSULAS_PROPIAS;
        String ELEM_COMP = isBox ? CAJAS_COMPARTIDAS : CAPSULAS_COMPARTIDAS;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference boxDoc = db.collection(COL).document(boxInfo.getId());
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

        //acceder a las boxes de mi user actual y eliminar del array el id de la caja

        usersCollection.whereEqualTo(CORREO, mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot d : task.getResult()) {


                    //recorremos los elementos de la caja para borrarlos del storage

                    boxDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    List<String> photos = (List<String>) document.get(FOT);
                                    if (photos != null) {

                                        for (String f : photos) {

                                            Log.d("CLAU", "Foto");
                                            FirebaseStorage imageStorage = new FirebaseStorage();
                                            //manipuilamos la referencia de la imagen para quedarnos con el id para borrarlo del storage:
                                            int startIndex = f.indexOf("/o/") + 3; // Sumamos 3 para avanzar hasta después de "/o/"
                                            int endIndex = f.indexOf(".png");
                                            String res = f.substring(startIndex, endIndex);


                                            StorageReference fileReference = imageStorage.getStorageRef().child(res + ".png");

                                            // Delete the file
                                            fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("CLAU", "Borrado del storage");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Uh-oh, an error occurred!
                                                    Log.d("CLAU", "Borrado del storage MAL");
                                                    cb.onCallbackExito(false);
                                                }
                                            });

                                        }
                                    }
                                    List<String> docs = (List<String>) document.get(DOC);
                                    if (docs != null) {
                                        // Para cada doc de la caja hay que eliminarla del storage
                                        for (String doc : docs) {
                                            Log.d("CLAU", "Borrado de lista");
                                            FirebaseStorage imageStorage = new FirebaseStorage();

                                            int startIndex = doc.indexOf("/o/") + 3; // Sumamos 3 para avanzar hasta después de "/o/"
                                            int endIndex = doc.indexOf(".pdf");
                                            String res = doc.substring(startIndex, endIndex);

                                            StorageReference fileReference = imageStorage.getStorageRef().child(res + ".pdf");

                                            // Delete the file
                                            fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("CLAU", "Borrado del storage");
                                                    cb.onCallbackExito(true);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Uh-oh, an error occurred!
                                                    Log.d("CLAU", "Borrado del storage MAL");
                                                    cb.onCallbackExito(false);
                                                }
                                            });

                                        }
                                    }

                                    int startIndex = boxInfo.getImg().toString().indexOf("/o/") + 3; // Sumamos 3 para avanzar hasta después de "/o/"
                                    int endIndex = boxInfo.getImg().toString().indexOf(".png");
                                    String res = boxInfo.getImg().toString().substring(startIndex, endIndex);

                                    FirebaseStorage imageStorage2 = new FirebaseStorage();
                                    StorageReference fileReference2 = imageStorage2.getStorageRef().child(res + ".png");

                                    // Delete the file
                                    fileReference2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("CLAU", "Portada borrada del storage");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Uh-oh, an error occurred!
                                            Log.d("CLAU", "Error portada borrada del storage MAL");
                                            cb.onCallbackExito(false);
                                        }
                                    });
                                } else {
                                    cb.onCallbackExito(false);
                                }
                            } else {
                                cb.onCallbackExito(false);
                            }
                        }
                    });


                    if(boxInfo.getColaborators().isEmpty()){//No hay colaboradores, solo el current user
                        usersCollection.document(mAuth.getUid()).update(ELEM_PROP, FieldValue.arrayRemove(boxInfo.getId())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //eliminado del array de cajas del usuario

                                        // Ahora se elimina la caja en si
                                        boxDoc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("CLAU", "caja borrada");
                                                cb.onCallbackExito(true);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                cb.onCallbackExito(false);
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //error al eliminar del array de cajas del usuario
                                        cb.onCallbackExito(false);
                                    }
                                });
                    }
                    else {
                        usersCollection.document(mAuth.getUid()).update(ELEM_COMP, FieldValue.arrayRemove(boxInfo.getId())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //eliminado del array de cajas del usuario

                                    // Ahora se elimina la caja en si
                                    boxDoc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            for (String correo: boxInfo.getColaborators()) {
                                                if(!correo.equals(mAuth.getCurrentUser().getEmail())) {
                                                    usersCollection.whereEqualTo(CORREO, correo).get().addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()) {
                                                            for (QueryDocumentSnapshot d2 : task2.getResult()) {
                                                                String userId = d2.getId();
                                                                usersCollection.document(userId).update(ELEM_COMP, FieldValue.arrayRemove(boxInfo.getId()));
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                            cb.onCallbackExito(true);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            cb.onCallbackExito(false);
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //error al eliminar del array de cajas del usuario
                                    cb.onCallbackExito(false);
                                }
                            });


                    }
                }
            }

        });

    }

    public void exitBox(String id, Callbacks cb){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);
        CollectionReference boxDocument = SingletonDataBase.getInstance().getDB().collection(COL_BOX);

        try {
            //Quitamos de la lista de cajas compartidas del usuario
            usersCollection.document(currentUser.getUid()).update(CAJAS_COMPARTIDAS, FieldValue.arrayRemove(id));
            //Quitamos de la lista de colaboradores al usuario
            boxDocument.document(id).update(COLABORADORES, FieldValue.arrayRemove(currentUser.getEmail()));
            //vemos si quedan colaboradoes
            boxDocument.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Document found in the offline cache
                        DocumentSnapshot ds = task.getResult();

                        ArrayList<String> colab = new ArrayList<>();
                        colab = (ArrayList<String>) ds.getData().get(COLABORADORES);
                        if(colab.size() > 1) cb.onCallbackExito(true); //quedan colaboradores
                        else{//si queda un colaborador -> pasa a ser caja propia

                            //Quitamos ese ultimo colaborador
                            boxDocument.document(id).update(COLABORADORES, FieldValue.arrayRemove(colab.get(0)));

                            //Gestionamos la caja en el usuario
                            DAOUsuario daoUsuario = new DAOUsuario();
                            daoUsuario.boxComToProp(colab.get(0), id, new Callbacks() {
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

    public void addSong(String id, String song, String artist, String uriSong, String uriImage, Callbacks cb){
        //TODO
    }


}
