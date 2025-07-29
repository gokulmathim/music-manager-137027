package com.example.musicplayerfrontend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    private ImageView albumArtView;
    private TextView titleView, artistView;
    private ImageButton playBtn, nextBtn, prevBtn, repeatBtn, shuffleBtn, equalizerBtn;
    private SeekBar seekBar;
    private MusicService musicService;
    private boolean serviceBound = false;
    private Handler handler;
    private ArrayList<Song> songs;
    private EqualizerDialog equalizerDialog;
    
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            serviceBound = true;
            setupService();
            setupObservers();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    // PUBLIC_INTERFACE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        handler = new Handler(Looper.getMainLooper());
        
        @SuppressWarnings("unchecked")
        ArrayList<Song> songList = (ArrayList<Song>) getIntent().getSerializableExtra("songList");
        if (songList != null) {
            songs = songList;
        } else {
            songs = new ArrayList<>();
            finish();
            return;
        }
        
        initializeViews();
        setupClickListeners();
        
        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(serviceIntent);
    }

    private void initializeViews() {
        albumArtView = findViewById(R.id.player_album_art);
        titleView = findViewById(R.id.player_song_title);
        artistView = findViewById(R.id.player_song_artist);
        playBtn = findViewById(R.id.player_play_button);
        prevBtn = findViewById(R.id.player_prev_button);
        nextBtn = findViewById(R.id.player_next_button);
        repeatBtn = findViewById(R.id.player_repeat_button);
        shuffleBtn = findViewById(R.id.player_shuffle_button);
        equalizerBtn = findViewById(R.id.player_eq_button);
        seekBar = findViewById(R.id.player_seekbar);
    }

    private void setupService() {
        if (serviceBound) {
            musicService.setPlaylist(songs, getIntent().getIntExtra("songIndex", 0));
        }
    }

    private void setupObservers() {
        musicService.currentSong.observe(this, song -> {
            if (song != null) {
                titleView.setText(song.title);
                artistView.setText(song.artist);
                Bitmap art = song.getAlbumArt(this);
                if (art != null) {
                    albumArtView.setImageBitmap(art);
                } else {
                    albumArtView.setImageResource(R.drawable.ic_music);
                }
                setupSeekBar();
            }
        });

        musicService.isPlaying.observe(this, playing -> {
            playBtn.setImageResource(playing ? R.drawable.ic_pause : R.drawable.ic_play);
        });
    }

    private void setupClickListeners() {
        playBtn.setOnClickListener(v -> {
            if (serviceBound) {
                musicService.playPause();
            }
        });

        nextBtn.setOnClickListener(v -> {
            if (serviceBound) {
                musicService.playNext();
            }
        });

        prevBtn.setOnClickListener(v -> {
            if (serviceBound) {
                musicService.playPrevious();
            }
        });

        repeatBtn.setOnClickListener(v -> {
            if (serviceBound) {
                boolean isRepeat = repeatBtn.getAlpha() < 1.0f;
                repeatBtn.setAlpha(isRepeat ? 1.0f : 0.3f);
                musicService.setRepeat(isRepeat);
            }
        });

        shuffleBtn.setOnClickListener(v -> {
            if (serviceBound) {
                boolean isShuffle = shuffleBtn.getAlpha() < 1.0f;
                shuffleBtn.setAlpha(isShuffle ? 1.0f : 0.3f);
                musicService.setShuffle(isShuffle);
            }
        });

        equalizerBtn.setOnClickListener(v -> {
            if (serviceBound) {
                if (equalizerDialog == null) {
                    equalizerDialog = new EqualizerDialog(this, musicService.getAudioSessionId());
                }
                equalizerDialog.show();
            }
        });
    }

    private void setupSeekBar() {
        if (!serviceBound) return;
        
        seekBar.setMax(musicService.getDuration());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (serviceBound) {
                    seekBar.setProgress(musicService.getCurrentPosition());
                    handler.postDelayed(this, 500);
                }
            }
        }, 500);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (serviceBound) {
                    musicService.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
