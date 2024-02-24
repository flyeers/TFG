package es.ucm.fdi.boxit.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;

import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.ucm.fdi.boxit.R;

public class SpotifyActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "84e06632856840c38d929188d2bfd919";
    private static final String REDIRECT_URI = "com.spotify.boxit://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    EditText search;
    Button buscar;
    TextView info;


    //PLAYERAPI PARA REPRODUCIR, PARAR, SKIP.... INTERACCION CON CANCION
    //CONNECT API VOLUMEN
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }






    private void connected() {
        // Play a playlist
       // mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX5KpP2LN299J");
        ContentApi contentApi = mSpotifyAppRemote.getContentApi();
        String query = "Karma";


        contentApi.getRecommendedContentItems()
        contentApi.getRecommendedContentItems(ContentApi.ContentType.DEFAULT)
                .setResultCallback(new CallResult.ResultCallback<ListItems>() {
                    @Override
                    public void onResult(ListItems listItems) {
                        List<ListItem> playlist = Arrays.asList(listItems.items);

                        for (ListItem track : playlist) {
                            if (track.title.equals("Recently played")) {



                                Log.d("CLAU", "cancion");
                                break; // Sal del bucle una vez que se haya encontrado la canción
                            }
                            else{
                                Log.d("CLAU", "no");
                            }
                        }
                    }
                });



    }


}