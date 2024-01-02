package es.ucm.fdi.boxit.integracion;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.StorageReference;

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



    public DAOBox(){
        this.mAuth = FirebaseAuth.getInstance();
    }


    public void createBox(BoxInfo b, Callbacks cb){
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            CollectionReference boxCollection = SingletonDataBase.getInstance().getDB().collection(COL_BOX);
            CollectionReference usersCollection = SingletonDataBase.getInstance().getDB().collection(COL_USERS);

            Map<String, Object> data = new HashMap<>();
            data.put(NOMBRE, b.getTitle());
            data.put(COLABORADORES, b.getColaborators());

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
                                    if(b.getColaborators().isEmpty()){ //UNICO PROPIETARIO
                                        return usersCollection.document(user.getUid()).update(CAJAS_PROPIAS, FieldValue.arrayUnion(newBoxId));
                                    }
                                    else{
                                        for(String id: b.getColaborators()){ //COLABORADORES
                                            usersCollection.document(id).update(CAJAS_COMPARTIDAS, FieldValue.arrayUnion(newBoxId));
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


}
