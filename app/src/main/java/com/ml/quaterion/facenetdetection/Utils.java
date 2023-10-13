package com.ml.quaterion.facenetdetection;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

    private static final String MAIN_DIR_PREFERENCE_KEY = "main_dir_key";

    private static String _mainDir;

    private static Context _context;

    public static void setContext(Context context) {
        _context = context;
    }

    public static void setMainDirectory(Uri dir) {
        String [] pathsections = dir.getPath().split(":");
        _mainDir  = Environment.getExternalStorageDirectory().getPath() + "/" + pathsections[pathsections.length-1];

        SharedPreferences sharedPref = _context.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MAIN_DIR_PREFERENCE_KEY, _mainDir);
        editor.apply();
    }

    public static String getMainDirectory() {
        if (_mainDir == null) {
            SharedPreferences sharedPref = _context.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE);
            _mainDir = sharedPref.getString(MAIN_DIR_PREFERENCE_KEY, "");
        }

        return _mainDir;
    }

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

        if (folderPath.equals("Unknown") || folderPath.equals("Please remove the mask")) {
            Log.d("Log", "couldn't identify");
        }
        else {
            File dir = new File(_mainDir + "/" + folderPath + "/");
            File[] directoryListing = dir.listFiles();

            for (File child : directoryListing) {
                if (Utils.isPicturePath(child.getName())) {
                    picturePath = child.getAbsolutePath();
                }
            }
        }

        return picturePath;
    }

    public static boolean isPicturePath(String path){
        return path.endsWith(".jpeg");
    }
}
