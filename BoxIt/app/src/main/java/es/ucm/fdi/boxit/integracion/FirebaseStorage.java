package es.ucm.fdi.boxit.integracion;

import com.google.firebase.storage.StorageReference;

public class FirebaseStorage {

    private StorageReference storageRef;
    private final String URL_TO_STORAGE = "gs://boxit-9b1b9.appspot.com";

    public FirebaseStorage () {
        com.google.firebase.storage.FirebaseStorage storage = com.google.firebase.storage.FirebaseStorage.getInstance();
        this.storageRef = storage.getReferenceFromUrl(URL_TO_STORAGE);
    }

    public StorageReference getStorageRef() {
        return storageRef;
    }
}
