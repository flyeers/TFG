package es.ucm.fdi.boxit.presentacion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.SAUser;
import es.ucm.fdi.boxit.negocio.UserInfo;
import kotlin.jvm.internal.Intrinsics;

public class AmigosBuscarFragment extends Fragment {

    private SearchView s;
    private ArrayList<UserInfo> resBusqueda;
    private TextView text_buscar;
    private AmigosAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intrinsics.checkNotNullParameter(inflater, "inflater"); //comprobamos que lo que llega no es nulo
        View view = inflater.inflate(R.layout.fragment_amigos_buscar, container, false);

        resBusqueda = new ArrayList<>();

        adapter = new AmigosAdapter();
        adapter.setUsersData(resBusqueda, 3);
        RecyclerView recyclerView  = view.findViewById(R.id.recycler_view_buscar_amigos);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        text_buscar = view.findViewById(R.id.text_buscar);
        text_buscar.setVisibility(View.GONE);
        s = view.findViewById(R.id.search_user);

        s.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SAUser saUser = new SAUser();
                saUser.searchUsuario(query, new Callbacks() {
                    @Override
                    public void onCallbackUsers(ArrayList<UserInfo> users) {
                        if(!users.isEmpty()){
                            adapter.setUsersData(users, 3);
                            text_buscar.setVisibility(View.GONE);
                        }
                        else {
                            adapter.setUsersData(users, 3);
                            text_buscar.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyData();
                    }
                });
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }


}
