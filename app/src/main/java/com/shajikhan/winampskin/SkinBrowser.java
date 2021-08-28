package com.shajikhan.winampskin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class SkinBrowser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_browser);
        WebView webView = findViewById(R.id.skin_web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://skins.webamp.org");
    }
}