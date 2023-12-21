package es.ucm.fdi.boxit.negocio;

import android.telecom.Call;

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

    public void infoUsuario(String correo, Callbacks cb) {
        DAOUsuario dao = new DAOUsuario();
        dao.getUsuario(correo, cb);
    }

    public void loginCorreo(String correo, String contraseña, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.loginCorreo(correo, contraseña, cb);
    }
    public void getUsuarioByUsername(String username, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.getUsuarioByUserName(username, cb);
    }
}
