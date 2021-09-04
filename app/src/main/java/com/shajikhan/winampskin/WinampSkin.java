package com.shajikhan.winampskin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.exoplayer2.C;
import com.shajikhan.winampskin.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class WinampSkin {
    Skin skin;
    Button  prev, next, play, pause, stop, eject, shuffle, repeat, about,
            eq_pl, on, auto, preset;
    SeekBar seek, volume, balance ;
    LinearLayout mainWindow ;
    MainActivity mainActivity ;
    WinampSkin self ;
    boolean shuffleState, repeatState ;
    Context context ;
    DisplayMetrics displayMetrics ;
    String TAG = "WinampSkin";
    // FIXME: 7/24/21 Determine the following automagically
    float UPSCALE_FACTOR = 1.43f ;
    float density ;
    public ArrayAdapter playlistAdapter ;

    List <String> playlistElements ;
    HashMap playlistUri ;
    TextView bigClock, trackTitle ;

    Canvas mainCanvas ;
    Drawable mainDrawable ;
    Paint paint ;
    ListView playlistView ;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    WinampSkin (Context context_, MainActivity mainActivity_) {
        mainActivity = mainActivity_ ;
        self = this;
        context = context_ ;
        displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.scaledDensity;

        paint = new Paint();
        skin = new Skin(context,false);
//        skin.downloadSkin("https://cdn.webampskins.org/skins/01829a4d2b8b379ed34da0a87dd5c0ee.wsz");
//        skin.downloadSkin("https://cdn.webampskins.org/skins/b0fb83cc20af3abe264291bb17fb2a13.wsz");
//        skin.renameSkinFiles(skin.defaultSkinDir);
//        setup();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setup () {
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Log.d(TAG, displayMetrics.toString());
        LinearLayout window = mainActivity.findViewById(R.id.window);

        mainWindow = mainActivity.findViewById(R.id.main_window);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel((int) (width / displayMetrics.scaledDensity  * 116f/275f))) ;
//        Log.d(TAG, "setup: " + width / displayMetrics.scaledDensity  * 116f/275f);
        mainWindow.setLayoutParams(layoutParams);

        setupMainWindow(skin, false, false);
        setupEqualizer();
        setupPlaylist();
        setupPlaylistManagement();
    }

    public void setupMainWindow (Skin skin, boolean shuffleActive, boolean repeatActive) {
        LinearLayout mainWindow = mainActivity.findViewById(R.id.main_window);
        shuffleState = shuffleActive ;
        repeatState = repeatActive ;
        seek = mainWindow.findViewById(R.id.seek_bar);
        volume = mainWindow.findViewById(R.id.volume_bar);
        bigClock = mainWindow.findViewById(R.id.big_clock);
        trackTitle = mainWindow.findViewById(R.id.track_title);
        eq_pl = mainWindow.findViewById(R.id.eq_pl);

        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                mainCanvas = canvas;
                mainDrawable = this ;
                setBounds(0, 0, convertDpToPixel(275), convertDpToPixel(116));
                Paint paint = new Paint();
                canvas.drawBitmap(
                        upscaleBitmap(
                        loadSkinBitmap(0, 0, 275, 116, skin, "main")),
                        0,
                        0,
                        paint
                );
                // titlebar
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                loadSkinBitmap(27, 57, 275, 13, skin, "titlebar")),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        0,
                        convertDpToPixel(3),
                        paint
                );
                // cbuttons
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                loadSkinBitmap(0, 0, 114, 18, skin,"cbuttons")),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(15 * UPSCALE_FACTOR) + 3,
                        convertDpToPixel(87 * UPSCALE_FACTOR) + 3,
                        paint
                );
                //eject
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                loadSkinBitmap(115, 0, 21, 16, skin,"cbuttons")),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(136 * UPSCALE_FACTOR) + 3,
                        convertDpToPixel(88 * UPSCALE_FACTOR) + 3,
                        paint
                );
                //shuffle
