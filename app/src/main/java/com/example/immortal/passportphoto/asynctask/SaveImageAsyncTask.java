package com.example.immortal.passportphoto.asynctask;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.immortal.passportphoto.activity.RotationImageActivity;
import com.example.immortal.passportphoto.activity.SaveImageActivity;

import java.io.File;
import java.io.FileOutputStream;

public class SaveImageAsyncTask extends AsyncTask<Bitmap, Void, Void> {
    private ProgressDialog dialog;
    private Context context;
    private File file;

    public SaveImageAsyncTask(Context context, File file) {
        this.context = context;
        dialog = new ProgressDialog(this.context);
        this.file = file;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Đang lưu ảnh vào: \" " + file.getPath() + "\". " + "Vui lòng chờ giây lát.");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Void doInBackground(Bitmap... bitmaps) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmaps[0].recycle();

            refreshGallery(file);
            addImageToGallery(file.getPath(), this.context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Toast.makeText(context, "Ảnh đã được lưu với tên là: " + file.getName(), Toast.LENGTH_LONG).show();
    }

    public void refreshGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        this.context.sendBroadcast(intent);
    }

    public void addImageToGallery(String filePath, Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}
