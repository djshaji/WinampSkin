package com.shajikhan.winampskin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WinampSkin {
    Button prev, next, play, pause, stop, eject, shuffle, repeat, about ;
    SeekBar seek ;
    MainActivity mainActivity ;
    Context context ;
    DisplayMetrics displayMetrics ;
    String TAG = "WinampSkin";
    // FIXME: 7/24/21 Determine the following automagically
    float UPSCALE_FACTOR = 1.43f ;
    float density ;

    List <String> playlistElements ;
    HashMap playlistUri ;
    TextView bigClock, trackTitle ;

    ListView playlistView ;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    WinampSkin (Context context_, MainActivity mainActivity_) {
        mainActivity = mainActivity_ ;
        context = context_ ;
        displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.scaledDensity;

//        setup();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setup () {
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Log.d(TAG, displayMetrics.toString());
        LinearLayout window = mainActivity.findViewById(R.id.window);

        LinearLayout mainWindow = mainActivity.findViewById(R.id.main_window);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel((int) (width / displayMetrics.scaledDensity  * 116f/275f))) ;
//        Log.d(TAG, "setup: " + width / displayMetrics.scaledDensity  * 116f/275f);
        mainWindow.setLayoutParams(layoutParams);

        setupMainWindow();
        setupEqualizer();
        setupPlaylist();
    }

    public void setupMainWindow () {
        LinearLayout mainWindow = mainActivity.findViewById(R.id.main_window);
        seek = mainWindow.findViewById(R.id.seek_bar);
        bigClock = mainWindow.findViewById(R.id.big_clock);
        trackTitle = mainWindow.findViewById(R.id.track_title);

        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                setBounds(0, 0, convertDpToPixel(275), convertDpToPixel(116));
                Paint paint = new Paint();
                canvas.drawBitmap(
                        upscaleBitmap(getBitmap(0, 0, 275, 116, R.drawable.main)),
                        0,
                        0,
                        paint
                );
                // titlebar
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                getBitmap(27, 57, 275, 13, R.drawable.titlebar)),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        0,
                        convertDpToPixel(3),
                        paint
                );
                // cbuttons
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                getBitmap(0, 0, 114, 18, R.drawable.cbuttons)),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(15 * UPSCALE_FACTOR) + 3,
                        convertDpToPixel(87 * UPSCALE_FACTOR) + 3,
                        paint
                );
                //eject
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                getBitmap(115, 0, 21, 16, R.drawable.cbuttons)),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(136 * UPSCALE_FACTOR) + 3,
                        convertDpToPixel(88 * UPSCALE_FACTOR) + 3,
                        paint
                );
                //shuffle
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                getBitmap(28, 0, 45, 15, R.drawable.shufrep)),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(164 * UPSCALE_FACTOR),
                        convertDpToPixel(89 * UPSCALE_FACTOR),
                        paint
                );
                //repeat
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                getBitmap(0, 0, 29, 15, R.drawable.shufrep)),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(209 * UPSCALE_FACTOR),
                        convertDpToPixel(89 * UPSCALE_FACTOR),
                        paint
                );

                // stereo
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                getBitmap(0, 0, 29, 12, R.drawable.monoster)),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(239 * UPSCALE_FACTOR),
                        convertDpToPixel(41 * UPSCALE_FACTOR),
                        paint
                );

                // mono
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                getBitmap(29, 12, 29, 12, R.drawable.monoster)),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(210 * UPSCALE_FACTOR),
                        convertDpToPixel(41 * UPSCALE_FACTOR),
                        paint
                );

                //eq
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                getBitmap(0, 61, 24, 12, R.drawable.shufrep)),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(219 * UPSCALE_FACTOR),
                        convertDpToPixel(58 * UPSCALE_FACTOR),
                        paint
                );

                //pl
                canvas.drawBitmap(
                        upscaleBitmap(//Bitmap.createScaledBitmap(
                                getBitmap(23, 61, 24, 12, R.drawable.shufrep)),
                        //convertDpToPixel(275), convertDpToPixel(16), true)),
                        convertDpToPixel(243 * UPSCALE_FACTOR),
                        convertDpToPixel(58 * UPSCALE_FACTOR),
                        paint
                );

                //vol
                canvas.drawBitmap(
                        upscaleBitmap(Bitmap.createScaledBitmap(
                                getBitmap(0, 0, 67.6f, 12.3f, R.drawable.volume),
                                convertDpToPixel(69), convertDpToPixel(14), true)),
                        convertDpToPixel(106.5f * UPSCALE_FACTOR),
                        convertDpToPixel(56.5f * UPSCALE_FACTOR),
                        paint
                );

                //balance
                canvas.drawBitmap(
                        upscaleBitmap(Bitmap.createScaledBitmap(
                                getBitmap(9.3f, 0, 37.5f, 13.6f, R.drawable.balance),
                                convertDpToPixel(40), convertDpToPixel(15), true)),
                        convertDpToPixel(176.5f * UPSCALE_FACTOR),
                        convertDpToPixel(56.5f * UPSCALE_FACTOR),
                        paint
                );

                SeekBar volume = mainActivity.findViewById(R.id.volume_bar);
