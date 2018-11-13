package com.example.immortal.passportphoto.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.immortal.passportphoto.R;
import com.example.immortal.passportphoto.asynctask.MyAsyncTask;
import com.example.immortal.passportphoto.utils.MyConstant;
import com.tistory.dwfox.dwrulerviewlibrary.utils.DWUtils;
import com.tistory.dwfox.dwrulerviewlibrary.view.ObservableHorizontalScrollView;
import com.tistory.dwfox.dwrulerviewlibrary.view.ScrollingValuePicker;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;

public class RotationImageActivity extends AppCompatActivity implements View.OnClickListener {
    public static String TAG = "MainActivity";

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV Succesfully loaded!");
        } else {
            Log.d(TAG, "OpenCV not loaded");
        }
    }

    private Toolbar tbRotationImage;
    private ImageView imgPhoto;
    private ScrollingValuePicker svRuler;
    private ImageButton btnFlip, btnRotate;
    private Button btnAutoAdjustment;

    private static final int VALUE_MULTIPLE = 5;
    private static final float MIN_VALUE = -30.0f;
    private static final float MAX_VALUE = 30.0f;
    private static final float LINE_RULER_MULTIPLE_SIZE = 1.5f;
    private float angle = 0.0f;
    private int currentAngle = 0;
    private CascadeClassifier mCascadeClassifier;
//    private int orientation = 1;

    public Bitmap imgBitmap, imgBitmapCpy;
    public static String filename = "filename";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation_image);
        init();
        controls();
    }

    private void init() {
        tbRotationImage = findViewById(R.id.tb_RotationImage);
        imgPhoto = findViewById(R.id.img_RIPhoto);
        svRuler = findViewById(R.id.sv_Ruler);
        svRuler.setViewMultipleSize(LINE_RULER_MULTIPLE_SIZE);
        svRuler.setMaxValue(MIN_VALUE, MAX_VALUE);
        svRuler.setValueTypeMultiple(VALUE_MULTIPLE);
        svRuler.setInitValue(0);
        svRuler.getScrollView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    svRuler.getScrollView().startScrollerTask();
                }
                return false;
            }
        });
        btnFlip = findViewById(R.id.btn_RIFlip);
        btnRotate = findViewById(R.id.btn_RIRotate);
        btnAutoAdjustment = findViewById(R.id.btn_RIAutoAdjustment);
        setSupportActionBar(tbRotationImage);
        setTitle("Điều chỉnh độ nghiêng");
        loadingActionbar();

        Intent iReceive = getIntent();
        Uri imageUri = Uri.parse(iReceive.getStringExtra("img"));
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            imgBitmap = BitmapFactory.decodeStream(inputStream);
            imgBitmapCpy = imgBitmap.copy(imgBitmap.getConfig(), imgBitmap.isMutable());
            imgPhoto.setImageBitmap(imgBitmap);
            Log.d("uri", imageUri.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadingActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tbRotationImage.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
    }

    private void controls() {
        tbRotationImage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        svRuler.setOnScrollChangedListener(new ObservableHorizontalScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ObservableHorizontalScrollView observableHorizontalScrollView, int i, int i1) {
            }

            @Override
            public void onScrollStopped(int i, int i1) {
//                Log.d("RS", String.valueOf(DWUtils.getValueAndScrollItemToCenter(svRuler.getScrollView(),
//                        i, i1, MAX_VALUE, MIN_VALUE, svRuler.getViewMultipleSize())));
                int newAngle = DWUtils.getValueAndScrollItemToCenter(svRuler.getScrollView(),
                        i, i1, MAX_VALUE, MIN_VALUE, svRuler.getViewMultipleSize());
                float value = newAngle - currentAngle;
                currentAngle = newAngle;
                if (angle + value >= 360.0f){
                    angle = angle + value - 360.0f;
                }else {
                    angle += value;
                }
//                imgBitmapCpy = rotateBitmap(imgBitmapCpy, (float) DWUtils.getValueAndScrollItemToCenter(svRuler.getScrollView(),
//                        i, i1, MAX_VALUE, MIN_VALUE, svRuler.getViewMultipleSize()));
                imgBitmapCpy = rotateBitmap(imgBitmap, angle);
                imgPhoto.setImageBitmap(imgBitmapCpy);
            }
        });

        btnRotate.setOnClickListener(this);
        btnFlip.setOnClickListener(this);
        btnAutoAdjustment.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mn_Check) {
            MyAsyncTask myAsyncTask = new MyAsyncTask(this);
            myAsyncTask.execute(imgBitmapCpy);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private Bitmap flipBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_RIRotate:
                rotateImage();
                break;
            case R.id.btn_RIFlip:
                flipImage();
                break;
            case R.id.btn_RIAutoAdjustment:
                autoAdjustment();
                break;
        }
    }

    private void rotateImage() {
        angle = angle + 90.0f;
        if (angle >= 360.0f){
            angle = angle - 360.0f;
        }
        imgBitmapCpy = rotateBitmap(imgBitmap, angle);
        imgPhoto.setImageBitmap(imgBitmapCpy);
    }

    private void flipImage() {
        imgBitmapCpy = flipBitmap(imgBitmapCpy);
        imgPhoto.setImageBitmap(imgBitmapCpy);
    }

    private double getAngle(org.opencv.core.Point firstPoint, org.opencv.core.Point secondPoint){
        double dx = firstPoint.x - secondPoint.x;
        double dy = firstPoint.y - secondPoint.y;
        double inRads = Math.atan2(dy, dx);
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2 * Math.PI - inRads;

        return Math.toDegrees(inRads);
    }

    private void autoAdjustment(){
        mCascadeClassifier = MyConstant.Cascade_Setting_Eye(this);
        Mat myImageMat = new Mat();
        MatOfRect eyes = new MatOfRect();
        Utils.bitmapToMat(imgBitmap, myImageMat);
        Imgproc.cvtColor(myImageMat,myImageMat, Imgproc.COLOR_RGBA2GRAY);
        mCascadeClassifier.detectMultiScale(myImageMat, eyes, 1.1, 10, 10, new Size(50, 50), new Size());
        Rect[] eyesArray = eyes.toArray();
//        for (int i = 0; i < eyesArray.length; i++)
//            Imgproc.rectangle(myImageMat, eyesArray[i].tl(), eyesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
//        Utils.matToBitmap(myImageMat, imgBitmap);
//        imgPhoto.setImageBitmap(imgBitmap);
        org.opencv.core.Point first, second;
        first = new org.opencv.core.Point(eyesArray[0].tl().x + (eyesArray[0].width/2), eyesArray[0].tl().y + (eyesArray[0].height/2));
        second =new org.opencv.core.Point(eyesArray[1].tl().x + (eyesArray[1].width/2), eyesArray[1].tl().y + (eyesArray[1].height/2));
//        double angle1 = getAngle(eyesArray[0].tl(), eyesArray[1].tl());
        double angle1 = 360 - getAngle(first, second);
        Log.d("Angle", "" + String.valueOf(angle1));
        imgBitmapCpy = rotateBitmap(imgBitmapCpy, (float) - angle1 );
        imgPhoto.setImageBitmap(imgBitmapCpy);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Test", String.valueOf(imgBitmap == null));
        if (imgBitmap == null || imgBitmapCpy == null ){
            try {
                FileInputStream is = this.openFileInput(RotationImageActivity.filename);
                imgBitmap = BitmapFactory.decodeStream(is);
                imgBitmapCpy = imgBitmap.copy(imgBitmap.getConfig(), imgBitmap.isMutable());
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d("Resume", "resume");
    }
}