//                paintShuffle(false);
                if (!shuffleActive) {
                    canvas.drawBitmap(
                            upscaleBitmap(//Bitmap.createScaledBitmap(
                                    loadSkinBitmap(28, 0, 45, 15, skin,"shufrep")),
                            //convertDpToPixel(275), convertDpToPixel(16), true)),
                            convertDpToPixel(164 * UPSCALE_FACTOR),
                            convertDpToPixel(89 * UPSCALE_FACTOR),
                            paint
                    );
                } else {
                    canvas.drawBitmap(
                            upscaleBitmap(//Bitmap.createScaledBitmap(
                                    loadSkinBitmap(28, 30, 45, 15, skin, "shufrep")),
                            //convertDpToPixel(275), convertDpToPixel(16), true)),
                            convertDpToPixel(164 * UPSCALE_FACTOR),
                            convertDpToPixel(89 * UPSCALE_FACTOR),
                            paint
                    );

                }
                //repeat
                if (! repeatActive) {
                    canvas.drawBitmap(
                            upscaleBitmap(//Bitmap.createScaledBitmap(
                                    loadSkinBitmap(0, 0, 29, 15, skin, "shufrep")),
                            //convertDpToPixel(275), convertDpToPixel(16), true)),
                            convertDpToPixel(209 * UPSCALE_FACTOR),
                            convertDpToPixel(89 * UPSCALE_FACTOR),
                            paint
                    );
                } else {
                    canvas.drawBitmap(
                            upscaleBitmap(//Bitmap.createScaledBitmap(
                                    loadSkinBitmap(0, 30, 29, 15, skin, "shufrep")),
                            //convertDpToPixel(275), convertDpToPixel(16), true)),
                            convertDpToPixel(209 * UPSCALE_FACTOR),
                            convertDpToPixel(89 * UPSCALE_FACTOR),
                            paint
                    );
                }


                // stereo
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                loadSkinBitmap(0, 0, 29, 12, skin, "monoster")),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(239 * UPSCALE_FACTOR),
                        convertDpToPixel(41 * UPSCALE_FACTOR),
                        paint
                );

                // mono
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                loadSkinBitmap(29, 12, 29, 12, skin,"monoster")),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(210 * UPSCALE_FACTOR),
                        convertDpToPixel(41 * UPSCALE_FACTOR),
                        paint
                );

                //eq
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                loadSkinBitmap(0, 61, 24, 12, skin, "shufrep")),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(219 * UPSCALE_FACTOR),
                        convertDpToPixel(58 * UPSCALE_FACTOR),
                        paint
                );

                //pl
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                loadSkinBitmap(23, 61, 24, 12, skin,"shufrep")),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(243 * UPSCALE_FACTOR),
                        convertDpToPixel(58 * UPSCALE_FACTOR),
                        paint
                );

                //vol
                canvas.drawBitmap(
                        upscaleBitmap(Bitmap.createScaledBitmap(
                                loadSkinBitmap(0, 0, 67.6f, 12.3f, skin , "volume"),
                                convertDpToPixel(69), convertDpToPixel(14), true)),
                        convertDpToPixel(106.5f * UPSCALE_FACTOR),
                        convertDpToPixel(56.5f * UPSCALE_FACTOR),
                        paint
                );

                //balance
                canvas.drawBitmap(
                        upscaleBitmap(Bitmap.createScaledBitmap(
                                loadSkinBitmap(9.3f, 0, 37.5f, 13.6f, skin,"balance"),
                                convertDpToPixel(40), convertDpToPixel(15), true)),
                        convertDpToPixel(176.5f * UPSCALE_FACTOR),
                        convertDpToPixel(56.5f * UPSCALE_FACTOR),
                        paint
                );

//                seekbar
                canvas.drawBitmap(
                        upscaleBitmap(Bitmap.createScaledBitmap(
                                loadSkinBitmap(0f, 0, 249f, 10f, skin, "posbar"),
                                convertDpToPixel(249), convertDpToPixel(10), true)),
                        convertDpToPixel(16f * UPSCALE_FACTOR),
                        convertDpToPixel(73f * UPSCALE_FACTOR),
                        paint
                );

