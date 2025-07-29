package com.example.musicplayerfrontend;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // PUBLIC_INTERFACE
    @Override
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MusicPlayer_Light);
        setContentView(R.layout.activity_main);

        setupPermissions();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the Library as the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new LibraryFragment())
                .commit();
        }

    private void setupPermissions() {
        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                // Permission is handled, we can continue
            }
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, 
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            // PUBLIC_INTERFACE
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected = null;
                int id = item.getItemId();
                if (id == R.id.menu_library) {
                    selected = new LibraryFragment();
                } else if (id == R.id.menu_playlists) {
                    selected = new PlaylistFragment();
                } else if (id == R.id.menu_search) {
                    selected = new SearchFragment();
                }
                if (selected != null) {
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, selected)
                        .commit();
                }
                return true;
            }
        });
    }
}
