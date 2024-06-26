package es.ucm.fdi.boxit.negocio;

import android.net.Uri;

import java.util.ArrayList;

public class MusicInfo {

    private String nombre, artista, uriCancion, uriImagen, id;

    private boolean isPlayed;

    public void setId(String id) {
        this.id = id;
    }

    public MusicInfo(String nombre, String artista, String uriCancion, String uriImagen, String id){
        this.nombre = nombre;
        this.artista = artista;
        this.uriCancion = uriCancion;
        this.uriImagen = uriImagen;
        this.id = id;
        isPlayed = false;
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
    public String getId() {
        return id;
    }

    public boolean isPlaying(){return isPlayed;}
    public void setPlayed(boolean p){ isPlayed = p;}
}
