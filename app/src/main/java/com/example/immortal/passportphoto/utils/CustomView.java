package com.example.immortal.passportphoto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.immortal.passportphoto.R;
import com.example.immortal.passportphoto.asynctask.EyesRecognizeAsyncTask;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.video.BackgroundSubtractorMOG2;

import java.util.concurrent.ExecutionException;

public class CustomView extends View {

    public static String TAG = "MainActivity";

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV Succesfully loaded!");
        } else {
            Log.d(TAG, "OpenCV not loaded");
        }
    }

    private static final float mm2pxConst = 3.779528f;
    private String photoSize;
    private float p1x, p1y, p2x, p2y;
    //    private Rect face;
    private Point point1, point2;
    private String size;
    private Canvas canvas;
    private float currentX, currentY, deltaX, deltaY, percent;
    private Paint paint, paint2;
    private Context context;


    private EyesRecognizeAsyncTask eyesRecognizeAsyncTask;

    private boolean moveRect, scaleRect, stopWorking;
    @Nullable
    private Bitmap bitmap;

    @Nullable
    Bitmap bitmapCpy;

    public CustomView(Context context) {
        super(context, null);
        super.setClickable(true);
        init(context);
        this.context = context;

    }

    private void init(Context context) {
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.blue_blur));
        eyesRecognizeAsyncTask = new EyesRecognizeAsyncTask(context);
        float[] intervals = new float[]{50.0f, 20.0f};
        float phase = 0;

        DashPathEffect dashPathEffect =
                new DashPathEffect(intervals, phase);

        paint.setPathEffect(dashPathEffect);

        paint2 = new Paint();
        paint2.setStrokeWidth(15);
        paint2.setAntiAlias(true);
        paint2.setDither(true);
        paint2.setStrokeJoin(Paint.Join.ROUND);
        paint2.setStrokeCap(Paint.Cap.ROUND);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(getResources().getColor(R.color.blur));


        photoSize = new String();
        p1x = p2x = p1y = p2y = 0.0f;

        moveRect = false;
        stopWorking = false;
        scaleRect = false;

    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);

        }
        if (p1x != Float.MIN_VALUE && p2x != Float.MIN_VALUE && p1y != Float.MIN_VALUE && p2y != Float.MIN_VALUE) {
            if (size.equals("2x3") && !scaleRect) {
                p2x = p1x + 20 * mm2pxConst;
                p2y = p1y + 30 * mm2pxConst;
            } else if (size.equals("3x4") && !scaleRect) {
                p2x = p1x + 30 * mm2pxConst;
                p2y = p1y + 40 * mm2pxConst;
            } else if (size.equals("4x6") && !scaleRect) {
                p2x = p1x + 40 * mm2pxConst;
                p2y = p1y + 60 * mm2pxConst;
            }
            if (p1x > p2x && p1y > p2y) {
                canvas.drawRect(p2x, p2y, p1x, p1y, paint);
            } else {
                canvas.drawRect(p1x, p1y, p2x, p2y, paint);

            }

            canvas.drawCircle(p2x, p2y, 10f, paint2);

            if (size.equals("4x6")) {
                Path baseLine = new Path();
                baseLine.moveTo(p1x, ((p2y - p1y) * 70f / 100) + p1y);
                baseLine.lineTo(p2x, ((p2y - p1y) * 70f / 100) + p1y);
                canvas.drawPath(baseLine, paint);

                baseLine.moveTo(p1x, ((p2y - p1y) * 10f / 100) + p1y);
                baseLine.lineTo(p2x, ((p2y - p1y) * 10f / 100) + p1y);
                canvas.drawPath(baseLine, paint);
            } else if (size.equals("3x4")) {
                Path baseLine = new Path();
                baseLine.moveTo(p1x, ((p2y - p1y) * 81.25f / 100) + p1y);
                baseLine.lineTo(p2x, ((p2y - p1y) * 81.25f / 100) + p1y);
                canvas.drawPath(baseLine, paint);

                baseLine.moveTo(p1x, ((p2y - p1y) * 6.25f / 100) + p1y);
                baseLine.lineTo(p2x, ((p2y - p1y) * 6.25f / 100) + p1y);
                canvas.drawPath(baseLine, paint);

            } else {
                Path baseLine = new Path();
                baseLine.moveTo(p1x, ((p2y - p1y) * 76.7f / 100) + p1y);
                baseLine.lineTo(p2x, ((p2y - p1y) * 76.7f / 100) + p1y);
                canvas.drawPath(baseLine, paint);

                baseLine.moveTo(p1x, ((p2y - p1y) * 10f / 100) + p1y);
                baseLine.lineTo(p2x, ((p2y - p1y) * 10f / 100) + p1y);
                canvas.drawPath(baseLine, paint);
            }

        }
        canvas.save();
    }

    private Matrix getScaleImageMatrix(Canvas canvas, Bitmap bitmap) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), Matrix.ScaleToFit.CENTER);
        return m;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (bitmap != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (event.getX() >= p2x - 20f && event.getX() <= p2x + 20f && event.getY() >= p2y - 20f && event.getY() <= p2y + 20f) {
                        scaleRect = true;
//                        percent = (event.getX() - p2x) / p2x;
//                        p2x = event.getX();
//                        p2y = event.getY();
//                        p2y = p2y + percent * p2y;
                    } else if (event.getX() > p1x && event.getX() < p2x && event.getY() > p1y && event.getY() < p2y) {
                        moveRect = true;
                        currentX = event.getX();
                        currentY = event.getY();
                        paint2.setColor(getResources().getColor(R.color.my_secondary));
                    } else {
//                        p1x = event.getX();
//                        p1y = event.getY();
                        stopWorking = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (stopWorking) {
                        break;
                    }

                    if (moveRect) {
                        deltaX = event.getX() - currentX;
                        deltaY = event.getY() - currentY;
                        if (Math.abs(deltaX) >= 1f || Math.abs(deltaY) >= 1f) {
                            if (!checkMoveAble(deltaX, deltaY)) {
                                currentX = event.getX();
                                currentY = event.getY();
                            } else {

                                p1x = movePointX(p1x, deltaX);
                                p1y = movePointY(p1y, deltaY);
                                p2x = movePointX(p2x, deltaX);
                                p2y = movePointY(p2y, deltaY);

                                currentX = event.getX();
                                currentY = event.getY();
                                paint2.setColor(getResources().getColor(R.color.my_lighter_secondary));
                            }
                        }
                    } else {
                        scaleRect = true;
//                            percent = (event.getX() - p2x) * 100 / p2x;
                        p2x = event.getX();
                        Log.d("today", "p2x: " + String.valueOf(p2x));
                        Log.d("today", "p1x: " + String.valueOf(p1x));
                        Log.d("today", "p1y: " + String.valueOf(p1y));
                        if (size.equals("3x4")) {
                            p2y = p1y + ((p2x - p1x) * (4f / 3f));
                        } else {
                            p2y = p1y + ((p2x - p1x) * (3f / 2f));
                        }
                        Log.d("today", "p2y: " + String.valueOf(p2y));
                        paint2.setColor(getResources().getColor(R.color.my_lighter_secondary));


                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if (stopWorking) {
                        stopWorking = false;
                        break;
                    }
                    if (!moveRect) {
                        p2x = event.getX();
                        Log.d("today", "p2x: " + String.valueOf(p2x));
                        Log.d("today", "p1x: " + String.valueOf(p1x));
                        Log.d("today", "p1y: " + String.valueOf(p1y));
                        if (size.equals("3x4")) {
                            p2y = p1y + ((p2x - p1x) * (4f / 3f));
                        } else {
                            p2y = p1y + ((p2x - p1x) * (3f / 2f));
                        }
                        Log.d("today", "p2y: " + String.valueOf(p2y));
                    }
                    paint2.setColor(getResources().getColor(R.color.blur));
                    moveRect = false;
                    scaleRect = true;
                    break;

            }
            if (p2x > this.bitmap.getWidth()) {
                p2x = this.bitmap.getWidth();
            }
            if (p2y > this.bitmap.getHeight()) {
                p2y = this.bitmap.getHeight();
            }
            invalidate();

        }
        return true;
    }


    public void drawFaceRect(Point point1, Point point2) {
        float x1, y1, x2, y2;
        x1 = (float) point1.x;
        y1 = (float) point1.y;
        x2 = (float) point2.x;
        y2 = (float) point2.y;
        this.scaleRect = true;

        if (size.equals("4x6")) {
//            p1x = x1 - (x2 - x1) * 32.5f / 35f;
//            p1y = y1 - (x2 - x1) * 3 * 40f / 70f;
//            p2x = x2 + (x2 - x1) * 32.5f / 35f;
//            p2y = y1 + (x2 - x1) * 3 * 60f / 70f;
            p1x = x1 - (x2 - x1) * 41f / 18f;
            p1y = y1 - (x2 - x1) *40f / 12f;
            p2x = x2 + (x2 - x1) * 41f / 18f;
            p2y = y1 + (x2 - x1) * 60f/ 12f;
        } else if (size.equals("3x4")) {

//            p1x = x1 - (x2 - x1) * 33.3f / 33.4f;
//            p1y = y1 - (x2 - x1) * 43.75f / 25f;
//            p2x = x2 + (x2 - x1) * 33.3f / 33.4f;
//            p2y = y1 + (x2 - x1) * 56.25f / 25f;
            p1x = x1 - (x2 - x1) * 39f / 22f;
            p1y = y1 - (x2 - x1) * 40f / 16.5f;
            p2x = x2 + (x2 - x1) * 39f / 22f;
            p2y = y1 + (x2 - x1) * 60f / 16.5f;

        } else {
//            p1x = x1 - (x2 - x1) * 30f / 40f;
//            p1y = y1 - (x2 - x1) * 3 * 43.35f / 80f;
//            p2x = x2 + (x2 - x1) * 30f / 40f;
//            p2y = y1 + (x2 - x1) * 3 * 56.65f / 80f;
            p1x = x1 - (x2 - x1) * 38.5f / 23f;
            p1y = y1 - (x2 - x1) * 3f * 40f / 46f;
            p2x = x2 + (x2 - x1) * 38.5f / 23f;
            p2y = y1 + (x2 - x1) * 3f * 60f / 46f;
        }

        invalidate();
    }

    private float movePointX(float X, float deltaX) {
        if (X + deltaX < 0 || X + deltaX > bitmap.getWidth()) {
            X = X;
        } else {
            X = X + deltaX;
        }
        return X;
    }

    private float movePointY(float Y, float deltaY) {
        if (Y + deltaY < 0 || Y + deltaY > bitmap.getHeight()) {
            Y = Y;
        } else {
            Y = Y + deltaY;
        }
        return Y;
    }

    private boolean checkMoveAble(float deltaX, float deltaY) {
        if (movePointX(p1x, deltaX) == p1x) return false;
        if (movePointX(p2x, deltaX) == p2x) return false;
        if (movePointY(p1y, deltaY) == p1y) return false;
        if (movePointY(p2y, deltaY) == p2y) return false;
        return true;
    }

    public void setSize(String size) {
        this.size = size;
        this.scaleRect = true;

        if (point1 != null && point2 != null) {
            this.drawFaceRect(point1, point2);
        }
        invalidate();
    }

    public String getSize() {
        return size;
    }

    public Bitmap getBitmap() {
        bitmapCpy = Bitmap.createBitmap(this.bitmap, (int) p1x, (int) p1y, (int) (p2x - p1x), (int) (p2y - p1y));
        return bitmapCpy;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;

        eyesRecognizeAsyncTask.execute(bitmap);
        try {
            this.point1 = eyesRecognizeAsyncTask.get()[0];
            this.point2 = eyesRecognizeAsyncTask.get()[1];
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (bitmap.getWidth() <= bitmap.getHeight()) {
                point1 = new org.opencv.core.Point(bitmap.getWidth() / 4, bitmap.getHeight() / 2);
                point2 = new org.opencv.core.Point(3 * bitmap.getWidth() / 8, bitmap.getHeight() / 2);
            }
        } catch (ExecutionException e) {
            if (bitmap.getWidth() <= bitmap.getHeight()) {
                point1 = new org.opencv.core.Point(bitmap.getWidth() / 4, bitmap.getHeight() / 2);
                point2 = new org.opencv.core.Point(3 * bitmap.getWidth() / 8, bitmap.getHeight() / 2);
            }
            e.printStackTrace();
        }
        this.drawFaceRect(this.point1, this.point2);
        invalidate();

    }

    public float getP1x() {
        return p1x;
    }

    public float getP1y() {
        return p1y;
    }

    public float getP2x() {
        return p2x;
    }

    public float getP2y() {
        return p2y;
    }
}
