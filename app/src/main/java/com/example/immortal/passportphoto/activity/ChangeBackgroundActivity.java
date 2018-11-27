package com.example.immortal.passportphoto.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.immortal.passportphoto.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ChangeBackgroundActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar tbChangeBackground;
    private ImageButton btnRefresh;
    private ImageView imgImage;
    private TextView txtChangeColor;
    private Bitmap bitmap, bitmapCpy, bitmapForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_background);
        init();
        controls();
    }

    private void init() {
        tbChangeBackground = findViewById(R.id.tb_ChangeBackground);
        btnRefresh = findViewById(R.id.btn_CBFresh);
        imgImage = findViewById(R.id.img_CBImage);
        txtChangeColor = findViewById(R.id.txt_CBChangeColor);
        setSupportActionBar(tbChangeBackground);
        setTitle("Đổi màu nền");
        loadingActionBar();

        try {
            byte[] bytes = getIntent().getByteArrayExtra("image");
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imgImage.setImageBitmap(bitmap);
        bitmapCpy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        bitmapForeground = hsvSegmentation(bitmap);
    }

    private void loadingActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tbChangeBackground.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
    }

    private void controls() {
        txtChangeColor.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_save, menu);
        return true;
    }

    private Bitmap hsvSegmentation(Bitmap src) {
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Bitmap bBlur = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Mat mBlur = new Mat();
        int A, R, G, B, pixel;
        float currentH, H;
        float[] hsv = new float[3];

        pixel = src.getPixel(0, 0);
        A = Color.alpha(pixel);
        R = Color.red(pixel);
        G = Color.green(pixel);
        B = Color.blue(pixel);

        Color.RGBToHSV(R, G, B, hsv);

        currentH = hsv[0];
        for (int i = 0; i < src.getWidth(); i++) {
            for (int j = 0; j < src.getHeight(); j++) {
                pixel = src.getPixel(i, j);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                Color.RGBToHSV(R, G, B, hsv);

                H = hsv[0];

//                if (H == currentH) {
//                    hsv[0] = hsv[1] = hsv[2] = 0;
//                }
//                else
                if (H >= currentH - 10 && H <= currentH + 10) {
                    hsv[0] = hsv[1] = hsv[2] = 0;
                    A = 0;

                }
                result.setPixel(i, j, Color.HSVToColor(A, hsv));
            }
        }
        List<MatOfPoint> contours = findBitmapCotour(src);

        Utils.bitmapToMat(result, mBlur);
        Imgproc.GaussianBlur(mBlur, mBlur, new Size(5, 5), 0);
        Utils.matToBitmap(mBlur, bBlur);
//        Imgproc.blur(mBlur, mBlur, new Size(3,3));
//        Imgproc.medianBlur(mBlur, mBlur, 3);
//        Utils.matToBitmap(mBlur, result);
        for (int i = 0; i < contours.size(); i++){
            for (int j = 0; j < contours.get(i).toArray().length; j++){
                pixel = bBlur.getPixel((int) contours.get(i).toArray()[j].x, (int) contours.get(i).toArray()[j].y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                result.setPixel((int) contours.get(i).toArray()[j].x, (int) contours.get(i).toArray()[j].y, Color.argb(A, R, G, B));
            }
        }
        return result;
    }

    private List<MatOfPoint> findBitmapCotour(Bitmap src) {
        Mat mGray = new Mat();
        Mat cannyOutput = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Utils.bitmapToMat(src, mGray);
        Imgproc.cvtColor(mGray, mGray, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.blur(mGray, mGray, new Size(3, 3));
        Imgproc.Canny(mGray, cannyOutput, 50, 200);

        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

//        Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
//        for (int i = 0; i < contours.size(); i++) {
//            Scalar color = new Scalar(0, 255, 0);
//            Imgproc.drawContours(drawing, contours, i, color, 2, Core.LINE_8, hierarchy, 0, new Point());
//        }

//        Utils.matToBitmap(drawing, result);
        return contours;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_CBChangeColor:
                Canvas canvas = new Canvas(bitmapCpy);
                canvas.drawColor(Color.argb(255, 0, 255, 0));
                canvas.drawBitmap(bitmapForeground, 0, 0, null);

                imgImage.setImageBitmap(bitmapCpy);
        }
    }
}
