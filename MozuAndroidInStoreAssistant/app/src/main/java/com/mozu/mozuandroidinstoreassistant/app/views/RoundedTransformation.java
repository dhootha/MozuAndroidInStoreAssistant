package com.mozu.mozuandroidinstoreassistant.app.views;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

// enables hardware accelerated rounded corners
// original idea here : http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/
public class RoundedTransformation implements com.squareup.picasso.Transformation {

    @Override
    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        float canvasHalfWidth = canvas.getWidth()/2;
        float canvasHalfHeight = canvas.getHeight()/2;
        float radius = (float) Math.sqrt((canvasHalfHeight*canvasHalfHeight) + canvasHalfWidth*canvasHalfWidth);

        canvas.drawCircle(canvas.getWidth() / 2f, canvas.getHeight() / 2f, radius , paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }
}