//                big clock
                if (skin.resourceType != Skin.ResourceType.RESOURCE) {
                    canvas.drawBitmap(
                            upscaleBitmap(Bitmap.createScaledBitmap(
                                    loadSkinBitmap(90f, 0, 9f, 13f, skin, "numbers"),
                                    convertDpToPixel(10), convertDpToPixel(14), true)),
                            convertDpToPixel(59f * UPSCALE_FACTOR),
                            convertDpToPixel(26f * UPSCALE_FACTOR),
                            paint
                    );

                    canvas.drawBitmap(
                            upscaleBitmap(Bitmap.createScaledBitmap(
                                    loadSkinBitmap(90f, 0, 9f, 13f, skin, "numbers"),
                                    convertDpToPixel(10), convertDpToPixel(14), true)),
                            convertDpToPixel(77 * UPSCALE_FACTOR),
                            convertDpToPixel(26f * UPSCALE_FACTOR),
                            paint
                    );

                    canvas.drawBitmap(
                            upscaleBitmap(Bitmap.createScaledBitmap(
                                    loadSkinBitmap(90f, 0, 9f, 13f, skin, "numbers"),
                                    convertDpToPixel(10), convertDpToPixel(14), true)),
                            convertDpToPixel(89 * UPSCALE_FACTOR),
                            convertDpToPixel(26f * UPSCALE_FACTOR),
                            paint
                    );

                    canvas.drawBitmap(
                            upscaleBitmap(Bitmap.createScaledBitmap(
                                    loadSkinBitmap(90f, 0, 9f, 13f, skin, "numbers"),
                                    convertDpToPixel(10), convertDpToPixel(14), true)),
                            convertDpToPixel(47f * UPSCALE_FACTOR),
                            convertDpToPixel(26f * UPSCALE_FACTOR),
                            paint
                    );

                }

            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.OPAQUE;
            }
        };

        SeekBar volume = mainActivity.findViewById(R.id.volume_bar);
        volume.setThumb(new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                float left = (float) ((volume.getProgress() / 1000.0) * (convertDpToPixel(68) - 14));
                Log.d(TAG, String.format("set thumb: [%d -> %f]", volume.getProgress(), left));
                setBounds(0, 0, 14,14);
                canvas.drawBitmap(
                        upscaleBitmap(Bitmap.createScaledBitmap(
                                loadSkinBitmap(0, 422, 14, 11, skin, "volume"),
                                convertDpToPixel(14), convertDpToPixel(11), true)),
                        left,
                        0,
                        paint
                );
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        });

        SeekBar balance = mainActivity.findViewById(R.id.balance_bar);
        balance.setThumb(new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                float left = (float) ((balance.getProgress() / 1000.0) * (convertDpToPixel(68) - 14));
                Log.d(TAG, String.format("set thumb: [%d -> %f]", balance.getProgress(), left));
                setBounds(0, 0, 14,14);
                canvas.drawBitmap(
                        upscaleBitmap(Bitmap.createScaledBitmap(
                                loadSkinBitmap(0, 422, 14, 11, skin, "balance"),
                                convertDpToPixel(14), convertDpToPixel(11), true)),
                        left,
                        0,
                        paint
                );
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        });

        SeekBar seekBar = mainActivity.findViewById(R.id.seek_bar);
        seekBar.setThumb(new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                float left = (float) ((seekBar.getProgress() / 100.0) * (convertDpToPixel(249) - 29));
                Log.d(TAG, String.format("set thumb: [%d -> %f]", seekBar.getProgress(), left));
                setBounds(0, 0, 30,10);
                canvas.drawBitmap(
                        upscaleBitmap(Bitmap.createScaledBitmap(
                                loadSkinBitmap(248, 0, 29, 10, skin, "posbar"),
                                convertDpToPixel(29), convertDpToPixel(10), true)),
                        left,
                        0,
                        paint
                );
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        });

        prev = mainActivity.findViewById(R.id.prev);
        play = mainActivity.findViewById(R.id.play);
        pause = mainActivity.findViewById(R.id.pause);
        next = mainActivity.findViewById(R.id.next);
        stop = mainActivity.findViewById(R.id.stop);
        eject = mainActivity.findViewById(R.id.eject);
        shuffle = mainActivity.findViewById(R.id.shuffle);
        repeat = mainActivity.findViewById(R.id.repeat);
        about = mainActivity.findViewById(R.id.about);

        Button buttons [] = {prev, next, stop, eject, play, pause, shuffle, repeat, about} ;
        /*
        for (Button b: buttons) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setMessage(v.toString())
                            .setPositiveButton("Ok !", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // FIRE ZE MISSILES!
                                }
                            }).show();
                }
            });
        }

         */
        mainWindow.setBackground(drawable);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setupEqualizer () {
        LinearLayout equalizer = mainActivity.findViewById(R.id.equalizer);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel((int) (displayMetrics.widthPixels / displayMetrics.scaledDensity  * 116f/275f))) ;

        Bitmap mBitmap = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.eqmain),
                mBitmap1 = Bitmap.createBitmap(mBitmap, 0, 0, convertDpToPixel(275), convertDpToPixel(116));
//        equalizer.setBackground(new BitmapDrawable(mainActivity.getResources(), mBitmap1));
        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                this.setBounds(0, 0, convertDpToPixel(275), convertDpToPixel(116));
                Paint paint = new Paint();
                // Because we have to change the drawable we apply to the layout, we have to draw the eq main
                canvas.drawBitmap(
                        upscaleBitmap(loadSkinBitmap(0, 0, 275, 116, skin,"eqmain")),
                        0,
                        0,
                        paint
                );
                // title bar
                canvas.drawBitmap(
                        upscaleBitmap(//getBitmap(0, 134f, 275, 13.5f, R.drawable.eqmain)),
                                Bitmap.createScaledBitmap(
                                        loadSkinBitmap(0, 134f, 275, 13.5f, skin, "eqmain"),
                                        convertDpToPixel(275), convertDpToPixel(15), true
                                )
                        ),
                        0,
                        0,
                        paint
                );
                // that equalizer waveform thingy
                canvas.drawBitmap(
                        upscaleBitmap(
                                Bitmap.createScaledBitmap(
                                        loadSkinBitmap(0, 294f, 113, 19, skin, "eqmain"),
                                        convertDpToPixel(113), convertDpToPixel(19), true
                                )
                        ),
                        convertDpToPixel(86 * UPSCALE_FACTOR),
                        convertDpToPixel(17 * UPSCALE_FACTOR),
                        paint
                );
                // pregain slider
                float slider_coords [] = {20.5f, 77.5f, 95.5f, 113.5f, 131.f, 149.5f, 167.5f, 185.5f, 203.5f, 221.5f, 239.5f} ;
                int x = 13 ;
                for (int i = 0 ; i < slider_coords.length ; i ++) {
                    canvas.drawBitmap(
                            upscaleBitmap(//getBitmap(28.5f, 164.5f, 13.5f, 63f, R.drawable.eqmain)
                                    Bitmap.createScaledBitmap(
                                            loadSkinBitmap(x, 164.5f, 13f, 63, skin, "eqmain"),
                                            convertDpToPixel(16), convertDpToPixel(65), true
                                    )
                            ),
                            convertDpToPixel(slider_coords[i] * UPSCALE_FACTOR),
                            convertDpToPixel(37.5f * UPSCALE_FACTOR),
                            paint
                    );

                    x += 15;
                }


            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.OPAQUE;
            }
        } ;
        Log.d(TAG, "setupEqualizer: got this far");

        equalizer.setBackground(drawable);
        if (on == null) {
            equalizer.setLayoutParams(layoutParams);
            on = new WinampButton(
                    context,
                    mainActivity,
                    "eqmain",
                    10, 119, 24, 12
            );

            auto = new WinampButton(
                    context,
                    mainActivity,
                    "eqmain",
                    35, 119, 33, 12
            );
            preset = new WinampButton(
                    context,
                    mainActivity,
                    "eqmain",
                    224, 164, 44, 12
            );


            LinearLayout buttons = mainActivity.findViewById(R.id.equalizer_buttons);
            buttons.removeAllViews();
            LinearLayout.LayoutParams layoutParamsButtons = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParamsButtons.setMargins(convertDpToPixel(14 * UPSCALE_FACTOR), convertDpToPixel(18 * UPSCALE_FACTOR), convertDpToPixel(4 * UPSCALE_FACTOR), 0);
            buttons.setLayoutParams(layoutParamsButtons);
            buttons.addView(on);
            buttons.addView(auto);
            buttons.addView(preset);
            LinearLayout.LayoutParams layoutParamsPreset = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParamsPreset.setMargins((int) (144 * UPSCALE_FACTOR * density), 0, 0, 0);

            preset.setLayoutParams(layoutParamsPreset);

            LinearLayout sliders = mainActivity.findViewById(R.id.equalizer_sliders);
            LinearLayout.LayoutParams layoutParamsSliders = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParamsSliders.setMargins(convertDpToPixel(21 * UPSCALE_FACTOR), convertDpToPixel(7 * UPSCALE_FACTOR), convertDpToPixel(4 * UPSCALE_FACTOR), convertDpToPixel(14 * UPSCALE_FACTOR));
        }

        Log.d(TAG, "setupEqualizer: buttons done");
