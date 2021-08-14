package nn1211.http;

import java.io.IOException;

/**
 * Simple HTTP handler
 *
 * @author nn1211
 *
 */
@FunctionalInterface
public interface Handler {

    /**
     * Handle a {@link HttpRequest} and returns a {@link HttpResponse}
     *
     * @param req
     * @return a {@link HttpResponse}
     * @throws IOException
     */
    HttpResponse handle(HttpRequest req) throws IOException;
}
