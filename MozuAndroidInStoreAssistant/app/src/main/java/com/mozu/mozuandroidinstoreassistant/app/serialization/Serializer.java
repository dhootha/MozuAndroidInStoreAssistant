package com.mozu.mozuandroidinstoreassistant.app.serialization;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozu.api.utils.JsonUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Serializer {

    private String mFileName;

    private ObjectMapper mMapper;

    public Serializer(String fileName) {
        mFileName = fileName;

        mMapper = JsonUtils.initObjectMapper();
    }

    public String getFileName() {
        return mFileName;
    }

    protected void setFileName(String fileName) {
        mFileName = fileName;
    }

    protected void writeJsonToFile(String json) throws IOException {
        File fileToWrite = new File(mFileName);

        if (fileToWrite.exists()) {
            fileToWrite.delete();
        }

        FileWriter outputStream = new FileWriter(fileToWrite);
        outputStream.write(json);
        outputStream.close();
    }

    protected ObjectMapper getMapper() {
        return mMapper;
    }

    public void deleteFile() {
        File file = new File(getFileName());

        if (file.exists()) {
            file.delete();
        }
    }

    public boolean doesFileExist() {
        File file = new File(getFileName());

        return file.exists();
    }
}
