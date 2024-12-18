package utils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.writeString;

class RemoteInput implements Input {

    private static AtomicBoolean FIRST = new AtomicBoolean(true);

    private final String text;

    RemoteInput(int year, int day) throws Exception {
        var inputPath = Paths.get(format("input/%d/%02d-input", year, day));
        if (!inputPath.toFile().exists()) {
            var parent = inputPath.getParent().toFile();
            if (!parent.exists()) {
                checkState(parent.mkdirs());
            }

            System.out.println("Requesting input file...");

            if(!FIRST.get()) {
                Thread.sleep(10_000L);
            }

            FIRST.set(false);

            try (var client = httpClient()) {
                final var request = HttpRequest.newBuilder()
                        .uri(URI.create(format("https://adventofcode.com/%d/day/%d/input", year, day)))
                        .header("Cookie", "session=" + cookieHash())
                        .timeout(Duration.ofMinutes(1))
                        .GET()
                        .build();

                final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    writeString(inputPath, response.body(), UTF_8);
                } else {
                    throw new IllegalStateException("Got an invalid HTTP status reading the input file: " + response.statusCode());
                }
            }
        }
        text = readString(inputPath, UTF_8).trim();
    }

    @Override
    public String text() {
        return text;
    }

    private String cookieHash() throws IOException {
        return readString(Paths.get("session"), UTF_8);
    }

    private HttpClient httpClient() throws NoSuchAlgorithmException, KeyManagementException {
        return HttpClient.newBuilder()
                .sslContext(disabledContext())
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    private SSLContext disabledContext() throws NoSuchAlgorithmException, KeyManagementException {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        return sslContext;
    }
}
