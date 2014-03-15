package org.setareh.wadl.codegen.utils;

import java.io.IOException;
import java.io.Reader;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public class IOUtils {
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static String toString(final Reader input) throws IOException {
        return toString(input, DEFAULT_BUFFER_SIZE);
    }
    public static String toString(final Reader input, int bufSize) throws IOException {

        StringBuilder buf = new StringBuilder();
        final char[] buffer = new char[bufSize];
        int n = 0;
        n = input.read(buffer);
        while (-1 != n) {
            if (n == 0) {
                throw new IOException("0 bytes read in violation of InputStream.read(byte[])");
            }
            buf.append(new String(buffer, 0, n));
            n = input.read(buffer);
        }
        input.close();
        return buf.toString();
    }
}
