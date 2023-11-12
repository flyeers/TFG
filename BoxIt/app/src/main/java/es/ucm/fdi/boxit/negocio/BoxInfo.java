package es.ucm.fdi.boxit.negocio;
public class BoxInfo {

    private String title;
    private String img;
    public BoxInfo(String title, String img){
        this.title=title;
        this.img=img;
    }

    public String getTitle(){
        return title;
    }

    public String getImg(){
        return img;
    }

}
