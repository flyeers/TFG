package es.ucm.fdi.boxit.negocio;

import android.graphics.Bitmap;

import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.integracion.DAOCapsule;

public class SACapsule {

    public void createCapsule(CapsuleInfo c, Callbacks cb){
        DAOCapsule dao = new DAOCapsule();
        dao.createCapsule(c,cb);
    }

    public void getCapsuleById(String id, Callbacks cb){
        DAOCapsule dao = new DAOCapsule();
        dao.getCapsuleById(id, cb);
    }

    public void addPhotos( CapsuleInfo c, String img, Callbacks cb){
        DAOCapsule dao = new DAOCapsule();
        dao.addPhotos(c, img, cb);
    }

    public void addPhotosFromCamera(CapsuleInfo c, Bitmap img, Callbacks cb){
        DAOCapsule dao = new DAOCapsule();
        dao.addPhotoFromCamera(c, img, cb);
    }

    public void getPhotos(String id, Callbacks cb){
        DAOCapsule dao = new DAOCapsule();
        dao.getPhotos(id, cb);

    }

    public void addDocs(CapsuleInfo c , String d, String name, Callbacks cb){
        DAOCapsule dao = new DAOCapsule();
        dao.addDocs(c, d, name, cb);
    }

    public void getDocs(String id, Callbacks cb){
        DAOCapsule dao = new DAOCapsule();
        dao.getDocs(id, cb);

    }

    public void getItems(String id, Callbacks cb){
        DAOCapsule dao = new DAOCapsule();
        dao.getDocs(id, cb);
        dao.getPhotos(id, cb);

    }

    public void deletePhoto(String id, String foto){
        DAOCapsule dao = new DAOCapsule();
        //TODO
    }

}
