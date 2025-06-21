package com.ea.utils;

import java.io.File;

public class FileUtils {

    private FileUtils() {
    }

    public static void createDirectories(String filePath, boolean isFile) {
        File file = new File(filePath);
        String directoryPath = isFile ? file.getParent() : file.getPath();
        File directory = new File(directoryPath);
        directory.mkdirs();
    }
}
