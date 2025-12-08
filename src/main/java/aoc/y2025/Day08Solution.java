package aoc.y2025;

import com.google.common.collect.*;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector3;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingDouble;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2025, day = 8, name = "Playground")
public class Day08Solution {

    private static final String MOCK = """
            162,817,812
            57,618,57
            906,360,560
            592,479,940
            352,342,300
            466,668,158
            542,29,236
            431,825,988
            739,650,466
            52,470,668
            216,146,977
            819,987,18
            117,168,530
            805,96,715
            346,949,466
            970,615,88
            941,993,340
            862,61,35
            984,92,344
            425,690,689
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(40, solve(mockInput(MOCK), 10));
    }

    @Test
    public void part1() {
        assertEquals(135169, solve(input(this), 1000));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(25272, solve2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(302133440, solve2(input(this)));
    }

    private int solve(Input input, int numConnections) {
        var junctions = input.lines().stream()
                .map(this::vector)
                .collect(Collectors.toSet());

        var junctionPairs = sortedPairs(junctions);

        var circuits = Lists.<Set<Vector3>>newArrayList();

        for (Set<Vector3> next : Iterables.limit(junctionPairs, numConnections)) {
            add(circuits, next);
        }

        return circuits.stream().map(Set::size)
                .sorted((o1, o2) -> Integer.compare(o2, o1))
                .limit(3)
                .reduce((left, right) -> left * right)
                .orElse(0);
    }

    private long solve2(Input input) {
        var junctions = input.lines().stream()
                .map(this::vector)
                .collect(Collectors.toSet());

        var junctionPairs = sortedPairs(junctions);
        var circuits = Lists.<Set<Vector3>>newArrayList();

        for (Set<Vector3> next : junctionPairs) {
            add(circuits, next);

            if (circuits.size() == 1 && circuits.getFirst().size() == junctions.size()) {
                return (long) next.stream()
                        .mapToDouble(Vector3::x)
                        .reduce((left, right) -> left * right)
                        .orElse(0.0);
            }
        }

        return 0;
    }

    private Collection<Set<Vector3>> sortedPairs(Set<Vector3> junctions) {
        var result = Sets.<Set<Vector3>>newTreeSet(comparingDouble(this::distance));

        for (Vector3 c1 : junctions) {
            for (Vector3 c2 : junctions) {
                if (c1 != c2) {
                    result.add(Set.of(c1, c2));
                }
            }
        }

        return result;
    }


    private void add(List<Set<Vector3>> circuits, Set<Vector3> pair) {
        var matched = Maps.<Set<Vector3>, Set<Vector3>>newHashMap();
        for (Set<Vector3> circuit : circuits) {
            var intersection = Sets.intersection(pair, circuit);
            if (!intersection.isEmpty()) {
                matched.put(circuit, intersection);
            }
        }

        if (matched.isEmpty()) {
            circuits.add(Sets.newHashSet(pair));
        } else if (matched.size() == 1) {
            matched.forEach((circuit, intersect) -> {
                if (intersect.size() == 1) {
                    circuit.addAll(pair);
                }
            });
        } else {
            var iter = matched.keySet().iterator();
            var c1 = iter.next();
            var c2 = iter.next();
            circuits.remove(c2);
            c1.addAll(c2);
        }
    }

    private double distance(Set<Vector3> junctions) {
        var iter = junctions.iterator();
        var v1 = iter.next();
        var v2 = iter.next();
        return distance(v1, v2);
    }

    private double distance(Vector3 vector, Vector3 other) {
        var tmp = Math.pow(Math.abs(vector.x - other.x), 2) +
                Math.pow(Math.abs(vector.y - other.y), 2) +
                Math.pow(Math.abs(vector.z - other.z), 2);
        return Math.sqrt(tmp);
    }

    private Vector3 vector(String coordinate) {
        var points = Utils.parseInts(coordinate);
        return new Vector3(points.get(0), points.get(1), points.get(2));
    }
}
