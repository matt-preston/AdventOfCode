package aoc.y2024;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import utils.AdventOfCode;
import utils.Input;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 4, name = "Ceres Search")
public class Day04Solution {

    private static final String MOCK = """
            MMMSXXMASM
            MSAMXMSMSA
            AMXSXMAAMM
            MSAMASMSMX
            XMASAMXAMM
            XXAMMXXAMA
            SMSMSASXSS
            SAXAMASAAA
            MAMMMXMMMM
            MXMXAXMASX
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(18, countXmas(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(2524, countXmas(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(9, countX_mas(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(1873, countX_mas(input(this)));
    }

    private Map<Coordinate, String> parseInput(Input input) {
        var map = Maps.<Coordinate, String>newHashMap();
        var lines = input.linesArray();
        for (int y = 0; y < lines.length; y++) {
            var line = lines[y].toCharArray();
            for (int x = 0; x < line.length; x++) {
                map.put(new Coordinate(x, y), String.valueOf(line[x]));
            }
        }
        return map;
    }

    private int countXmas(Input input) {
        var map = parseInput(input);

        var translations = List.<UnaryOperator<Coordinate>>of(
                c -> new Coordinate(c.x + 1, c.y),          // right
                c -> new Coordinate(c.x + 1, c.y + 1),   // right down
                c -> new Coordinate(c.x, c.y + 1),          // down
                c -> new Coordinate(c.x - 1, c.y + 1),   // left down
                c -> new Coordinate(c.x - 1, c.y),          // left
                c -> new Coordinate(c.x - 1, c.y - 1),   // up left
                c -> new Coordinate(c.x, c.y - 1),          // up
                c -> new Coordinate(c.x + 1, c.y - 1)    // up right
        );

        var count = 0;
        for (Coordinate c : map.keySet()) {
            for (UnaryOperator<Coordinate> t : translations) {
                if (xmas(map, c, t)) {
                    count++;
                }
            }
        }

        return count;
    }

    private int countX_mas(Input input) {
        var map = parseInput(input);

        var translations = List.<UnaryOperator<Coordinate>>of(
                c -> new Coordinate(c.x + 1, c.y + 1),   // right down
                c -> new Coordinate(c.x - 1, c.y + 1),   // left down
                c -> new Coordinate(c.x - 1, c.y - 1),   // up left
                c -> new Coordinate(c.x + 1, c.y - 1)    // up right
        );

        var set = Sets.<Coordinate>newHashSet();

        var count = 0;
        for (Coordinate coordinate : map.keySet()) {
            for (UnaryOperator<Coordinate> t : translations) {
                var c = mas(map, coordinate, t);
                if (c != null && !set.add(c)) {
                    count++;
                }
            }
        }

        return count;
    }

    private boolean xmas(Map<Coordinate, String> map, Coordinate c, UnaryOperator<Coordinate> transform) {
        if ("X".equals(map.get(c))) {
            c = transform.apply(c);
            if ("M".equals(map.get(c))) {
                c = transform.apply(c);
                if ("A".equals(map.get(c))) {
                    return "S".equals(map.get(transform.apply(c)));
                }
            }
        }
        return false;
    }

    private Coordinate mas(Map<Coordinate, String> map, Coordinate c, UnaryOperator<Coordinate> transform) {
        if ("M".equals(map.get(c))) {
            c = transform.apply(c);
            if ("A".equals(map.get(c))) {
                if ("S".equals(map.get(transform.apply(c)))) {
                    return c;
                }
            }
        }
        return null;
    }
}
