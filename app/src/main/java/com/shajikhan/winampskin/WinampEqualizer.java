package com.shajikhan.winampskin;

import android.media.audiofx.DynamicsProcessing;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class WinampEqualizer {
    DynamicsProcessing dynamicsProcessing = null;
    DynamicsProcessing.Eq eq;
    String TAG = "WinampEqualizer";

    @RequiresApi(api = Build.VERSION_CODES.P)
    WinampEqualizer (int sessionId) {
        if (dynamicsProcessing == null) dynamicsProcessing = new DynamicsProcessing(sessionId);
        eq = dynamicsProcessing.getPostEqByChannelIndex(0) ;
        Log.d(TAG, String.format("WinampEqualizer: %s", eq.getBandCount()));
    }

    void configureEqualizer () {
    }
}
