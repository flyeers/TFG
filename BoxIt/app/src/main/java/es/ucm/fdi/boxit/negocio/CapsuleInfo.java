package es.ucm.fdi.boxit.negocio;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class CapsuleInfo extends BoxInfo{

    private Date apertura, cierre;
    public CapsuleInfo(String id, String title, Uri img) {
        super(id, title, img);
    }

    protected CapsuleInfo(Parcel in) {
        super(in);
    }

    public static final Creator<CapsuleInfo> CREATOR = new Creator<CapsuleInfo>() {
        @Override
        public CapsuleInfo createFromParcel(Parcel in) {
            return new CapsuleInfo(in);
        }

        @Override
        public CapsuleInfo[] newArray(int size) {
            return new CapsuleInfo[size];
        }
    };

    public Date getApertura() {
        return apertura;
    }

    public void setApertura(Date apertura) {
        this.apertura = apertura;
    }

    public Date getCierre() {
        return cierre;
    }

    public void setCierre(Date cierre) {
        this.cierre = cierre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
