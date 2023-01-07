package com.tzsombi.utils;

import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class Logger {

    public void convertDataToCSvAndWriteToFile(String logLine) throws IOException {
        try {
            // String pathToCsvLocation = Constants.STATIC_FOLDER_CSV_PATH_RESOURCE.getFile().getAbsolutePath();
            // File csvOutputFile = new File(pathToCsvLocation);

            try (FileOutputStream logFile = new FileOutputStream(Constants.STATIC_FOLDER_CSV_PATH_RESOURCE.getFile())) {
                logFile.write(logLine.getBytes());
                logFile.write("\n".getBytes());
            }
        } catch(IOException exception) {
            throw new IOException("File not found!" + exception);
        }
    }
}
