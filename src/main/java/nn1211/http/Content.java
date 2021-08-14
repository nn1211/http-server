package nn1211.http;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * The body content of a {@link HttpMessage}
 *
 * @author nn1211
 *
 */
public interface Content {

    /**
     * Build a HTML content from a string
     *
     * @param html
     * @return
     */
    static Content html(String html) {
        return new TextContent(html, Type.HTML);
    }

    /**
     * Build a text content from a string
     *
     * @param text
     * @return a {@link Content} object
     */
    static Content text(String text) {
        return new TextContent(text, Type.TEXT);
    }

    /**
     * Build a {@link Content} object from a {@link Type} and data
     *
     * @param type
     * @param data
     * @return a {@link Content} object
     */
    static Content build(Type type, byte[] data) {
        if (Type.HTML == type || Type.TEXT == type) {
            return new TextContent(data, type);
        }

        return new ByteArrayContent(data, type);
    }

    /**
     * Get this content data as a byte array
     *
     * @return a byte array
     */
    byte[] toBytes();

    /**
     * Get the type of this content
     *
     * @return the type of this content
     */
    Type type();

    /**
     * Graceful cast to a {@link TextContent} object
     *
     * @return null or a {@link TextContent} object
     */
    default TextContent asTextContent() {
        return null;
    }

    /**
     * Supported types of a {@link Content}
     *
     * @author nn1211
     *
     */
    public enum Type {
        CSS("text/css"), GIF("image/gif"), GZIP("application/gzip"), HTML("text/html"),
        ICON("image/x-icon"), JAR("application/java-archive"),
        JPEG("image/jpeg"), JS("text/javascript"), JSON("application/json"),
        PNG("image/png"), SVG("image/svg+xml"), TEXT("text/plain"),
        XHTML("application/xhtml+xml"), XML("application/xml"),
        ZIP("application/zip");

        private static final Map<String, Type> SUPPORTED_TYPES = new HashMap<>();

        static {
            SUPPORTED_TYPES.put("css", CSS);
            SUPPORTED_TYPES.put("gif", GIF);
            SUPPORTED_TYPES.put("gz", GZIP);
            SUPPORTED_TYPES.put("html", HTML);
            SUPPORTED_TYPES.put("htm", HTML);
            SUPPORTED_TYPES.put("jar", JAR);
            SUPPORTED_TYPES.put("java", TEXT);
            SUPPORTED_TYPES.put("jpeg", JPEG);
            SUPPORTED_TYPES.put("jpg", JPEG);
            SUPPORTED_TYPES.put("js", JS);
            SUPPORTED_TYPES.put("json", JSON);
            SUPPORTED_TYPES.put("ico", ICON);
            SUPPORTED_TYPES.put("png", PNG);
            SUPPORTED_TYPES.put("svg", SVG);
            SUPPORTED_TYPES.put("txt", TEXT);
            SUPPORTED_TYPES.put("xhtml", XHTML);
            SUPPORTED_TYPES.put("xml", XML);
            SUPPORTED_TYPES.put("zip", ZIP);
        }

        private final String value;

        /**
         * Initialize value
         *
         * @param value
         */
        Type(String value) {
            this.value = value;
        }

        /**
         * Get the content type of a resource from its extension
         *
         * @param uri
         * @return null or a {@link Type} object
         */
        public static Type from(String uri) {
            int i = uri.lastIndexOf(".");
            if (-1 == i) {
                return null;
            }

            return SUPPORTED_TYPES.get(uri.substring(i + 1));
        }

        /**
         * Check if this type is a text type
         *
         * @return true if this type is text type, false otherwise.
         */
        public boolean isText() {
            return this == HTML;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * A byte array content
     *
     * @author nn1211
     *
     */
    public static class ByteArrayContent implements Content {

        private byte[] bytes;
        private Type type;

        /**
         * Create new instance with data and a content type
         *
         * @param bytes
         * @param type
         */
        public ByteArrayContent(byte[] bytes, Type type) {
            this.bytes = bytes;
            this.type = type;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public byte[] toBytes() {
            return bytes;
        }
    }

    /**
     * A text content
     *
     * @author nn1211
     *
     */
    public static class TextContent extends ByteArrayContent {

        private final Charset charset;

        /**
         * Create new instance with a text, a type, and a charset
         *
         * @param text
         * @param type
         * @param charset
         */
        public TextContent(String text, Type type, Charset charset) {
            super(text.getBytes(charset), type);

            this.charset = charset;
        }

        /**
         * Create new instance with a text and a type
         *
         * @param text
         * @param type
         */
        public TextContent(String text, Type type) {
            super(text.getBytes(), type);

            this.charset = Charset.defaultCharset();
        }

        /**
         * Create new instance with a byte array data and a type
         *
         * @param data
         * @param type
         */
        public TextContent(byte[] data, Type type) {
            super(data, type);

            this.charset = Charset.defaultCharset();
        }

        /**
         * Get the charset of this text content
         *
         * @return the charset of this text content
         */
        public final Charset charset() {
            return charset;
        }

        @Override
        public TextContent asTextContent() {
            return this;
        }
    }

}
