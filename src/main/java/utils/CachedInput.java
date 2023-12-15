package utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.writeString;

public class CachedInput implements Input {

  private final Path inputPath;

  public CachedInput(int year, int day) throws Exception {
    inputPath = Paths.get(format("input/%d/%d-input", year, day));

    if (!inputPath.toFile().exists()) {
      var parent = inputPath.getParent().toFile();
      if(!parent.exists()) {
        checkState(parent.mkdirs());
      }

      System.out.println("Requesting input file...");

      final var client = HttpClient.newBuilder()
          .sslContext(CustomSSLContext.disabledContext())
          .version(HttpClient.Version.HTTP_1_1)
          .followRedirects(HttpClient.Redirect.NORMAL)
          .connectTimeout(Duration.ofSeconds(20))
          .build();

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

  @Override
  public String text() {
    try {
      return readString(inputPath, UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String cookieHash() throws IOException {
    return readString(Paths.get("session"), UTF_8);
  }
}
