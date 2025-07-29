package com.example.musicplayerfrontend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.Serializable;

public class Song implements Serializable {
    public long id;
    public String title;
    public String artist;
    public String path;
    public String album;
    public long albumId;

    public Song(long id, String title, String artist, String path, String album, long albumId) {
        this.id = id; this.title = title; this.artist = artist; this.path = path; this.album = album; this.albumId = albumId;
    }

    public Bitmap getAlbumArt(Context context) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(path);
            byte[] art = mmr.getEmbeddedPicture();
            if (art != null) {
                return BitmapFactory.decodeByteArray(art, 0, art.length);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
