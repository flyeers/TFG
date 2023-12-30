package es.ucm.fdi.boxit.negocio;

import java.util.ArrayList;

public class BoxInfo {

    private String title;
    private android.net.Uri img;

    private ArrayList<String> colaborators = new ArrayList<>(); //Array de ids de usuarios
    public BoxInfo(String title, android.net.Uri img){
        this.title=title;
        this.img=img;
    }

    public String getTitle(){
        return title;
    }

    public android.net.Uri  getImg(){
        return img;
    }

    public ArrayList<String> getColaborators(){ return colaborators;}

    public void setColaborators(ArrayList<String> colaborators) {
        this.colaborators = colaborators;
    }
}
