package aoc.y2020;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Direction;
import utils.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 12, name = "Rain Risk")
public class Day12Solution {

    private static final String MOCK = """
            F10
            N3
            F7
            R90
            F11
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(25, navigateShip(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(2458, navigateShip(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(286, navigateByWaypoint(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(145117, navigateByWaypoint(input(this)));
    }

    private int navigateShip(Input input) {
        var direction = Direction.EAST;
        var north = 0;
        var east = 0;

        for (String line : input.lines()) {
            var distance = Integer.parseInt(line.substring(1));
            switch (line.charAt(0)) {
                case 'N' -> north += distance;
                case 'S' -> north -= distance;
                case 'E' -> east += distance;
                case 'W' -> east -= distance;
                case 'L' -> direction = distance == 90 ? direction.turnLeft() :
                        distance == 180 ? direction.turnLeft().turnLeft() :
                                direction.turnRight();
                case 'R' -> direction = distance == 90 ? direction.turnRight() :
                        distance == 180 ? direction.turnRight().turnRight() :
                                direction.turnLeft();
                default -> {
                    switch (direction) {
                        case EAST -> east += distance;
                        case WEST -> east -= distance;
                        case NORTH -> north += distance;
                        case SOUTH -> north -= distance;
                    }
                }
            }
        }

        return Math.abs(east) + Math.abs(north);
    }

    record Point(int east, int north){
        Point right() {
            return new Point(this.north, this.east * -1);
        }

        Point aboutFace() {
            return right().right();
        }

        Point left() {
            return right().right().right();
        }
    }

    private int navigateByWaypoint(Input input) {
        var ship = new Point(0, 0);
        var waypoint = new Point(10, 1);

        for (String line : input.lines()) {
            var distance = Integer.parseInt(line.substring(1));
            switch (line.charAt(0)) {
                case 'N' -> waypoint = new Point(waypoint.east, waypoint.north + distance);
                case 'S' -> waypoint = new Point(waypoint.east, waypoint.north - distance);
                case 'E' -> waypoint = new Point(waypoint.east + distance, waypoint.north);
                case 'W' -> waypoint = new Point(waypoint.east - distance, waypoint.north);
                case 'L' -> waypoint = distance == 90 ? waypoint.left() :
                        distance == 180 ? waypoint.aboutFace() :
                                waypoint.right();
                case 'R' -> waypoint = distance == 90 ? waypoint.right() :
                        distance == 180 ? waypoint.aboutFace() :
                                waypoint.left();
                default -> {
                    ship = new Point(ship.east + (waypoint.east * distance),
                            ship.north + (waypoint.north * distance));
                }
            }
        }

        return Math.abs(ship.east) + Math.abs(ship.north);
    }
}
