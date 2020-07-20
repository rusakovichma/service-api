package com.epam.ta.reportportal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClassPathUtil {

    private ClassPathUtil() {
    }

    public static List<String> getResourceFiles(String path) {
        List<String> filenames = new ArrayList<>();

        try (InputStream in = getResourceAsStream(path)) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String resource;

                while ((resource = br.readLine()) != null) {
                    filenames.add(resource);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return filenames;
    }

    private static InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? ClassPathUtil.class.getResourceAsStream(resource) : in;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
