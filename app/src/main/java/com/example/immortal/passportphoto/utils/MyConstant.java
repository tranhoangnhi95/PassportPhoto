package com.example.immortal.passportphoto.utils;

import android.content.Context;

import com.example.immortal.passportphoto.R;

import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyConstant {
    public static final String TAG = "PassportPhoto_TAG";

    public static final CascadeClassifier Cascade_Setting(Context context) {
        File mCascadeFile = null;
        try {
            // load cascade file from application resources
            InputStream is = context.getResources().openRawResource(R.raw.haarcascade_profileface);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "haarcascade_profileface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        CascadeClassifier mCascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        return mCascadeClassifier;
    }

    public static final CascadeClassifier Cascade_Setting_Eye(Context context) {
        File mCascadeFile = null;
        try {
            // load cascade file from application resources
            InputStream is = context.getResources().openRawResource(R.raw.haarcascade_eye);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "haarcascade_eye.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        CascadeClassifier mCascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        return mCascadeClassifier;
    }
}
