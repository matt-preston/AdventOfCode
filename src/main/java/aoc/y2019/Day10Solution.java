package aoc.y2019;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.algorithm.RobustLineIntersector;
import org.locationtech.jts.geom.Coordinate;
import utils.AdventOfCode;
import utils.Input;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.atan;
import static java.lang.Math.toDegrees;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2019, day = 10, name = "Monitoring Station")
public class Day10Solution {

    @Test
    public void testAngles() {
        var origin = new Coordinate(11, 13);

        assertEquals(0.0, angle(origin, new Coordinate(11, 12)));
        assertEquals(45.0, angle(origin, new Coordinate(12, 12)));
        assertEquals(90.0, angle(origin, new Coordinate(17, 13)));
        assertEquals(135.0, angle(origin, new Coordinate(12, 14)));
        assertEquals(180.0, angle(origin, new Coordinate(11, 17)));
        assertEquals(225.0, angle(origin, new Coordinate(10, 14)));
        assertEquals(270.0, angle(origin, new Coordinate(8, 13)));
        assertEquals(315.0, angle(origin, new Coordinate(10, 12)));
    }

    @Test
    public void part1WithSampleData() {
        assertEquals(8, maxVisibleAsteroids(mockInput("""
                .#..#
                .....
                #####
                ....#
                ...##
                """)));
    }

    @Test
    public void part1WithSampleData2() {
        assertEquals(35, maxVisibleAsteroids(mockInput("""
                #.#...#.#.
                .###....#.
                .#....#...
                ##.#.#.#.#
                ....#.#.#.
                .##..###.#
                ..#...##..
                ..##....##
                ......#...
                .####.###.
                """)));
    }

    @Test
    public void part1() {
        assertEquals(256, maxVisibleAsteroids(input(this)));
    }

    @Test
    public void part2WithSampleData() {
        assertEquals(802, vaporizeAsteroids(mockInput("""
                .#..##.###...#######
                ##.############..##.
                .#.######.########.#
                .###.#######.####.#.
                #####.##.#.##.###.##
                ..#####..#.#########
                ####################
                #.####....###.#.#.##
                ##.#################
                #####.##.###..####..
                ..######..##.#######
                ####.##.####...##..#
                .#####..#.######.###
                ##...#.##########...
                #.##########.#######
                .####.#.###.###.#.##
                ....##.##.###..#####
                .#.#.###########.###
                #.#.#.#####.####.###
                ###.##.####.##.#..##
                """), new Coordinate(11, 13)));
    }

    @Test
    public void part2() {
        assertEquals(1707, vaporizeAsteroids(input(this), new Coordinate(29, 28)));
    }

    private int maxVisibleAsteroids(Input input) {
        final var asteroids = asteroids(input);

        final var asteroidsWithLineOfSight = Sets.combinations(asteroids, 2).stream()
                .filter(pair -> hasLineOfSight(pair, asteroids))
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(HashMultiset::create));

        return asteroidsWithLineOfSight.stream()
                .mapToInt(asteroidsWithLineOfSight::count)
                .max()
                .orElseThrow();
    }

    private int vaporizeAsteroids(Input input, Coordinate origin) {
        var asteroids = asteroids(input);
        asteroids.remove(origin);

        var visible = Lists.<Coordinate>newArrayList();
        for (Coordinate to : asteroids) {
            if (hasLineOfSight(ImmutableSet.of(origin, to), asteroids)) {
                visible.add(to);
            }
        }

        visible.sort((o1, o2) -> Double.compare(angle(origin, o1), angle(origin, o2)));

        final var twoHundredth = visible.get(199);
        return (int) ((twoHundredth.x * 100) + twoHundredth.y);
    }

    private boolean hasLineOfSight(Set<Coordinate> pair, Set<Coordinate> asteroids) {
        final var lineIntersector = new RobustLineIntersector();
        final var iterator = pair.iterator();
        final var a1 = iterator.next();
        final var a2 = iterator.next();

        for (Coordinate asteroid : asteroids) {
            if (!pair.contains(asteroid)) {
                lineIntersector.computeIntersection(asteroid, a1, a2);
                if (lineIntersector.hasIntersection()) {
                    return false;
                }
            }
        }

        return true;
    }

    private double angle(Coordinate origin, Coordinate point) {
        var angle = toDegrees(atan((point.x - origin.x) / (origin.y - point.y)));
        if (point.x >= origin.x && point.y > origin.y) {
            angle += 180; // quadrant 2
        } else if (point.x < origin.x && point.y > origin.y) {
            angle += 180; // quadrant 3
        } else if (point.x < origin.x && point.y <= origin.y) {
            angle += 360; // quadrant 3
        }
        return angle;
    }

    private Set<Coordinate> asteroids(Input input) {
        var asteroids = Sets.<Coordinate>newHashSet();
        final var lines = input.linesArray();
        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[y].length(); x++) {
                if (lines[y].charAt(x) == '#') {
                    asteroids.add(new Coordinate(x, y));
                }
            }
        }
        return asteroids;
    }
}
