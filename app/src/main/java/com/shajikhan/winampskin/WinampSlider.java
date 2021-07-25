package com.shajikhan.winampskin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WinampSlider extends androidx.appcompat.widget.AppCompatSeekBar {
    float x, y, width, height ;
    int resource ;
    MainActivity mainActivity ;
    Context context ;
    Bitmap bitmap, bitmapThumb ; // we save this coz we might need to restore it later and we don't
                    // want to keep generating it over and over

    public WinampSlider(Context context) {
        super(context);
    }

    public WinampSlider(@NonNull Context _context, MainActivity _mainActivity,
                        int _resource, float _x, float _y, float _width, float _height) {
        super(_context);

        context = _context;
        mainActivity = _mainActivity;
        resource = _resource;
        x = _x;
        y = _y;
        width = _width;
        height = _height;
        setup();

    }

    private void setup () {
        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                this.setBounds(mainActivity.convertDpToPixel(x), mainActivity.convertDpToPixel(y), mainActivity.convertDpToPixel(width), mainActivity.convertDpToPixel(height));
                bitmap = mainActivity.getBitmap(x, y, width, height, resource);
                bitmap = mainActivity.upscaleBitmapEx(bitmap);
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
                this.setBounds(mainActivity.convertDpToPixel(0), mainActivity.convertDpToPixel(11), mainActivity.convertDpToPixel(11), mainActivity.convertDpToPixel(11));
                bitmapThumb = mainActivity.getBitmap(0, 164, 11, 11, resource);
                bitmapThumb = mainActivity.upscaleBitmapEx(bitmapThumb);
                canvas.drawBitmap(
                        bitmapThumb,
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
        } ;

//        this.setBackground(drawable);
        this.setProgressDrawable(drawable);
        this.setThumb(thumb);
//        this.setThumb();
//        this.setBackgroundColor(getResources().getColor(R.color.Blue_Orchid));
//        this.setMaxHeight(mainActivity.convertDpToPixel(64));
//        this.setMinimumHeight(mainActivity.convertDpToPixel(64 * mainActivity.UPSCALE_FACTOR));
//        this.setMinimumWidth(mainActivity.convertDpToPixel(64 * mainActivity.UPSCALE_FACTOR));
//        this.setMaxHeight(mainActivity.convertDpToPixel(64 * mainActivity.UPSCALE_FACTOR));
//        this.setMaxHeight(mainActivity.convertDpToPixel(64));
//        this.setMaxWidth(mainActivity.convertDpToPixel(13));
//        this.setMinWidth(mainActivity.convertDpToPixel(13));
        this.setRotation(0);
        this.setPadding(0, 0, mainActivity.convertDpToPixel(14), 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
        setMax(100);
//        setSplitTrack(false);
//        this.setWidth((int) (mainActivity.convertDpToPixel(width * mainActivity.UPSCALE_FACTOR) + mainActivity.density));
//        this.setHeight((int) (mainActivity.convertDpToPixel(height * mainActivity.UPSCALE_FACTOR) + mainActivity.density));
//        this.setBackgroundColor(getResources().getColor(R.color.Water));
    }
}

