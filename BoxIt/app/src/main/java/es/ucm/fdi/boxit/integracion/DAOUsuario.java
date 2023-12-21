package es.ucm.fdi.boxit.integracion;

import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.ucm.fdi.boxit.negocio.UserInfo;

public class DAOUsuario {

    private FirebaseAuth mAuth;
    private final String NOMBRE_USU = "username";
    private final String NOMBRE = "nombre";
    private final String CORREO = "correo";
    private final String COL_USERS = "users";
    private final String TOKEN = "token";

    public DAOUsuario(){
        this.mAuth = FirebaseAuth.getInstance();
    }


    public void createAccount(UserInfo usuarioInsertar, Callbacks cb)  {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    String token = task.getResult();

                    mAuth.createUserWithEmailAndPassword(usuarioInsertar.getCorreo(), usuarioInsertar.getContraseña())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)  {

                                    if (task.isSuccessful()) {

                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("USUARIO", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        new ArrayList<String>();

                                        //metemos en la bd la informacion del usuario
                                        Map<String, Object> data = new HashMap<>();
                                        data.put(NOMBRE_USU, usuarioInsertar.getNombreUsuario());
                                        data.put(CORREO, usuarioInsertar.getCorreo());
                                        data.put(NOMBRE, usuarioInsertar.getNombre());
                                        data.put(TOKEN, token);

                                        //getUID() me devuelve el user id de la tabla de usuarios para emparejarlo con el usuario correspondiente
                                        SingletonDataBase.getInstance().getDB().collection(COL_USERS).document(user.getUid()).set(data);
                                        cb.onCallbackExito(true);


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("USUARIO", "createUserWithEmail:failure", task.getException());
                                        cb.onCallbackExito(false);
                                    }
                                }
                            });
                } else {
                    cb.onCallbackExito(false);
                }
            }
        });


    }

    public void logOut(){
        FirebaseAuth.getInstance().signOut();
    }

    public void getUsuario(String email, Callbacks cb){
        UserInfo userInfo = new UserInfo();
        FirebaseUser user = mAuth.getCurrentUser();
        SingletonDataBase.getInstance().getDB().collection(COL_USERS).whereEqualTo(CORREO,
                email).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot d: task.getResult()){

                    userInfo.setNombre(d.get(NOMBRE).toString());
                    userInfo.setNombreUsuario(d.get(NOMBRE_USU).toString());

                }

                cb.onCallback(userInfo);
            }
        });

    }

    public void getUsuarioByUserName(String username, Callbacks cb){
        UserInfo userInfo = new UserInfo();
        FirebaseUser user = mAuth.getCurrentUser();
        SingletonDataBase.getInstance().getDB().collection(COL_USERS).whereEqualTo(NOMBRE_USU,
                username).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot d: task.getResult()){

                    userInfo.setNombre(d.get(NOMBRE).toString());
                    userInfo.setCorreo(d.get(CORREO).toString());

                }

                cb.onCallback(userInfo);
            }
        });

    }

    public void loginCorreo(String correo, String contraseña, Callbacks cb){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    String newToken = task.getResult();

                    mAuth.signInWithEmailAndPassword(correo, contraseña)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("USUARIO", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("token", newToken);
                                        SingletonDataBase.getInstance().getDB()
                                                .collection(COL_USERS).document(user.getUid()).update(updates);

                                        cb.onCallbackExito(true);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("USUARIO", "signInWithEmail:failure", task.getException());
                                        cb.onCallbackExito(false);

                                    }
                                }
                            });
                }
            }
        });
    }

    public void loginUserName(String username, String contraseña, Callbacks cb){


    }

}
