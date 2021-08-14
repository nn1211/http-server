package nn1211.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection of {@link HttpHeader}
 *
 * @author nn1211
 */
public class HttpHeaders {

    private final Map<String, HttpHeader> headers = new HashMap<>();

    /**
     * Put a {@link HttpHeader} to this collection
     *
     * @param header
     * @return this
     */
    public HttpHeaders put(HttpHeader header) {
        HttpHeader storedHeader = headers.get(header.nameAsString());
        if (null != storedHeader && header.isAppendable()) {
            storedHeader.append(header);
        } else {
            headers.put(header.nameAsString(), header);
        }

        return this;
    }

    /**
     * Determine this collection has any element or not
     *
     * @return {@code true} if this collection has elements, {@code false}
     * otherwise.
     */
    public final boolean isEmpty() {
        return headers.isEmpty();
    }

    /**
     * Get this as a {@link List}
     *
     * @return this as a {@link List}
     */
    public final List<HttpHeader> asList() {
        return new ArrayList<>(headers.values());
    }
}
