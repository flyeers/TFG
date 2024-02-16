package es.ucm.fdi.boxit.negocio;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class CapsuleInfo extends BoxInfo{

    private Date apertura, cierre;
    public CapsuleInfo(String id, String title, Uri img) {
        super(id, title, img);
    }

    protected CapsuleInfo(Parcel in) {
        super(in);
    }

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

}
