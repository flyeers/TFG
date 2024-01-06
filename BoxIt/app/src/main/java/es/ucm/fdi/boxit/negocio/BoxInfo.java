package es.ucm.fdi.boxit.negocio;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class BoxInfo implements Parcelable {

    private String title;
    private android.net.Uri img;

    private ArrayList<String> colaborators = new ArrayList<>(); //Array de ids de usuarios
    public BoxInfo(String title, android.net.Uri img){
        this.title=title;
        this.img=img;
    }

    protected BoxInfo(Parcel in) {
        title = in.readString();
        img = in.readParcelable(Uri.class.getClassLoader());
        colaborators = in.createStringArrayList();
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

    public android.net.Uri  getImg(){
        return img;
    }

    public ArrayList<String> getColaborators(){ return colaborators;}

    public void setColaborators(ArrayList<String> colaborators) {
        this.colaborators = colaborators;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
       // dest.writeString(String.valueOf(img));
    }
}
