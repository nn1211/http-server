package nn1211.http;

import java.util.HashMap;
import java.util.Map;

import nn1211.http.HttpRequest.Method;

/**
 * Manage handlers for {@link HttpServer}s
 *
 * @author nn1211
 *
 */
public enum HandlerManager {

    INSTANCE;

    private final Map<String, Handler> handlers = new HashMap<>();
    private Handler defaultHandler = new DefaultHandler();

    /**
     * Get the handler for the given request based on its method and URI.
     *
     * @param req
     * @return the handler for the given request.
     */
    public Handler get(HttpRequest req) {
        Handler handler = handlers
                .get(req.method().name() + req.uri().substring(1));

        if (null == handler) {
            return defaultHandler;
        }

        return handler;
    }

    /**
     * Register a handler based on a request URI and method
     *
     * @param method
     * @param uri
     * @param handler
     * @return this
     */
    public HandlerManager register(Method method, String uri, Handler handler) {
        if (Method.ALL == method) {
            handlers.put(Method.GET.name() + uri, handler);
            handlers.put(Method.POST.name() + uri, handler);
            handlers.put(Method.PUT.name() + uri, handler);
            handlers.put(Method.DELETE.name() + uri, handler);
        } else {
            handlers.put(method.name() + uri, handler);
        }

        return this;
    }

}
