package es.ucm.fdi.boxit.integracion;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

}
