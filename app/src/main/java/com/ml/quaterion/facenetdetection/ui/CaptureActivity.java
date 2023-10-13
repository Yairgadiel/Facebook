package com.ml.quaterion.facenetdetection.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ml.quaterion.facenetdetection.MainActivity;
import com.ml.quaterion.facenetdetection.R;
import com.ml.quaterion.facenetdetection.databinding.ActivityCaptureBinding;
import com.ml.quaterion.facenetdetection.databinding.ActivityMainBinding;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CaptureActivity extends AppCompatActivity {

    private ArrayList<Bitmap> _bitmapsToDisplay = new ArrayList<>();


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_FROM_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCaptureBinding activityCaptureBinding = ActivityCaptureBinding.inflate(getLayoutInflater());
        setContentView(activityCaptureBinding.getRoot());

        PredicationsAdapter adapter = new PredicationsAdapter();
        adapter.setOnItemClickListener(prediction -> Toast.makeText(CaptureActivity.this, prediction.getLabel(), Toast.LENGTH_SHORT).show());

        activityCaptureBinding.predicationsRv.setAdapter(adapter);
        activityCaptureBinding.predicationsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        activityCaptureBinding.captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent object with the ACTION_IMAGE_CAPTURE action.
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Add the EXTRA_OUTPUT extra to the Intent object, specifying the URI where you want the photo to be saved.
                mPhotoUri = ContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);

                // Start the camera app by calling startActivityForResult().
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check the result code to see if the photo was captured successfully.
        if (resultCode == RESULT_OK) {
            if (requestCode== REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_IMAGE_FROM_STORAGE) {
                // Get the photo URI from the data Intent object.
                Uri imageUri = (Uri) data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                }
                catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
                // Display the photo in an ImageView.
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageURI(mPhotoUri);
            }
        }
    }
}}