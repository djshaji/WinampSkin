package com.shajikhan.winampskin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.app.ActivityCompat;
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
    LayoutInflater inflater ;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        inflater = getLayoutInflater();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int introVersion = sharedPref.getInt("introVersion", -1);
        if (introVersion < BuildConfig.VERSION_CODE) {
            Intent intent = new Intent(this, IntroScreen.class) ;
            startActivity(intent);
        }

        Log.d(TAG, "onCreate: " + context.getFilesDir());

        if (! checkPermissionForReadExtertalStorage()) {
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!checkPermissionForWriteExtertalStorage()) {
            try {
                requestPermissionForWriteExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        ProgressDialog progressDialog = new ProgressDialog(this) ;
//        progressDialog.setIndeterminate(true);
//        progressDialog.show(this, "Winamp", "Whipping the Llama's Ass ...");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        winampSkin = new WinampSkin(context, this);
        winampSkin.setup ();
        winampMedia = new WinampMedia(context, this);

//        setup();
//        progressDialog.cancel();
//        Log.d(TAG, "onCreate: Setup complete");
//        winampMedia.exoPlayer.addMediaItem(MediaItem.fromUri("https://cdn.rawgit.com/captbaritone/webamp/43434d82/mp3/llama-2.91.mp3"));
//        winampMedia.exoPlayer.play();

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

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 41;
    private static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 42;
    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public boolean checkPermissionForWriteExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForWriteExtertalStorage() throws Exception {
        Log.d(TAG, "requestPermissionForWriteExtertalStorage: Starting request");
        try {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            Log.e(TAG, "requestPermissionForWriteExtertalStorage: Unable to complete operation!");
            e.printStackTrace();
            throw e;
        }
    }

}