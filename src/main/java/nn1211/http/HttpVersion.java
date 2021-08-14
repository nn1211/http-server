package nn1211.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Supported HTTP versions
 *
 * @author nn1211
 *
 */
public enum HttpVersion {

    V1_1("HTTP/1.1");

    private final byte[] bytes;
    private final String value;

    /**
     * Initialize the value
     *
     * @param value
     */
    HttpVersion(String value) {
        this.value = value;
        bytes = value.getBytes();
    }

    /**
     * Parse the HTTP version from the request stream
     *
     * @param reqStream
     * @return null if invalid or unsupported version. A {@link HttpVersion}
     * object, otherwise.
     * @throws IOException
     */
    public static HttpVersion parse(InputStream reqStream)
            throws IOException {

        if (72 != reqStream.read()) {
            return null;
        }

        if (84 != reqStream.read()) {
            return null;
        }

        if (84 != reqStream.read()) {
            return null;
        }

        if (80 != reqStream.read()) {
            return null;
        }

        if (47 != reqStream.read()) {
            return null;
        }

        if (49 != reqStream.read()) {
            return null;
        }

        if (46 != reqStream.read()) {
            return null;
        }

        if (49 != reqStream.read()) {
            return null;
        }

        if (13 != reqStream.read()) {
            return null;
        }

        if (10 != reqStream.read()) {
            return null;
        }

        return V1_1;
    }

    /**
     * Get the value of this HTTP version as a byte array
     *
     * @return the value of this HTTP version as a byte array
     */
    byte[] toBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return value;
    }
}
