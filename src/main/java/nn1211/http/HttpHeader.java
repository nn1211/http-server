package nn1211.http;

import nn1211.http.Content.TextContent;

/**
 * A HTTP header
 *
 * @author nn1211
 *
 */
public interface HttpHeader {

    /**
     * Build a {@link HttpHeader} from a {@link Name} and a value
     *
     * @param name
     * @param value
     * @return a {@link HttpHeader}
     */
    static HttpHeader from(Name name, String value) {
        return new HttpHeaderImpl(name, value);
    }

    /**
     * Build a Content-Type header from a {@link Content} object
     *
     * @param content
     * @return a Content-Type header
     */
    static HttpHeader buildContentType(Content content) {
        TextContent textContent = content.asTextContent();
        if (null == textContent) {
            return new HttpHeaderImpl(Name.CONTENT_TYPE,
                    content.type().toString());
        }

        return new HttpHeaderImpl(Name.CONTENT_TYPE, content.type().toString()
                + "; charset=" + textContent.charset().name());
    }

    /**
     * Build a Content-Length header from a {@link Content} object
     *
     * @param content
     * @return a Content-Length header
     */
    static HttpHeader buildContentLength(Content content) {
        return new HttpHeaderImpl(Name.CONTENT_LENGTH,
                content.toBytes().length + "");
    }

    /**
     * Indicate this header values are able to be appended
     *
     * @return false (only true for special cases such as Set-Cookie)
     */
    default boolean isAppendable() {
        return false;
    }

    /**
     * Append the value of the same header name only.
     *
     * @param header
     * @throws IllegalArgumentException if the argument header doesn't have the
     * same name
     */
    void append(HttpHeader header) throws IllegalArgumentException;

    /**
     * Get the name of this header
     *
     * @return the name of this header
     */
    Name name();

    /**
     * Get the name of this header as a string
     *
     * @return the name of this header
     */
    default String nameAsString() {
        return name().name();
    }

    /**
     * Get the value of this header
     *
     * @return
     */
    String value();

    /**
     * Get the content of this header as a bytes array
     *
     * @return the content of this header as a bytes array
     */
    byte[] toBytes();

    /**
     * Support name
     *
     * @author nn1211
     *
     */
    public enum Name {

        CONNECTION("Connection"), CONTENT_LENGTH("Content-Length"),
        CONTENT_TYPE("Content-Type");

        private final String value;

        /**
         * Initialize value
         *
         * @param value
         */
        Name(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    /**
     * Default implementation for {@link HttpHeader}
     *
     * @author nn1211
     *
     */
    class HttpHeaderImpl implements HttpHeader {

        private static final String SET_COOKIE = "Set-Cookie";

        private final Name name;

        private String value;
        private byte[] bytes;

        /**
         * Initialize name and value of this header
         *
         * @param name
         * @param value
         */
        HttpHeaderImpl(Name name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean isAppendable() {
            return SET_COOKIE.equals(name.value);
        }

        @Override
        public void append(HttpHeader header) throws IllegalArgumentException {
            if (name != header.name()) {
                throw new IllegalArgumentException(
                        "Couldn't append other header name");
            }

            value += "; " + header.value();
        }

        @Override
        public Name name() {
            return name;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public byte[] toBytes() {
            if (null == bytes) {
                bytes = toString().getBytes();
            }

            return bytes;
        }

        @Override
        public String toString() {
            return name + ": " + value;
        }
    }
}
