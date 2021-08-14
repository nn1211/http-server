package nn1211.http.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import nn1211.http.HandlerManager;
import nn1211.http.HttpHeader;

import nn1211.http.HttpHeader.Name;
import nn1211.http.HttpRequest;
import nn1211.http.HttpRequest.Method;
import nn1211.http.HttpResponse;

/**
 * A simple HTTP Server
 *
 * @author nn1211
 *
 */
public class HttpServer {

    private static HttpServer CURRENT = null;

    /**
     * We haven't implemented persistent connection, so we need to send this
     * back to the client for every response messages
     */
    private static final HttpHeader CONNECTION_CLOSE_HEADER = HttpHeader
            .from(Name.CONNECTION, "close");

    private final int port;
    private final ServerSocket listener;

    private String resourcesPath = ".";

    private HttpServer(int port) throws IOException {
        listener = new ServerSocket(port);
        this.port = port;
    }

    /**
     * Create a new {@link HttpServer} instance with a specified port
     *
     * @param port
     * @return a new {@link HttpServer} instance
     * @throws IOException
     */
    public static HttpServer withPort(int port) throws IOException {
        return CURRENT = new HttpServer(port);
    }

    /**
     * Gets the current instance
     *
     * @return the current instance or null
     */
    public static HttpServer current() {
        return CURRENT;
    }

    /**
     * Set the path of the resources of this server
     *
     * @param path
     * @return this
     * @throws FileNotFoundException
     */
    public HttpServer resourcePath(String resourcesPath)
            throws FileNotFoundException {

        File resourceDir = new File(resourcesPath);
        if (!resourceDir.exists()) {
            throw new FileNotFoundException(resourceDir.getAbsolutePath());
        }

        this.resourcesPath = resourcesPath;
        return this;
    }

    /**
     * Get the resource path
     *
     * @return the resource path
     */
    public final String resourcePath() {
        return resourcesPath;
    }

    /**
     * Start this server
     */
    public final void start() {
        new Thread(() -> {
            while (true) {
                listen();
            }
        }).start();

        System.out.println("HttpServer - Start listen on port " + port
                + " and resource path '"
                + new File(resourcesPath).getAbsolutePath() + "'");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop()));
    }

    /**
     * Stop this server
     */
    public final void stop() {
        try {
            listener.close();
            System.out.println("HttpServer - Stopped");
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void listen() {
        try {
            final Socket connection = listener.accept();
            new Thread(() -> handle(connection)).start();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void handle(Socket connection) {
        try {
            HttpRequest req = HttpRequest.parse(connection.getInputStream());

            HttpResponse resp;
            if (null == req.method() || null == req.uri()) {
                return;
            }

            System.out.println(req);
            if (Method.UNSUPPORTED == req.method()) {
                resp = HttpResponse.methodNotAllowed();
            } else {
                resp = HandlerManager.INSTANCE.get(req).handle(req);
            }

            writeResponse(connection.getOutputStream(), resp);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        } finally {
            // TODO: need to implement persistence connection instead
            try {
                connection.close();
            } catch (IOException iex) {
                iex.printStackTrace(System.err);
            }
        }
    }

    private void writeResponse(OutputStream outStream, HttpResponse resp)
            throws IOException {

        try (outStream) {
            // We haven't implemented persistent connection,
            // so we need to send Connection: close header to client
            resp.headers().put(CONNECTION_CLOSE_HEADER);

            outStream.write(resp.toBytes());
            outStream.flush();

            System.out.println(resp.statusCode() + "\n\n");
        }
    }
}
