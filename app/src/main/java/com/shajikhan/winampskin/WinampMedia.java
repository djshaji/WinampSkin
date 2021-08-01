package com.shajikhan.winampskin;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.HashMap;
import java.util.List;

public class WinampMedia {
    String TAG = "WinampMedia";
    Context context ;
    MainActivity mainActivity ;
    WinampSkin winampSkin ;
    ExoPlayer exoPlayer = null;
    Handler handler ;

    String shaji [][] = {
            // yay!
            {"Winamp Intro", "https://cdn.rawgit.com/captbaritone/webamp/43434d82/mp3/llama-2.91.mp3"},
            {"Stay Away from the Naughty Boys", "https://music.shaji.in/media/ACE%20Riff%20Project%20(With%20Rohan)/Stay%20Away%20From%20The%20Naughty%20Boys.mp3"},
            {"Savera", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3"},
            {"Naukri", "https://music.shaji.in/media/No%20Destination%20(Preview)/naukri.mp3"},
            {"Jannat Ke Raaste", "https://music.shaji.in/media/No%20Destination%20(Preview)/JannatkeRaaste.mp3"},
            {"Jung", "https://music.shaji.in/media/Give%20Time%20Its%20Time%20(2011)/Jung.mp3"},
            {"Rehnuma", "https://music.shaji.in/media/Give%20Time%20Its%20Time%20(2011)/Rehnuma.mp3"},
            {"Sound of Freedom", "https://music.shaji.in/media/ACE%20Riff%20Project%20(With%20Rohan)/The%20Sound%20of%20Freedom.mp3"},
            {"Aashian", "https://music.shaji.in/media/Abstractions%20Inconclusive%20(2009)/Aashian.mp3"},
            {"Tere Bin", "https://music.shaji.in/media/Best%20of%20Solitude%20Saints/TereBin.mp3"},
            {"We three", "https://music.shaji.in/media/Best%20of%20Solitude%20Saints/WeThree.mp3"},
    };

    WinampMedia (Context _context, MainActivity _mainActivity) {
        context = _context ;
        mainActivity = _mainActivity ;
        winampSkin = mainActivity.winampSkin;
        setupPlayer();
        setupPlaylist();
//        play (shaji [shaji.length -1][0], shaji [shaji.length -1][1]);
    }

    void setupPlayer () {
        if (exoPlayer == null) exoPlayer = new SimpleExoPlayer.Builder(context).build();
        if (handler == null) handler = new Handler();

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                updateProgressBar();
            }
        });

        winampSkin.seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (! fromUser) return;
                exoPlayer.seekTo((long) ((float)exoPlayer.getDuration() * (float) (progress / 100.0)));
                Log.d (TAG, String.valueOf(exoPlayer.getDuration() + ' ' + (progress / 100)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
//        winampSkin.playlistAdd("U2 - Vertigo", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");
//        winampSkin.playlistAdd("Oasis - Live Forever", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");
//        winampSkin.playlistAdd("Shaji - Savera", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");
//        winampSkin.playlistAdd("Shaji - Savera", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");

        for (String []track: shaji) {
            winampSkin.playlistAdd(track [0], track [1]);
        }

        winampSkin.playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                winampSkin.seek.setVisibility(View.VISIBLE);
                winampSkin.bigClock.setVisibility(View.VISIBLE);
                winampSkin.trackTitle.setVisibility(View.VISIBLE);
                winampSkin.trackTitle.setText(winampSkin.playlistElements.get(position));
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

    public void destroy () {
        exoPlayer.release();
    }

    private void updateProgressBar() {
        long duration = exoPlayer == null ? 0 : exoPlayer.getDuration();
        long position = exoPlayer == null ? 0 : exoPlayer.getCurrentPosition();
        float p =  ((float)position /(float) duration) * 100.0f ;
        long time = position / 1000 ;
        int min = (int) (time / 60);
        int sec = (int) (time - (60*min));
        String sec_ = String.valueOf(sec);
        if (sec < 10) {
            sec_ = '0' + sec_ ;
        }
        String text = min +"  " + sec_ ;
        winampSkin.bigClock.setText(text);
        Log.d(TAG, "updateProgressBar: " + time + " " + time/60);
        winampSkin.seek.setProgress((int)(p));
        long bufferedPosition = exoPlayer == null ? 0 : exoPlayer.getBufferedPosition();
        Log.d(TAG, "updateProgressBar: " + p);
        winampSkin.seek.setSecondaryProgress((int) bufferedPosition);
        // Remove scheduled updates.
        handler.removeCallbacks(updateProgressAction);
        // Schedule an update if necessary.
        int playbackState = exoPlayer == null ? Player.STATE_IDLE : exoPlayer.getPlaybackState();
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            long delayMs;
            if (exoPlayer.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
            handler.postDelayed(updateProgressAction, delayMs);
        } else {
            winampSkin.bigClock.setVisibility(View.INVISIBLE);
            winampSkin.seek.setVisibility(View.INVISIBLE);
            winampSkin.trackTitle.setVisibility(View.INVISIBLE);
        }
    }

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgressBar();
        }
    };

    void play (String track, String uri) {
        winampSkin.seek.setVisibility(View.VISIBLE);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();

    }
}
