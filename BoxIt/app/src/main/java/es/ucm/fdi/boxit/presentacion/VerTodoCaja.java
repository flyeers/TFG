package es.ucm.fdi.boxit.presentacion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.negocio.BoxInfo;

public class VerTodoCaja extends AppCompatActivity {
    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertodo_caja);

        gestureDetector = new GestureDetector(this, new VerTodoCaja.GestureListener());

        android.net.Uri img = null;
        BoxInfo boxAdd = new BoxInfo("","ADD", img);
        BoxInfo box1 = new BoxInfo("","prueba", img);
        BoxInfo box2 = new BoxInfo("","ahhhhhhhhhhhh", img);
        BoxInfo box3 = new BoxInfo("","help", img);
        ArrayList<BoxInfo> a = new ArrayList<>();
        a.add(boxAdd);
        a.add(box1);
        a.add(box2);
        a.add(box3);

        BoxAdapter b = new BoxAdapter();
        b.setBoxData(a, true, false);
        RecyclerView recyclerView2 = findViewById(R.id.reclyclerViewBox);
        recyclerView2.setAdapter(b);


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();

            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Deslizamiento hacia la derecha
                    abrirNuevaActividad();
                } else {
                    // Deslizamiento hacia la izquierda
                }
                return true;
            }
            return false;
        }
    }

    private void abrirNuevaActividad() {
        //TODO cambiar 
        Intent intent = new Intent(this, VerTodoCapsula.class);
        startActivity(intent);
    }


}
