package nn1211.http;

import nn1211.http.server.HttpServer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static nn1211.http.HttpRequest.Method.GET;
import static nn1211.http.HttpResponse.*;
import static nn1211.http.Content.*;

/**
 * Default handler for all HTTP request
 *
 * @author nn1211
 *
 */
public class DefaultHandler implements Handler {

    @Override
    public HttpResponse handle(HttpRequest req) throws IOException {
        if (GET != req.method()) // DefaultHandler only support GET
        {
            return methodNotAllowed();
        }

        File file = new File(HttpServer.current().resourcePath() + req.uri());
        if (!file.exists()) {
            return notFound();
        }

        Type contentType;
        if (file.isDirectory()) { // Serve index.html for '/' and folder paths
            File indexFile = new File(file, "index.html");
            if (!indexFile.exists()) {
                return notFound();
            }

            contentType = Type.HTML;
            file = indexFile;
        } else {
            contentType = Type.from(req.uri());
        }

        if (null == contentType) {
            return unsupportedMediaType();
        }

        if (!file.canRead()) {
            return forbidden();
        }

        return ok(build(contentType, Files.readAllBytes(file.toPath())));
    }

}
