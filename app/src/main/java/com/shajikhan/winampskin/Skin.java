package com.shajikhan.winampskin;

import java.util.HashMap;

public class Skin {
    HashMap bitmaps ;
    public enum ResourceType {
        RESOURCE,
        FILE
    }

    ResourceType resourceType ;

    Skin (boolean def) {
        if (def) {
            bitmaps = new HashMap <String, Integer>();
            resourceType = ResourceType.RESOURCE;
            loadDefault();
        }
        else {
            bitmaps =  new HashMap <String, String> ();
            resourceType = ResourceType.FILE;
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
             dir = "/Xenamp/Skins/.current/" ;

        resourceType = ResourceType.FILE;

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
}
