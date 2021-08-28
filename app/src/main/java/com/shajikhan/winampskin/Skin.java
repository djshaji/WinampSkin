package com.shajikhan.winampskin;

import java.util.HashMap;

public class Skin {
    HashMap bitmaps ;

    Skin (boolean def) {
        if (def) bitmaps = new HashMap <String, Integer>();
        else bitmaps =  new HashMap <String, String> ();
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
        bitmaps.put("list", R.drawable.list);
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
}
