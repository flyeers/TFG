package es.ucm.fdi.boxit.negocio;

import es.ucm.fdi.boxit.integracion.DAOUsuario;

public class SAUser {

    public void crearUsuario(UserInfo u){
        DAOUsuario dao = new DAOUsuario();
        dao.createAccount(u);
    }
}
