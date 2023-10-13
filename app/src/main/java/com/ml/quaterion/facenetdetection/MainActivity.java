/*
 * Copyright 2023 Shubham Panchal
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ml.quaterion.facenetdetection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.ml.quaterion.facenetdetection.databinding.ActivityMainBinding;
import com.ml.quaterion.facenetdetection.model.FaceNetModel;
import com.ml.quaterion.facenetdetection.model.ModelInfo;
import com.ml.quaterion.facenetdetection.model.Models;
import com.ml.quaterion.facenetdetection.ui.PredicationsAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_FILES_PERM_KEY = 3535;

    private boolean isSerializedDataStored = false;

    // Serialized data will be stored (in the app's private storage) with this filename.
    private final String SERIALIZED_DATA_FILENAME = "image_data";

    // Shared Pref key to check if the data was stored.
    private final String SHARED_PREF_IS_DATA_STORED_KEY = "is_data_stored";

    private ActivityMainBinding activityMainBinding;
    private PreviewView previewView;
    private FrameAnalyser frameAnalyser;
    private FaceNetModel faceNetModel;
    private FileReader fileReader;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private SharedPreferences sharedPreferences;

    // <----------------------- User controls --------------------------->

    // Use the device's GPU to perform faster computations.
    // Refer https://www.tensorflow.org/lite/performance/gpu
    private final boolean useGpu = true;

    // Use XNNPack to accelerate inference.
    // Refer https://blog.tensorflow.org/2020/07/accelerating-tensorflow-lite-xnnpack-integration.html
    private final boolean useXNNPack = true;

    // You may change the models here.
    // Use the model configs in Models.java
    // Default is Models.FACENET; Quantized models are faster
    private final ModelInfo modelInfo = Models.Companion.getFACENET();

    // Camera Facing
    private final Integer cameraFacing = CameraSelector.LENS_FACING_BACK;

    // <---------------------------------------------------------------->

    public static TextView logTextView;

    public static void setMessage(String message) {
        logTextView.setText(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // You already have permission to access external storage.
                loadApp();
            } else {
                // Request permission to access external storage.
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_FILES_PERM_KEY);
            }
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == 3501) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                loadApp();
//            }
//            else {
//                ActivityCompat.requestPermissions(MainActivity.this, permissions, 3501);
//                Log.d("Log", "permission not granted.");
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FILES_PERM_KEY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission granted, you can access external storage now.
                    loadApp();
                } else {
                    // Permission denied, handle accordingly.
                    Log.d("Log", "no storage permissions");
                }
            }
        }
    }


    public void loadApp() {
        // Remove the status bar to have a full screen experience
        // See this answer on SO -> https://stackoverflow.com/a/68152688/10878733
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getDecorView().getWindowInsetsController()
                    .hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        previewView = activityMainBinding.previewView;
        logTextView = activityMainBinding.logTextview;
        logTextView.setMovementMethod(new ScrollingMovementMethod());
        // Necessary to keep the Overlay above the PreviewView so that the boxes are visible.
        BoundingBoxOverlay boundingBoxOverlay = activityMainBinding.bboxOverlay;
        boundingBoxOverlay.setCameraFacing(cameraFacing);
        boundingBoxOverlay.setWillNotDraw(false);
        boundingBoxOverlay.setZOrderOnTop(true);

        PredicationsAdapter adapter = new PredicationsAdapter();
        adapter.setOnItemClickListener(prediction -> {
            Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
            intent.putExtra(PersonActivity.PERSON_FOLDER_PATH_KEY, prediction.getLabel());
            intent.putExtra(PersonActivity.PERSON_IMAGE_PATH_KEY, prediction.getImage());
            startActivity(intent);

            Toast.makeText(MainActivity.this, prediction.getLabel(), Toast.LENGTH_SHORT).show();
        });

        Utils.setContext(this);
        activityMainBinding.predicationsRv.setAdapter(adapter);
        activityMainBinding.predicationsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        faceNetModel = new FaceNetModel(this, modelInfo, useGpu, useXNNPack);
        frameAnalyser = new FrameAnalyser(this, boundingBoxOverlay, faceNetModel, adapter);
        fileReader = new FileReader(faceNetModel);

        // We'll only require the CAMERA permission from the user.
        // For scoped storage, particularly for accessing documents, we won't require WRITE_EXTERNAL_STORAGE or
        // READ_EXTERNAL_STORAGE permissions. See https://developer.android.com/training/data-storage
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            startCameraPreview();
        }

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        isSerializedDataStored = sharedPreferences.getBoolean(SHARED_PREF_IS_DATA_STORED_KEY, false);
        if (!isSerializedDataStored) {
            Logger.Companion.log("No serialized data was found. Select the images directory.");
            showSelectDirectoryDialog();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Serialized Data")
                    .setMessage("Existing image data was found on this device. Would you like to load it?")
                    .setCancelable(false)
                    .setNegativeButton("LOAD", (dialog, which) -> {
                        dialog.dismiss();
                        frameAnalyser.setFaceList(loadSerializedImageData());
                        Logger.Companion.log("Serialized data loaded.");
                    })
                    .setPositiveButton("RESCAN", (dialog, which) -> {
                        dialog.dismiss();
                        launchChooseDirectoryIntent();
                    })
                    .create();
            alertDialog.show();
        }
    }

    // Attach the camera stream to the PreviewView.
    private void startCameraPreview() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            bindPreview(cameraProvider);
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraFacing)
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageAnalysis imageFrameAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(480, 640))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build();
        imageFrameAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), frameAnalyser);
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageFrameAnalysis);
    }

    // We let the system handle the requestCode. This doesn't require onRequestPermissionsResult and
    // hence makes the code cleaner.
    private void requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    private final ActivityResultLauncher<String> cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    startCameraPreview();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle("Camera Permission")
                            .setMessage("The app couldn't function without the camera permission.")
                            .setCancelable(false)
                            .setPositiveButton("ALLOW", (dialog, which) -> {
                                dialog.dismiss();
                                requestCameraPermission();
                            })
                            .setNegativeButton("CLOSE", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            })
                            .create();
                    alertDialog.show();
                }
            }
    );

    // Open File chooser to choose the images directory.
    private void showSelectDirectoryDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Select Images Directory")
                .setMessage("As mentioned in the project's README file, please select a directory which contains the images.")
                .setCancelable(false)
                .setPositiveButton("SELECT", (dialog, which) -> {
                    dialog.dismiss();
                    launchChooseDirectoryIntent();
                })
                .create();
        alertDialog.show();
    }

    private void launchChooseDirectoryIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        directoryAccessLauncher.launch(intent);
    }

    // Read the contents of the select directory here.
    private final ActivityResultLauncher<Intent> directoryAccessLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Uri dirUri = result.getData().getData();
                Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                        dirUri,
                        DocumentsContract.getTreeDocumentId(dirUri)
                );
                DocumentFile tree = DocumentFile.fromTreeUri(this, childrenUri);
                ArrayList<kotlin.Pair<String, Bitmap>> images = new ArrayList<>();
                boolean errorFound = false;

                if (tree != null && tree.listFiles().length > 0) {
                    for (DocumentFile doc : tree.listFiles()) {
                        if (doc.isDirectory() && !errorFound) {
                            String name = doc.getName();
                            for (DocumentFile imageDocFile : doc.listFiles()) {
                                try {
                                    images.add(new kotlin.Pair<>(name, getFixedBitmap(imageDocFile.getUri())));
                                } catch (Exception e) {
                                    errorFound = true;
                                    Logger.Companion.log("Could not parse an image in " + name + " directory. ");
                                    break;
                                }
                            }
                            Logger.Companion.log("Found " + doc.listFiles().length + " images in " + name + " directory");
                        } else {
                            errorFound = true;
                            Logger.Companion.log("The selected folder should contain only directories. ");
                        }
                    }
                } else {
                    errorFound = true;
                    Logger.Companion.log("The selected folder doesn't contain any directories.");
                }

                if (images.isEmpty()) {
                    errorFound = true;
                    Logger.Companion.log("The selected folder is empty.");
                }

                if (!errorFound) {
                    Utils.setMainDirectory(dirUri);

                    activityMainBinding.loader.setVisibility(View.VISIBLE);
                    fileReader.run(images, new FileReader.ProcessCallback() {
                        @Override
                        public void onProcessCompleted(@NonNull ArrayList<kotlin.Pair<String, float[]>> data, int numImagesWithNoFaces) {
                            frameAnalyser.setFaceList(data);
                            saveSerializedImageData(data);
                            activityMainBinding.loader.setVisibility(View.INVISIBLE);
                            Logger.Companion.log("Images parsed. Found " + numImagesWithNoFaces + " images with no faces.");
                        }
                    });
                    Logger.Companion.log("Detecting faces in " + images.size() + " images ...");
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle("Error while parsing directory")
                            .setMessage("There were some errors while parsing the directory. Please see the log below. " +
                                    "Make sure that the file structure is as described in the README of the project and then tap RESELECT")
                            .setCancelable(false)
                            .setPositiveButton("RESELECT", (dialog, which) -> {
                                dialog.dismiss();
                                launchChooseDirectoryIntent();
                            })
                            .setNegativeButton("CANCEL", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            })
                            .create();
                    alertDialog.show();
                }
            }
    );

    // Get the image as a Bitmap from the given Uri and fix the rotation using the Exif interface
    private Bitmap getFixedBitmap(Uri imageFileUri) throws IOException {
        Bitmap imageBitmap = BitmapUtils.Companion.getBitmapFromUri(getContentResolver(), imageFileUri);
        ExifInterface exifInterface = new ExifInterface(getContentResolver().openInputStream(imageFileUri));
        switch (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                imageBitmap = BitmapUtils.Companion.rotateBitmap(imageBitmap, 90f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                imageBitmap = BitmapUtils.Companion.rotateBitmap(imageBitmap, 180f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                imageBitmap = BitmapUtils.Companion.rotateBitmap(imageBitmap, 270f);
                break;
        }
        return imageBitmap;
    }

    private void saveSerializedImageData(ArrayList<kotlin.Pair<String, float[]>> data) {
        File serializedDataFile = new File(getFilesDir(), SERIALIZED_DATA_FILENAME);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializedDataFile));
            oos.writeObject(data);
            oos.flush();
            oos.close();
            sharedPreferences.edit().putBoolean(SHARED_PREF_IS_DATA_STORED_KEY, true).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<kotlin.Pair<String, float[]>> loadSerializedImageData() {
        File serializedDataFile = new File(getFilesDir(), SERIALIZED_DATA_FILENAME);
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedDataFile));
            ArrayList<kotlin.Pair<String, float[]>> data = (ArrayList<kotlin.Pair<String, float[]>>) ois.readObject();
            ois.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
