package com.example.immortal.passportphoto.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.immortal.passportphoto.R;

public class SaveImageActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar tbSavaImage;
    private ImageView imgImage;
    private TextView txtQuantity, txtSize;
    private Bitmap bitmap;
    private int paperWidth, paperHeight;
    private int max, quantity;

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
        setSupportActionBar(tbSavaImage);
        setTitle("Lưu ảnh");
        loadingActionBar();
        paperWidth = 98;
        paperHeight = 152;
        txtSize.setText(String.valueOf(paperWidth) + " x " + String.valueOf(paperHeight) + " cm");
        try {
            byte[] bytes = getIntent().getByteArrayExtra("image");
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imgImage.setImageBitmap(bitmap);

    }

    private void controls() {
        txtSize.setOnClickListener(this);
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

                Toast.makeText(SaveImageActivity.this, "Width: " + String.valueOf(paperWidth) + " ---- Height: " + String.valueOf(paperHeight), Toast.LENGTH_SHORT).show();
                txtSize.setText(String.valueOf(paperWidth) + " x " + String.valueOf(paperHeight) + " mm");
                txtQuantity.setText(String.valueOf(calculateMaxQuantity(paperWidth, paperHeight, bitmap.getWidth(), bitmap.getHeight(), 10)));
                ;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_SISize:
                selectPaperSize();
                break;
        }
    }

    private int calculateMaxQuantity(int paperWidth, int paperHeight, int bitmapWidth, int bitmapHeight, int gap) {
        int col, row, pWidthPixel, pHeightPixel;
        pWidthPixel = Math.round(paperWidth * CropImageActivity.mm2pxConst);
        pHeightPixel = Math.round(paperHeight * CropImageActivity.mm2pxConst);
        Log.d("Today", "Paper1: " + String.valueOf(paperWidth) + "------ " + String.valueOf(paperHeight));
        Log.d("Today", "Paper: " + String.valueOf(pWidthPixel) + "------ " + String.valueOf(pHeightPixel));
        Log.d("Today", "Bitmap: " + String.valueOf(bitmapWidth) + "------ " + String.valueOf(bitmapHeight));
        col = pWidthPixel / (bitmapWidth + gap);
        row = pHeightPixel / (bitmapHeight + gap);
        Log.d("Today", "Max: " + String.valueOf(col * row));
        return col * row;
    }
}
