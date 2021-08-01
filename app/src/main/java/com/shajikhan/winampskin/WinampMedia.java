package com.shajikhan.winampskin;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.HashMap;
import java.util.List;

public class WinampMedia {
    Context context ;
    MainActivity mainActivity ;
    WinampSkin winampSkin ;
    ExoPlayer exoPlayer = null;

    WinampMedia (Context _context, MainActivity _mainActivity) {
        context = _context ;
        mainActivity = _mainActivity ;
        winampSkin = mainActivity.winampSkin;
        setupPlayer();
        setupPlaylist();
    }

    void setupPlayer () {
        if (exoPlayer == null)
            exoPlayer = new SimpleExoPlayer.Builder(context).build();

        winampSkin.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.play();
            }
        });

        winampSkin.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.pause();
            }
        });

        winampSkin.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.stop();
            }
        });

        winampSkin.prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.previous();
            }
        });

        winampSkin.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.next();
            }
        });
    }

    void setupPlaylist () {
        winampSkin.playlistAdd("U2 - Vertigo", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");
        winampSkin.playlistAdd("Oasis - Live Forever", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");
        winampSkin.playlistAdd("Coldplay - Yellow", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");

        winampSkin.playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Build the media item.
                MediaItem mediaItem = MediaItem.fromUri(winampSkin.playlistUri.get(parent.getAdapter().getItem(position).toString()).toString());
                // Set the media item to be played.
                exoPlayer.setMediaItem(mediaItem);
                // Prepare the player.
                exoPlayer.prepare();
                // Start the playback.
                exoPlayer.play();

            }
        });
    }
}
