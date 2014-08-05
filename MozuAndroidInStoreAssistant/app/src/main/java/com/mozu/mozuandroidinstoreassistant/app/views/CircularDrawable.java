package com.mozu.mozuandroidinstoreassistant.app.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;


public class CircularDrawable extends PathDrawable {
    private Paint mPaint;

    public CircularDrawable(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        Path path = new Path();
        path.addCircle(bounds.centerX(), bounds.centerY(), (float)bounds.width() / 2f, Path.Direction.CW);
        path.close();

        setPath(path);
        super.draw(canvas);
    }
}
