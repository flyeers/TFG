package es.ucm.fdi.boxit.negocio;

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

}