//        sliders.setLayoutParams(layoutParamsSliders);

        SeekBar preamp = mainActivity.findViewById(R.id.equalizer_preamp);
//        preamp.setPadding(0, 0, convertDpToPixel(14 * UPSCALE_FACTOR), 0);
//        preamp.setMinimumWidth(mainActivity.convertDpToPixel(64 * mainActivity.UPSCALE_FACTOR));
//        preamp.setMinimumHeight(mainActivity.convertDpToPixel(14 * mainActivity.UPSCALE_FACTOR));
//        preamp.setMinHeight(convertDpToPixel(14));
//        preamp.setMinimumHeight(convertDpToPixel(14));

//        LinearLayout.LayoutParams layoutParamsPreamp = new LinearLayout.LayoutParams(convertDpToPixel(64 * UPSCALE_FACTOR), ViewGroup.LayoutParams.WRAP_CONTENT);
//        preamp.setLayoutParams(layoutParamsPreamp);
//        preamp.setRotation(270);
//        skinEqualizerSlider(preamp);
//        preamp.setBackgroundColor(mainActivity.getResources().getColor(R.color.Aztec_Purple));
//        AppCompatSeekBar gain = new WinampSlider(context, mainActivity, R.drawable.eqmain, 13, 164, 14, 64);
//        sliders.addView(gain);
        Log.d(TAG, "setupEqualizer: giong to do seekbars");
        int [] seekBars = {
           R.id.equalizer_preamp,
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
        } ;

        for (int seekBar: seekBars) {
            SeekBar eq_preamp = mainActivity.findViewById(seekBar);
//            eq_preamp.setMinimumHeight(convertDpToPixel(15));
//            eq_preamp.setMinimumWidth(convertDpToPixel(15));
            eq_preamp.setThumb(new Drawable() {
                @Override
                public void draw(@NonNull Canvas canvas) {
                    float left = (float) ((eq_preamp.getProgress() / 100.0) * (convertDpToPixel(64) - 12));
                    Log.d(TAG, String.format("set thumb: [%d -> %f]", volume.getProgress(), left));
                    setBounds(0, 0, convertDpToPixel(12), convertDpToPixel(12));
                    canvas.drawBitmap(
                            upscaleBitmap(Bitmap.createScaledBitmap(
                                    loadSkinBitmap(0.5f, 164.5f, 10.5f, 11.5f, skin, "eqmain"),
                                    convertDpToPixel(12), convertDpToPixel(11), true)),
                            left,
                            0,
                            paint
                    );
                }

                @Override
                public void setAlpha(int alpha) {

                }

                @Override
                public void setColorFilter(@Nullable ColorFilter colorFilter) {

                }

                @Override
                public int getOpacity() {
                    return 0;
                }
            });
        }

        Log.d(TAG, "setupEqualizer: seekbars done");

    }

    public int convertDpToPixel(float dp){
        double px = dp * (displayMetrics.densityDpi / 160D);
        return (int) Math.round(px);
    }

    public void setupPlaylist () {
        LinearLayout linearLayout = mainActivity.findViewById(R.id.playlist);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel((height / displayMetrics.scaledDensity) - (116+116)));
        //TODO: The following works but is unnecessary. But since I've already written it and it works perfectly,
        //      I've kept it in.
