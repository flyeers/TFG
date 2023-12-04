package es.ucm.fdi.boxit.integracion;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

import es.ucm.fdi.boxit.negocio.UserInfo;

public class DAOUsuario {

    private FirebaseAuth mAuth;
    private final String NOMBRE = "nombre";
    private final String CORREO = "correo";
    private final String COL_USERS = "users";

    public void createAccount(UserInfo usuarioInsertar)  {

        mAuth.createUserWithEmailAndPassword(usuarioInsertar.getCorreo(), usuarioInsertar.getContraseña())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)  {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("USUARIO", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //metemos en la bd la informacion del usuario

                            Map<String, Object> data = new HashMap<>();
                            data.put(NOMBRE, usuarioInsertar.getNombreUsuario());
                            data.put(CORREO, usuarioInsertar.getCorreo());

                            //getUID() me devuelve el user id de la tabla de usuarios para emparejarlo con el usuario correspondiente
                            SingletonDataBase.getInstance().getDB().collection(COL_USERS).document(user.getUid()).set(data);



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("USUARIO", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }
}
