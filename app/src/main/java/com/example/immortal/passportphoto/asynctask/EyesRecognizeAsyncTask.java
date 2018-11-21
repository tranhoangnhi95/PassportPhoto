package com.example.immortal.passportphoto.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import com.example.immortal.passportphoto.activity.CropImageActivity;
import com.example.immortal.passportphoto.activity.RotationImageActivity;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class EyesRecognizeAsyncTask extends AsyncTask<Bitmap, Void, org.opencv.core.Point[]> {
    private ProgressDialog dialog;
    private Context context;

    public EyesRecognizeAsyncTask(Context context) {
        dialog = new ProgressDialog(context);
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Đang xử lý ảnh , vui lòng chờ giây lát.");
        dialog.setCancelable(false);
        dialog.show();
        Log.d("Today", "Show progress");
    }

    @Override
    protected org.opencv.core.Point[] doInBackground(Bitmap... bitmaps) {
        Point[] points = new Point[2];
        Mat myImageMat = new Mat();
        MatOfRect eyes = new MatOfRect();
        Utils.bitmapToMat(bitmaps[0], myImageMat);
        Imgproc.cvtColor(myImageMat, myImageMat, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(myImageMat, myImageMat, new Size(bitmaps[0].getWidth() / 5, bitmaps[0].getHeight() / 5));
        RotationImageActivity.mCascadeClassifier.detectMultiScale(myImageMat, eyes, 1.1, 10, 10, new Size(20, 20), new Size());
        org.opencv.core.Rect[] eyesArray = eyes.toArray();

        if (eyesArray.length == 2) {
            Log.d("Today", "eyes.length = 2");
//            points[0].x = (eyesArray[0].tl().x + eyesArray[0].br().x)/2;
//            points[0].y = (eyesArray[0].tl().y + eyesArray[0].br().y)/2;
            points[0] = new Point((eyesArray[0].tl().x + eyesArray[0].br().x) * 5 / 2, (eyesArray[0].tl().y + eyesArray[0].br().y) * 5 / 2);
            points[1] = new Point((eyesArray[1].tl().x + eyesArray[1].br().x) * 5 / 2, (eyesArray[1].tl().y + eyesArray[1].br().y) * 5 / 2);
//            points[1].x = (eyesArray[1].tl().x + eyesArray[1].br().x)/2;
//            points[1].y = (eyesArray[1].tl().y + eyesArray[1].br().y)/2;
        } else {

            Log.d("Today", "eye != 2");
            Point tl, br;
            if (bitmaps[0].getWidth() <= bitmaps[0].getHeight()) {
//                points[0] = new Point(bitmaps[0].getWidth() / 8, bitmaps[0].getWidth() / 8);
//                points[1] = new Point(3 * bitmaps[0].getWidth() / 8, bitmaps[0].getWidth() / 8);
                points[0] = new Point(200, 200);
                points[1] = new Point(300, 200);
            } else {
//                points[0] = new Point(bitmaps[0].getHeight() / 8, bitmaps[0].getHeight() / 8);
//                points[1] = new Point(3 * bitmaps[0].getHeight() / 8, bitmaps[0].getHeight() / 8);
                points[0] = new Point(200, 200);
                points[1] = new Point(300, 200);
            }
        }
        return points;
    }

    @Override
    protected void onPostExecute(Point[] points) {
        super.onPostExecute(points);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