//        if (on ==null)
//            linearLayout.setLayoutParams(layoutParams);

        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                this.setBounds(0, 0, width, convertDpToPixel((height / displayMetrics.scaledDensity) - (116+116)));
                int canvasHeight = convertDpToPixel((height / displayMetrics.scaledDensity) - (116+116)) ;
                Paint paint = new Paint();
                Bitmap topLeft = loadSkinBitmap(0,0,24,20, skin, "pledit");

                /*  So now the problem is that the playlist is drawn with a white
                    background in normal mode, and black background in dark mode.

                    So we try to paint the background with a portion from the
                    playlist bitmap
                 */
                if (skin.resourceType == Skin.ResourceType.FILE) {
                    Config config = new Config();
                    config.load(skin.defaultSkinDir + "pledit.txt");
                    String bg = config.get("NormalBG");
                    canvas.drawColor(Color.parseColor(bg));
                } else {
                    for (int j = 1 ; j < 19 ; j ++) {
                        for (int t = 1; t < 21; t++) {
                            canvas.drawBitmap(
                                    upscaleBitmap(loadSkinBitmap(208, 10, 17f, 18, skin, "pledit")),
                                    convertDpToPixel(17 * t),
                                    convertDpToPixel(25 * j),
                                    paint
                            );
                        }
                    }

                }

                canvas.drawBitmap(upscaleBitmap(topLeft), 0, 0, paint);
                Bitmap topLeftEx = upscaleBitmap(loadSkinBitmap(127.5f,0,24f,20, skin, "pledit"));
                for (int i = 1 ; i < 6 ; i ++) {
                    canvas.drawBitmap(topLeftEx, convertDpToPixel(24) * i, 0, paint);
                }

                for (int i = 10 ; i < 16 ; i ++) {
                    canvas.drawBitmap(topLeftEx, convertDpToPixel(24) * i, 0, paint);
                }

                // top right
                canvas.drawBitmap(
                        upscaleBitmap(loadSkinBitmap(153.5f, 0, 24f, 20, skin, "pledit")),
//                        convertDpToPixel(24 * 15f),
                        displayMetrics.widthPixels - convertDpToPixel(24 * UPSCALE_FACTOR),
                        0,
                        paint
                );

                Bitmap left = upscaleBitmap(loadSkinBitmap(0,42.5f,12,29, skin, "pledit"));
                canvas.drawBitmap(left, 0, convertDpToPixel(20.5f), paint);
                for (int i = 2 ; i < 30 ; i ++) {
                    canvas.drawBitmap(left, 0, i * convertDpToPixel(20 * UPSCALE_FACTOR), paint);
//                    Log.d(TAG, "draw: " + i * convertDpToPixel(20) + ", " + convertDpToPixel(272) +", " + displayMetrics.heightPixels);
//                    if ((i * convertDpToPixel(20) + convertDpToPixel(350)) > ((float)displayMetrics.heightPixels ))
                    if (i * convertDpToPixel(29 * UPSCALE_FACTOR) > displayMetrics.heightPixels - convertDpToPixel(212))
                        break ;
                }

                Bitmap right = upscaleBitmap(loadSkinBitmap(31f,42.5f,20f,29, skin, "pledit"));
//                canvas.drawBitmap(right, displayMetrics.widthPixels - convertDpToPixel(21f), convertDpToPixel(20.5f), paint);
                canvas.drawBitmap(right, displayMetrics.widthPixels - convertDpToPixel(20 * UPSCALE_FACTOR), convertDpToPixel(20 * UPSCALE_FACTOR), paint);
                for (int i = 2 ; i < 30 ; i ++) {
//                    canvas.drawBitmap(right, displayMetrics.widthPixels - convertDpToPixel(21f), i * convertDpToPixel(20), paint);
//                    Log.d(TAG, "draw: " + i * convertDpToPixel(20) + ", " + convertDpToPixel(272) +", " + displayMetrics.heightPixels);
                    canvas.drawBitmap(right, displayMetrics.widthPixels - convertDpToPixel(20 * UPSCALE_FACTOR), i *convertDpToPixel(20 * UPSCALE_FACTOR), paint);
//                    if ((i * convertDpToPixel(20) + convertDpToPixel(350)) > ((float)displayMetrics.heightPixels ))
                    if (i * convertDpToPixel(29 * UPSCALE_FACTOR) > displayMetrics.heightPixels - convertDpToPixel(212))
                        break ;
                }

                // top winamp bar
                canvas.drawBitmap(
                        upscaleBitmap(loadSkinBitmap(26.5f, 0, 99f, 20, skin, "pledit")),
                        convertDpToPixel(24 * 6f),
                        0,
                        paint
                );

                // bottom bar
                for (int i = 0 ; i < 24 * 5 ; i = i + 24) {
                    canvas.drawBitmap(
                            upscaleBitmap(loadSkinBitmap(179.5f, 0.5f, 24, 37.5f, skin, "pledit")),
                            convertDpToPixel(i + 124f),
                            displayMetrics.heightPixels - convertDpToPixel(212)  - convertDpToPixel(38 * displayMetrics.scaledDensity * UPSCALE_FACTOR),
                            paint
                    );
                }

                // bottom left
                canvas.drawBitmap(
                        upscaleBitmap(loadSkinBitmap(0, 72.5f, 124.5f, 37.5f, skin, "pledit")),
                        0,
//                        displayMetrics.heightPixels - (convertDpToPixel(232) + convertDpToPixel(38 * UPSCALE_FACTOR)),
                        displayMetrics.heightPixels - convertDpToPixel(212)  - convertDpToPixel(38 * displayMetrics.scaledDensity * UPSCALE_FACTOR),
                        paint
                );
