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
import com.example.immortal.passportphoto.activity.CropImageActivity;
import com.example.immortal.passportphoto.asynctask.FaceRecognizeAsyncTask;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
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
    private Rect face;
    private String size;
    private Canvas canvas;
    private float currentX, currentY, deltaX, deltaY, percent;
    private Paint paint, paint2;
    private Context context;

    private FaceRecognizeAsyncTask faceRecognizeAsyncTask;

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
        faceRecognizeAsyncTask = new FaceRecognizeAsyncTask(context);
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
                baseLine.moveTo(p1x, ((p2y - p1y) * 80f / 100) + p1y);
                baseLine.lineTo(p2x, ((p2y - p1y) * 80f / 100) + p1y);
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
                        percent = (event.getX() - p2x) / p2x;
                        p2x = event.getX();
//                        p2y = event.getY();
                        p2y = p2y + percent * p2y;
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
                        percent = (event.getX() - p2x) / p2x;
                        p2x = event.getX();
//                        p2y = event.getY();
                        p2y = p2y + percent * p2y;
                        paint2.setColor(getResources().getColor(R.color.my_lighter_secondary));
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if (stopWorking) {
                        stopWorking = false;
                        break;
                    }
                    if (!moveRect) {
                        percent = (event.getX() - p2x) / p2x;
                        p2x = event.getX();
//                        p2y = event.getY();
                        p2y = p2y + percent * p2y;
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


    public void drawFaceRect(Rect rect) {
        float tlx, tly, brx, bry;
        tlx = (float) rect.tl().x;
        tly = (float) rect.tl().y;
        brx = (float) rect.br().x;
        bry = (float) rect.br().y;
        this.scaleRect = true;

        if (size.equals("4x6")) {
//            p1x = tlx - (brx - tlx) * 5f / 90f;
            p1x = tlx - (bry - tly) * 5f / 90f;
            p1y = tly - (bry - tly) / 6f;
//            p2x = brx + ((brx - tlx) * 5f / 90f);
            p2x = brx + ((bry - tly) * 5f / 90f);
            p2y = bry + (bry - tly) / 2f;
        } else if (size.equals("3x4")) {

            p1x = tlx;
            p1y = tly - (bry - tly) * 6.25f / 75f;
            p2x = brx;
            p2y = bry + (bry - tly) * 18.75f / 75f;

        } else {
            p1x = tlx;
            p1y = tly - (bry - tly) * 10f / 70f;
            p2x = brx;
            p2y = bry + (bry - tly) * 10f / 70f;
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

        if (face != null) {
            this.drawFaceRect(face);
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

        faceRecognizeAsyncTask.execute(bitmap);
        try {
            this.face = faceRecognizeAsyncTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Point tl, br;
            if (bitmap.getWidth() <= bitmap.getHeight()) {
                tl = new Point(bitmap.getWidth() / 4, bitmap.getWidth() / 4);
                br = new Point(3 * bitmap.getWidth() / 4, 3 * bitmap.getWidth() / 4);
            } else {
                tl = new Point(bitmap.getHeight() / 4, bitmap.getHeight() / 4);
                br = new Point(3 * bitmap.getHeight() / 4, 3 * bitmap.getHeight() / 4);
            }
            this.face = new Rect(tl, br);

        } catch (ExecutionException e) {
            Point tl, br;
            if (bitmap.getWidth() <= bitmap.getHeight()) {
                tl = new Point(bitmap.getWidth() / 4, bitmap.getWidth() / 4);
                br = new Point(3 * bitmap.getWidth() / 4, 3 * bitmap.getWidth() / 4);
            } else {
                tl = new Point(bitmap.getHeight() / 4, bitmap.getHeight() / 4);
                br = new Point(3 * bitmap.getHeight() / 4, 3 * bitmap.getHeight() / 4);
            }
            this.face = new Rect(tl, br);
            e.printStackTrace();
        }
        this.drawFaceRect(this.face);
        invalidate();

    }


}
