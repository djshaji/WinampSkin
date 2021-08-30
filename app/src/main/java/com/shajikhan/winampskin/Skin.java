package com.shajikhan.winampskin;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.wwdablu.soumya.wzip.WZip;
import com.wwdablu.soumya.wzip.WZipCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Skin {
    String TAG = "Skin";
    public String skinDir, defaultSkinDir ;
    HashMap bitmaps ;
    public enum ResourceType {
        RESOURCE,
        FILE
    }

    Context context ;
    ResourceType resourceType ;

    Skin (Context _context, boolean def) {
        context = _context;
        defaultSkinDir = context.getFilesDir().toString() + "/skins/current/";
        if (def) {
            bitmaps = new HashMap <String, Integer>();
            resourceType = ResourceType.RESOURCE;
            loadDefault();
        }
        else {
            bitmaps =  new HashMap <String, String> ();
            resourceType = ResourceType.FILE;
            loadfromDir(null);
        }
    }

    public void loadDefault () {
        bitmaps.put("avs", R.drawable.avs);
        bitmaps.put("balance", R.drawable.balance);
        bitmaps.put("cbuttons", R.drawable.cbuttons);
        bitmaps.put("eq_bg", R.drawable.eq_bg);
        bitmaps.put("eq_ex", R.drawable.eq_ex);
        bitmaps.put("eqmain", R.drawable.eqmain);
        bitmaps.put("gen", R.drawable.gen);
        bitmaps.put("genex", R.drawable.genex);
        bitmaps.put("main", R.drawable.main);
        bitmaps.put("mb", R.drawable.mb);
        bitmaps.put("monoster", R.drawable.monoster);
        bitmaps.put("numbers", R.drawable.numbers);
        bitmaps.put("playpaus", R.drawable.playpaus);
        bitmaps.put("pledit", R.drawable.pledit);
        bitmaps.put("posbar", R.drawable.posbar);
        bitmaps.put("seekbar", R.drawable.seekbar);
        bitmaps.put("seekbg", R.drawable.seekbg);
        bitmaps.put("shufrep", R.drawable.shufrep);
        bitmaps.put("text", R.drawable.text);
        bitmaps.put("thumb", R.drawable.thumb);
        bitmaps.put("titlebar", R.drawable.titlebar);
        bitmaps.put("video", R.drawable.video);
        bitmaps.put("volume", R.drawable.volume);
        bitmaps.put("volume_thumb", R.drawable.volume_thumb);
    }

    public void loadfromDir (String dir) {
        if (dir == null)
             dir = defaultSkinDir ;

        bitmaps = new HashMap <String, Integer>();
        resourceType = ResourceType.RESOURCE;

        File f = new File(dir);
        File[] files = f.listFiles();
        if (files == null) {
            Log.e(TAG, "Cannot read requested skin: " + dir + "! Fallback to inbuilt default." );
            loadDefault();
            return;
        }

        resourceType = ResourceType.FILE;
        skinDir = dir ;
        bitmaps.put("avs", dir + "avs");
        bitmaps.put("balance", dir + "balance");
        bitmaps.put("cbuttons", dir + "cbuttons");
        bitmaps.put("eq_bg", dir + "eq_bg");
        bitmaps.put("eq_ex", dir + "eq_ex");
        bitmaps.put("eqmain", dir + "eqmain");
        bitmaps.put("gen", dir + "gen");
        bitmaps.put("genex", dir + "genex");
        bitmaps.put("main", dir + "main");
        bitmaps.put("mb", dir + "mb");
        bitmaps.put("monoster", dir + "monoster");
        bitmaps.put("numbers", dir + "numbers");
        bitmaps.put("playpaus", dir + "playpaus");
        bitmaps.put("pledit", dir + "pledit");
        bitmaps.put("posbar", dir + "posbar");
        bitmaps.put("seekbar", dir + "seekbar");
        bitmaps.put("seekbg", dir + "seekbg");
        bitmaps.put("shufrep", dir + "shufrep");
        bitmaps.put("text", dir + "text");
        bitmaps.put("thumb", dir + "thumb");
        bitmaps.put("titlebar", dir + "titlebar");
        bitmaps.put("video", dir + "video");
        bitmaps.put("volume", dir + "volume");
        bitmaps.put("volume_thumb", dir + "volume_thumb");
    }

    public void renameSkinFiles (String dir) {
        Log.e(TAG, "renameSkinFiles: Renaming files in " + dir);
        File f = new File(dir);
        File[] files = f.listFiles();
        if (files == null) {
            Log.e(TAG, "renameSkinFiles: unable to get directory listing!" );
            return ;
        }
        Log.e(TAG, "renameSkinFiles: Files found:"+ files.toString());
        for (File inFile : files) {
            if (! inFile.isDirectory()) {
                Log.d(TAG, "renameSkinFiles: Rename: " + inFile.toString() + " to " + inFile.toString().toLowerCase());
                inFile.renameTo(new File (inFile.toString().toLowerCase()));
            }
        }

    }

    void unzipCallback () {
        Log.d(TAG, "unzipCallback: Complete!");
    }

    public void downloadSkin (String url) {
        new DownloadSkin().execute(url);
    }

    public class DownloadSkin extends AsyncTask<String, Void, Void> {

        private Exception exception;

        protected Void doInBackground(String... urls) {
            InputStream input = null;
            File file = new File(context.getFilesDir(), "downloadedSkin.zip");

            try {
                Log.d(TAG, "downloadSkin: Opening file");
                input = new URL(urls [0]).openStream();
            } catch (FileNotFoundException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Log.d(TAG, "downloadSkin: Downloading skin: " + urls [0]);
                try (OutputStream output = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                    int read;

                    while ((read = input.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }

                    output.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    input.close();
                    Log.d(TAG, "doInBackground: Download complete!");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        void unzipComplete () {
            Log.d(TAG, "unzipComplete: Done!");
        }

        protected void onPostExecute(Void params) {
            // TODO: check this.exception
            // TODO: do something with the feed
            File file = new File(context.getFilesDir(), "downloadedSkin.zip");
            WZip wZip = new WZip();

            if (file == null) {
                Log.e(TAG, "doInBackground: FILE is null");
            }
            if (! file.exists())
                Log.e(TAG, "onPostExecute: file does not exist! " +  file.toString() );
            Log.d(TAG, "downloadSkin: Starting unzip of " + file.toString() + " to " + defaultSkinDir);
            WZipCallback wZipCallback = new WZipCallback() {
                @Override
                public void onStarted(String identifier) {
                    Log.d(TAG, "onStarted: Unzip started");
                }

                @Override
                public void onZipCompleted(File zipFile, String identifier) {
                    Log.d(TAG, "onZipCompleted: unzip complete");
                }

                @Override
                public void onUnzipCompleted(String identifier) {
                    Log.d(TAG, "onUnzipCompleted: unzip complete");
                    renameSkinFiles(defaultSkinDir);
                }

                @Override
                public void onError(Throwable throwable, String identifier) {
                    Log.e(TAG, "onError: " + identifier + "\n" + throwable.toString() );
                }
            };
            wZip.unzip(file,
                    new File(defaultSkinDir),
                    "backupUnzipper",
                    wZipCallback);

        }

    }

}
