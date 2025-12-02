package aoc.y2020;

import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 2, name = "Password Philosophy")
public class Day02Solution {

    private static final String MOCK = """
            1-3 a: abcde
            1-3 b: cdefg
            2-9 c: ccccccccc
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(2, validPasswordsPart1(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(586, validPasswordsPart1(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(1, validPasswordsPart2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(352, validPasswordsPart2(input(this)));
    }

    private long validPasswordsPart1(Input input) {
        return input.lines().stream()
                .filter(l -> valid(l, this::part1Check))
                .count();
    }

    private long validPasswordsPart2(Input input) {
        return input.lines().stream()
                .filter(l -> valid(l, this::part2Check))
                .count();
    }

    private static final Pattern REGEX = Pattern.compile("(\\d+)-(\\d+) (\\w): (\\w+)");

    private boolean valid(String line, Checker checker) {
        var matcher = REGEX.matcher(line);
        if (matcher.matches()) {
            int a = Integer.parseInt(matcher.group(1));
            int b = Integer.parseInt(matcher.group(2));
            char c = matcher.group(3).charAt(0);
            var password = matcher.group(4);
            return checker.valid(a, b, c, password);
        }
        return false;
    }

    private interface Checker {
        boolean valid(int a, int b, char c, String password);
    }

    private boolean part1Check(int a, int b, char c, String password) {
        int count = 0;
        for(int i = 0; i < password.length(); i++) {
            if (password.charAt(i) == c) {
                count++;
                if (count > b) {
                    return false;
                }
            }
        }
        return count >= a;
    }

    private boolean part2Check(int a, int b, char c, String password) {
        return (password.charAt(a - 1) == c) ^ (password.charAt(b - 1) == c);
    }
}
