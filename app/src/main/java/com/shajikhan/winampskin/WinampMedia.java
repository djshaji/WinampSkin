package com.shajikhan.winampskin;

import static android.app.Activity.RESULT_OK;
import static android.media.audiofx.AudioEffect.CONTENT_TYPE_MUSIC;
import static android.media.audiofx.AudioEffect.EXTRA_AUDIO_SESSION;
import static android.media.audiofx.AudioEffect.EXTRA_CONTENT_TYPE;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.DynamicsProcessing;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class WinampMedia {
    private int notificationId = 69;
    private PlayerNotificationManager playerNotificationManager;

    private PlayerNotificationManager.MediaDescriptionAdapter mediaDescriptionAdapter = new PlayerNotificationManager.MediaDescriptionAdapter() {
        @Override
        public String getCurrentSubText(Player player) {
            if (exoPlayer.isPlaying())
                return mainActivity.getResources().getString(R.string.app_name) + " playing" ;
            else
                return mainActivity.getResources().getString(R.string.app_name) + " stopped" ;
        }

        @Override
        public String getCurrentContentTitle(Player player) {
            return (String) winampSkin.trackTitle.getText();
        }

        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            return null;
        }

        @Override
        public String getCurrentContentText(Player player) {
            return (String) winampSkin.bigClock.getText();
        }

        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            return BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.winamp_logo);
        }
    };

    String TAG = "WinampMedia";
    Context context ;
    MainActivity mainActivity ;
    WinampSkin winampSkin ;
    ExoPlayer exoPlayer = null;
    PopupMenu playlistMenuAdd, playlistMenuRemove,
            playlistMenuSelect,  getPlaylistMenuLoad,
            playlistMenuMisc, getPlaylistMenuFab;
    Handler handler ;
    WinampEqualizer winampEqualizer ;
    int OPEN_FILE = 1,
        OPEN_FOLDER = 2 ;

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

    @RequiresApi(api = Build.VERSION_CODES.P)
    WinampMedia (Context _context, MainActivity _mainActivity) {
        context = _context ;
        mainActivity = _mainActivity ;
        winampSkin = mainActivity.winampSkin;
        setupPlayer();
        setupPlaylist();
        setupPlaylistMenu();
        setupEqualizer();
//        play (shaji [shaji.length -1][0], shaji [shaji.length -1][1]);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
//        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(mainActivity.context, "Xenamp", R.string.app_name, notificationId, mediaDescriptionAdapter, new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
            }

            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
            }
        });
        playerNotificationManager.setPlayer(exoPlayer);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    void setupPlayer () {
        if (exoPlayer == null) {
            exoPlayer = new SimpleExoPlayer.Builder(context).build();
            winampEqualizer = new WinampEqualizer(exoPlayer.getAudioComponent().getAudioSessionId());
        }

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                int sourceIndex = exoPlayer.getCurrentWindowIndex();
//                TrackSelection trackSelection = trackSelections.get(sourceIndex);
                MediaItem mediaItem = exoPlayer.getCurrentMediaItem() ;
                if (mediaItem == null) return ;
                String uri = String.valueOf(exoPlayer.getCurrentMediaItem().mediaMetadata.title);
                Log.d(TAG, "onTracksChanged: " + uri);

            }
        });

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                int sourceIndex = exoPlayer.getCurrentWindowIndex();
                MediaItem mediaItem =  exoPlayer.getCurrentMediaItem() ;
                if (mediaItem == null) return ;

                Log.d(TAG, "onPositionDiscontinuity: " + mediaItem.playbackProperties.uri.getLastPathSegment());
                winampSkin.trackTitle.setText(mediaItem.playbackProperties.uri.getLastPathSegment());


            }
        });

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

        winampSkin.eq_pl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eqIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                eqIntent.putExtra(EXTRA_CONTENT_TYPE, CONTENT_TYPE_MUSIC);
                eqIntent.putExtra(EXTRA_AUDIO_SESSION, CONTENT_TYPE_MUSIC);

                mainActivity.startActivityForResult(eqIntent, 0);

            }
        });

        winampSkin.about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                winampSkin.SkinBrowserDialog();
