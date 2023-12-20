package es.ucm.fdi.boxit.negocio;

import android.content.Context;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidarFormulario {
    String email;
    String username;
    String name;
    String password;
    Context ctx;

    public ValidarFormulario(Context ctx, String email, String username, String password, String name){
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name;
        this.ctx = ctx;

    }

    public boolean isValidUserName(){
        //TODO HACE FALTA REVISAR QUE EL USERNAME NO ESTE YA EN LA BD
        return username != null && !username.trim().isEmpty();
    }

    public boolean isValidName(){

        return name != null && !name.trim().isEmpty();
    }
    public boolean isValidEmail(){
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Contraseña segura -> al menos 11 caracteres, 1 número y 1 mayuscula
    public boolean isValidPassword(){

        String regex = "^(?=.*[A-Z])(?=.*\\d).{11,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
