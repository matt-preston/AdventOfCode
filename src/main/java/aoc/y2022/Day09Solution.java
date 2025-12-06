package aoc.y2022;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Direction;
import utils.Input;
import utils.Vector2;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2022, day = 9, name = "Rope Bridge")
public class Day09Solution {

    private static final String MOCK = """
            R 4
            U 4
            L 3
            D 1
            R 4
            D 1
            L 5
            R 2
            """;

    private static final Map<String, Direction> DIR = Map.of(
            "R", Direction.EAST,
            "L", Direction.WEST,
            "U", Direction.NORTH,
            "D", Direction.SOUTH
    );

    @Test
    public void part1WithMockData() {
        assertEquals(13, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(6376, solve(input(this)));
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

        var head = new Vector2(0, 0);
        var tail = new Vector2(0, 0);

        var set = Sets.<Vector2>newHashSet();
        set.add(tail);

//        System.out.printf("H:%s T:%s%n", head, tail);

        for (String command : input.lines()) {
//            System.out.println("== " + command + " ==");

            var parts = command.split("\\s");
            var direction = DIR.get(parts[0]);
            var distance = Integer.parseInt(parts[1]);

            for (int i = 0; i < distance; i++) {
                head = head.move(direction);

                if (Math.abs(head.x() - tail.x()) < 2 && Math.abs(head.y() - tail.y()) < 2) {
                    // do nothing, already adjacent or on top of each other
                } else if (head.x() == tail.x() || head.y() == tail.y()) {
                    // tail follows
                    tail = tail.move(direction);
                } else {
                    tail = tail.move(direction);
                    var extra = switch(direction) {
                        case NORTH, SOUTH -> head.x() > tail.x() ? Direction.EAST : Direction.WEST;
                        case EAST, WEST -> head.y() > tail.y() ? Direction.SOUTH : Direction.NORTH;
                    };
                    tail = tail.move(extra);
                }

//                System.out.printf("H:%s T:%s%n", head, tail);
                set.add(tail);
            }
        }

        return set.size();
    }

}
