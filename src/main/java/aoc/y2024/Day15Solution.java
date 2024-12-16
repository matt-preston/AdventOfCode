package aoc.y2024;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.find;
import static utils.Utils.mirrorVertically;

@AdventOfCode(year = 2024, day = 15, name = "Warehouse Woes")
public class Day15Solution {

    private static final String SIMPLE = """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########
            
            <^^>>>vv<v>>v<<
            """;

    private static final String MOCK = """
            ##########
            #..O..O.O#
            #......O.#
            #.OO..O.O#
            #..O@..O.#
            #O#..O...#
            #O..O..O.#
            #.OO.O.OO#
            #....O...#
            ##########
            
            <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
            vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
            ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
            <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
            ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
            ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
            >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
            <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
            ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
            v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
            """;

    @Test
    public void part1WithSimpleMockData() {
        assertEquals(2028, sumOfBoxPositions(mockInput(SIMPLE)));
    }

    @Test
    public void part1WithMockData() {
        assertEquals(10092, sumOfBoxPositions(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(1441031, sumOfBoxPositions(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(9021, sumOfBoxPositions2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(1425169, sumOfBoxPositions2(input(this)));
    }

    private int sumOfBoxPositions(Input input) {
        var matrix = Utils.matrix(input.section(0).text());

        for (char c : input.section(1).text().replaceAll("[\r\n]", "").toCharArray()) {
            var vector = switch (c) {
                case '<' -> new Vector2(-1, 0);
                case '>' -> new Vector2(1, 0);
                case '^' -> new Vector2(0, -1);
                case 'v' -> new Vector2(0, 1);
                default -> throw new IllegalStateException("[" + c + "]");
            };
            moveIfPossible(matrix, find(matrix, '@'), vector);
        }

        return score(matrix);
    }

    private int sumOfBoxPositions2(Input input) {
        var map = Utils.matrix(expandMap(input.section(0).text()));

        for (char c : input.section(1).text().replaceAll("[\r\n]", "").toCharArray()) {
            switch (c) {
                case '<' -> moveIfPossible(map, find(map, '@'), new Vector2(-1, 0));
                case '>' -> moveIfPossible(map, find(map, '@'), new Vector2(1, 0));
                case '^' -> mirrorVertically(moveRobotSouth(mirrorVertically(map)));
                case 'v' -> moveRobotSouth(map);
                default -> throw new IllegalStateException("[" + c + "]");
            };
        }

        return score(map);
    }

    private int score(char[][] map) {
        var sum = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == '[' || map[y][x] == 'O') {
                    sum += (y * 100) + x;
                }
            }
        }
        return sum;
    }

    private char[][] moveRobotSouth(char[][] map) {
        var robot = Utils.find(map, '@');
        var visited = Sets.<Vector2>newHashSet();

        if (canMoveSouth(map, robot, visited)) {
            var startPoints = Sets.<Vector2>newHashSet();
            for (Vector2 v1 : visited) {
                if (!visited.contains(v1.north())) {
                    startPoints.add(v1);
                }
            }

            for (Vector2 startPoint : startPoints) {
                shuffleSouth(map, startPoint);
            }
        }

        return map;
    }

    private String expandMap(String map) {
        return map.replaceAll("#", "##")
                .replaceAll("O", "[]")
                .replaceAll("\\.", "..")
                .replaceAll("@", "@.");
    }

    private boolean moveIfPossible(char[][] map, Vector2 position, Vector2 vector) {
        var next = position.add(vector);

        if (Utils.get(map, position) == '.') {
            return true;
        } else if (Utils.get(map, position) == '#') {
            return false;
        } else if (moveIfPossible(map, next, vector)) {
            var temp = map[next.y()][next.x()];
            map[next.y()][next.x()] = map[position.y()][position.x()];
            map[position.y()][position.x()] = temp;
            return true;
        }
        return false;
    }

    private void shuffleSouth(char[][] map, Vector2 start) {
        var freeSpace = findNextFreeSpace(map, start, new Vector2(0, 1));
        for (int y = freeSpace.y(); y > start.y(); y--) {
            map[y][start.x()] = map[y - 1][start.x()];
        }
        map[start.y()][start.x()] = '.';
    }

    private boolean canMoveSouth(char[][] map, Vector2 start, Set<Vector2> visited) {
        visited.add(start);

        var below = map[start.y() + 1][start.x()];
        if (below == '#') {
            return false;
        } else if (below == '.') {
            return true;
        } else if (below == ']') {
            return canMoveSouth(map, start.south(), visited) && canMoveSouth(map, start.south().west(), visited);
        } else { // must be [
            return canMoveSouth(map, start.south(), visited) && canMoveSouth(map, start.south().east(), visited);
        }
    }

    private Vector2 findNextFreeSpace(char[][] map, Vector2 start, Vector2 vector) {
        var next = start;
        do {
            next = next.add(vector);
            if (Utils.get(map, next) == '.') {
                return next;
            }
        } while (Utils.get(map, next) != '#');

        return null;
    }
}
