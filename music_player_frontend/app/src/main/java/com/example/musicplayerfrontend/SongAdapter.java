package com.example.musicplayerfrontend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private ArrayList<Song> songs;
    private Context context;
    private boolean openPlayerOnClick;

    public SongAdapter(Context context, ArrayList<Song> songs, boolean openPlayerOnClick) {
        this.context = context;
        this.songs = songs;
        this.openPlayerOnClick = openPlayerOnClick;
    }

    // PUBLIC_INTERFACE
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    // PUBLIC_INTERFACE
    @Override
    public void onBindViewHolder(SongAdapter.ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.title.setText(song.title);
        holder.artist.setText(song.artist);
        Bitmap albumArt = song.getAlbumArt(context);
        if (albumArt != null)
            holder.albumArt.setImageBitmap(albumArt);
        else
            holder.albumArt.setImageResource(R.drawable.ic_music);

        holder.itemView.setOnClickListener(v -> {
            if (openPlayerOnClick) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("songIndex", position);
                intent.putExtra("songList", songs);
                context.startActivity(intent);
            }
        });
    }

    // PUBLIC_INTERFACE
    @Override
    public int getItemCount() {
        return songs.size();
    }

    // PUBLIC_INTERFACE
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView albumArt;
        TextView title;
        TextView artist;

        public ViewHolder(View itemView) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.song_album_art);
            title = itemView.findViewById(R.id.song_title);
            artist = itemView.findViewById(R.id.song_artist);
        }
    }
}
