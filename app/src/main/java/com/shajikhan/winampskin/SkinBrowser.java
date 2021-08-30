package com.shajikhan.winampskin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SkinBrowser extends AppCompatActivity {
    Context context ;
    String TAG = "SkinBrowser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_skin_browser);
        WebView webView = findViewById(R.id.skin_web);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading: " + url);
                String skinUrl = url.split ("\\?skinUrl=")[1];
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);

            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://skins.webamp.org");
    }
}