//                Intent eqIntent = new Intent(mainActivity, SkinBrowser.class);
//                mainActivity.startActivityForResult(eqIntent, 0);
            }
        });

        winampSkin.volume.setProgress((int) exoPlayer.getVolume() * 1000);
        winampSkin.volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                exoPlayer.setVolume((float)progress / 1000.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        winampSkin.shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.setShuffleModeEnabled(!exoPlayer.getShuffleModeEnabled());
                winampSkin.paintShuffle(exoPlayer.getShuffleModeEnabled());
            }
        });

        winampSkin.repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int repeatMode = exoPlayer.getRepeatMode() ;
                if (repeatMode == 0) {
                    winampSkin.paintRepeat(true);
                    repeatMode = Player.REPEAT_MODE_ALL ;
                } else {
                    repeatMode = 0;
                    winampSkin.paintRepeat(false);
                }

                exoPlayer.setRepeatMode(repeatMode);
            }
        });
    }

    void setupPlaylist () {
//        winampSkin.playlistAdd("U2 - Vertigo", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");
//        winampSkin.playlistAdd("Oasis - Live Forever", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");
//        winampSkin.playlistAdd("Shaji - Savera", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");
//        winampSkin.playlistAdd("Shaji - Savera", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");

        // if there is no saved playilst, graciously give the user somethign to listen to
        JSONObject savedPlaylist = winampSkin.loadPlaylist(null);
        if (savedPlaylist == null) {
            for (String[] track : shaji) {
                winampSkin.playlistAdd(Uri.parse(track[1]).getLastPathSegment(), track[1]);
            }
        } else {
            JSONArray keys = savedPlaylist.names();
            for (int i = 0 ; i < keys.length() ; i ++) {
                try {
                    winampSkin.playlistAdd(keys.getString(i), savedPlaylist.getString(keys.getString(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d(TAG, "setupPlaylist: " +
                winampSkin.playlistElements.toString() + "\n"+
                winampSkin.playlistUri.toString() + "\n"
                );

        winampSkin.playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("onItemClick: Playing %s", winampSkin.playlistUri.get(parent.getAdapter().getItem(position).toString()).toString()));
                winampSkin.seek.setVisibility(View.VISIBLE);
                winampSkin.bigClock.setVisibility(View.VISIBLE);
                winampSkin.trackTitle.setVisibility(View.VISIBLE);
                winampSkin.trackTitle.setText(winampSkin.playlistElements.get(position));
                // Build the media item.
//                MediaItem mediaItem = MediaItem.fromUri(winampSkin.playlistUri.get(parent.getAdapter().getItem(position).toString()).toString());
//                exoPlayer.addMediaItem(mediaItem);
                // Set the media item to be played.

                exoPlayer.clearMediaItems();

                for (int i = position ; i < winampSkin.playlistElements.size() ; i ++) {
                    exoPlayer.addMediaItem(MediaItem.fromUri(winampSkin.playlistUri.get(parent.getAdapter().getItem(i).toString()).toString()));
                }

                for (int i = 0 ; i < position ; i ++) {
                    exoPlayer.addMediaItem(MediaItem.fromUri(winampSkin.playlistUri.get(parent.getAdapter().getItem(i).toString()).toString()));
                }

                // Prepare the player.
                exoPlayer.prepare();
                // Start the playback.
                exoPlayer.play();
            }
        });
    }

    public void destroy () {
        exoPlayer.release();
        winampEqualizer.destroy();
        playerNotificationManager.setPlayer(null);
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

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void setupEqualizer () {
        int [] bars  = {
                R.id.equalizer_60,
                R.id.equalizer_170,
                R.id.equalizer_310,
                R.id.equalizer_600,
                R.id.equalizer_1000,
                R.id.equalizer_3000,
                R.id.equalizer_6000,
                R.id.equalizer_12000,
                R.id.equalizer_14000,
                R.id.equalizer_16000
        };

        for (int i = 0 ; i < 10 ; i ++) {
            SeekBar seekBar = mainActivity.findViewById(bars [i]) ;
            int finalI = i;
            seekBar.setProgress((int) ((winampEqualizer.eq.getBand(i).getGain() * (10 / 3)) + 50));
            int finalI1 = i;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    DynamicsProcessing.EqBand eq = winampEqualizer.dynamicsProcessing.getConfig().getChannelByChannelIndex(0).getPreEq().getBand(finalI);
                    eq.setGain((progress - 50) * 3 / 10);
                    Log.d(TAG, String.format("onProgressChanged: setting eq %d -> %d", finalI1, (progress - 50) * 3 / 10));
//                    Log.d(TAG, String.format("onProgressChanged: %s", winampEqualizer.eq.toString()));
//                    winampEqualizer.dynamicsProcessing.setEnabled(false);
//                    winampEqualizer.dynamicsProcessing.setEnabled(true);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    void setupPlaylistMenu () {
        Button  b = mainActivity.findViewById(R.id.playlist_add),
                r = mainActivity.findViewById(R.id.playlist_remove),
                s = mainActivity.findViewById(R.id.playlist_select),
                m = mainActivity.findViewById(R.id.playlist_misc),
                l = mainActivity.findViewById(R.id.playlist_load) ;
        FloatingActionButton fab = mainActivity.findViewById(R.id.fab);

        playlistMenuAdd = new PopupMenu(mainActivity.context, b);
        MenuInflater inflater = playlistMenuAdd.getMenuInflater();
        inflater.inflate(R.menu.playlist_add, playlistMenuAdd.getMenu());
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistMenuAdd.show();
            }
        });

        playlistMenuRemove = new PopupMenu(context, r);
        playlistMenuRemove.inflate(R.menu.playlist_remove);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistMenuRemove.show();
            }
        });

        playlistMenuSelect = new PopupMenu(context, s);
        playlistMenuSelect.inflate(R.menu.playlist_select);
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistMenuSelect.show();
            }
        });

        playlistMenuSelect = new PopupMenu(context, s);
        playlistMenuSelect.inflate(R.menu.playlist_select);
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistMenuSelect.show();
            }
        });

        getPlaylistMenuLoad = new PopupMenu(context, l);
        getPlaylistMenuLoad.inflate(R.menu.playlist_load);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlaylistMenuLoad.show();
            }
        });

        playlistMenuMisc = new PopupMenu(context, m);
        playlistMenuMisc.inflate(R.menu.playlist_misc);
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistMenuMisc.show();
            }
        });

        getPlaylistMenuFab = new PopupMenu(context, fab);
        getPlaylistMenuFab.inflate(R.menu.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlaylistMenuFab.show();
            }
        });

        getPlaylistMenuFab.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent ;
                switch (item.getItemId()) {
                    default:
                        break ;
                    case R.id.menu_skin:
                        winampSkin.SkinBrowserDialog();
                        break ;
                    case R.id.menu_skin_default:
                        winampSkin.setDefaultSkin();
                        break ;
                    case R.id.menu_open_folder:
                        mainActivity.startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), OPEN_FOLDER);
                        break ;
                    case R.id.menu_open_file:
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.setType("audio/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        mainActivity.startActivityForResult(intent, OPEN_FILE);

                        break ;
                }
                return false;
            }
        });

        // intents for all the menu items
        playlistMenuRemove.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SparseBooleanArray checked = winampSkin.playlistView.getCheckedItemPositions();
                switch (item.getItemId()) {
                    default:
                        break;
                    case R.id.playlist_remove_all:
                        winampSkin.playlistClear();
                        exoPlayer.clearMediaItems();
                        break ;
                    case R.id.playlist_remove_selected:
//                        for (int i = 0 ; i < winampSkin.playlistView.getChildCount() ; i ++) {
//                            if (winampSkin.playlistView.isItemChecked(i))
//                                winampSkin.playlistRemove(i);
//                        }

                        for (int i = 0; i < winampSkin.playlistView.getAdapter().getCount(); i++) {
                            if (checked.get(i)) {
                                winampSkin.playlistRemove(i);
                                // Do something
                            }
                        }

                        break ;
                    case R.id.playlist_remove_unselected:
                        for (int i = 0; i < winampSkin.playlistView.getAdapter().getCount(); i++) {
                            if (!checked.get(i)) {
                                winampSkin.playlistRemove(i);
                                // Do something
                            }
                        }

                        break ;
                }
                    return true;
            }
        });

        playlistMenuAdd.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent ;
                switch (item.getItemId()) {
                    default:
                        break ;
                    case R.id.add_local_folder:
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//                        intent.setType("*/*");
//                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                        mainActivity.startActivityForResult(intent, OPEN_FOLDER);
                        break ;

                    case R.id.add_local_file:
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.setType("audio/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        mainActivity.startActivityForResult(intent, OPEN_FILE);
                        break ;

                    case R.id.add_url:
                        addUrl();
                        break;
                    case R.id.add_featured:
                        winampSkin.jsonDialog("https://xenamp-android.web.app/featured.json");
                        break ;
                }
                return false;
            }
        });

        playlistMenuMisc.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    default:
                        break ;
                    case R.id.misc_randomize:
                        winampSkin.playlistShuffle();
                        break ;
                    case R.id.misc_reverse:
                        winampSkin.playlistReverse();
                        break ;
                }
                return false;
            }
        });

        playlistMenuSelect.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    default:
                        break ;
                    case R.id.playlist_select_all:
                        for (int i = 0 ; i < winampSkin.playlistView.getChildCount() ; i ++) {
                            winampSkin.playlistView.setItemChecked(i, true);
                        }
                        break ;
                    case R.id.playlist_select_none:
                        for (int i = 0 ; i < winampSkin.playlistView.getChildCount() ; i ++) {
                            winampSkin.playlistView.setItemChecked(i, false);
                        }
                        break ;
                    case R.id.playlist_select_invert:
                        for (int i = 0 ; i < winampSkin.playlistView.getChildCount() ; i ++) {
                            winampSkin.playlistView.setItemChecked(i, ! winampSkin.playlistView.isItemChecked(i));
                        }
                        break ;
                }

                return false;
            }
        });
    }

    public void addUrl () {
        AlertDialog.Builder alert = new AlertDialog.Builder(mainActivity);
        final EditText edittext = new EditText(mainActivity);
        alert.setMessage("Add URL");
        alert.setTitle(R.string.app_name);

        alert.setView(edittext);
        edittext.setPadding(10,10,10,10);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
//                Editable YouEditTextValue = edittext.getText();
                //OR
                String YouEditTextValue = edittext.getText().toString();
                winampSkin.playlistAdd(Uri.parse(YouEditTextValue).getLastPathSegment(), YouEditTextValue.toString());
                exoPlayer.addMediaItem(MediaItem.fromUri(YouEditTextValue));

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();

    }
}
