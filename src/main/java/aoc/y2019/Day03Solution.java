package aoc.y2019;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import utils.AdventOfCode;
import utils.Input;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2019, day = 3, name = "Crossed Wires")
public class Day03Solution {

    @Test
    public void part1WithMockData() {
        assertEquals(159, closestIntersectionDistance(mockInput("""
                R75,D30,R83,U83,L12,D49,R71,U7,L72
                U62,R66,U55,R34,D71,R55,D58,R83
                """)));

        assertEquals(135, closestIntersectionDistance(mockInput("""
                R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
                U98,R91,D20,R16,D67,R40,U7,R15,U6,R7
                """)));
    }

    @Test
    public void part1() {
        assertEquals(1064, closestIntersectionDistance(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(30, shortestIntersectionPath(mockInput("""
                R8,U5,L5,D3
                U7,R6,D4,L4
                """)));

        assertEquals(610, shortestIntersectionPath(mockInput("""
                R75,D30,R83,U83,L12,D49,R71,U7,L72
                U62,R66,U55,R34,D71,R55,D58,R83
                """)));

        assertEquals(410, shortestIntersectionPath(mockInput("""
                R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
                U98,R91,D20,R16,D67,R40,U7,R15,U6,R7
                """)));
    }

    @Test
    public void part2() {
        assertEquals(25676, shortestIntersectionPath(input(this)));
    }

    private int closestIntersectionDistance(Input input) {
        var path1 = lineSegments(input.linesArray()[0]);
        var path2 = lineSegments(input.linesArray()[1]);

        var closest = Integer.MAX_VALUE;

        for (LineSegment lineSegment1 : path1) {
            for (LineSegment lineSegment2 : path2) {
                var intersection = lineSegment1.intersection(lineSegment2);
                if (intersection != null && intersection.x != 0 && intersection.y != 0) {
                    closest = Math.min(closest, Math.abs((int)intersection.x) + Math.abs((int)intersection.y));
                }
            }
        }

        return closest;
    }

    private int shortestIntersectionPath(Input input) {
        var path1 = lineSegments(input.linesArray()[0]);
        var path2 = lineSegments(input.linesArray()[1]);

        var minDistance = Integer.MAX_VALUE;

        var path1Distance = 0;
        for (LineSegment lineSegment1 : path1) {
            var path2Distance = 0;
            for (LineSegment lineSegment2 : path2) {
                var intersection = lineSegment1.intersection(lineSegment2);
                if (intersection != null && intersection.x != 0 && intersection.y != 0) {
                    var path1Total = path1Distance + (int) lineSegment1.p0.distance(intersection);
                    var path2Total = path2Distance + (int) lineSegment2.p0.distance(intersection);
                    minDistance = Math.min(path1Total + path2Total, minDistance);
                }
                path2Distance += (int) lineSegment2.getLength();
            }
            path1Distance += (int) lineSegment1.getLength();
        }

        return minDistance;
    }

    private Collection<LineSegment> lineSegments(String input) {
        var result = Lists.<LineSegment>newArrayList();
        var from = new Coordinate(0, 0);
        for (String step : input.split(",")) {
            var offset = Integer.parseInt(step.substring(1));
            var to = switch(step.charAt(0)) {
                case 'R' -> new Coordinate(from.x + offset, from.y);
                case 'L' -> new Coordinate(from.x - offset, from.y);
                case 'U' -> new Coordinate(from.x, from.y + offset);
                case 'D' -> new Coordinate(from.x, from.y - offset);
                default -> throw new IllegalStateException();
            };
            result.add(new LineSegment(from, to));
            from = to;
        }
        return result;
    }
}
