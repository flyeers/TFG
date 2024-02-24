package es.ucm.fdi.boxit.negocio;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class BoxInfo implements Parcelable {


    private String title, id;
    private android.net.Uri img;

    private ArrayList<String> collaborators = new ArrayList<>(); //Array de ids de usuarios
    public BoxInfo(String id, String title, android.net.Uri img){
        this.title=title;
        this.img=img;
        this.id=id;
    }

    protected BoxInfo(Parcel in) {
        title = in.readString();
        img = in.readParcelable(Uri.class.getClassLoader());
        collaborators = in.createStringArrayList();
        id = in.readString();
    }

    public static final Creator<BoxInfo> CREATOR = new Creator<BoxInfo>() {
        @Override
        public BoxInfo createFromParcel(Parcel in) {
            return new BoxInfo(in);
        }

        @Override
        public BoxInfo[] newArray(int size) {
            return new BoxInfo[size];
        }
    };

    public String getTitle(){
        return title;
    }

    public String getId(){
        return id;
    }

    public android.net.Uri  getImg(){
        return img;
    }

    public ArrayList<String> getColaborators(){ return collaborators;}

    public void setCollaborators(ArrayList<String> collaborators) {
        this.collaborators = collaborators;
    }

    public void setId(String id){this.id = id;}

    public void setImg(Uri img) {
        this.img = img;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        // CUIDADO !! EL ORDEN EN EL QUE SE PONEN ESTOS ATRIBUTOS INFLUYE
        dest.writeString(title);
        dest.writeParcelable(img, flags);
        dest.writeStringList(collaborators);
        dest.writeString(id);
    }

}
