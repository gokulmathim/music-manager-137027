package com.example.musicplayerfrontend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NowPlayingBar extends LinearLayout {
    private ImageView albumArt;
    private TextView title;
    private ImageButton playPause;
    private MusicService musicService;
    private boolean serviceBound = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            serviceBound = true;
            setupObservers();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    public NowPlayingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_now_playing_bar, this, true);
        
        albumArt = findViewById(R.id.np_album_art);
        title = findViewById(R.id.np_title);
        playPause = findViewById(R.id.np_play_pause);
        
        setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            context.startActivity(intent);
        });
        
        playPause.setOnClickListener(v -> {
            if (serviceBound) {
                musicService.playPause();
            }
        });

        // Bind to MusicService
        Intent serviceIntent = new Intent(context, MusicService.class);
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setupObservers() {
        if (!serviceBound) return;

        if (!(getContext() instanceof MainActivity)) return;
        
        MainActivity activity = (MainActivity) getContext();
        musicService.currentSong.observe(activity, song -> {
            if (song != null) {
                title.setText(song.title + " • " + song.artist);
                Bitmap art = song.getAlbumArt(getContext());
                if (art != null) {
                    albumArt.setImageBitmap(art);
                } else {
                    albumArt.setImageResource(R.drawable.ic_music);
                }
            }
        });

        musicService.isPlaying.observe(activity, playing -> {
            playPause.setImageResource(playing ? R.drawable.ic_pause : R.drawable.ic_play);
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (serviceBound) {
            getContext().unbindService(serviceConnection);
            serviceBound = false;
        }
    }
}
