package aoc.y2025;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import utils.AdventOfCode;
import utils.Combinations;
import utils.Input;
import utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.LongHolder.longHolder;

@AdventOfCode(year = 2025, day = 9, name = "Movie Theater")
public class Day09Solution {

    private static final String MOCK = """
            7,1
            11,1
            11,7
            9,7
            9,5
            2,5
            2,3
            7,3
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(50, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(4759531084L, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(24, solve2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(1539238860, solve2(input(this)));
    }

    private long solve(Input input) {
        var points = input.lines().stream()
                .map(this::coordinate)
                .toList();

        var max = longHolder(0);
        Combinations.forEachPair(points, (c1, c2) -> {
            max.set(Math.max(max.get(), area(c1, c2)));
        });

        return max.get();
    }

    private long solve2(Input input) {
        var points = input.lines().stream()
                .map(this::coordinate)
                .toList();

        var gf = new GeometryFactory();

        var pointArray = points.toArray(new Coordinate[points.size() + 1]);
        pointArray[points.size()] = pointArray[0].copy();  // close the ring

        // Linear rings are just line strings, they have no interior or area
        var shell = gf.createLinearRing(pointArray);
        // polygons have interior and area
        var polygon = gf.createPolygon(shell);
        // Improves performance
        var prepared = PreparedGeometryFactory.prepare(polygon);

        var max = longHolder(0);

        Combinations.forEachPair(points, (c1, c2) -> {
            var area = area(c1, c2);
            if (area > max.get() && prepared.covers(rect(c1, c2, gf))) {
                max.set(area);
            }
        });

        return max.get();
    }

    private Polygon rect(Coordinate c1, Coordinate c2, GeometryFactory gf) {
        var x1 = Math.min(c1.x, c2.x);
        var x2 = Math.max(c1.x, c2.x);
        var y1 = Math.min(c1.y, c2.y);
        var y2 = Math.max(c1.y, c2.y);

        return gf.createPolygon(new Coordinate[]{
                new Coordinate(x1, y1),
                new Coordinate(x2, y1),
                new Coordinate(x2, y2),
                new Coordinate(x1, y2),
                new Coordinate(x1, y1)
        });
    }

    private long area(Coordinate c1, Coordinate c2) {
        var dx = Math.abs(c1.x - c2.x) + 1L;
        var dy = Math.abs(c1.y - c2.y) + 1L;
        return (long) (dx * dy);
    }

    private Coordinate coordinate(String line) {
        var nums = Utils.parseInts(line);
        return new Coordinate(nums.get(0), nums.get(1));
    }
}
