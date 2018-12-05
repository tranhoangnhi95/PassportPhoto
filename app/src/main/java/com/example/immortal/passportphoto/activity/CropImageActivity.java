package com.example.immortal.passportphoto.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.FileInputStream;
import java.io.FileOutputStream;

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
    private Point point1, point2;
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
        mCascadeClassifier = MyConstant.Cascade_Setting_Eye(this);
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
        btnSelectSize.setText(String.valueOf(size));
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
        getMenuInflater().inflate(R.menu.option_menu_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mn_Check) {
            float p1x, p1y, p2x, p2y;
            p1x = customView.getP1x() / (cvPhoto.getWidth() / bmImage.getWidth());
            p1y = customView.getP1y() / (cvPhoto.getHeight() / bmImage.getHeight());
            p2x = customView.getP2x() / (cvPhoto.getWidth() / bmImage.getWidth());
            p2y = customView.getP2y() / (cvPhoto.getHeight() / bmImage.getHeight());
            bmImageCpy = Bitmap.createBitmap(bmImage, (int) p1x, (int) p1y, (int) (p2x - p1x), (int) (p2y - p1y));
            Utils.bitmapToMat(bmImageCpy, mat);
            if (customView.getSize().equals("2x3")) {
                Imgproc.resize(mat, mat, new Size(MyConstant.mmToPx(20), MyConstant.mmToPx(30)), 0, 0, Imgproc.INTER_AREA);
                bmImageCpy = Bitmap.createBitmap(MyConstant.mmToPx(20), MyConstant.mmToPx(30), bmImage.getConfig());
            } else if (customView.getSize().equals("3x4")) {
                Imgproc.resize(mat, mat, new Size(MyConstant.mmToPx(30), MyConstant.mmToPx(40)), 0, 0, Imgproc.INTER_AREA);
//                Imgproc.resize(mat, mat, new Size(354, 472),0,0, Imgproc.INTER_AREA);
                bmImageCpy = Bitmap.createBitmap(MyConstant.mmToPx(30), MyConstant.mmToPx(40), bmImage.getConfig());
            } else {
                Imgproc.resize(mat, mat, new Size(MyConstant.mmToPx(40), MyConstant.mmToPx(60)), 0, 0, Imgproc.INTER_AREA);
                bmImageCpy = Bitmap.createBitmap(MyConstant.mmToPx(40), MyConstant.mmToPx(60), bmImage.getConfig());
            }

            Utils.matToBitmap(mat, bmImageCpy);
//            imgTest.setImageBitmap(bmImageCpy);
            try {
                //Write file
                String filename = "bitmap.png";
                FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                bmImageCpy.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //Cleanup
                stream.close();
                bmImageCpy.recycle();

                //Pop intent
                Intent iToImagePrg = new Intent(CropImageActivity.this, ImageProcessingActivity.class);
                iToImagePrg.putExtra("image", filename);
                startActivity(iToImagePrg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void controls() {
        tbCropImage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnSelectSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sizeSelect();
            }
        });

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
                btnSelectSize.setText(String.valueOf(size));
                Toast.makeText(CropImageActivity.this, size, Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
