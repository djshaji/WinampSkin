package com.shajikhan.winampskin;

import android.media.audiofx.DynamicsProcessing;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class WinampEqualizer {
    DynamicsProcessing dynamicsProcessing = null;
    DynamicsProcessing.Eq eq;
    String TAG = "WinampEqualizer";

    int [] bands = {
            60,
            170,
            310,
            600,
            1000,
            3000,
            6000,
            12000,
            14000,
            16000
    } ;

    @RequiresApi(api = Build.VERSION_CODES.P)
    WinampEqualizer (int sessionId) {
        if (dynamicsProcessing == null) {
            DynamicsProcessing.Config.Builder builder =
                    new DynamicsProcessing.Config.Builder(
                            DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                            1,       // number of channels
                            true, 10, // preEq: use, number of bands  -> Pre Equalizer
                            true, 1, // mbc: use, number of bands    -> Multi-Band Compressor
                            true, 10, // postEq: use, number of bands -> Post Equalizer
                            true     // Limiter: use
                    );
            builder.setPreferredFrameDuration(10); //ms
            DynamicsProcessing.Config config = builder.build();

            eq = config.getPreEqByChannelIndex(0);
            for (int i = 0; i < 10; i++) {
                DynamicsProcessing.EqBand postEqBand = eq.getBand(i);
                postEqBand.setGain(0);
                postEqBand.setCutoffFrequency(bands [i]);
            }

            dynamicsProcessing = new DynamicsProcessing(0, sessionId, config);
//            eq = dynamicsProcessing.getConfig().getPreEqByChannelIndex(0);
        }

        dynamicsProcessing.setEnabled(true);
        eq.setEnabled(true);
    }

    void configureEqualizer () {
    }

    public void destroy () {
        dynamicsProcessing.release();
    }
}
