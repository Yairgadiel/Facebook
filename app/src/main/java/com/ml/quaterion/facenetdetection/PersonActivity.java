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
;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.ml.quaterion.facenetdetection.databinding.ActivityPersonBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PersonActivity extends AppCompatActivity {

    private static final String DETAILS_FILE_NAME = "person_details.json";
    private String _personFolder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPersonBinding activityPersonBinding = ActivityPersonBinding.inflate(getLayoutInflater());
        setContentView(activityPersonBinding.getRoot());
        _personFolder = savedInstanceState.getString("person_folder");

        Bitmap personBitmap = BitmapFactory.decodeFile(Utils.getPicturePath(_personFolder));
        activityPersonBinding.personImage.setImageBitmap(personBitmap);

        activityPersonBinding.personName.setText(new File(_personFolder).getName());
        activityPersonBinding.personDetails.setText(getPersonDetails(_personFolder));
    }

    private String getPersonDetails(String personFolder) {
        String personDetails = "";
        String personDetailsPath = personFolder + DETAILS_FILE_NAME;
        if(new File(personDetailsPath).exists()) {
            personDetails = readFile(personDetailsPath);
        }

        return personDetails;
    }

    private String readFile(String filepath) {
        String content = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            // delete the last new line separator
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();
        content = stringBuilder.toString();
        }
        catch (IOException e) {
            Log.e( "Error" , e.getMessage());
        }

        return content;
    }

}
