package aoc.y2023;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static java.util.Comparator.comparingInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@SuppressWarnings({"UnstableApiUsage", "DataFlowIssue"})
@AdventOfCode(year = 2023, day = 23, name = "A Long Walk")
public class Day23Solution {

  enum Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN
  }

  public static final String MOCK = """
          #.#####################
          #.......#########...###
          #######.#########.#.###
          ###.....#.>.>.###.#.###
          ###v#####.#v#.###.#.###
          ###.>...#.#.#.....#...#
          ###v###.#.#.#########.#
          ###...#.#.#.......#...#
          #####.#.#.#######.#.###
          #.....#.#.#.......#...#
          #.#####.#.#.#########v#
          #.#...#...#...###...>.#
          #.#.#v#######v###.###v#
          #...#.>.#...>.>.#.###.#
          #####v#.#.###v#.#.###.#
          #.....#...#...#.#.#...#
          #.#########.###.#.#.###
          #...###...#...#...#.###
          ###.###.#.###v#####v###
          #...#...#.#.>.>.#.>.###
          #.###.###.#.###.#.#v###
          #.....###...###...#...#
          #####################.#
          """;

  @Test
  public void part1WithMockData() {
    assertEquals(94, maxNumberOfStepsBFS(mockInput(MOCK)));
  }

  @Test
  public void part1() {
    assertEquals(2106, maxNumberOfStepsBFS(input(this)));
  }

  @Test
  public void part2WithMockData() {
    assertEquals(154, maxNumberOfStepsRecursively(mockInput(MOCK)));
  }

  @Test
  public void part2() {
    assertEquals(6350, maxNumberOfStepsRecursively(input(this)));
  }

  private int maxNumberOfStepsBFS(Input input) {
    var graph = build(input, true);

    var start = start(graph);
    var target = target(graph);

    record State(String node, String previous, int cost) {}

    var frontier = new LinkedList<State>();
    frontier.add(new State(start, null, 0));

    var results = Sets.<Integer>newTreeSet();
    while (!frontier.isEmpty()) {
      var current = frontier.poll();
      if (current.node().equals(target)) {
        results.add(current.cost());
      }

      for (String successor : graph.successors(current.node())) {
        var cost = graph.edgeValueOrDefault(current.node(), successor, 1);
        if (!successor.equals(current.previous())) { // no backwards
          frontier.add(new State(successor, current.node(), current.cost() + cost));
        }
      }
    }

    return results.last(); // max
  }

  private int maxNumberOfStepsRecursively(final Input input) {
    var graph = build(input, false);
    return maxNumberOfSteps(start(graph), target(graph), graph, 0, Sets.newHashSet());
  }

  private int maxNumberOfSteps(String current, String target, ValueGraph<String, Integer> graph, int length, Set<String> path) {
    if (current.equals(target)) {
      return length;
    }

    path.add(current);

    var max = 0;
    for (String node : graph.adjacentNodes(current)) {
      if (!path.contains(node)) {
        Integer cost = graph.edgeValueOrDefault(current, node, 1);
        max = Math.max(max, maxNumberOfSteps(node, target, graph, length + cost, path));
      }
    }

    path.remove(current);

    return max;
  }

  private static String start(ValueGraph<String, Integer> graph) {
    return graph.nodes().stream()
            .filter(n -> n.endsWith(",0"))
            .findFirst()
            .orElseThrow();
  }

  private static String target(ValueGraph<String, Integer> graph) {
    return graph.nodes().stream()
            .max(comparingInt(n -> parseInt(n.split(",")[1])))
            .orElseThrow();
  }

  private ValueGraph<String, Integer> build(final Input input, final boolean withSlopes) {
    MutableValueGraph<String, Integer> graph = withSlopes ?
            ValueGraphBuilder.directed().build() :
            ValueGraphBuilder.undirected().build() ;

    char[][] matrix = Utils.matrix(input.text());
    for (int y = 0; y < matrix.length; y++) {
      for (int x = 0; x < matrix[0].length; x++) {
        if (matrix[y][x] != '#') {
          var node = x + "," + y;
          var current = matrix[y][x];
          addConnectedNode(node, current, x, y - 1, Direction.UP, matrix, graph, withSlopes);
          addConnectedNode(node, current, x, y + 1, Direction.DOWN, matrix, graph, withSlopes);
          addConnectedNode(node, current, x - 1, y, Direction.LEFT, matrix, graph, withSlopes);
          addConnectedNode(node, current, x + 1, y, Direction.RIGHT, matrix, graph, withSlopes);
        }
      }
    }

    contractEdges(graph);

    return graph;
  }

  private void addConnectedNode(String node, char current, int x, int y, Direction direction, char[][] matrix, MutableValueGraph<String, Integer> graph, final boolean withSlopes) {
    if ((x >= 0 && x < matrix[0].length) && (y >= 0 && y < matrix.length)) {
      if (matrix[y][x] != '#') {
        var allowed = !withSlopes || switch (current) {
          case '>' -> direction == Direction.RIGHT;
          case '<' -> direction == Direction.LEFT;
          case 'v' -> direction == Direction.DOWN;
          case '^' -> direction == Direction.UP;
          default -> true;
        };

        if (allowed) {
          graph.putEdgeValue(node, x + "," + y, 1);
        }
      }
    }
  }

  private void contractEdges(final MutableValueGraph<String, Integer> graph) {
    for (String node : ImmutableList.copyOf(graph.nodes())) {
      var successors = graph.successors(node);
      var predecessors = graph.predecessors(node);

      if (successors.size() == 2 && predecessors.size() == 2) {
        Iterator<String> iterator = successors.iterator();
        String first = iterator.next();
        String second = iterator.next();

        var costToFirst = graph.edgeValueOrDefault(node, first, 1);
        var costToSecond = graph.edgeValueOrDefault(node, second, 1);

        graph.removeNode(node);

        var newCost = costToFirst + costToSecond;
        graph.putEdgeValue(first, second, newCost);
        graph.putEdgeValue(second, first, newCost);
      }
    }
  }
}
