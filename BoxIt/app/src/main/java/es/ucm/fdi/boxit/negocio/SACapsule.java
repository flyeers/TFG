package es.ucm.fdi.boxit.negocio;

import android.graphics.Bitmap;

import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.integracion.DAOBox;
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

    public void updateCap(CapsuleInfo c, Callbacks cb) {
        DAOCapsule dao = new DAOCapsule();
        dao.updateCap(c, cb);
    }

    public void exitCapsule(String id, Callbacks cb) {
        DAOCapsule dao = new DAOCapsule();
        dao.exitCapsule(id, cb);
    }


}
