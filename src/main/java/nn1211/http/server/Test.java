package nn1211.http.server;

import java.io.IOException;

/**
 * {@link HttpServer} testing class
 *
 * @author nn1211
 *
 */
public class Test {

    /**
     * Testing point
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        HttpServer.withPort(50505).resourcePath("/Users/nghianguyen/Desktop")
                .start();
    }
}
