package aoc.y2024;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.contains;
import static utils.Utils.get;

@AdventOfCode(year = 2024, day = 21, name = "Keypad Conundrum")
public class Day21Solution {

    private static final String MOCK = """
            029A
            980A
            179A
            456A
            379A
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(126384, complexity(mockInput(MOCK)));
    }


    // 138560 too high
    // 135260 too high
    @Test
    public void part1() {
        assertEquals(0, complexity(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(0, complexity(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(0, complexity(input(this)));
    }

    private int complexity(Input input) {
        return input.lines().stream()
                .mapToInt(this::complexity)
                .sum();
    }

    @Test
    public void debug() {
        complexity("456A");
    }

    private int complexity(String code) {
        var sequence1 = keypadSequence(NUMERIC_KEYPAD, code);
        System.out.printf("%s => %s%n", code, sequence1);

        var sequence2 = keypadSequence(DIRECTIONAL_KEYPAD, sequence1);
        System.out.printf("%s => %s%n", code, sequence2);

        var sequence3 = keypadSequence(DIRECTIONAL_KEYPAD, sequence2);
        System.out.printf("%s => %s%n", code, sequence3);

        var numericPart = Integer.parseInt(code.replaceAll("\\D", ""));

        System.out.printf("%d * %d%n", sequence3.length(), numericPart);
        System.out.println();

        return numericPart * sequence3.length();
    }


    private static final char[][] NUMERIC_KEYPAD = Utils.matrix("""
            789
            456
            123
             0A
            """);

    private static final char[][] DIRECTIONAL_KEYPAD = Utils.matrix("""
             ^A
            <v>
            """);


    private String keypadSequence(char[][] map, String value) {
        var start = Utils.find(map, 'A');

        var result = "";

        for (char c : value.toCharArray()) {
            var target = Utils.find(map, c);
            result += keypadSequence(map, start, target);
            start = target;
        }

        return result;
    }

    enum Direction {
        EAST, WEST, NORTH, SOUTH;

        List<BFSState> next(BFSState state) {
            var result = Lists.<BFSState>newArrayList();
            result.add(new BFSState(state.position.add(vector()), this, state.turns())); // same direction

            if (state.turns() < 1) {
                for (var d : Direction.values()) {
                    if (d != this) {
                        result.add(new BFSState(state.position.add(d.vector()), d, state.turns() + 1)); // new direction
                    }
                }
            }

            return result;
        }

        Vector2 vector() {
            return switch (this) {
                case NORTH -> new Vector2(0, -1);
                case SOUTH -> new Vector2(0, 1);
                case EAST -> new Vector2(1, 0);
                case WEST -> new Vector2(-1, 0);
            };
        }
    }


    record BFSState(Vector2 position, Direction direction, int turns) {
        List<BFSState> next() {
            return direction().next(this);
        }

        String command() {
            return switch (direction()) {
                case NORTH -> "^";
                case SOUTH -> "v";
                case EAST -> ">";
                case WEST -> "<";
            };
        }
    }

    private String keypadSequence(char[][] map, Vector2 start, Vector2 target) {
        var frontier = Lists.<BFSState>newLinkedList();
        frontier.add(new BFSState(start, Direction.EAST, 0));
        frontier.add(new BFSState(start, Direction.WEST, 0));
        frontier.add(new BFSState(start, Direction.SOUTH, 0));
        frontier.add(new BFSState(start, Direction.NORTH, 0));

        var parents = Maps.<BFSState, BFSState>newHashMap();

        while (!frontier.isEmpty()) {
            var current = frontier.poll();

            if (current.position().equals(target)) {
                var node = current;
                var path = "A";
                while (parents.containsKey(node)) {
                    var parent = parents.get(node);
                    path += node.command();

                    if (parent.position().equals(start)) {
                        break;
                    }

                    node = parent;
                }

                return new StringBuilder(path).reverse().toString();
            }

            for (BFSState next : current.next()) {
                if (contains(map, next.position()) && get(map, next.position()) != ' ') {
                    frontier.add(next);
                    parents.put(next, current);
                }
            }
        }

        return "X";
    }
}
