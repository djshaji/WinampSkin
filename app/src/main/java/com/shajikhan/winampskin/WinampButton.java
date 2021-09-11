package com.shajikhan.winampskin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shajikhan.winampskin.MainActivity;

public class WinampButton extends androidx.appcompat.widget.AppCompatButton {
    float x, y, width, height ;
    String resource ;
    MainActivity mainActivity ;
    Context context ;
    Bitmap bitmap ; // we save this coz we might need to restore it later and we don't
                    // want to keep generating it over and over

    public WinampButton(@NonNull Context context) {
        super(context);
    }

    public WinampButton(@NonNull Context _context, MainActivity _mainActivity,
                        String _resource, float _x, float _y,
                        float _width, float _height) {
        super(_context);

        context = _context ;
        mainActivity = _mainActivity ;
        resource = _resource ;
        x = _x ;
        y = _y ;
        width = _width ;
        height = _height ;

        setupButton();
        /*
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage("Ye button kuch karta hai")
                        .setPositiveButton("Ok !", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                            }
                        }).show ();

            }
        });

         */
    }

    private void setupButton () {
        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                this.setBounds(mainActivity.winampSkin.convertDpToPixel(x), mainActivity.winampSkin.convertDpToPixel(y), mainActivity.winampSkin.convertDpToPixel(width), mainActivity.winampSkin.convertDpToPixel(height));
                bitmap = mainActivity.winampSkin.loadSkinBitmap(x, y, width, height, mainActivity.winampSkin.skin, resource);
                bitmap = mainActivity.winampSkin.upscaleBitmapEx(bitmap);
                canvas.drawBitmap(
                        bitmap,
//                        mainActivity.convertDpToPixel(top),
//                        mainActivity.convertDpToPixel(left),
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

        this.setBackground(drawable);
        this.setWidth((int) (mainActivity.winampSkin.convertDpToPixel(width * mainActivity.winampSkin.UPSCALE_FACTOR) + mainActivity.winampSkin.density));
        this.setHeight((int) (mainActivity.winampSkin.convertDpToPixel(height * mainActivity.winampSkin.UPSCALE_FACTOR) + mainActivity.winampSkin.density));
//        this.setBackgroundColor(getResources().getColor(R.color.Water));
    }
}
