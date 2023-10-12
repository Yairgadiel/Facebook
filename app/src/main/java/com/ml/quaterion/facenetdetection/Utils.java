package com.ml.quaterion.facenetdetection;

import java.io.File;

public class Utils {


    public static String getPicturePath(String folderPath){
        String picturePath = null;
        File dir = new File(folderPath);
        File[] directoryListing = dir.listFiles();
        for (File child : directoryListing) {
            if(Utils.isPicturePath(child.getName())){
                picturePath = child.getAbsolutePath();
            }
        }

        return picturePath;
    }

    public static boolean isPicturePath(String path){
        return path.endsWith(".jpeg");
    }
}