//                Log.d(TAG, "draw : %d x %d".format ("%d %d", canvasHeight, convertDpToPixel(40 * displayMetrics.scaledDensity))) ;

                //bottom right
                canvas.drawBitmap(
                        upscaleBitmap(loadSkinBitmap(126.5f, 72.5f, 149.5f, 37.5f, skin, "pledit")),
                        displayMetrics.widthPixels - convertDpToPixel(149.5f * UPSCALE_FACTOR),
                        displayMetrics.heightPixels - convertDpToPixel(212) - convertDpToPixel(38f * displayMetrics.scaledDensity * UPSCALE_FACTOR),
                        paint
                );


            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.OPAQUE;
            }
        } ;

        linearLayout.setBackground(drawable);

    }

    void setupPlaylistManagement () {
        String [] songs = {
//                "U2 - Vertigo",
//                "Oasis - Live Forever"
        } ;


        final List< String > ListElementsArrayList = new ArrayList< String >
                (Arrays.asList(songs));

        final ArrayAdapter< String > adapter = new ArrayAdapter < String >
                (context,
//                        R.layout.listview,
                        android.R.layout.simple_list_item_activated_1,
                        ListElementsArrayList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.WHITE);

                return view;
            }
        };

        ListView listView = (ListView) mainActivity.findViewById(R.id.playlist_view);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        playlistAdapter = adapter ;
//        listView.setBackgroundColor(mainActivity.getResources().getColor(R.color.Winamp3));

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setSelection(position);
                return false;
            }
        });

        playlistView = listView ;
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alert (playlistUri.get(parent.getAdapter().getItem(position).toString()).toString());
            }
        });

         */

//        ListElementsArrayList.add("Coldplay - Yellow");
        playlistElements = ListElementsArrayList ;
        playlistUri = new HashMap<String, String>();
