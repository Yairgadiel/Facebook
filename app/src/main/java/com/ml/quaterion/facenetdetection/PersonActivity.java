package com.ml.quaterion.facenetdetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.ml.quaterion.facenetdetection.databinding.ActivityPersonBinding;

import java.io.File;

public class PersonActivity extends AppCompatActivity {

    // region Constants

    private static final String DETAILS_FILE_NAME = "person_details.json";

    private static String PERSON_FOLDER_PATH_KEY = "person_folder";

    private static String PERSON_IMAGE_PATH_KEY = "person_image";

    // endregion

    // region Members

    private String _personFolder;

    // endregion

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPersonBinding activityPersonBinding = ActivityPersonBinding.inflate(getLayoutInflater());
        setContentView(activityPersonBinding.getRoot());

        Intent startIntent = getIntent();

        _personFolder = startIntent.getStringExtra(PERSON_FOLDER_PATH_KEY);

        Bitmap personBitmap = BitmapFactory.decodeFile(startIntent.getStringExtra(PERSON_IMAGE_PATH_KEY));

        activityPersonBinding.personImage.setImageBitmap(personBitmap);
        activityPersonBinding.personName.setText(new File(_personFolder).getName());
        activityPersonBinding.personDetails.setText(getPersonDetails(_personFolder));
    }

    private String getPersonDetails(String personFolder) {
        String personDetails = "";
        String personDetailsPath = personFolder + DETAILS_FILE_NAME;

        if (new File(personDetailsPath).exists()) {
            personDetails = Utils.readFile(personDetailsPath);
        }

        return personDetails;
    }

}
