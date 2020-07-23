package com.epam.ta.reportportal.security.fileupload;

import com.epam.ta.reportportal.util.ClassPathUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MaliciousFileUpload {

    private MaliciousFileUpload() {
    }

    private static Collection<MaliciousFile> getFilesByClasspath(String classpathFolder) {
        List<String> fileNames = ClassPathUtil.getResourceFiles(classpathFolder);

        Collection<MaliciousFile> fileLinks = new ArrayList<>(fileNames.size());
        for (String fileName : fileNames) {
            fileLinks.add(
                    new MaliciousFile(classpathFolder + File.separator + fileName)
            );
        }
        return fileLinks;
    }

    public static MaliciousFile getXssSvgFile() {
        return new MaliciousFile("image/svg/image_with_malicious_content.svg");
    }

    public static MaliciousFile getXssSwfFile() {
        return new MaliciousFile("image/swf/xssproject.swf");
    }

    public static Collection<MaliciousFile> getActiveContentFiles() {
        return getFilesByClasspath("image/active-content");
    }

    public static Collection<MaliciousFile> getInfectedFiles() {
        return getFilesByClasspath("image/infected");
    }

}
