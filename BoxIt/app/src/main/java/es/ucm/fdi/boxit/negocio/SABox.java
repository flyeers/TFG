package es.ucm.fdi.boxit.negocio;

import android.graphics.Bitmap;

import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.integracion.DAOBox;
import es.ucm.fdi.boxit.integracion.DAOCapsule;
import es.ucm.fdi.boxit.integracion.DAOUsuario;

public class SABox {

    public void createBox(BoxInfo b, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.createBox(b,cb);
    }

    public void getBoxById(String id, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.getBoxById(id, cb);
    }

    public void addPhotos( BoxInfo b, String img, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.addPhotos(b, img, cb);
    }

    public void addPhotosFromCamera(BoxInfo b, Bitmap img, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.addPhotoFromCamera(b, img, cb);
    }

    public void getPhotos(String id, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.getPhotos(id, cb);

    }

    public void addDocs(BoxInfo b , String d, String name, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.addDocs(b, d, name, cb);
    }

    public void getDocs(String id, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.getDocs(id, cb);

    }

    public void getItems(String id, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.getDocs(id, cb);
        dao.getPhotos(id, cb);

    }

    public void deletePhoto(String id, String foto, Callbacks cb){
        DAOBox daoBox = new DAOBox();
        daoBox.borrarFoto(id,foto, cb);
    }

    public void deleteDoc(String id, String file, Callbacks cb){
        DAOBox daoBox = new DAOBox();
        daoBox.deleteDocument(id,file, cb);
    }
    public void deleteBox(String id, String boxName, Callbacks cb){
        DAOBox daoBox = new DAOBox();
        daoBox.deleteBox(id,boxName, cb);
    }

    public void exitBox(String id, Callbacks cb) {
        DAOBox dao = new DAOBox();
        dao.exitBox(id, cb);
    }

}
