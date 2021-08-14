package nn1211.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * A simple HTTP request
 *
 * @author nn1211
 *
 */
public interface HttpRequest extends HttpMessage {

    /**
     * Parse from the request stream
     *
     * @param reqStream
     * @return null if invalid or unsupported request. A {@link HttpRequest}
     * object, otherwise.
     * @throws IOException
     */
    public static HttpRequest parse(InputStream reqStream) throws IOException {
        HttpRequestImpl req = new HttpRequestImpl(reqStream);
        req.parseRequestLine();

        return req;
    }

    /**
     * Get the method of this request
     *
     * @return the method of this request
     */
    Method method();

    /**
     * GEt the URI of this request
     *
     * @return the URI of this request
     */
    String uri();

    /**
     * Get the HTTP version of this request
     *
     * @return the HTTP version of this request
     */
    HttpVersion httpVersion();

    /**
     * Supported methods that a HTTP request can make
     *
     * @author nn1211
     *
     */
    public enum Method {
        GET, POST, PUT, DELETE,
        /**
         * GET, POST, PUT, and DELETE
         */
        ALL,
        /**
         * Used to identify a 405 response
         */
        UNSUPPORTED;

        /**
         * Parse request method from the request stream
         *
         * @param inputStream
         * @return null if invalid or unsupported method. A {@link Method}
         * object, otherwise.
         * @throws IOException
         */
        public static Method parse(InputStream reqStream) throws IOException {
            int b = reqStream.read();
            if (-1 == b) {
                return null;
            }

            if (71 != b) {
                return UNSUPPORTED; // Invalid or unsupported method
            }
            return parseGet(reqStream);
        }

        private static Method parseGet(InputStream reqStream)
                throws IOException {

            if (69 != reqStream.read()) {
                return null;
            }

            if (84 != reqStream.read()) {
                return null;
            }

            if (32 != reqStream.read()) {
                return null;
            }

            return GET;
        }
    }

    /**
     * Default implementation of {@link HttpRequest}
     *
     * @author nn1211
     *
     */
    static class HttpRequestImpl extends BaseMessage implements HttpRequest {

        private final InputStream reqStream;

        private Method method;
        private String uri;
        private HttpVersion httpVersion;

        /**
         * Create a new instance with a given request stream
         *
         * @param inStream
         */
        HttpRequestImpl(InputStream reqStream) {
            this.reqStream = reqStream;
        }

        @Override
        public HttpVersion httpVersion() {
            return httpVersion;
        }

        @Override
        public final Method method() {
            return method;
        }

        @Override
        public final String uri() {
            return uri;
        }

        @Override
        public String toString() {
            return (method == null ? "null" : method.name()) + ' ' + uri + ' '
                    + (httpVersion == null ? "null" : httpVersion.toString());
        }

        /**
         * Parse request line from the request stream
         *
         * @throws IOException
         */
        void parseRequestLine() throws IOException {
            if (null == (method = Method.parse(reqStream))) {
                return;
            }

            if (null == (uri = parseRequestURI())) {
                return;
            }

            httpVersion = HttpVersion.parse(reqStream);
        }

        /**
         * Parse the request URI from the request stream
         *
         * @return
         * @throws IOException
         */
        String parseRequestURI() throws IOException {
            byte[] buf = new byte[1024];
            int i = 0;
            int b;
            while (-1 != (b = reqStream.read())) {
                if (32 == b) {
                    byte[] uri = new byte[i];
                    System.arraycopy(buf, 0, uri, 0, i);
                    return new String(uri);
                }

                buf[i++] = (byte) b;
            }

            return null;
        }
    }
}
