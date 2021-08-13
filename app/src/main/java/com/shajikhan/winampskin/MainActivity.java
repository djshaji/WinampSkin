package com.shajikhan.winampskin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.shajikhan.winampskin.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Space;
;import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String TAG = "Main Activity";
    // HJ Story
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    WinampSkin winampSkin;
    Context context ;
    WinampMedia winampMedia ;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ProgressDialog progressDialog = new ProgressDialog(this) ;
//        progressDialog.setIndeterminate(true);
//        progressDialog.show(this, "Winamp", "Whipping the Llama's Ass ...");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = getApplicationContext();
        winampSkin = new WinampSkin(context, this);
        winampSkin.setup ();
        winampMedia = new WinampMedia(context, this);

//        setup();
//        progressDialog.cancel();
//        Log.d(TAG, "onCreate: Setup complete");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        winampMedia.destroy ();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == winampMedia.OPEN_FILE && resultCode == RESULT_OK) {
            Uri fullPhotoUri = data.getData();
            if (fullPhotoUri != null) {
                Log.d(TAG, "onActivityResult: adding local track " + data.toString());
                winampSkin.playlistAdd(fullPhotoUri.getLastPathSegment(), fullPhotoUri.toString());
                winampMedia.exoPlayer.addMediaItem(MediaItem.fromUri(fullPhotoUri));
            } else {
                int count = data.getClipData().getItemCount(),
                        i = 0 ;
                for (i = 0 ; i < count ; i ++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    winampSkin.playlistAdd(imageUri.getLastPathSegment(), imageUri.toString());
                    winampMedia.exoPlayer.addMediaItem(MediaItem.fromUri(imageUri));
                }
            }
        }
    }


}