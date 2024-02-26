package es.ucm.fdi.boxit.presentacion;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.ImageUri;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.boxit.R;
import es.ucm.fdi.boxit.integracion.Callbacks;
import es.ucm.fdi.boxit.negocio.BoxInfo;
import es.ucm.fdi.boxit.negocio.MusicInfo;
import es.ucm.fdi.boxit.negocio.SABox;
import es.ucm.fdi.boxit.negocio.SACapsule;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<MusicInfo> musicData;
    private SpotifyAppRemote mSpotifyAppRemote;
    private String boxId;
    private Context ctx;
    private boolean isBox;

    public void setData(List<MusicInfo> musicData, SpotifyAppRemote mSpotifyAppRemote, String boxId, Context ctx){
        this.musicData = (ArrayList<MusicInfo>) musicData;
        this.mSpotifyAppRemote = mSpotifyAppRemote;
        this.boxId = boxId;
        this.ctx = ctx;
        isBox = true;
    }

    public void setType(boolean notBox){
        this.isBox = notBox;
    }
    @NonNull
    @Override
    public MusicAdapter.MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_view, parent, false);
        return new MusicAdapter.MusicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder h, int position) {

        MusicInfo song = musicData.get(position);
        int inicio = song.getUriImagen().indexOf('{') + 1;
        int fin = song.getUriImagen().indexOf('}') - 1;
        String id = song.getUriImagen().substring(inicio, fin);
        ImageUri imageUri = new ImageUri(id);

        mSpotifyAppRemote.getImagesApi().getImage(imageUri).setResultCallback(
                bitmap -> {

                    h.songCover.setImageBitmap(bitmap);

                });
        h.songName.setText(song.getNombre());
        h.artist.setText(song.getArtista());

        h.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpotifyAppRemote.getPlayerApi().play(song.getUriCancion());
            }
        });

        h.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialogConfirm = new Dialog(ctx);
                dialogConfirm.setContentView(R.layout.eliminar_confirm);
                Button cancelar = dialogConfirm.findViewById(R.id.buttonCancelar);
                Button confirmar = dialogConfirm.findViewById(R.id.buttonEliminar);

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConfirm.dismiss();
                    }
                });

                confirmar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SABox saBox = new SABox();
                        saBox.deleteSong(boxId, song.getId(), isBox, new Callbacks() {
                            @Override
                            public void onCallbackExito(Boolean exito) {
                                if(exito){
                                    int pos = musicData.indexOf(song);
                                    if(pos != -1){
                                        musicData.remove(pos);
                                        notifyDataSetChanged();
                                    }
                                    Toast.makeText(ctx,R.string.deleteBien , Toast.LENGTH_SHORT).show();
                                    dialogConfirm.dismiss();
                                }
                                else{
                                    Toast.makeText(ctx,R.string.deleteMal , Toast.LENGTH_SHORT).show();
                                    dialogConfirm.dismiss();
                                }
                            }
                        });

                    }


                });

                dialogConfirm.show();

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicData.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder{

        private ImageView imagen, songCover;
        private CardView cardView;
        private TextView fileName, songName, artist;


        public MusicViewHolder(View view) {
            super(view);
            imagen = view.findViewById(R.id.photoBox);
            cardView=  view.findViewById(R.id.card_amigo);
            fileName = view.findViewById(R.id.nombreDoc);
            songCover =  view.findViewById(R.id.imageSong);
            songName = view.findViewById(R.id.songTitle);
            artist = view.findViewById(R.id.artist);

        }
    }
}

