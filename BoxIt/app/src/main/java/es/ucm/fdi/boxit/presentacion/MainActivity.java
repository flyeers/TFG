package es.ucm.fdi.boxit.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.negocio.BoxInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);*/

        /*
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("AHHH", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("AHHH", "Error getting documents.", task.getException());
                        }
                    }
                });*/

        BoxInfo box1 = new BoxInfo("prueba", "");
        BoxInfo box2 = new BoxInfo("ahhhhhhhhhhhh", "");
        BoxInfo box3 = new BoxInfo("help", "");
        ArrayList<BoxInfo> a = new ArrayList<>();
        a.add(box1);
        a.add(box2);
        a.add(box3);

        BoxAdapter b = new BoxAdapter();
        b.setBoxData(a, false);
        RecyclerView recyclerView2 = findViewById(R.id.recycler_view_capsule);
        recyclerView2.setAdapter(b);



        BoxAdapter b2 = new BoxAdapter();
        b2.setBoxData(a, true);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_box);
        recyclerView.setAdapter(b2);




    }
}