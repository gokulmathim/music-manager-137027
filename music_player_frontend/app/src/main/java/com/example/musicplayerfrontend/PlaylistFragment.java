package com.example.musicplayerfrontend;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistFragment extends Fragment {
    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private Map<String, List<Song>> playlists;
    private static final String PLAYLISTS_PREF_KEY = "playlists";
    private SharedPreferences prefs;
    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = requireContext().getSharedPreferences("MusicPlayer", Context.MODE_PRIVATE);
        gson = new Gson();
        loadPlaylists();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);

        recyclerView = view.findViewById(R.id.playlists_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlaylistAdapter(new ArrayList<>(playlists.keySet()), this::onPlaylistSelected);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_playlist);
        fab.setOnClickListener(v -> showCreatePlaylistDialog());

        return view;
    }

    private void loadPlaylists() {
        String playlistsJson = prefs.getString(PLAYLISTS_PREF_KEY, "{}");
        Type type = new TypeToken<Map<String, List<Song>>>(){}.getType();
        playlists = gson.fromJson(playlistsJson, type);
        if (playlists == null) {
            playlists = new HashMap<>();
        }
    }

    private void savePlaylists() {
        String playlistsJson = gson.toJson(playlists);
        prefs.edit().putString(PLAYLISTS_PREF_KEY, playlistsJson).apply();
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_playlist, null);
        EditText nameInput = dialogView.findViewById(R.id.playlist_name_input);

        builder.setTitle("Create Playlist")
               .setView(dialogView)
               .setPositiveButton("Create", (dialog, which) -> {
                   String name = nameInput.getText().toString().trim();
                   if (!name.isEmpty() && !playlists.containsKey(name)) {
                       playlists.put(name, new ArrayList<>());
                       savePlaylists();
                       adapter.updatePlaylists(new ArrayList<>(playlists.keySet()));
                   }
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void onPlaylistSelected(String playlistName) {
        List<Song> songs = playlists.get(playlistName);
        if (songs != null) {
            // Show playlist songs in a new activity or dialog
            showPlaylistSongsDialog(playlistName, songs);
        }
    }

    private void showPlaylistSongsDialog(String playlistName, List<Song> songs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_playlist_songs, null);
        RecyclerView songsRecyclerView = dialogView.findViewById(R.id.playlist_songs_recycler_view);
        
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SongAdapter songAdapter = new SongAdapter(getContext(), new ArrayList<>(songs), true);
        songsRecyclerView.setAdapter(songAdapter);

        builder.setTitle(playlistName)
               .setView(dialogView)
               .setPositiveButton("Close", null)
               .show();
    }

    public void addSongToPlaylist(String playlistName, Song song) {
        List<Song> songs = playlists.get(playlistName);
        if (songs != null && !songs.contains(song)) {
            songs.add(song);
            savePlaylists();
        }
    }

    public void removeSongFromPlaylist(String playlistName, Song song) {
        List<Song> songs = playlists.get(playlistName);
        if (songs != null) {
            songs.remove(song);
            savePlaylists();
        }
    }

    public void deletePlaylist(String playlistName) {
        playlists.remove(playlistName);
        savePlaylists();
        adapter.updatePlaylists(new ArrayList<>(playlists.keySet()));
    }
}
