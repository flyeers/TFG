package es.ucm.fdi.boxit.presentacion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;
import kotlin.jvm.internal.Intrinsics;

public class AmigosSolicitudesFragment extends Fragment {

    private ArrayList<UserInfo> solicitudes;
    private TextView text_amigo;
    private AmigosAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intrinsics.checkNotNullParameter(inflater, "inflater"); //comprobamos que lo que llega no es ulo
        View view = inflater.inflate(R.layout.fragment_amigos_solicitudes, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        SAUser saUser = new SAUser();

        solicitudes = new ArrayList<>();

        saUser.getSolicitudes(currentUser.getEmail(), new Callbacks() {
            @Override
            public void onCallbackUsers(ArrayList<UserInfo> users) {
                solicitudes = users;
            }
        });

        text_amigo = view.findViewById(R.id.text_amigos);

        if(!solicitudes.isEmpty()){
            text_amigo.setVisibility(View.GONE);

            adapter = new AmigosAdapter();
            adapter.setUsersData(solicitudes, 2);
            RecyclerView recyclerView  = view.findViewById(R.id.recycler_view_solicitudes);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
        }else{
            text_amigo.setText("NO SOLICITUDES");
        }

        return view;
    }
}
