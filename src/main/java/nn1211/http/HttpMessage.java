package nn1211.http;

/**
 * A {@link HttpMessage}
 *
 * @author nn1211
 *
 */
public interface HttpMessage {

    /**
     * Get headers of this message
     *
     * @return headers of this message
     */
    HttpHeaders headers();

    /**
     * Gets the body content of this response if any
     *
     *
     * @return null or the body content of this message
     */
    Content body();

    /**
     * A base class for all {@link HttpMessage}
     *
     * @author nn1211
     *
     */
    public static abstract class BaseMessage implements HttpMessage {

        /**
         * CRLF data
         */
        public static final byte[] CRLF = {13, 10};

        private final HttpHeaders headers = new HttpHeaders();
        private Content body;

        /**
         * Create an empty message
         */
        protected BaseMessage() {
        }

        /**
         * Create a message with a body
         *
         * @param body
         */
        protected BaseMessage(Content body) {
            this.body = body;

            headers.put(HttpHeader.buildContentType(body));
            headers.put(HttpHeader.buildContentLength(body));
        }

        @Override
        public final Content body() {
            return body;
        }

        @Override
        public final HttpHeaders headers() {
            return headers;
        }
    }
}
