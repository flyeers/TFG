package es.ucm.fdi.boxit.integracion;

import java.util.ArrayList;

import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.CapsuleInfo;
import es.ucm.fdi.boxit.negocio.UserInfo;

public interface Callbacks {
    default void onCallback(UserInfo u){}
    default void onCallbackExito(Boolean exito){}

    default void onCallbackBox(BoxInfo b){}
    default void onCallbackBoxes(ArrayList<BoxInfo> boxes){}

    default void onCallbackItems(ArrayList<String> items){}

    default void onCallbackUsers(ArrayList<UserInfo> users){}

    default void onCallbackCapsule(CapsuleInfo c){}
    default void onCallbackCapsules(ArrayList<CapsuleInfo> capsules){}

}
