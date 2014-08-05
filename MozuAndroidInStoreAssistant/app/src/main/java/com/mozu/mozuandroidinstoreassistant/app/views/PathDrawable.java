package com.mozu.mozuandroidinstoreassistant.app.views;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;


public class PathDrawable extends Drawable {

    private final Bitmap mBitmap;
    private Bitmap mTransformedBitmap;
    private Path mPath;

    private Paint mPaint;

    public PathDrawable(Bitmap bitmap) {
        mBitmap = bitmap;
        mPaint = new Paint();

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        if(mTransformedBitmap != null)
            mTransformedBitmap.recycle();

        mTransformedBitmap = Bitmap.createScaledBitmap(mBitmap, bounds.width(), bounds.height(), false);

        BitmapShader shader = new BitmapShader(mTransformedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        //get bounds of path to scale to bounds of view if different
        RectF pathBounds = new RectF();
        mPath.computeBounds(pathBounds, true);

        //scale path to view
        Matrix pathToBoundsMatrix = new Matrix();
        pathToBoundsMatrix.postScale((float)bounds.width()/pathBounds.width(), (float)bounds.height()/pathBounds.height());
        pathToBoundsMatrix.postTranslate(bounds.left, bounds.top);
        canvas.concat(pathToBoundsMatrix);

        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    protected void setPath(Path path) {
        mPath = path;
    }
}
