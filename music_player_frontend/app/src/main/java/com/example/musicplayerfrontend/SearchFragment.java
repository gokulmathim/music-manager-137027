package com.example.musicplayerfrontend;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchInput;
    private SongAdapter songAdapter;
    private ArrayList<Song> allSongs;

    // PUBLIC_INTERFACE
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = v.findViewById(R.id.search_recycler_view);
        searchInput = v.findViewById(R.id.search_input);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        allSongs = SongLoader.getAllSongs(getContext());
        songAdapter = new SongAdapter(getContext(), new ArrayList<>(allSongs), true);
        recyclerView.setAdapter(songAdapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            // PUBLIC_INTERFACE
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            // PUBLIC_INTERFACE
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSongs(s.toString());
            }
            // PUBLIC_INTERFACE
            @Override
            public void afterTextChanged(Editable s) {}
        });

        return v;
    }

    private void filterSongs(String text) {
        ArrayList<Song> filtered = new ArrayList<>();
        for (Song song : allSongs) {
            if (song.title.toLowerCase().contains(text.toLowerCase()) ||
                song.artist.toLowerCase().contains(text.toLowerCase())) {
                filtered.add(song);
            }
        }
        songAdapter = new SongAdapter(getContext(), filtered, true);
        recyclerView.setAdapter(songAdapter);
    }
}
