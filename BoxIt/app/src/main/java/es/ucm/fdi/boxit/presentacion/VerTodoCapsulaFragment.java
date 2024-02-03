package es.ucm.fdi.boxit.presentacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import kotlin.jvm.internal.Intrinsics;

public class VerTodoCapsulaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intrinsics.checkNotNullParameter(inflater, "inflater"); //comprobamos que lo que llega no es ulo
        View view = inflater.inflate(R.layout.fragment_vertodo_capsula, container, false);

        android.net.Uri img = null;
        BoxInfo boxAdd = new BoxInfo("", "ADD", img);
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
        RecyclerView recyclerView2 = view.findViewById(R.id.reclyclerViewBox);
        recyclerView2.setAdapter(b);

        return view;
    }


}