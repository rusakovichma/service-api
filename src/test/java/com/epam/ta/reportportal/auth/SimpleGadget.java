package com.epam.ta.reportportal.auth;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

public class SimpleGadget implements Serializable {

    private void payloadMethod() {
        final String simpleRcePayload =
                System.getProperty("os.name", "generic")
                        .toLowerCase(Locale.ENGLISH).indexOf("win") >= 0
                        ? "systeminfo"
                        : "uname";
        try {
            Runtime.getRuntime().exec(simpleRcePayload);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SimpleGadget() {
        payloadMethod();
    }

}
