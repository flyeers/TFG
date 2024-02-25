package es.ucm.fdi.boxit.negocio;

import android.net.Uri;

import java.util.ArrayList;

public class MusicInfo {

    private String nombre, artista, uriCancion, uriImagen;


    public MusicInfo(String nombre, String artista, String uriCancion, String uriImagen){
        this.nombre = nombre;
        this.artista = artista;
        this.uriCancion = uriCancion;
        this.uriImagen = uriImagen;
    }

    public String getNombre() {
        return nombre;
    }

    public String getArtista() {
        return artista;
    }

    public String getUriCancion() {
        return uriCancion;
    }

    public String getUriImagen() {
        return uriImagen;
    }
}
