package com.example.immortal.passportphoto.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.immortal.passportphoto.R;
import com.example.immortal.passportphoto.adapter.MainFunctionAdapter;
import com.example.immortal.passportphoto.model.MainFunction;
import com.example.immortal.passportphoto.utils.MyConstant;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.FileInputStream;
import java.util.ArrayList;

public class ImageProcessingActivity extends AppCompatActivity {

    private static double DELTA_INDEX[] = {
            0, 0.01, 0.02, 0.04, 0.05, 0.06, 0.07, 0.08, 0.1, 0.11,
            0.12, 0.14, 0.15, 0.16, 0.17, 0.18, 0.20, 0.21, 0.22, 0.24,
            0.25, 0.27, 0.28, 0.30, 0.32, 0.34, 0.36, 0.38, 0.40, 0.42,
            0.44, 0.46, 0.48, 0.5, 0.53, 0.56, 0.59, 0.62, 0.65, 0.68,
            0.71, 0.74, 0.77, 0.80, 0.83, 0.86, 0.89, 0.92, 0.95, 0.98,
            1.0, 1.06, 1.12, 1.18, 1.24, 1.30, 1.36, 1.42, 1.48, 1.54,
            1.60, 1.66, 1.72, 1.78, 1.84, 1.90, 1.96, 2.0, 2.12, 2.25,
            2.37, 2.50, 2.62, 2.75, 2.87, 3.0, 3.2, 3.4, 3.6, 3.8,
            4.0, 4.3, 4.7, 4.9, 5.0, 5.5, 6.0, 6.5, 6.8, 7.0,
            7.3, 7.5, 7.8, 8.0, 8.4, 8.7, 9.0, 9.4, 9.6, 9.8,
            10.0
    };

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
    private SeekBar sbBrightness, sbContrast, sbSaturation, sbTemperature;
    private RecyclerView rvMainFunction;
    private MainFunctionAdapter mainFunctionAdapter;
    private ArrayList<MainFunction> mainFunctions;
    private Bitmap imgBitmap, myImageBitmap;
    private TextView txtValue;
    private CascadeClassifier mCascadeClassifier;
    private MatOfRect faces;
    private Mat myImageMat;
    private int brightness, contrast, saturation, temperature;

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
        sbBrightness = findViewById(R.id.sb_Brightness);
        sbContrast = findViewById(R.id.sb_Contrast);
        sbSaturation = findViewById(R.id.sb_Saturation);
        sbTemperature = findViewById(R.id.sb_Temperature);
        changeSeekBarColor();
        setSupportActionBar(tbImageProc);
        setTitle("Chỉnh sửa");
        loadingActionBar();
        brightness = contrast = saturation = temperature = 0;
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            imgBitmap = BitmapFactory.decodeStream(is);
            myImageBitmap = Bitmap.createBitmap(imgBitmap, 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight());
            imgMyPhoto.setImageBitmap(imgBitmap);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mainFunctions == null) {
            mainFunctions = new ArrayList<>();
        }
        if (mainFunctions.isEmpty()) {
            MainFunction mainFunction1 = new MainFunction("Đặt lại", this.imgBitmap);
            mainFunctions.add(mainFunction1);

            MainFunction mainFunction2 = new MainFunction("Tự động", applyAdjust(imgBitmap, 10, 10, 10, 0));
            mainFunctions.add(mainFunction2);

            MainFunction mainFunction3 = new MainFunction("Sáng tối", adjustBrightness(imgBitmap, 10));
            mainFunctions.add(mainFunction3);

            MainFunction mainFunction4 = new MainFunction("Tương phản", adjustContrast(imgBitmap, 10));
            mainFunctions.add(mainFunction4);

            MainFunction mainFunction5 = new MainFunction("Bão hòa", adjustSaturation(imgBitmap, 10));
            mainFunctions.add(mainFunction5);

            MainFunction mainFunction6 = new MainFunction("Nhiệt độ", adjustTemperature(imgBitmap, 20));
            mainFunctions.add(mainFunction6);

        }
        mainFunctionAdapter = new MainFunctionAdapter(this, R.layout.layout_mainfunction_item, mainFunctions, sbBrightness, sbContrast, sbSaturation, sbTemperature, txtValue);
        rvMainFunction.setHasFixedSize(true);
        rvMainFunction.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMainFunction.setAdapter(mainFunctionAdapter);
    }

    private void loadingActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tbImageProc.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
    }

    private void control() {
        tbImageProc.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                brightness = i - 100;
                myImageBitmap = applyAdjust(imgBitmap, brightness, contrast, saturation, temperature);
                imgMyPhoto.setImageBitmap(myImageBitmap);
                txtValue.setText(String.valueOf(brightness));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sbContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                contrast = i - 100;
                myImageBitmap = applyAdjust(imgBitmap, brightness, contrast, saturation, temperature);
                imgMyPhoto.setImageBitmap(myImageBitmap);
                txtValue.setText(String.valueOf(contrast));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sbSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                saturation = i - 100;
                myImageBitmap = applyAdjust(imgBitmap, brightness, contrast, saturation, temperature);
                imgMyPhoto.setImageBitmap(myImageBitmap);
                txtValue.setText(String.valueOf(saturation));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sbTemperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                temperature = i - 100;
                myImageBitmap = applyAdjust(imgBitmap, brightness, contrast, saturation, temperature);
                imgMyPhoto.setImageBitmap(myImageBitmap);
                txtValue.setText(String.valueOf(temperature));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mn_Check) {
            Intent iToChangebackground = new Intent(ImageProcessingActivity.this, ChangeBackgroundActivity.class);
            iToChangebackground.putExtra("image", MyConstant.bitmapToByteArray(myImageBitmap));
            startActivity(iToChangebackground);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Bitmap adjustBrightness(Bitmap src, int value) {
        if (value == 0) {
            return src;
        }
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
        return bmOut;
    }


    protected float cleanValue(float p_val, float p_limit) {
        return Math.min(p_limit, Math.max(-p_limit, p_val));
    }

    public Bitmap adjustContrast(Bitmap src, float value) {
        value = value / 4;
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        ColorMatrix cm = new ColorMatrix();
        value = (int) cleanValue(value, 100);
        if (value == 0) {
            return src;
        }
        float x;
        if (value < 0) {
            x = 127 + (float) value / 100 * 127;
        } else {
            x = value % 1;
            if (x == 0) {
                x = (float) DELTA_INDEX[(int) value];
            } else {
                //x = DELTA_INDEX[(p_val<<0)]; // this is how the IDE does it.
                x = (float) DELTA_INDEX[((int) value << 0)] * (1 - x) + (float) DELTA_INDEX[((int) value << 0) + 1] * x; // use linear interpolation for more granularity.
            }
            x = x * 127 + 127;
        }

        float[] mat = new float[]
                {
                        x / 127, 0, 0, 0, 0.5f * (127 - x),
                        0, x / 127, 0, 0, 0.5f * (127 - x),
                        0, 0, x / 127, 0, 0.5f * (127 - x),
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                };
        cm.postConcat(new ColorMatrix(mat));

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setColorFilter(filter);
        canvas.drawBitmap(src, 0, 0, paint);
        return result;
    }

    public Bitmap adjustSaturation(Bitmap src, float value) {
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        ColorMatrix cm = new ColorMatrix();
        value = (value + 100) / 100;

        cm.setSaturation(value);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setColorFilter(filter);
        canvas.drawBitmap(src, 0, 0, paint);
        return result;
    }

    public Bitmap adjustTemperature(Bitmap src, float value) {
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        int temperature = Math.round(value/5);
        int pixel, A, R, G, B;
        for (int i = 0; i < src.getWidth(); i++) {
            for (int j = 0; j < src.getHeight(); j++) {
                pixel = src.getPixel(i, j);

                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                R = R + temperature;
                B = B - temperature;

                if (R > 255){
                    R = 255;
                }else if (R < 0){
                    R = 0;
                }
                if (B > 255){
                    B = 255;
                }else if (B < 0){
                    B = 0;
                }

                result.setPixel(i, j, Color.argb(A, R, G, B));
            }
        }
        return result;
    }

    private Bitmap applyAdjust(Bitmap src, int brightness, int contrast, int saturation, int temperature) {
        Bitmap result;
        result = adjustBrightness(src, brightness);
        result = adjustContrast(result, contrast);
        result = adjustSaturation(result, saturation);
        result = adjustTemperature(result, temperature);
        return result;
    }

    private void changeSeekBarColor() {
        sbBrightness.getProgressDrawable().setColorFilter(getResources().getColor(R.color.my_lighter_primary), PorterDuff.Mode.MULTIPLY);
        sbBrightness.getThumb().setColorFilter(getResources().getColor(R.color.my_lighter_primary), PorterDuff.Mode.SRC_ATOP);

        sbContrast.getProgressDrawable().setColorFilter(getResources().getColor(R.color.my_lighter_primary), PorterDuff.Mode.MULTIPLY);
        sbContrast.getThumb().setColorFilter(getResources().getColor(R.color.my_lighter_primary), PorterDuff.Mode.SRC_ATOP);

        sbSaturation.getProgressDrawable().setColorFilter(getResources().getColor(R.color.my_lighter_primary), PorterDuff.Mode.MULTIPLY);
        sbSaturation.getThumb().setColorFilter(getResources().getColor(R.color.my_lighter_primary), PorterDuff.Mode.SRC_ATOP);

        sbTemperature.getProgressDrawable().setColorFilter(getResources().getColor(R.color.my_lighter_primary), PorterDuff.Mode.MULTIPLY);
        sbTemperature.getThumb().setColorFilter(getResources().getColor(R.color.my_lighter_primary), PorterDuff.Mode.SRC_ATOP);
    }


}
