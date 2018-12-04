package com.example.immortal.passportphoto.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import com.example.immortal.passportphoto.activity.CropImageActivity;
import com.example.immortal.passportphoto.activity.RotationImageActivity;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CheckAutoEnableAsyncTask extends AsyncTask<Bitmap, Void, org.opencv.core.Point[]> {
    private Context context;
    private Button btnAuto;
    private boolean enable;

    public CheckAutoEnableAsyncTask(Context context, Button btnAuto) {
        this.context = context;
        this.btnAuto = btnAuto;
        this.enable = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        btnAuto.setEnabled(false);
    }

    @Override
    protected org.opencv.core.Point[] doInBackground(Bitmap... bitmaps) {
        org.opencv.core.Point[] points = new org.opencv.core.Point[2];
        Mat myImageMat = new Mat();
        MatOfRect eyes = new MatOfRect();
        Utils.bitmapToMat(bitmaps[0], myImageMat);
        Imgproc.cvtColor(myImageMat, myImageMat, Imgproc.COLOR_RGBA2GRAY);
//        Imgproc.resize(myImageMat, myImageMat, new Size(bitmaps[0].getWidth() / 5, bitmaps[0].getHeight() / 5));
        RotationImageActivity.mCascadeClassifier.detectMultiScale(myImageMat, eyes, 1.1, 10, 10, new Size(20, 20), new Size());
        org.opencv.core.Rect[] eyesArray = eyes.toArray();

        if (eyesArray.length == 2) {
            enable = true;
            Log.d("Today", "eyes.length = 2");
//            points[0].x = (eyesArray[0].tl().x + eyesArray[0].br().x)/2;
//            points[0].y = (eyesArray[0].tl().y + eyesArray[0].br().y)/2;
//            points[0] = new org.opencv.core.Point((eyesArray[0].tl().x + eyesArray[0].br().x) * 5 / 2, (eyesArray[0].tl().y + eyesArray[0].br().y) * 5 / 2);
//            points[1] = new org.opencv.core.Point((eyesArray[1].tl().x + eyesArray[1].br().x) * 5 / 2, (eyesArray[1].tl().y + eyesArray[1].br().y) * 5 / 2);
            points[0] = new org.opencv.core.Point((eyesArray[0].tl().x + eyesArray[0].br().x)/ 2, (eyesArray[0].tl().y + eyesArray[0].br().y)/ 2);
            points[1] = new org.opencv.core.Point((eyesArray[1].tl().x + eyesArray[1].br().x)/ 2, (eyesArray[1].tl().y + eyesArray[1].br().y)/ 2);

//            points[1].x = (eyesArray[1].tl().x + eyesArray[1].br().x)/2;
//            points[1].y = (eyesArray[1].tl().y + eyesArray[1].br().y)/2;
        } else {
            enable = false;
            Log.d("Today", "eye != 2");
            if (bitmaps[0].getWidth() <= bitmaps[0].getHeight()) {
                points[0] = new org.opencv.core.Point(bitmaps[0].getWidth() / 4, bitmaps[0].getWidth() / 4);
                points[1] = new org.opencv.core.Point(3 * bitmaps[0].getWidth() / 8, bitmaps[0].getWidth() / 4);
            } else {

                points[0] = new org.opencv.core.Point(bitmaps[0].getHeight() / 4, bitmaps[0].getHeight() / 4);
                points[1] = new org.opencv.core.Point(3 * bitmaps[0].getHeight() / 8, bitmaps[0].getHeight() / 4);
            }
        }
        Log.d("Today", "eye = " + String.valueOf(eyesArray.length));
        return points;
    }

    @Override
    protected void onPostExecute(org.opencv.core.Point[] points) {
        super.onPostExecute(points);
        if (enable) {
            btnAuto.setEnabled(true);
        } else {
            btnAuto.setEnabled(false);
        }
    }
}
