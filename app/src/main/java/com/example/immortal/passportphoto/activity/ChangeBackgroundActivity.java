package com.example.immortal.passportphoto.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.immortal.passportphoto.R;
import com.example.immortal.passportphoto.utils.MyConstant;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ChangeBackgroundActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar tbChangeBackground;
    private ImageButton btnRefresh;
    private ImageView imgImage;
    private TextView txtChangeColor;
    private Bitmap bitmap, bitmapCpy, bitmapForeground;
    private int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_background);
        init();
        controls();
    }

    private void init() {
        tbChangeBackground = findViewById(R.id.tb_ChangeBackground);
        btnRefresh = findViewById(R.id.btn_CBReFresh);
        imgImage = findViewById(R.id.img_CBImage);
        txtChangeColor = findViewById(R.id.txt_CBChangeColor);
        setSupportActionBar(tbChangeBackground);
        setTitle("Đổi màu nền");
        loadingActionBar();
        currentColor = getResources().getColor(R.color.blue);
        try {
            byte[] bytes = getIntent().getByteArrayExtra("image");
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imgImage.setImageBitmap(bitmap);
        bitmapCpy = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
        bitmapForeground = hsvSegmentation(bitmap);
    }

    private void loadingActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tbChangeBackground.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
    }

    private void controls() {
        tbChangeBackground.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        txtChangeColor.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mn_Check) {
            Intent iToSaveImage = new Intent(ChangeBackgroundActivity.this, SaveImageActivity.class);
            iToSaveImage.putExtra("image", MyConstant.bitmapToByteArray(bitmapCpy));
            startActivity(iToSaveImage);
        }
        return true;
    }

    private Bitmap hsvSegmentation(Bitmap src) {
        ArrayList<Float> arrayListH, arrayListS, arrayListV;
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Bitmap mask = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Mat mMask = new Mat();
        arrayListH = new ArrayList<Float>();
        arrayListS = new ArrayList<Float>();
        arrayListV = new ArrayList<Float>();
        int A, R, G, B, pixel, A1, R1, G1, B1, pixel1;
        float currentH, currentS, currentV, H, S, V;
        float[] hsv = new float[3];

        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 1:
                    pixel = src.getPixel(src.getWidth() / 2, 1);
                    break;
                case 2:
                    pixel = src.getPixel(src.getWidth() - 1, 1);
                    break;
                case 3:
                    pixel = src.getPixel(3, src.getHeight() / 2 - 5);
                    break;
                case 4:
                    pixel = src.getPixel(src.getWidth() - 3, src.getHeight() / 2 - 5);
                    break;
                default:
                    pixel = src.getPixel(1, 1);
                    break;
            }
            R = Color.red(pixel);
            G = Color.green(pixel);
            B = Color.blue(pixel);

            Color.RGBToHSV(R, G, B, hsv);

//            currentH = hsv[0];
            arrayListH.add(hsv[0]);
            arrayListS.add(hsv[1]);
            arrayListV.add(hsv[2]);
        }
        Collections.sort(arrayListH);
        Collections.sort(arrayListS);
        Collections.sort(arrayListV);

        currentH = arrayListH.get(2);
        currentS = arrayListS.get(2);
        currentV = arrayListV.get(2);

        for (int i = 0; i < src.getWidth(); i++) {
            for (int j = 0; j < src.getHeight(); j++) {
                pixel = src.getPixel(i, j);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                Color.RGBToHSV(R, G, B, hsv);
                H = hsv[0];
                S = hsv[1];
                V = hsv[2];

                result.setPixel(i, j, Color.argb(A, R, G, B));
                if (H >= currentH - 15 && H <= currentH + 15 && S >= currentS - 40f/ 100f && S <= currentS + 40f / 100f
                        && V >= currentV - 40f / 100f && V <= currentV + 40f / 100f) {
                    //background
//     hsv[0] = hsv[1] = hsv[2] = 0;
                    mask.setPixel(i, j, Color.argb(0, 0, 0, 0));
                } else {
                    //foreground
                    A = 255;
                    G = R = B = 255;
                    mask.setPixel(i, j, Color.argb(A, R, G, B));
                }

            }
        }

        Utils.bitmapToMat(mask, mMask);
        Imgproc.erode(mMask, mMask, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
        Imgproc.dilate(mMask, mMask, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6, 6)));
        Imgproc.erode(mMask, mMask, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
        Imgproc.GaussianBlur(mMask, mMask, new Size(3, 3), 1 / 273);
        Utils.matToBitmap(mMask, mask);
        for (int i = 0; i < src.getWidth(); i++) {
            for (int j = 0; j < src.getHeight(); j++) {
                pixel = mask.getPixel(i, j);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);


                pixel1 = src.getPixel(i, j);
                A1 = Color.alpha(pixel1);
                R1 = Color.red(pixel1);
                G1 = Color.green(pixel1);
                B1 = Color.blue(pixel1);

                A1 = (int) (A / 255f) * A1;
                R1 = (int) (R / 255f) * R1;
                G1 = (int) (G / 255f) * G1;
                B1 = (int) (B / 255f) * B1;

                result.setPixel(i, j, Color.argb(A1, R1, G1, B1));
            }
        }
        return result;
    }

    private void openDialog(boolean supportsAlpha) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentColor, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentColor = color;
                txtChangeColor.setTextColor(color);
                bitmapCpy = changeBackground(bitmap, color);
                imgImage.setImageBitmap(bitmapCpy);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });
        dialog.show();
    }

    private Bitmap changeBackground(Bitmap src, int color) {
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawColor(color);
        canvas.drawBitmap(bitmapForeground, 0, 0, null);
        return result;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_CBChangeColor:
                openDialog(true);
                break;
            case R.id.btn_CBReFresh:
                bitmapCpy = bitmap;
                imgImage.setImageBitmap(bitmapCpy);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
