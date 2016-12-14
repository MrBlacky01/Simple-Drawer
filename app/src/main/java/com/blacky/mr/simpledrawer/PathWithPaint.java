package com.blacky.mr.simpledrawer;

import android.graphics.Paint;
import android.graphics.Path;

public class PathWithPaint {

    private Path mPath;
    private Paint mPaint;

    public PathWithPaint(Path path, int paintColor,int lineSize) {
        mPath = path;
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(paintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(lineSize);
    }

    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        mPath = path;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }
}
