package com.example.immortal.passportphoto.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.example.immortal.passportphoto.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class NewImageActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar tbNewImage;
    private ImageButton btnCamera, btnGallery;
    private Uri imageUri;
    private static final int CAM_REQUEST = 1313;
    private static final int PICK_REQUEST = 1212;
    private Bitmap bitmapImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_image);
        init();
        control();
    }

    private void init() {
        btnCamera = findViewById(R.id.btn_Camera);
        btnGallery = findViewById(R.id.btn_Gallery);
        tbNewImage = findViewById(R.id.tb_NewImage);
        setSupportActionBar(tbNewImage);
        setTitle("Ảnh mới");
        loadingActionBar();
    }

    private void loadingActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tbNewImage.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
    }

    private void control() {
        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        tbNewImage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Camera:
                captureNewImage();
                break;
            case R.id.btn_Gallery:
                selectImageFromGallery();
        }

    }

    private void captureNewImage() {
        /*Intent iToImageProc = new Intent(MainActivity.this, ImageProcessingActivity.class);
        startActivity(iToImageProc);*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAM_REQUEST);
    }

    private void selectImageFromGallery() {
        Intent iToGallery = new Intent(Intent.ACTION_PICK);
        File fileInput = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String stringFile = fileInput.getPath();
        Uri data = Uri.parse(stringFile);
        iToGallery.setDataAndType(data, "image/*");
        startActivityForResult(iToGallery, PICK_REQUEST);
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAM_REQUEST) {
            bitmapImage = (Bitmap) data.getExtras().get("data");
            Intent iToRotationImage = new Intent(NewImageActivity.this, RotationImageActivity.class);
            imageUri = getImageUri(getApplicationContext(), bitmapImage);
            iToRotationImage.putExtra("img", imageUri.toString());
            startActivity(iToRotationImage);
        } else if (resultCode == RESULT_OK && requestCode == PICK_REQUEST) {
            imageUri = data.getData();
            Intent iToRotationImage = new Intent(NewImageActivity.this, RotationImageActivity.class);
            iToRotationImage.putExtra("img", imageUri.toString());
            startActivity(iToRotationImage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