//        playlistUri.put("U2 - Vertigo", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");
//        playlistUri.put("Oasis - Live Forever", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");
//        playlistUri.put("Coldplay - Yellow", "https://music.shaji.in/media/No%20Destination%20(Preview)/savera.mp3");


    }
    public void playlistAdd (String track, String uri) {
        playlistElements.add(track);
        playlistUri.put (track, uri);
        playlistAdapter.notifyDataSetChanged();
    }

    public void playlistShuffle () {
        Collections.shuffle(playlistElements);
        playlistAdapter.notifyDataSetChanged();
    }

    public void playlistReverse () {
        Collections.reverse(playlistElements);
        playlistAdapter.notifyDataSetChanged();
    }

    public Bitmap getBitmap (float x, float y, float width, float height, int resource) {
        Log.d(TAG, String.format("getBitmap:  %f , %f => %f x %f", x,y,width,height) );
        Bitmap mBitmap = BitmapFactory.decodeResource(mainActivity.getResources(), resource) ;
        Log.d(TAG, String.format("getBitmap: image dimensions: %d x %d", mBitmap.getWidth(), mBitmap.getHeight()));
        if (x + width > mBitmap.getWidth())
            x = mBitmap.getWidth() - width ;
        try {
            mBitmap = Bitmap.createBitmap(mBitmap, convertDpToPixel(x), convertDpToPixel(y), convertDpToPixel(width), convertDpToPixel(height));
        } catch (IllegalArgumentException e) {
            Log.e(TAG, String.format("getBitmap: failed function call %d %d %d %d",
                    convertDpToPixel(x), convertDpToPixel(y), convertDpToPixel(width), convertDpToPixel(height)));
            return mBitmap;
        }

        return mBitmap ;
    }

    public Bitmap loadSkinBitmap (float x, float y, float width, float height, Skin skin, String resource) {
        Bitmap mBitmap = null ;
        Log.d(TAG, String.format("loadSkinBitmap: Loading %s", resource));
        if (skin.resourceType == Skin.ResourceType.RESOURCE) {
            return getBitmap(x,y,width,height, (Integer) skin.bitmaps.get(resource));
//            mBitmap = BitmapFactory.decodeResource(mainActivity.getResources(), (Integer) skin.bitmaps.get(resource));
//            mBitmap = getBitmap(x, y, width,height, (Integer) skin.bitmaps.get(resource));
//            mBitmap = Bitmap.createBitmap(mBitmap, convertDpToPixel(x), convertDpToPixel(y), convertDpToPixel(width), convertDpToPixel(height));
//            return mBitmap;
        } else {
            Log.d(TAG, "loadSkinBitmap: Tring to load " + skin.bitmaps.get(resource));
            File file = new File((String) skin.bitmaps.get(resource) + ".bmp") ;
//            if (! file.exists()) {
//                if (!checkPermissionForReadExtertalStorage()) {
//                    Log.e(TAG, "loadSkinBitmap: No permission for storage, trying to ask" );
//                    try {
//                        requestPermissionForReadExtertalStorage();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                skin.renameSkinFiles(skin.skinDir);
//            }
            if (! file.exists())
                file = new File((String) skin.bitmaps.get(resource) + ".png") ;

            mBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }

        Log.e(TAG, String.format ("loadSkinBitmap: (%s) %d x %d [%f %f %f %f]", resource, mBitmap.getWidth(), mBitmap.getHeight(), width, height, x, y));
        Log.d(TAG, String.format ("loadSkinBitmap: Converting to: [%d %d] %d x %d", convertDpToPixel(x), convertDpToPixel(y), convertDpToPixel(width), convertDpToPixel(height)));
//        if (width == mBitmap.getWidth() && height == mBitmap.getHeight())
//            return mBitmap ;
//        else
        {
            if (mBitmap.getWidth() < x + width)
                mBitmap = Bitmap.createScaledBitmap(mBitmap,
                        convertDpToPixel(width + x),
                        convertDpToPixel(height),
                        true
                );

            if (mBitmap.getHeight() < y + height)
                mBitmap = Bitmap.createScaledBitmap(mBitmap,
                        convertDpToPixel(width + x),
                        convertDpToPixel(height + y),
                        true
                );
            mBitmap = Bitmap.createBitmap(mBitmap, (int) x, (int) y, (int) width, (int) height);
            mBitmap = Bitmap.createScaledBitmap(mBitmap,
                    convertDpToPixel(width),
                    convertDpToPixel(height),
                    true
            );
        }
        return mBitmap ;
    }


    public Bitmap upscaleBitmap (Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap,
                (int)((float) bitmap.getWidth() * UPSCALE_FACTOR),
                (int)((float) bitmap.getHeight() * UPSCALE_FACTOR),
                true
        );
    }

    public Bitmap upscaleBitmapEx (Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap,
                (int)((float) bitmap.getWidth() * UPSCALE_FACTOR) + (int) density,
                (int)((float) bitmap.getHeight() * UPSCALE_FACTOR) + (int) density,
                true
        );
    }

    public void skinButton (Button button, int resource, int x, int y, int width, int height, int left, int top) {
        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint() ;
                setBounds(0, 0, width, height);
                canvas.drawBitmap(
                        upscaleBitmap(
                                getBitmap(x, y, width, height, resource)),
                        convertDpToPixel(left),
                        convertDpToPixel(top),
                        paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        };
    }

    public void skinEqualizerSlider (SeekBar seekBar) {
        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                this.setBounds(convertDpToPixel(0), convertDpToPixel(0),
                        convertDpToPixel(14), convertDpToPixel(64));
                Bitmap bitmap = getBitmap(13, 164, 14, 64, R.drawable.eqmain);
                bitmap = upscaleBitmapEx(bitmap);
                bitmap = rotateBitmap(bitmap, 90);
                canvas.drawBitmap(
                        bitmap,
                        0, 0,
                        paint
                );
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.OPAQUE;
            }
        }, thumb = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                this.setBounds(convertDpToPixel(0), convertDpToPixel(11), convertDpToPixel(11), convertDpToPixel(11));
                Bitmap bitmapThumb = getBitmap(0, 164, 11, 11, R.drawable.eqmain);
                bitmapThumb = upscaleBitmapEx(bitmapThumb);
                bitmapThumb = rotateBitmap(bitmapThumb, 90);
                canvas.drawBitmap(
                        bitmapThumb,
                        UPSCALE_FACTOR * convertDpToPixel(1), 0,
                        paint
                );
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.OPAQUE;
            }
        } ;

        seekBar.setProgressDrawable(drawable);
