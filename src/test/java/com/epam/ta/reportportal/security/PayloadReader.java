package com.epam.ta.reportportal.security;

import com.epam.ta.reportportal.util.ClassPathUtil;

import java.io.*;
import java.util.Iterator;

public class PayloadReader implements Iterable<String> {

    private final String payloadFilePath;

    public PayloadReader(final String payloadFilePath) {
        assert payloadFilePath != null;
        this.payloadFilePath = payloadFilePath;
    }

    @Override
    public Iterator<String> iterator() {
        return new PayloadIterator();
    }

    private final class PayloadIterator implements Iterator<String> {

        final BufferedReader bufferedReader;
        final InputStream inputStream;
        String nextline;

        public PayloadIterator() {
            try {
                this.inputStream = ClassPathUtil
                        .getResourceAsStream(payloadFilePath);
                this.bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream));

                nextline = bufferedReader.readLine();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean hasNext() {
            return nextline != null;
        }

        public String next() {
            String result = nextline;
            try {
                if (nextline != null) {
                    nextline = bufferedReader.readLine();
                    if (nextline == null) {
                        bufferedReader.close();
                    }
                }
                return result;
            } catch (IOException e) {
                nextline = null;
                return result;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }
}
