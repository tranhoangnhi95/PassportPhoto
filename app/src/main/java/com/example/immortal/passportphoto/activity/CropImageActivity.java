package com.example.immortal.passportphoto.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.immortal.passportphoto.R;
import com.example.immortal.passportphoto.utils.CustomView;
import com.example.immortal.passportphoto.utils.MyConstant;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileInputStream;

public class CropImageActivity extends AppCompatActivity {
    public static String TAG = "MainActivity";

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV Succesfully loaded!");
        } else {
            Log.d(TAG, "OpenCV not loaded");
        }
    }

    private Toolbar tbCropImage;
    private Button btnSelectSize;
    private FrameLayout cvPhoto;
    private Bitmap bmImage, bmImageCpy;
    private CustomView customView;
    private String size;
//    private ImageView imgTest;
    private Mat mat;

    public static CascadeClassifier mCascadeClassifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        init();
        controls();
    }

    private void init() {
        tbCropImage = findViewById(R.id.tb_CropImage);
        btnSelectSize = findViewById(R.id.btn_SelectImageSize);
        cvPhoto = findViewById(R.id.cv_CIPhoto);
        mat = new Mat();
        mCascadeClassifier = MyConstant.Cascade_Setting(this);

//        imgTest = findViewById(R.id.img_Test);
        setSupportActionBar(tbCropImage);
        setTitle("Cắt ảnh");
        loadingActionbar();
        bmImage = null;
        size = "3x4";
        try {
            FileInputStream is = this.openFileInput(RotationImageActivity.filename);
            bmImage = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        customView = new CustomView(this);


        ViewTreeObserver observer = cvPhoto.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (customView != null) {

                    bmImage = Bitmap.createScaledBitmap(bmImage, cvPhoto.getWidth(), cvPhoto.getHeight(), true);
                    customView.setBitmap(bmImage);
                }
                cvPhoto.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        customView.setSize(size);
        cvPhoto.addView(customView);


    }

    private void loadingActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tbCropImage.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mn_Check){
            Utils.bitmapToMat(customView.getBitmap(), mat);
            if (customView.getSize().equals("2x3")){
                Imgproc.resize(mat, mat, new Size(76, 113),0,0, Imgproc.INTER_AREA);
                bmImageCpy = Bitmap.createBitmap(76, 113, bmImage.getConfig());
            }else if (customView.getSize().equals("3x4")){
                Imgproc.resize(mat, mat, new Size(113, 151),0,0, Imgproc.INTER_AREA);
                bmImageCpy = Bitmap.createBitmap(113, 151, bmImage.getConfig());
            }else {
                Imgproc.resize(mat, mat, new Size(151, 227),0,0, Imgproc.INTER_AREA);
                bmImageCpy = Bitmap.createBitmap(151, 227, bmImage.getConfig());
            }

            Utils.matToBitmap(mat, bmImageCpy);
//            imgTest.setImageBitmap(bmImageCpy);
        }
        return true;
    }

    private void controls() {
        btnSelectSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                faceRecognize(bmImage);
                sizeSelect();
//                imgTest.setImageBitmap(customView.getBitmap());
            }
        });

    }


    private void faceRecognize(Bitmap imgBitmap) {
//        File x = MyConstant.Cascade_Setting(this);
//        mCascadeClassifier = new CascadeClassifier(x.getAbsolutePath());
//        Mat myImageMat = new Mat();
//        MatOfRect face = new MatOfRect();
//        Utils.bitmapToMat(imgBitmap, myImageMat);
//        Imgproc.cvtColor(myImageMat,myImageMat, Imgproc.COLOR_RGBA2GRAY);
//        mCascadeClassifier.detectMultiScale(myImageMat, face, 1.1, 5, 10, new Size(50, 50), new Size());
//        Rect[] facesArray = face.toArray();


//        for (int i = 0; i < facesArray.length; i++)
//            Imgproc.rectangle(myImageMat, facesArray[0].tl(), facesArray[0].br(), new Scalar(0, 255, 0, 255), 3);
//        Utils.matToBitmap(myImageMat, imgBitmap);
//        imgTest.setImageBitmap(imgBitmap);
//        org.opencv.core.Point first, second;
//        first = new org.opencv.core.Point(facesArray[0].tl().x + (facesArray[0].width/2), facesArray[0].tl().y + (facesArray[0].height/2));
//        double angle1 = getAngle(eyesArray[0].tl(), eyesArray[1].tl());
//        customView.setPoint((float)facesArray[0].tl().x, (float)facesArray[0].tl().y, (float)facesArray[0].br().x, (float) facesArray[0].br().y);
//        customView.setPoint(74.5f, 917.0f, 1881.0f, 1624.0f);
//        customView.setPoint(0f, 0f, 200.0f, 200.0f)
    }

    private void sizeSelect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View viewDial = inflater.inflate(R.layout.layout_select_size, null);
        builder.setView(viewDial);

        final RadioGroup rgSelect;
        RadioButton btn2x3, btn3x4, btn4x6;
        rgSelect = viewDial.findViewById(R.id.rg_Select);
        btn2x3 = viewDial.findViewById(R.id.btn_2x3);
        btn3x4 = viewDial.findViewById(R.id.btn_3x4);
        btn4x6 = viewDial.findViewById(R.id.btn_4x6);

        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (rgSelect.getCheckedRadioButtonId()) {
                    case R.id.btn_2x3:
                        size = "2x3";
                        break;
                    case R.id.btn_3x4:
                        size = "3x4";
                        break;
                    case R.id.btn_4x6:
                        size = "4x6";
                        break;
                    default:
                        size = "3x4";

                }

                dialogInterface.dismiss();
                customView.setSize(size);
//                Point point1, point2;
//                point1 = new Point(100.0f, 109.44f);
//                point2 = new Point(213.39f, 222.83f);
//                customView.drawFaceRect(new Rect(point1, point2));
                Toast.makeText(CropImageActivity.this, size, Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
