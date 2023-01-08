package com.tzsombi.utils;

import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class Logger {

    public void convertDataToCSvAndWriteToFile(String logLine) throws IOException {
        if(System.getenv("IS_TESTING").equals("true")) {
            return;
        }

        File newFile = new File(Constants.PATH_TO_LOGS_CSV);

        try {
            FileWriter fileWriter = new FileWriter (newFile, true);
            fileWriter.write(logLine + "\n");
            fileWriter.close();
        } catch(IOException exception) {
            throw new IOException(ErrorConstants.FILE_NOT_FOUND);
        }
    }
}
