package es.ucm.fdi.boxit.negocio;

import android.net.Uri;

import java.util.ArrayList;

public class UserInfo {
    private String nombreUsuario, correo, contraseña, nombre;
    private android.net.Uri imgPerfil;
    private ArrayList<String> boxPropias = new ArrayList<>(), boxCompartidas = new ArrayList<>(), capPropias = new ArrayList<>(), capCompartidas = new ArrayList<>();

    public UserInfo(String nombreUsuario, String correo, String contraseña, String nombre, Uri foto){
        this.contraseña = contraseña;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.nombre = nombre;
        this.imgPerfil = foto;
    }

    //constructor para usuario ya creado

    public UserInfo(){

    };
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public String getNombre() {
        return nombre;
    }


    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public ArrayList<String> getBoxPropias() {
        return boxPropias;
    }

    public void setBoxPropias(ArrayList<String> boxPropias) {
        this.boxPropias = boxPropias;
    }

    public ArrayList<String> getBoxCompartidas() {
        return boxCompartidas;
    }

    public void setBoxCompartidas(ArrayList<String> boxCompartidas) {
        this.boxCompartidas = boxCompartidas;
    }

    public ArrayList<String> getCapPropias() {
        return capPropias;
    }

    public void setCapPropias(ArrayList<String> capPropias) {
        this.capPropias = capPropias;
    }

    public ArrayList<String> getCapCompartidas() {
        return capCompartidas;
    }

    public void setCapCompartidas(ArrayList<String> capCompartidas) {
        this.capCompartidas = capCompartidas;
    }

    public Uri getImgPerfil() {
        return imgPerfil;
    }

    public void setImgPerfil(Uri imgPerfil) {
        this.imgPerfil = imgPerfil;
    }
}
