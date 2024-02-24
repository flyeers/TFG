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

    public void updateBox(BoxInfo b, Callbacks cb) {
        DAOBox dao = new DAOBox();
        dao.updateBox(b, cb);
    }

    public void addPhotos( BoxInfo b, String img, boolean isBox, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.addPhotos(b, img, isBox, cb);
    }

    public void addPhotosFromCamera(BoxInfo b, Bitmap img, boolean isBox, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.addPhotoFromCamera(b, img, isBox, cb);
    }

    public void getPhotos(String id, boolean isBox, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.getPhotos(id, isBox, cb);

    }

    public void addDocs(BoxInfo b , String d, String name, boolean isBox, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.addDocs(b, d, name, isBox, cb);
    }

    public void getDocs(String id, boolean isBox, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.getDocs(id, isBox, cb);

    }

    public void getItems(String id, boolean isBox, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.getDocs(id, isBox, cb);
        dao.getPhotos(id, isBox, cb);
        dao.getNotes(id, isBox, cb);
    }

    public void deletePhoto(String id, String foto, boolean isBox, Callbacks cb){
        DAOBox daoBox = new DAOBox();
        daoBox.borrarFoto(id,foto, isBox, cb);
    }

    public void deleteDoc(String id, String file, boolean isBox, Callbacks cb){
        DAOBox daoBox = new DAOBox();
        daoBox.deleteDocument(id,file, isBox, cb);
    }
    public void addNote(BoxInfo b, String note, boolean isBox, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.addNote(b, note, isBox, cb);
    }
    public void getNotes(String id, boolean isBox, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.getNotes(id, isBox, cb);

    }
    public void deleteNote(String id, String idNote, boolean isBox, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.deleteNote(id, idNote, isBox, cb);
    }

    public void updateNote(String id, String idNoteOld, String idNoteNew,  boolean isBox, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.updateNote(id, idNoteOld, idNoteNew, isBox, cb);
    }


    public void deleteBox(BoxInfo boxinfo, boolean isBox, Callbacks cb){
        DAOBox daoBox = new DAOBox();
        daoBox.deleteBox(boxinfo, isBox, cb);
    }

    public void exitBox(String id, Callbacks cb) {
        DAOBox dao = new DAOBox();
        dao.exitBox(id, cb);
    }

    public void addSong(String id, String song, String artist, String uriSong, String uriImage, Callbacks cb){
        DAOBox dao = new DAOBox();
        dao.addSong(id,song,artist,uriSong,uriImage, cb);
    }

}
