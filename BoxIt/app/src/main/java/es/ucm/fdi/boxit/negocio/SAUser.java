package es.ucm.fdi.boxit.negocio;

import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.integracion.DAOUsuario;

public class SAUser {

    public void crearUsuario(UserInfo u, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.createAccount(u, cb);
    }

    public void cerrarSesion(){
        DAOUsuario dao = new DAOUsuario();
        dao.logOut();
    }
}
