package aoc.y2023;

import com.google.common.collect.Sets;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import static com.google.common.base.Preconditions.checkState;
import static org.jgrapht.Graphs.addEdgeWithVertices;
import static org.jgrapht.Graphs.getOppositeVertex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.triangleSeries;

@AdventOfCode(year = 2023, day = 21, name = "Step Counter")
public class Day21Solution {

    private record GardenMap(SimpleGraph<Vector2, DefaultEdge> graph, Vector2 start, int maxX, int maxY) {
    }

    enum Parity {
        ODD, EVEN;

        public static Parity of(int value) {
            return ODD.is(value) ? ODD : EVEN;
        }

        public boolean is(int value) {
            return switch (this) {
                case ODD -> value % 2 == 1;
                case EVEN -> value % 2 == 0;
            };
        }

        public Parity flip() {
            return this == ODD ? EVEN : ODD;
        }
    }

    @Test
    public void part1WithMockData() {
        var map = gardenMap(mockInput("""
                ...........
                .....###.#.
                .###.##..#.
                ..#.#...#..
                ....#.#....
                .##..S####.
                .##..#...#.
                .......##..
                .##.#.####.
                .##..##.##.
                ...........
                """));

        assertEquals(16, numPlotsVisitedBFS(map, 6, map.start()));
    }

    @Test
    public void part1() {
        var map = gardenMap(input(this));
        assertEquals(3_724, numPlotsVisitedBFS(map, 64, map.start()));
    }

    @Test
    public void part2() {
        final var map = gardenMap(input(this));
        assertEquals(3_821, numPlotsVisited(map, 65));
        assertEquals(34_234, numPlotsVisited(map, 196));
        assertEquals(94_963, numPlotsVisited(map, 327));
        assertEquals(186_008, numPlotsVisited(map, 458));
        assertEquals(620_348_631_910_321L, numPlotsVisited(map, 26_501_365));
    }

    private long numPlotsVisited(GardenMap map, int numSteps) {
        checkState((numSteps - (map.maxX / 2)) % map.maxX == 0, "algorithm only works for step counts that are multiples of a garden plot");

        // number of maps from original middle to the edge
        final var multiplier = (numSteps - (map.maxX / 2)) / map.maxX;

        var diamond = numPlotsVisitedBFS(map, (map.maxX / 2), map.start());

        var count = 0L;

        if (multiplier > 0) {
            var odd = totalVisitedCount(map, Parity.ODD);
            var even = totalVisitedCount(map, Parity.EVEN);

            count += 2 * (diamond + odd); // top+bottom & left+right vertices - always odd
            count += multiplier * visitedCountForEvenCorners(map); // even edge parts corners
            count += (multiplier - 1) * (3 * odd + diamond); // odd edge parts

            var numEven = triangleSeries(multiplier) + triangleSeries(multiplier - 1);
            var numOdd = triangleSeries(multiplier - 1) + triangleSeries(multiplier - 2);

            count += numEven * even;
            count += numOdd * odd;
        } else {
            count += diamond;
        }

        return count;
    }

    private long visitedCountForEvenCorners(GardenMap map) {
        var numSteps = (map.maxX() / 2) - 1;
        var count = 0L;
        count += numPlotsVisitedBFS(map, numSteps, new Vector2(0, 0));
        count += numPlotsVisitedBFS(map, numSteps, new Vector2(map.maxX() - 1, 0));
        count += numPlotsVisitedBFS(map, numSteps, new Vector2(map.maxX() - 1, map.maxY() - 1));
        count += numPlotsVisitedBFS(map, numSteps, new Vector2(0, map.maxY() - 1));
        return count;
    }

    private long totalVisitedCount(GardenMap map, Parity startingParity) {
        final var graph = map.graph();

        var count = 0L;
        for (int y = 0; y < map.maxY; y++) {
            for (int x = 0; x < map.maxX; x++) {
                if (startingParity.is(x) && graph.containsVertex(new Vector2(x, y))) {
                    count++;
                }
            }
            startingParity = startingParity.flip();
        }

        return count;
    }

    private int numPlotsVisitedBFS(GardenMap gardenMap, int steps, Vector2 start) {
        final var graph = gardenMap.graph();

        var visited = Sets.<Vector2>newHashSet();
        var frontier = Sets.<Vector2>newHashSet();
        frontier.add(start);

        var count = 0;

        for (int step = 0; step < steps; step++) {
            visited = Sets.newHashSet();

            for (final Vector2 current : frontier) {
                for (DefaultEdge edge : graph.edgesOf(current)) {
                    visited.add(getOppositeVertex(graph, edge, current));
                }
            }

            count = visited.size();
            frontier = visited;
        }

        return count;
    }

    private GardenMap gardenMap(final Input input) {
        final var matrix = Utils.matrix(input.text());
        final var graph = new SimpleGraph<Vector2, DefaultEdge>(DefaultEdge.class);
        Vector2 start = null;

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                if (matrix[y][x] != '#') {
                    var node = new Vector2(x, y);
                    addConnectedNode(node, x, y - 1, matrix, graph);
                    addConnectedNode(node, x, y + 1, matrix, graph);
                    addConnectedNode(node, x - 1, y, matrix, graph);
                    addConnectedNode(node, x + 1, y, matrix, graph);
                    if (matrix[y][x] == 'S') {
                        start = node;
                    }
                }
            }
        }

        ConnectivityInspector<Vector2, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(graph);
        checkState(connectivityInspector.connectedSets().size() == 1, "If there are any disconnected sub-graphs, remove them ");

        return new GardenMap(graph, start, matrix[0].length, matrix.length);
    }

    private void addConnectedNode(Vector2 from, int x, int y, char[][] matrix, Graph<Vector2, DefaultEdge> graph) {
        if ((x >= 0 && x < matrix[0].length) && (y >= 0 && y < matrix.length)) {
            if (matrix[y][x] != '#') {
                addEdgeWithVertices(graph, from, new Vector2(x, y));
            }
        }
    }
}
