package aoc.y2023;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.*;

import static aoc.y2023.Day17Solution.Direction.*;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 17, name = "Clumsy Crucible")
public class Day17Solution {

    enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN;

        public Set<Direction> allowedNextPart(Type type, int directionCount) {
            return switch (type) {
                case STANDARD -> allowedNext(directionCount);
                case ULTRA -> allowedNextForUltra(directionCount);
            };
        }

        private Set<Direction> allowedNext(int directionCount) {
            final var directions = EnumSet.allOf(Direction.class);

            if (directionCount > 2) {
                directions.remove(this);
            }

            directions.remove(switch (this) {
                case RIGHT -> LEFT;
                case LEFT -> RIGHT;
                case UP -> DOWN;
                case DOWN -> UP;
            });

            return directions;
        }

        private Set<Direction> allowedNextForUltra(int directionCount) {
            if (directionCount > 0 && directionCount < 4) {
                return EnumSet.of(this); // Keep going for at least 4 blocks
            }

            final var directions = EnumSet.allOf(Direction.class);
            if (directionCount > 9) {
                directions.remove(this);
            }

            directions.remove(switch (this) {
                case RIGHT -> LEFT;
                case LEFT -> RIGHT;
                case UP -> DOWN;
                case DOWN -> UP;
            });

            return directions;
        }
    }

    static class Graph {

        private final Map<String, Node> nodes = Maps.newHashMap();

        public void addNode(Node node) {
            nodes.put(key(node.x, node.y), node);
        }

        final Node node(final int x, int y) {
            return nodes.get(key(x, y));
        }

        private String key(final int x, int y) {
            return x + "," + y;
        }
    }

    static class Node {

        record Edge(Node child, Direction direction, int cost) {
        }

        private final int x;
        private final int y;

        private final Map<Node, Edge> edges = new HashMap<>();

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void addEdge(Node child, Direction direction, int cost) {
            edges.put(child, new Edge(child, direction, cost));
        }

        public Collection<Edge> edges() {
            return edges.values();
        }
    }

    record NodeCost(NodeDirection nodeDirection, int cost) implements Comparable<NodeCost> {

        @Override
        public int compareTo(final NodeCost o) {
            return ComparisonChain.start()
                    .compare(cost, o.cost)
                    .compare(nodeDirection.node.x, o.nodeDirection.node.x)
                    .compare(nodeDirection.node.y, o.nodeDirection.node.y)
                    .compare(nodeDirection.direction, o.nodeDirection.direction)
                    .compare(nodeDirection.directionCount, o.nodeDirection.directionCount)
                    .result();
        }

        public NodeDirection next(final Node.Edge edge) {
            var count = nodeDirection.direction == edge.direction ?
                    nodeDirection().directionCount + 1 :
                    1;

            return new NodeDirection(edge.child, edge.direction, count);
        }
    }

    record NodeDirection(Node node, Direction direction, int directionCount) {

        public boolean canStop(final Type type) {
            return switch (type) {
                case STANDARD -> true;
                case ULTRA -> directionCount > 3;
            };
        }
    }

    enum Type {STANDARD, ULTRA}

    @Test
    public void part1WithMockData() {
        assertEquals(102, leastHeatLoss(mockInput("""
                2413432311323
                3215453535623
                3255245654254
                3446585845452
                4546657867536
                1438598798454
                4457876987766
                3637877979653
                4654967986887
                4564679986453
                1224686865563
                2546548887735
                4322674655533
                """), Type.STANDARD));
    }

    @Test
    public void part1() {
        assertEquals(1110, leastHeatLoss(input(this), Type.STANDARD));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(94, leastHeatLoss(mockInput("""
                2413432311323
                3215453535623
                3255245654254
                3446585845452
                4546657867536
                1438598798454
                4457876987766
                3637877979653
                4654967986887
                4564679986453
                1224686865563
                2546548887735
                4322674655533
                """), Type.ULTRA));
    }

    @Test
    public void part2WithMockData2() {
        assertEquals(71, leastHeatLoss(mockInput("""
                111111111111
                999999999991
                999999999991
                999999999991
                999999999991
                 """), Type.ULTRA));
    }

    @Test
    public void part2() {
        assertEquals(1294, leastHeatLoss(input(this), Type.ULTRA));
    }

    private int leastHeatLoss(final Input input, final Type type) {
        final var matrix = Utils.matrix(input.text());

        var graph = new Graph();

        // Build vertices
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                graph.addNode(new Node(x, y));
            }
        }

        // Build edges
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                var node = graph.node(x, y);

                if (x > 0) {
                    var left = graph.node(x - 1, y);
                    node.addEdge(left, LEFT, parseInt(valueOf(matrix[left.y][left.x])));
                }

                if (x < matrix[y].length - 1) {
                    var right = graph.node(x + 1, y);
                    node.addEdge(right, RIGHT, parseInt(valueOf(matrix[right.y][right.x])));
                }

                if (y > 0) {
                    var above = graph.node(x, y - 1);
                    node.addEdge(above, UP, parseInt(valueOf(matrix[above.y][above.x])));
                }

                if (y < matrix.length - 1) {
                    var below = graph.node(x, y + 1);
                    node.addEdge(below, DOWN, parseInt(valueOf(matrix[below.y][below.x])));
                }
            }
        }

        var start = graph.node(0, 0);
        var target = graph.node(matrix[0].length - 1, matrix.length - 1);

        var frontier = new PriorityQueue<NodeCost>();
        frontier.add(new NodeCost(new NodeDirection(start, RIGHT, 0), 0));

        var costSoFar = Maps.<NodeDirection, Integer>newHashMap();
        costSoFar.put(new NodeDirection(start, RIGHT, 0), 0);

        while (!frontier.isEmpty()) {
            final var current = frontier.poll();
            final var currentNodeDirection = current.nodeDirection;

            final var allowedNext = currentNodeDirection.direction.allowedNextPart(type, currentNodeDirection.directionCount);

            for (final Node.Edge next : currentNodeDirection.node.edges()) {
                if (allowedNext.contains(next.direction)) {
                    var newNodeDirection = current.next(next);
                    if (next.child != target || newNodeDirection.canStop(type)) {
                        var newCost = costSoFar.get(current.nodeDirection()) + next.cost();

                        if (newCost < costSoFar.getOrDefault(newNodeDirection, Integer.MAX_VALUE)) {
                            costSoFar.put(newNodeDirection, newCost);
                            frontier.add(new NodeCost(newNodeDirection, newCost));
                        }
                    }
                }
            }
        }

        return costSoFar.keySet().stream()
                .filter(nd -> nd.node == target)
                .mapToInt(costSoFar::get)
                .min()
                .orElseThrow();
    }
}
