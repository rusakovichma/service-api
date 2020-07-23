package com.epam.ta.reportportal.security.fileupload;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class MaliciousFile implements Serializable {

    private final String filePath;

    public MaliciousFile(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public InputStream getContent() {
        try {
            return new ClassPathResource(filePath).getInputStream();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

    }

    @Override
    public String toString() {
        return "MaliciousFile{" +
                "filePath='" + filePath + '\'' +
                '}';
    }
}
