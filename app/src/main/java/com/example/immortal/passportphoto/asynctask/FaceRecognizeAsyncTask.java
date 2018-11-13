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

public class FaceRecognizeAsyncTask extends AsyncTask<Bitmap, Void, org.opencv.core.Rect> {
    private ProgressDialog dialog;
    private Context context;

    public FaceRecognizeAsyncTask(Context context) {
        dialog = new ProgressDialog(context);
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Đang xử lý khuôn mặt, vui lòng chờ giây lát.");
        dialog.setCancelable(false);
        dialog.show();
        Log.d("Today", "Show progress");
    }

    @Override
    protected org.opencv.core.Rect doInBackground(Bitmap... bitmaps) {
        Mat myImageMat = new Mat();
        MatOfRect face = new MatOfRect();
        Utils.bitmapToMat(bitmaps[0], myImageMat);
        Imgproc.cvtColor(myImageMat, myImageMat, Imgproc.COLOR_RGBA2GRAY);
//        Imgproc.resize(myImageMat, myImageMat, new Size(bitmaps[0].getWidth() / 5, bitmaps[0].getHeight() / 5));
        CropImageActivity.mCascadeClassifier.detectMultiScale(myImageMat, face, 1.1, 10, 10, new Size(20, 20), new Size());
        org.opencv.core.Rect[] facesArray = face.toArray();

        if (facesArray.length > 0) {
            Log.d("Today", "faces.length > 0");
            return facesArray[0];
        } else {

            Log.d("Today", "faces.length < 0");
            Point tl, br;
            if (bitmaps[0].getWidth() <= bitmaps[0].getHeight()) {
                tl = new Point(bitmaps[0].getWidth() / 4, bitmaps[0].getWidth() / 4);
                br = new Point(3 * bitmaps[0].getWidth() / 4, 3 * bitmaps[0].getWidth() / 4);
            } else {
                tl = new Point(bitmaps[0].getHeight() / 4, bitmaps[0].getHeight() / 4);
                br = new Point(3 * bitmaps[0].getHeight() / 4, 3 * bitmaps[0].getHeight() / 4);
            }
            return new org.opencv.core.Rect(tl, br);
        }
    }

    @Override
    protected void onPostExecute(org.opencv.core.Rect rect) {
        super.onPostExecute(rect);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
