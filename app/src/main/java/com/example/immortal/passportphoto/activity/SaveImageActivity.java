package com.example.immortal.passportphoto.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.immortal.passportphoto.R;
import com.example.immortal.passportphoto.asynctask.SaveImageAsyncTask;
import com.example.immortal.passportphoto.utils.MyConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveImageActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar tbSavaImage;
    private ImageView imgImage;
    private TextView txtQuantity, txtSize;
    private ImageButton btnDecrease, btnIncrease, btnSelectPaperSize, btnRotatePaper;
    private Bitmap bitmap;
    private int paperWidth, paperHeight;
    private int max, quantity, gap;
    private Bitmap bitmapOut;
    private int backgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);
        init();
        controls();
    }

    private void init() {
        tbSavaImage = findViewById(R.id.tb_SaveImage);
        imgImage = findViewById(R.id.img_SIImage);
        txtQuantity = findViewById(R.id.txt_Quantity);
        txtSize = findViewById(R.id.txt_SISize);
        btnDecrease = findViewById(R.id.btn_Decrease);
        btnIncrease = findViewById(R.id.btn_Increase);
        btnSelectPaperSize = findViewById(R.id.btn_SelectPaperSize);
        btnRotatePaper = findViewById(R.id.btn_RotatePaper);
        setSupportActionBar(tbSavaImage);
        setTitle("Lưu ảnh");
        loadingActionBar();
        paperWidth = 98;
        paperHeight = 152;
        gap = 5;
        backgroundColor = getResources().getColor(R.color.my_white);
        txtSize.setText(String.valueOf(paperWidth) + " x " + String.valueOf(paperHeight) + " cm");
        try {
            byte[] bytes = getIntent().getByteArrayExtra("image");
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        quantity = max = calculateMaxQuantity(bitmap, paperWidth, paperHeight, 5);
        setQuantityText(quantity, max);
        bitmapOut = drawImages(bitmap, paperWidth, paperHeight, quantity, max, gap);
        Log.d("Today", "img width" + String.valueOf(bitmap.getWidth()));
        Log.d("Today", "img height" + String.valueOf(bitmap.getHeight()));
        Log.d("Today", "quantity: " + String.valueOf(quantity) + " -------- " + "max: " + String.valueOf(max));
        imgImage.setImageBitmap(bitmapOut);

    }

    private void controls() {
        tbSavaImage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        txtSize.setOnClickListener(this);
        btnDecrease.setOnClickListener(this);
        btnIncrease.setOnClickListener(this);
        btnSelectPaperSize.setOnClickListener(this);
        btnRotatePaper.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mn_Save) {
            Luu();
        }
        return true;
    }

    private void loadingActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tbSavaImage.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
    }

    private void selectPaperSize() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View viewDial = inflater.inflate(R.layout.layout_select_paper_size, null);
        builder.setView(viewDial);

        final RadioGroup rgSelect;
        rgSelect = viewDial.findViewById(R.id.rg_PaperSelect);
        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (rgSelect.getCheckedRadioButtonId()) {
                    case R.id.btn_Cp1:
                        paperWidth = 98;
                        paperHeight = 152;
                        break;
                    case R.id.btn_Cp2:
                        paperWidth = 127;
                        paperHeight = 178;
                        break;
                    case R.id.btn_Cp3:
                        paperWidth = 152;
                        paperHeight = 210;
                        break;
                    case R.id.btn_Cp4Cp5:
                        paperWidth = 200;
                        paperHeight = 254;
                        break;
                    case R.id.btn_Cp6:
                        paperWidth = 198;
                        paperHeight = 305;
                        break;
                    case R.id.btn_Cp10:
                        paperWidth = 254;
                        paperHeight = 373;
                        break;
                    case R.id.btn_Cp12:
                        paperWidth = 305;
                        paperHeight = 444;
                        break;
                    default:
                        paperWidth = 98;
                        paperHeight = 152;

                }

                dialogInterface.dismiss();

                Toast.makeText(SaveImageActivity.this, "Width: " + String.valueOf(MyConstant.mmToPx(paperWidth)) + " ---- Height: " + String.valueOf(MyConstant.mmToPx(paperHeight)), Toast.LENGTH_SHORT).show();
                Log.d("Today", "Width: " + String.valueOf(MyConstant.mmToPx(paperWidth)) + " ---- Height: " + String.valueOf(MyConstant.mmToPx(paperHeight)));
                txtSize.setText(String.valueOf(paperWidth) + " x " + String.valueOf(paperHeight) + " mm");
                quantity = max = calculateMaxQuantity(bitmap, paperWidth, paperHeight, gap);
                setQuantityText(quantity, max);
                bitmapOut = drawImages(bitmap, paperWidth, paperHeight, quantity, max, gap);
                imgImage.setImageBitmap(bitmapOut);
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void setQuantityText(int quantity, int max) {
        txtQuantity.setText(String.valueOf(quantity) + "/" + String.valueOf(max));
    }

    private Bitmap drawImages(Bitmap src, int paperWidth, int paperHeight, int quantity, int max, int gap) {
        int x, y;
        paperWidth = MyConstant.mmToPx(paperWidth);
        paperHeight = MyConstant.mmToPx(paperHeight);
        x = 0 + gap;
        y = 0 + gap;
        Bitmap result = Bitmap.createBitmap(paperWidth, paperHeight, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawColor(backgroundColor);
        for (int i = 0; i < quantity; i++) {
            canvas.drawBitmap(src, x, y, null);
            x = x + src.getWidth() + gap;
            if (x > paperWidth - src.getWidth()) {
                x = 0 + gap;
                y = y + src.getHeight() + gap;
            }
        }
//        do {
//            canvas.drawBitmap(src, x, y, null);
//            x = x + src.getWidth() + gap;
//            if (x >= paperWidth - gap - src.getWidth()) {
//                x = 0 + gap;
//                y = y + src.getHeight() + gap;
//            }
//        }
//        while (x <= paperWidth - src.getWidth() - gap && y <= paperHeight - src.getHeight() - gap);

        return result;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_SISize:
                selectPaperSize();
                break;
            case R.id.btn_Decrease:
                deCreaseImage();
                break;
            case R.id.btn_Increase:
                inCreaseImage();
                break;
            case R.id.btn_SelectPaperSize:
                selectPaperSize();
                break;
            case R.id.btn_RotatePaper:
                rotatePaper();
                break;
        }
    }

    private int calculateMaxQuantity(Bitmap src, int paperWidth, int paperHeight, int gap) {
//        int x, y, max;
        int row, col;
        row = col = 0;
        paperWidth = MyConstant.mmToPx(paperWidth);
        paperHeight = MyConstant.mmToPx(paperHeight);
//        x = 0 + gap;
//        y = 0 + gap;
//        max = 0;
//        do {
//            max = max + 1;
//            x = x + src.getWidth() + gap;
//            if (x >= paperWidth - gap - src.getWidth()) {
//                x = 0 + gap;
//                y = y + src.getHeight() + gap;
//            }
//        }
//        while (x <= paperWidth - src.getWidth() - gap && y <= paperHeight - src.getHeight() - gap);
        col = Math.round(paperWidth / (src.getWidth() + gap));
        row = Math.round(paperHeight / (src.getHeight() + gap));
        return col * row;
    }

    private void deCreaseImage() {
        if (this.quantity > 1) {
            this.quantity = this.quantity - 1;
            bitmapOut = drawImages(bitmap, paperWidth, paperHeight, this.quantity, max, gap);
            txtQuantity.setText(String.valueOf(quantity) + "/" + String.valueOf(max));
            imgImage.setImageBitmap(bitmapOut);
        }
        Log.d("Today", "quantity: " + quantity);
        Log.d("Today", "Decrease");
    }

    private void inCreaseImage() {
        if (quantity < max) {
            quantity = quantity + 1;
            bitmapOut = drawImages(bitmap, paperWidth, paperHeight, quantity, max, gap);
            txtQuantity.setText(String.valueOf(quantity) + "/" + String.valueOf(max));
            imgImage.setImageBitmap(bitmapOut);
        }
    }

    private void rotatePaper() {
        int temp;
        temp = this.paperWidth;
        paperWidth = paperHeight;
        paperHeight = temp;
        quantity = max = calculateMaxQuantity(bitmap, paperWidth, paperHeight, gap);
        bitmapOut = drawImages(bitmap, paperWidth, paperHeight, quantity, max, gap);
        txtQuantity.setText(String.valueOf(quantity) + "/" + String.valueOf(max));
        imgImage.setImageBitmap(bitmapOut);
    }

    //Phuong thuc luu anh
    public void Luu() {
        File filepath = Environment.getExternalStorageDirectory();
        File myDir = new File(filepath.getAbsolutePath() + "/MyPhoto_Id/");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String date = simpleDateFormat.format(new Date());
        String fname = "Image-" + date + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();

        SaveImageAsyncTask asyncTask = new SaveImageAsyncTask(this, file);
        asyncTask.execute(bitmapOut);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
