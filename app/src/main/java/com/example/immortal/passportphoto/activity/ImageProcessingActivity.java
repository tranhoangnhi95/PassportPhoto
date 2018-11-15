package com.example.immortal.passportphoto.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.immortal.passportphoto.R;
import com.example.immortal.passportphoto.adapter.MainFunctionAdapter;
import com.example.immortal.passportphoto.model.MainFunction;
import com.example.immortal.passportphoto.utils.MyConstant;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ImageProcessingActivity extends AppCompatActivity {
    public static String TAG = "MainActivity";

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV Succesfully loaded!");
        } else {
            Log.d(TAG, "OpenCV not loaded");
        }
    }

    private ImageView imgMyPhoto;
    private Toolbar tbImageProc;
    private SeekBar sbValue;
    private RecyclerView rvMainFunction;
    private MainFunctionAdapter mainFunctionAdapter;
    private ArrayList<MainFunction> mainFunctions;
    private Bitmap imgBitmap;
    private TextView txtValue;
    private CascadeClassifier mCascadeClassifier;
    private MatOfRect faces;
    private Bitmap myImageBitmap;
    private Mat myImageMat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        init();
        control();
    }

    private void init() {
        imgMyPhoto = findViewById(R.id.img_Photo);
        tbImageProc = findViewById(R.id.tb_ImageProcessing);
        rvMainFunction = findViewById(R.id.rv_MainFunction);
        txtValue = findViewById(R.id.txt_Value);
        sbValue = findViewById(R.id.sb_Value);
        sbValue.getProgressDrawable().setColorFilter(getResources().getColor(R.color.my_lighter_primary), PorterDuff.Mode.MULTIPLY);
        sbValue.getThumb().setColorFilter(getResources().getColor(R.color.my_lighter_primary), PorterDuff.Mode.SRC_ATOP);
        sbValue.setProgress(100);
        setSupportActionBar(tbImageProc);
        setTitle("Trở về");
        loadingActionBar();
//        File x = MyConstant.Cascade_Setting(this);
//        mCascadeClassifier = new CascadeClassifier(x.getAbsolutePath());
//        myImageMat = new Mat();
//        faces = new MatOfRect();

        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            imgBitmap = BitmapFactory.decodeStream(is);
            imgMyPhoto.setImageBitmap(imgBitmap);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mainFunctions == null) {
            mainFunctions = new ArrayList<>();
        }
        if (mainFunctions.isEmpty()) {
            MainFunction mainFunction1 = new MainFunction("Mặc định", this.imgBitmap);
            mainFunctions.add(mainFunction1);

            MainFunction mainFunction2 = new MainFunction("Sáng tối", this.imgBitmap);
            mainFunctions.add(mainFunction2);
        }

        mainFunctionAdapter = new MainFunctionAdapter(this, R.layout.layout_mainfunction_item, mainFunctions);
        rvMainFunction.setHasFixedSize(true);
        rvMainFunction.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMainFunction.setAdapter(mainFunctionAdapter);
        Intent iRecivew = getIntent();
    }

    private void loadingActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tbImageProc.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
    }

    private void control() {
//        addrectangle();
        tbImageProc.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sbValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChanged = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "Seekbar progess:" + progressChanged, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addrectangle() {
        mCascadeClassifier = MyConstant.Cascade_Setting_Eye(this);
        myImageMat = new Mat();
        faces = new MatOfRect();
        Utils.bitmapToMat(imgBitmap, myImageMat);
        Imgproc.cvtColor(myImageMat, myImageMat, Imgproc.COLOR_RGBA2GRAY);
        mCascadeClassifier.detectMultiScale(myImageMat, faces, 1.1, 10, 10, new Size(50, 50), new Size());
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(myImageMat, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
        Utils.matToBitmap(myImageMat, imgBitmap);
        imgMyPhoto.setImageBitmap(imgBitmap);

//        Mat imMat = new Mat();
//        Utils.bitmapToMat(imgBitmap, imMat);
//        Imgproc.cvtColor(imMat, imMat, Imgproc.COLOR_RGBA2RGB);
//        Log.d(TAG, "run");
//        Rect rect = new Rect(facesArray[0].tl(), facesArray[0].br());
//        Log.d(TAG, "run1");
//        Mat background = new Mat(imMat.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
//        Log.d(TAG, "run2");
//        Mat firstMask = new Mat();
//        Mat bgModel = new Mat();
//        Mat fgModel = new Mat();
//        Log.d(TAG, "run3");
//        Mat mask;
//        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
//        Log.d(TAG, "run4");
//
//        Mat dst = new Mat();
//        Imgproc.grabCut(imMat, firstMask, rect, bgModel, fgModel, 5, Imgproc.GC_INIT_WITH_RECT);
//        Log.d(TAG, "run5");
//        Core.compare(firstMask, source, firstMask, Core.CMP_EQ);
//
//        Mat foreground = new Mat(imMat.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
//        imMat.copyTo(foreground, firstMask);
//
//        Scalar color = new Scalar(255, 0, 0, 255);
//        Imgproc.rectangle(imMat, facesArray[0].tl(), facesArray[0].br(), color);
//
//        Mat tmp = new Mat();
//        Imgproc.resize(background, tmp, imMat.size());
//        background = tmp;
//        mask = new Mat(foreground.size(), CvType.CV_8UC1,
//                new Scalar(255, 255, 255));
//
//        Imgproc.cvtColor(foreground, mask, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.threshold(mask, mask, 254, 255, Imgproc.THRESH_BINARY_INV);
//        System.out.println();
//        Mat vals = new Mat(1, 1, CvType.CV_8UC3, new Scalar(0.0));
//        background.copyTo(dst);
//
//        background.setTo(vals, mask);
//
//        Core.add(background, foreground, dst, mask);
//        Bitmap grabCutImage = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
//        Bitmap processedImage = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.RGB_565);
//        Utils.matToBitmap(dst, grabCutImage);
////        dst.copyTo(sampleImage);
//        Utils.matToBitmap(imMat, imgBitmap);
//        imgMyPhoto.setImageBitmap(imgBitmap);
//        firstMask.release();
//        source.release();
//        bgModel.release();
//        fgModel.release();
        Log.d(TAG, "end");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mn_Check) {
            Intent iToRatationImage = new Intent(ImageProcessingActivity.this, RotationImageActivity.class);
            startActivity(iToRatationImage);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private int calculateBrightnessEstimate(Bitmap bitmap, int pixelSpacing) {
        int R = 0;
        int G = 0;
        int B = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i += pixelSpacing) {
            int color = pixels[i];
            R += Color.red(color);
            G += Color.green(color);
            B += Color.blue(color);
            n++;
        }
//        return (R + B + G) / (n * 3);
        return (int) (0.2126 * R + 0.7152 * G + 0.0722 * B);
    }

    private Bitmap adjustBrightness(Bitmap src, int value) {
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // increase/decrease each channel
                R += value;
                if (R > 255) {
                    R = 255;
                } else if (R < 0) {
                    R = 0;
                }

                G += value;
                if (G > 255) {
                    G = 255;
                } else if (G < 0) {
                    G = 0;
                }

                B += value;
                if (B > 255) {
                    B = 255;
                } else if (B < 0) {
                    B = 0;
                }

                // apply new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }
}
