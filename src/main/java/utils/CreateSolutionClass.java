package utils;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

// TODO: example!
public class CreateSolutionClass {

    public static void main(String[] args) throws IOException {
        var year = Integer.parseInt(args[0]);
        var day = Integer.parseInt(args[1]);

        checkState(year > 2014 && year <= Calendar.getInstance().get(Calendar.YEAR), "[" + year + "] is not a valid year");
        checkState(day > 0 && day < 26, "[" + day + "] is not a valid day");

        var path = format("src/main/java/aoc/y%d/Day%02dSolution.java", year, day);
        var file = new File(path);

        if (!file.getParentFile().exists()) {
            checkState(file.getParentFile().mkdirs());
        }

        if (file.exists()) {
            throw new IllegalStateException("Solution class already exists");
        }

        var puzzle = puzzle(year, day);

        var template = """
                package aoc.y%d;
                
                import utils.AdventOfCode;
                import org.junit.jupiter.api.Test;
                import utils.Input;
                
                import static org.junit.jupiter.api.Assertions.assertEquals;
                import static utils.Input.input;
                import static utils.Input.mockInput;
                
                @AdventOfCode(year = %d, day = %d, name = "%s")
                public class Day%02dSolution {
                
                    private static final String MOCK = ""\"
                            TODO
                            ""\";
                
                    @Test
                    public void part1WithMockData() {
                        assertEquals(0, solve(mockInput(MOCK)));
                    }
                
                    @Test
                    public void part1() {
                        assertEquals(0, solve(input(this)));
                    }
                
                    @Test
                    public void part2WithMockData() {
                        assertEquals(0, solve(mockInput(MOCK)));
                    }
                
                    @Test
                    public void part2() {
                        assertEquals(0, solve(input(this)));
                    }
                
                    private int solve(Input input) {
                        return 0;
                    }
                }
                """;

        var code = format(template, year, year, day, puzzleName(puzzle), day);
        System.out.println(code);

        Files.asCharSink(file, StandardCharsets.UTF_8).write(code);
    }

    private static String puzzle(int year, int day) {
        try (var client = HttpClient.newBuilder().build()) {
            return client.send(HttpRequest.newBuilder()
                    .uri(URI.create(format("https://adventofcode.com/%d/day/%d", year, day)))
                    .build(), HttpResponse.BodyHandlers.ofString())
                    .body();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String puzzleName(String puzzle) {
        var pattern = Pattern.compile("---.*: (.*) ---", Pattern.DOTALL);
        var matcher = pattern.matcher(puzzle);
        return matcher.find() ? matcher.group(1) : "";
    }
}
