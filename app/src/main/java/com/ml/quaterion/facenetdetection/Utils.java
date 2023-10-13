package com.ml.quaterion.facenetdetection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

    public static String readFile(String filepath) {
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
            Log.e( "Error" , e.getMessage() != null ? e.getMessage() : e.toString());
        }

        return content;
    }

    public static String getPicturePath(String folderPath){
        String picturePath = null;
        File dir = new File(folderPath);
        File[] directoryListing = dir.listFiles();

        for (File child : directoryListing) {
            if (Utils.isPicturePath(child.getName())){
                picturePath = child.getAbsolutePath();
            }
        }

        return picturePath;
    }

    public static boolean isPicturePath(String path){
        return path.endsWith(".jpeg");
    }
}
