package aoc.y2023;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Utils.predictNthInQuadratic;

@AdventOfCode(year = 2023, day = 21, name = "Step Counter")
public class Day21Solution {

    record Node(String name, Map<Node, Vector2> edges, AtomicBoolean start) {

        public static String name(int x, int y) {
            return x + "," + y;
        }

        public Node(final String name) {
            this(name, Maps.newHashMap(), new AtomicBoolean(false));
        }

        public void addConnected(Node connected, Vector2 mapVector) {
            edges.put(connected, mapVector);
        }

        @Override
        public String toString() {
            return "Node(" + name + ')';
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Node node = (Node) o;
            return Objects.equals(name, node.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    @Test
    public void part1WithMockData() {
        assertEquals(16, numNodesVisited(Input.mockInput("""
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
                """), 6, false));
    }

    @Test
    public void part1() {
        assertEquals(3724, numNodesVisited(Input.input(this), 64, false));
    }

    @Test
    public void part2WithMockData() {
        final var input = Input.mockInput("""
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
                """);

        assertEquals(16, numNodesVisited(input, 6, true));
        assertEquals(50, numNodesVisited(input, 10, true));
        assertEquals(1_594, numNodesVisited(input, 50, true));
        assertEquals(6_536, numNodesVisited(input, 100, true));
        assertEquals(16_7004, numNodesVisited(input, 500, true));
    }

    @Test
    public void part2() {
        final var input = Input.input(this);
        assertEquals(620348631910321L, predictNthInQuadratic(
                (26501365 - 65) / 131,
                numNodesVisited(input, 65, true),
                numNodesVisited(input, 65 + 131, true),
                numNodesVisited(input, 65 + (2 * 131), true)
        ));
    }

    private int numNodesVisited(Input input, int steps, boolean withWrap) {
        final var graph = graph(input, withWrap);
        final var start = graph.values().stream()
                .filter(n -> n.start.get())
                .findAny()
                .orElseThrow();

        record GridNode(Vector2 position, Node node) {
        }

        var visited = Sets.<GridNode>newHashSet();
        var frontier = Sets.<GridNode>newHashSet();
        frontier.add(new GridNode(new Vector2(0, 0), start));

        var count = 0;

        for (int step = 0; step < steps; step++) {
            var newFrontier = Sets.<GridNode>newHashSet();
            visited.clear();

            for (final GridNode current : frontier) {
                for (final Map.Entry<Node, Vector2> entry : current.node.edges.entrySet()) {
                    var newPosition = current.position().translate(entry.getValue());
                    var newGridNode = new GridNode(newPosition, entry.getKey());

                    if (!visited.contains(newGridNode)) {
                        newFrontier.add(newGridNode);
                        visited.add(newGridNode);
                    }
                }
            }

            frontier = newFrontier;
            count = visited.size();
        }

        return count;
    }

    private Map<String, Node> graph(final Input inout, boolean withWrap) {
        final var matrix = Utils.matrix(inout.text());
        final var graph = Maps.<String, Node>newHashMap();

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                if (matrix[y][x] == '.' || matrix[y][x] == 'S') {
                    var node = graph.computeIfAbsent(Node.name(x, y), Node::new);
                    addConnectedNode(node, x, y - 1, matrix, graph, withWrap);
                    addConnectedNode(node, x, y + 1, matrix, graph, withWrap);
                    addConnectedNode(node, x - 1, y, matrix, graph, withWrap);
                    addConnectedNode(node, x + 1, y, matrix, graph, withWrap);
                    if (matrix[y][x] == 'S') {
                        node.start().set(true);
                    }
                }
            }
        }
        return graph;
    }

    // make the graph wrap top/bottom and left/right
    private void addConnectedNode(Node node, int x, int y, char[][] matrix, Map<String, Node> graph, boolean withWrap) {
        var vector = new Vector2(0, 0);

        if (withWrap) {
            if (x < 0) {
                x = matrix[0].length - 1;
                vector = new Vector2(-1, 0);
            } else if (x >= matrix[0].length) {
                x = 0;
                vector = new Vector2(1, 0);
            }
            if (y < 0) {
                y = matrix.length - 1;
                vector = new Vector2(0, -1);
            } else if (y >= matrix.length) {
                y = 0;
                vector = new Vector2(0, 1);
            }
        }

        if ((x >= 0 && x < matrix[0].length) && (y >= 0 && y < matrix.length)) {
            if (matrix[y][x] == '.' || matrix[y][x] == 'S') {
                final var child = graph.computeIfAbsent(Node.name(x, y), Node::new);
                node.addConnected(child, vector);
            }
        }
    }
}
