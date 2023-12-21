package es.ucm.fdi.boxit.integracion;

import es.ucm.fdi.boxit.negocio.UserInfo;

public interface Callbacks {
    default void onCallback(UserInfo u){}
    default void onCallbackExito(Boolean exito){}

}
