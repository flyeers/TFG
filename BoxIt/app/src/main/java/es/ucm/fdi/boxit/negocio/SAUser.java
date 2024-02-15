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

    public void getBoxes(String correo, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.getUserBoxes(correo, cb);
    }

    public void getBoxesCompartidas(String correo, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.getUserBoxesCompartidas(correo, cb);
    }

    public void getAmigos(String correo, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.getAmigos(correo, cb);
    }

    public void addAmigo(String correo, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.addAmigo(correo, cb);
    }

    public void removeAmigo(String correo, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.removeAmigo(correo, cb);
    }

    public void getSolicitudes(String correo, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.getSolicitudes(correo, cb);
    }

    public void sendSolicitud(String correo, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.sendSolicitud(correo, cb);
    }

    public void removeSolicitud(String correo, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.removeSolicitud(correo, cb);
    }

    public void searchUsuario(String username, Callbacks cb){
        DAOUsuario dao = new DAOUsuario();
        dao.searchUsuario(username, cb);
    }

}
