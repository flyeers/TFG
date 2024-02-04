package es.ucm.fdi.boxit.negocio;

import android.graphics.Bitmap;

import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.integracion.DAOBox;
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

    public void addPhotos(String id, String img, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.addPhotos(id, img, cb);
    }

    public void addPhotosFromCamera(String id, Bitmap img, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.addPhotoFromCamera(id, img, cb);
    }

    public void getPhotos(String id, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.getPhotos(id, cb);

    }

    public void addDocs(String id, String d, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.addDocs(id, d, cb);
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


}