//        seekBar.setThumb(thumb);

    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void alert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                }).show();

    }

    void paintShuffle (boolean active) {
        setupMainWindow(skin, active, repeatState);
    }

    void paintRepeat (boolean active) {
        setupMainWindow(skin, shuffleState, active);
    }

    public void playlistClear () {
        playlistElements.clear();
        playlistUri.clear();
        playlistAdapter.notifyDataSetChanged();

    }

    public void playlistRemove (int position) {
        if (position >= playlistUri.size() || position > playlistElements.size())
            return;
        Log.d(TAG, "playlistRemove: " + playlistUri.toString());
        Log.d(TAG, "playlistRemove: " + playlistElements.toString());
        Log.d(TAG, "playlistRemove: " + playlistElements.get (position).toString());
        playlistUri.remove (playlistElements.get (position));
        playlistElements.remove(position);
        playlistAdapter.notifyDataSetChanged();

    }

    public class Config {

        private Properties configuration;
        private String configurationFile = "config.ini";

        public Config() {
            configuration = new Properties();
        }

        public boolean load(String filename) {
            boolean retval = false;

            try {
                configuration.load(new FileInputStream(filename));
                retval = true;
            } catch (IOException e) {
                System.out.println("Configuration error: " + e.getMessage());
            }

            return retval;
        }

        public boolean store() {
            boolean retval = false;

            try {
                configuration.store(new FileOutputStream(this.configurationFile), null);
                retval = true;
            } catch (IOException e) {
                System.out.println("Configuration error: " + e.getMessage());
            }

            return retval;
        }

        public void set(String key, String value) {
            configuration.setProperty(key, value);
        }

        public String get(String key) {
            return configuration.getProperty(key);
        }
    }


    public void SkinBrowserDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        WebView webView = new WebView(mainActivity) {
            @Override
            public boolean onCheckIsTextEditor() {
                return true;
            }

        } ;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://skins.webamp.org");
        webView.setMinimumHeight(1000);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 2.0f ;
        linearLayout.setLayoutParams(layoutParams);
        webView.setLayoutParams(layoutParams);
        linearLayout.addView(webView);
        linearLayout.setMinimumHeight(1000);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);

        LinearLayout box = new LinearLayout(context);
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        TextView textView = new TextView(context);
        textView.setText("Loading ...");
        box.addView(progressBar);
        Button button = new Button(context);
        button.setText("Download Skin");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = webView.getUrl();
                Log.d(TAG, "onClick: " + url);
                try {
                    String skinUrl = "https://cdn.webampskins.org/skins/" + url.toString().split("https://skins.webamp.org/skin/")[1].split("/")[0] + ".wsz";
                    Log.d(TAG, "shouldOverrideUrlLoading: Selected skin " + skinUrl);
                    box.setVisibility(View.VISIBLE);
                    textView.setText("Downloading skin ...");
                    skin.downloadSkin(skinUrl, self, box);
                } catch (ArrayIndexOutOfBoundsException e) {
                    alert("Select a skin to download.");
                    return;
                }
            }
        });

        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(10,10,10,10);
        progressBar.setLayoutParams(layoutParams1);
        textView.setLayoutParams(layoutParams1);
        box.setLayoutParams(layoutParams1);
        box.addView(textView);
        linearLayout.addView(box);
        linearLayout.addView(button);

        box.setVisibility(View.INVISIBLE);

        linearLayout.setMinimumWidth(context.getResources().getDisplayMetrics().widthPixels);
        linearLayout.setMinimumHeight(context.getResources().getDisplayMetrics().heightPixels);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.d(TAG, "onLoadResource: " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d(TAG, "shouldOverrideUrlLoading: " + request.toString());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "onPageStarted: " + url);
            }



            private boolean shouldOverrideUrlLoading(final WebView view, final Uri request) {
                if(request.getScheme().equals("blob")) {
                    // do your special handling for blob urls here
                    String skinUrl = "https://cdn.webampskins.org/skins/" + request.toString().split ("blob:https://skins.webamp.org")[1] + ".wsz";
                    Log.d(TAG, "shouldOverrideUrlLoading: Selected skin " + skinUrl);
                    box.setVisibility(View.VISIBLE);
                    textView.setText("Downloading skin ...");
                    skin.downloadSkin (skinUrl, self, box);
                }
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading: " + url);
                String skinUrl = url.split ("\\?skinUrl=")[1];
                Log.d(TAG, "shouldOverrideUrlLoading: Selected skin " + skinUrl);
                box.setVisibility(View.VISIBLE);
                textView.setText("Downloading skin ...");
                skin.downloadSkin (skinUrl, self, box);
//                skin.renameSkinFiles(skin.defaultSkinDir);
//                textView.setText("Applying skin ...");
//                applySkin(box);
//                textView.setText("Loading ...");
//                box.setVisibility(View.INVISIBLE);
                return true;
            }
        });

        webView.addJavascriptInterface(new Object() {
            public void performClick()
            {
                // Deal with a click on the OK button
                String url = webView.getUrl();
                String skinUrl = url.split ("\\?skinUrl=")[1];
                Log.d(TAG, "shouldOverrideUrlLoading: Selected skin " + skinUrl);
                box.setVisibility(View.VISIBLE);
                textView.setText("Downloading skin ...");
                skin.downloadSkin (skinUrl, self, box);

            }

        }, "ok");

        AlertDialog alertDialog = builder
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }

                })
                .setView(linearLayout)
                .create();

        alertDialog.setTitle("Select Skin");
//        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.getWindow().setDimAmount(0f);
        alertDialog.show();

    }

    void applySkin (View view) {
        Log.d(TAG, "applySkin: starting");
        skin = new Skin(context,false);
        Log.d(TAG, "applySkin: mainwindow");
        setupMainWindow(skin, false, false);
        Log.d(TAG, "applySkin: playlist");
        setupPlaylist();
        Log.d(TAG, "applySkin: equalizer");
        setupEqualizer();

        Log.d(TAG, "applySkin: complete");
        view.setVisibility(View.INVISIBLE);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, "Skin Applied", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}