//                volume.setThumb(new Drawable() {
//                    @Override
//                    public void draw(@NonNull Canvas canvas) {
//                        canvas.drawBitmap(
//                                upscaleBitmap(Bitmap.createScaledBitmap(
//                                        getBitmap(0, 422, 14, 11, R.drawable.volume),
//                                        convertDpToPixel(14), convertDpToPixel(11), true)),
//                                0,
//                                0,
//                                paint
//                        );
//                    }
//
//                    @Override
//                    public void setAlpha(int alpha) {
//
//                    }
//
//                    @Override
//                    public void setColorFilter(@Nullable ColorFilter colorFilter) {
//
//                    }
//
//                    @Override
//                    public int getOpacity() {
//                        return 0;
//                    }
//                });

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

        equalizer.setLayoutParams(layoutParams);
        Bitmap mBitmap = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.eqmain),
                mBitmap1 = Bitmap.createBitmap(mBitmap, 0, 0, convertDpToPixel(275), convertDpToPixel(116));
        equalizer.setBackground(new BitmapDrawable(mainActivity.getResources(), mBitmap1));
        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                this.setBounds(0, 0, convertDpToPixel(275), convertDpToPixel(116));
                Paint paint = new Paint();
                // Because we have to change the drawable we apply to the layout, we have to draw the eq main
                canvas.drawBitmap(
                        upscaleBitmap(getBitmap(0, 0, 275, 116, R.drawable.eqmain)),
                        0,
                        0,
                        paint
                );
                // title bar
                canvas.drawBitmap(
                        upscaleBitmap(//getBitmap(0, 134f, 275, 13.5f, R.drawable.eqmain)),
                                Bitmap.createScaledBitmap(
                                        getBitmap(0, 134f, 275, 13.5f, R.drawable.eqmain),
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
                                        getBitmap(0, 294f, 113, 19, R.drawable.eqmain),
                                        convertDpToPixel(113), convertDpToPixel(19), true
                                )
                        ),
                        convertDpToPixel(86 * UPSCALE_FACTOR),
                        convertDpToPixel(17 * UPSCALE_FACTOR),
                        paint
                );
                // pregain slider
                float slider_coords [] = {20.5f, 77.5f, 95.5f, 113.5f, 131.f, 149.5f, 167.5f, 185.5f, 203.5f, 221.5f, 239.5f} ;
                for (int i = 0 ; i < slider_coords.length ; i ++)
                    canvas.drawBitmap(
                            upscaleBitmap(//getBitmap(28.5f, 164.5f, 13.5f, 63f, R.drawable.eqmain)
                                    Bitmap.createScaledBitmap(
                                            getBitmap(28.8f, 164.5f, 13f, 63, R.drawable.eqmain),
                                            convertDpToPixel(16), convertDpToPixel(65), true
                                    )
                            ),
                            convertDpToPixel(slider_coords [i] * UPSCALE_FACTOR),
                            convertDpToPixel(37.5f * UPSCALE_FACTOR),
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

        equalizer.setBackground(drawable);

        Button on = new WinampButton(
                context,
                mainActivity,
                R.drawable.eqmain,
                10, 119, 24, 12
        ), auto = new WinampButton(
                context,
                mainActivity,
                R.drawable.eqmain,
                35, 119, 33, 12
        ),  preset = new WinampButton(
                context,
                mainActivity,
                R.drawable.eqmain,
                224, 164, 44, 12
        );

        LinearLayout buttons = mainActivity.findViewById(R.id.equalizer_buttons);
        LinearLayout.LayoutParams layoutParamsButtons = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsButtons.setMargins(convertDpToPixel(14 * UPSCALE_FACTOR), convertDpToPixel(18 * UPSCALE_FACTOR), convertDpToPixel(4 * UPSCALE_FACTOR), 0);
        buttons.setLayoutParams(layoutParamsButtons);
        buttons.addView(on);
        buttons.addView(auto);

        LinearLayout.LayoutParams layoutParamsPreset = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsPreset.setMargins((int) (144 * UPSCALE_FACTOR * density), 0, 0, 0);

        preset.setLayoutParams(layoutParamsPreset);
        buttons.addView(preset);

        LinearLayout sliders = mainActivity.findViewById(R.id.equalizer_sliders);
        LinearLayout.LayoutParams layoutParamsSliders = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParamsSliders.setMargins(convertDpToPixel(21 * UPSCALE_FACTOR), convertDpToPixel(7 * UPSCALE_FACTOR), convertDpToPixel(4 * UPSCALE_FACTOR), convertDpToPixel(14 * UPSCALE_FACTOR));
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
        linearLayout.setLayoutParams(layoutParams);

        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                this.setBounds(0, 0, width, convertDpToPixel((height / displayMetrics.scaledDensity) - (116+116)));
                int canvasHeight = convertDpToPixel((height / displayMetrics.scaledDensity) - (116+116)) ;
                Paint paint = new Paint();
                Bitmap topLeft = getBitmap(0,0,24,20, R.drawable.pledit);
                canvas.drawBitmap(upscaleBitmap(topLeft), 0, 0, paint);
                Bitmap topLeftEx = upscaleBitmap(getBitmap(127.5f,0,24f,20, R.drawable.pledit));
                for (int i = 1 ; i < 6 ; i ++) {
                    canvas.drawBitmap(topLeftEx, convertDpToPixel(24) * i, 0, paint);
                }

                for (int i = 10 ; i < 16 ; i ++) {
                    canvas.drawBitmap(topLeftEx, convertDpToPixel(24) * i, 0, paint);
                }

                // top right
                canvas.drawBitmap(
                        upscaleBitmap(getBitmap(153.5f, 0, 24f, 20, R.drawable.pledit)),
//                        convertDpToPixel(24 * 15f),
                        displayMetrics.widthPixels - convertDpToPixel(24 * UPSCALE_FACTOR),
                        0,
                        paint
                );

                Bitmap left = upscaleBitmap(getBitmap(0,42.5f,12,29, R.drawable.pledit));
                canvas.drawBitmap(left, 0, convertDpToPixel(20.5f), paint);
                for (int i = 2 ; i < 30 ; i ++) {
                    canvas.drawBitmap(left, 0, i * convertDpToPixel(20 * UPSCALE_FACTOR), paint);
//                    Log.d(TAG, "draw: " + i * convertDpToPixel(20) + ", " + convertDpToPixel(272) +", " + displayMetrics.heightPixels);
//                    if ((i * convertDpToPixel(20) + convertDpToPixel(350)) > ((float)displayMetrics.heightPixels ))
                    if (i * convertDpToPixel(29 * UPSCALE_FACTOR) > displayMetrics.heightPixels - convertDpToPixel(212))
                        break ;
                }

                Bitmap right = upscaleBitmap(getBitmap(31f,42.5f,20f,29, R.drawable.pledit));
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
                        upscaleBitmap(getBitmap(26.5f, 0, 99f, 20, R.drawable.pledit)),
                        convertDpToPixel(24 * 6f),
                        0,
                        paint
                );

                // bottom bar
                for (int i = 0 ; i < 24 * 5 ; i = i + 24) {
                    canvas.drawBitmap(
                            upscaleBitmap(getBitmap(179.5f, 0.5f, 24, 37.5f, R.drawable.pledit)),
                            convertDpToPixel(i + 124f),
                            displayMetrics.heightPixels - convertDpToPixel(212)  - convertDpToPixel(38 * displayMetrics.scaledDensity * UPSCALE_FACTOR),
                            paint
                    );
                }

                // bottom left
                canvas.drawBitmap(
                        upscaleBitmap(getBitmap(0, 72.5f, 124.5f, 37.5f, R.drawable.pledit)),
                        0,
//                        displayMetrics.heightPixels - (convertDpToPixel(232) + convertDpToPixel(38 * UPSCALE_FACTOR)),
                        displayMetrics.heightPixels - convertDpToPixel(212)  - convertDpToPixel(38 * displayMetrics.scaledDensity * UPSCALE_FACTOR),
                        paint
                );
//                Log.d(TAG, "draw : %d x %d".format ("%d %d", canvasHeight, convertDpToPixel(40 * displayMetrics.scaledDensity))) ;

                //bottom right
                canvas.drawBitmap(
                        upscaleBitmap(getBitmap(126.5f, 72.5f, 149.5f, 37.5f, R.drawable.pledit)),
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

        String [] songs = {
//                "U2 - Vertigo",
//                "Oasis - Live Forever"
        } ;

        linearLayout.setBackground(drawable);

        final List< String > ListElementsArrayList = new ArrayList< String >
                (Arrays.asList(songs));

        final ArrayAdapter< String > adapter = new ArrayAdapter < String >
                (context, R.layout.listview,
                        ListElementsArrayList);

        ListView listView = (ListView) mainActivity.findViewById(R.id.playlist_view);
        listView.setAdapter(adapter);

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

    }

    public Bitmap getBitmap (float x, float y, float width, float height, int resource) {
        Bitmap mBitmap = BitmapFactory.decodeResource(mainActivity.getResources(), resource) ;
        mBitmap = Bitmap.createBitmap(mBitmap, convertDpToPixel(x), convertDpToPixel(y), convertDpToPixel(width), convertDpToPixel(height));

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

}
