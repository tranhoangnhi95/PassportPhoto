package com.example.immortal.passportphoto.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.immortal.passportphoto.R;
import com.example.immortal.passportphoto.adapter.MainMenuAdapter;
import com.example.immortal.passportphoto.model.MainMenu;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnNewImage, btnOldImage;
    private Uri imageUri;
    private static final int CAM_REQUEST = 1313;
    private static final int PICK_REQUEST = 1212;
    private Bitmap bitmapImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        control();
    }

    private void init() {
        btnNewImage = findViewById(R.id.btn_NewImage);
        btnOldImage = findViewById(R.id.btn_OldImage);
    }

    private void control() {
        btnNewImage.setOnClickListener(this);
        btnOldImage.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_NewImage:
                captureNewImage();
                break;
            case R.id.btn_OldImage:
                selectImageFromGallery();
                break;
        }
    }


    private void captureNewImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAM_REQUEST);

//        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (photoFile != null){
//                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.provider",photoFile);
//
//            }
//            startActivityForResult(pictureIntent, CAM_REQUEST);
//        }

    }

    private void selectImageFromGallery() {
        Intent iToGallery = new Intent(Intent.ACTION_PICK);
        File fileInput = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String stringFile = fileInput.getPath();
        Uri data = Uri.parse(stringFile);
        iToGallery.setDataAndType(data, "image/*");
        startActivityForResult(iToGallery, PICK_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent iToRotationImage = new Intent(MainActivity.this, RotationImageActivity.class);
        if (resultCode == RESULT_OK && requestCode == CAM_REQUEST) {
//            bitmapImage = (Bitmap) data.getExtras().get("data");
//
//            imageUri = getImageUri(getApplicationContext(), bitmapImage);
//            iToRotationImage.putExtra("img", imageUri.toString());
//            startActivity(iToRotationImage);


        } else if (resultCode == RESULT_OK && requestCode == PICK_REQUEST) {
            imageUri = data.getData();
            iToRotationImage.putExtra("img", imageUri.toString());
            startActivity(iToRotationImage);
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


//    private File createImageFile() throws IOException {
//        String timeStamp =
//                new SimpleDateFormat("yyyyMMdd_HHmmss",
//                        Locale.getDefault()).format(new Date());
//        String imageFileName = "IMG_" + timeStamp + "_";
//        File storageDir =
//                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        imageFilePath = image.getAbsolutePath();
//        return image;
//    }
}
