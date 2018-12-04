package com.example.immortal.passportphoto.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.immortal.passportphoto.activity.CropImageActivity;
import com.example.immortal.passportphoto.activity.RotationImageActivity;

import java.io.FileOutputStream;

public class MyAsyncTask extends AsyncTask<Bitmap, Void, Void> {
    private ProgressDialog dialog;
    private RotationImageActivity activity;

    public MyAsyncTask(RotationImageActivity activity) {
        dialog = new ProgressDialog(activity);
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Đang xử lý, vui lòng chờ giây lát.");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Void doInBackground(Bitmap... bitmaps) {
        try {
            FileOutputStream stream = activity.openFileOutput(RotationImageActivity.filename, Context.MODE_PRIVATE);
            bitmaps[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
//            bitmaps[0].recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.startActivity(new Intent(activity, CropImageActivity.class));
    }
}
