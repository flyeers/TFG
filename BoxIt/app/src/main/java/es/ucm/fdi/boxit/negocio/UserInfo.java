package es.ucm.fdi.boxit.negocio;

public class UserInfo {
    private String nombreUsuario, correo, contraseña;

    public UserInfo(String nombreUsuario, String correo, String contraseña){
        this.contraseña = contraseña;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
    }

    //constructo para usuario ya creado

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


    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
}
