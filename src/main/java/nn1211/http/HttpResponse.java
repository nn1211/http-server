package nn1211.http;

import java.util.List;

/**
 * A simple HTTP response
 *
 * @author nn1211
 *
 */
public interface HttpResponse extends HttpMessage {

    /**
     * Returns a HTTP status 400 response
     *
     * @return a HTTP status 400 response
     */
    public static HttpResponse badRequest() {
        return new HttpResponseImpl(StatusCode.BAD_REQUEST,
                Content.text(StatusCode.BAD_REQUEST.reasonPhrase()));
    }

    /**
     * Returns a HTTP status 403 response
     *
     * @return a HTTP status 403 response
     */
    public static HttpResponse forbidden() {
        return new HttpResponseImpl(StatusCode.BAD_REQUEST,
                Content.text(StatusCode.BAD_REQUEST.reasonPhrase()));
    }

    /**
     * Returns a HTTP status 405 response
     *
     * @return a HTTP status 405 response
     */
    public static HttpResponse methodNotAllowed() {
        return new HttpResponseImpl(StatusCode.METHOD_NOT_ALLOWED,
                Content.text(StatusCode.METHOD_NOT_ALLOWED.reasonPhrase()));
    }

    /**
     * Returns a HTTP status 404 response
     *
     * @return a HTTP status 404 response
     */
    public static HttpResponse notFound() {
        return new HttpResponseImpl(StatusCode.NOT_FOUND,
                Content.text(StatusCode.NOT_FOUND.reasonPhrase()));
    }

    /**
     * Returns a HTTP status 501 response
     *
     * @return a HTTP status 501 response
     */
    public static HttpResponse notImplemented() {
        return new HttpResponseImpl(StatusCode.NOT_IMPLEMENTED,
                Content.text(StatusCode.NOT_IMPLEMENTED.reasonPhrase()));
    }

    /**
     * Returns a HTTP status 200 response
     *
     * @param content
     * @return
     */
    public static HttpResponse ok(Content content) {
        return new HttpResponseImpl(content);
    }

    /**
     * Returns a HTTP status 415 response
     *
     * @return a HTTP status 415 response
     */
    public static HttpResponse unsupportedMediaType() {
        return new HttpResponseImpl(StatusCode.UNSUPPORTED_MEDIA_TYPE,
                Content.text(StatusCode.UNSUPPORTED_MEDIA_TYPE.reasonPhrase()));
    }

    /**
     * Get the status code of this response
     *
     * @return the status code of this response
     */
    StatusCode statusCode();

    /**
     * Convert this to a byte array
     *
     * @return a byte array
     */
    byte[] toBytes();

    /**
     * Supported HTTP status codes
     *
     * @author nn1211
     *
     */
    static enum StatusCode {
        BAD_REQUEST(400, "Bad Request"), FORBIDDEN(403, "Forbidden"),
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
        NOT_FOUND(404, "Not Found"), NOT_IMPLEMENTED(501, "Not Implemented"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"), OK(200, "OK");

        private final int code;
        private final String reasonPhrase;

        /**
         * Initialize code and reason phrase of this status code
         *
         * @param code
         * @param reasonPharse
         */
        StatusCode(int code, String reasonPharse) {
            this.code = code;
            this.reasonPhrase = reasonPharse;
        }

        /**
         * Get the code of this status code
         *
         * @return the code of this status code
         */
        public int code() {
            return code;
        }

        /**
         * Get the reason phrase of this status code
         *
         * @return the reason phrase of this status code
         */
        public String reasonPhrase() {
            return reasonPhrase;
        }

        @Override
        public String toString() {
            return code + " " + reasonPhrase;
        }
    }

    /**
     * Default implementation of {@link HttpResponse}
     *
     * @author nn1211
     *
     */
    static class HttpResponseImpl extends BaseMessage implements HttpResponse {

        /**
         * Only supports HTTP/1.1 only
         */
        private static final byte[] HTTP_V1_1 = HttpVersion.V1_1.toBytes();

        private final StatusCode statusCode;

        /**
         * Create a response with an error code
         *
         * @param errorCode
         */
        HttpResponseImpl(StatusCode errorCode) {
            statusCode = errorCode;
        }

        /**
         * Create a response with an error code and a content
         *
         * @param errorCode
         * @param data
         */
        HttpResponseImpl(StatusCode errorCode, Content data) {
            super(data);

            statusCode = errorCode;
        }

        /**
         * Create a response with a body content
         *
         * @param data
         */
        HttpResponseImpl(Content data) {
            super(data);

            statusCode = StatusCode.OK;
        }

        @Override
        public StatusCode statusCode() {
            return statusCode;
        }

        @Override
        public byte[] toBytes() {
            byte[] reasonPhrase = statusCode.reasonPhrase().getBytes();

            int dataLength = HTTP_V1_1.length + 5 + reasonPhrase.length + 2;

            byte[][] headersData;
            if (headers().isEmpty()) {
                headersData = new byte[0][];
            } else {
                List<HttpHeader> headers = headers().asList();
                headersData = new byte[headers.size()][];
                for (int i = 0; i < headers.size(); i++) {
                    byte[] headerData = headers.get(i).toBytes();
                    headersData[i] = headerData;
                    dataLength += headerData.length + 2;
                }
            }

            dataLength += 2;

            byte[] bodyData = {};
            boolean hasBody;
            if (hasBody = (null != body())) {
                bodyData = body().toBytes();
                dataLength += bodyData.length;
            }

            byte[] data = new byte[dataLength];

            dataLength = HTTP_V1_1.length;

            System.arraycopy(HTTP_V1_1, 0, data, 0, dataLength);
            data[dataLength++] = 32;

            int code = statusCode.code();
            data[dataLength++] = (byte) (code / 100 % 10 + 48);
            data[dataLength++] = (byte) (code / 10 % 10 + 48);
            data[dataLength++] = (byte) (code % 10 + 48);
            data[dataLength++] = 32;

            System.arraycopy(reasonPhrase, 0, data, dataLength,
                    reasonPhrase.length);
            dataLength += reasonPhrase.length;

            System.arraycopy(CRLF, 0, data, dataLength, CRLF.length);
            dataLength += CRLF.length;

            for (byte[] headerData : headersData) {
                System.arraycopy(headerData, 0, data, dataLength,
                        headerData.length);

                dataLength += headerData.length;

                System.arraycopy(CRLF, 0, data, dataLength, CRLF.length);
                dataLength += CRLF.length;
            }

            System.arraycopy(CRLF, 0, data, dataLength, CRLF.length);
            dataLength += CRLF.length;

            if (hasBody) {
                System.arraycopy(bodyData, 0, data, dataLength,
                        bodyData.length);
            }

            return data;
        }

        @Override
        public String toString() {
            return statusCode + "";
        }
    }
}
