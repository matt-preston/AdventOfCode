package aoc.y2024;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.*;

import java.util.PriorityQueue;

import static java.util.Comparator.comparingInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Direction.EAST;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 16, name = "Reindeer Maze")
public class Day16Solution {

    private static final String MOCK1 = """
            ###############
            #.......#....E#
            #.#.###.#.###.#
            #.....#.#...#.#
            #.###.#####.#.#
            #.#.#.......#.#
            #.#.#####.###.#
            #...........#.#
            ###.#.#####.#.#
            #...#.....#.#.#
            #.#.#.###.#.#.#
            #.....#...#.#.#
            #.###.#.#.#.#.#
            #S..#.....#...#
            ###############
            """;

    private static final String MOCK2 = """
            #################
            #...#...#...#..E#
            #.#.#.#.#.#.#.#.#
            #.#.#.#...#...#.#
            #.#.#.#.###.#.#.#
            #...#.#.#.....#.#
            #.#.#.#.#.#####.#
            #.#...#.#.#.....#
            #.#.#####.#.###.#
            #.#.#.......#...#
            #.#.###.#####.###
            #.#.#...#.....#.#
            #.#.#.#####.###.#
            #.#.#.........#.#
            #.#.#.#########.#
            #S#.............#
            #################
            """;

    @Test
    public void part1WithMock1Data() {
        assertEquals(7036, shortestPathCost(mockInput(MOCK1)));
    }

    @Test
    public void part1WithMock2Data() {
        assertEquals(11048, shortestPathCost(mockInput(MOCK2)));
    }

    @Test
    public void part1() {
        assertEquals(102460, shortestPathCost(input(this)));
    }

    @Test
    public void part2WithMock1Data() {
        assertEquals(45, tilesVisitedInAllBestPaths(mockInput(MOCK1)));
    }

    @Test
    public void part2WithMock2Data() {
        assertEquals(64, tilesVisitedInAllBestPaths(mockInput(MOCK2)));
    }

    @Test
    public void part2() {
        assertEquals(527, tilesVisitedInAllBestPaths(input(this)));
    }

    record Position(Vector2 position, Direction direction) {
    }

    record State(Vector2 position, Direction direction, int cost) implements Comparable<State> {
        State moveForward() {
            return new State(position.move(direction), direction, cost + 1);
        }

        State turnRight() {
            return new State(position, direction.turnRight(), cost + 1000);
        }

        State turnLeft() {
            return new State(position, direction.turnLeft(), cost + 1000);
        }

        Position asPosition() {
            return new Position(position, direction);
        }

        @Override
        public int compareTo(State o) {
            return ComparisonChain.start()
                    .compare(cost, o.cost)
                    .compare(position, o.position)
                    .compare(cost, o.cost)
                    .result();
        }
    }

    private int shortestPathCost(Input input) {
        var map = Utils.matrix(input.text());

        var start = new Vector2(1, map.length - 2);
        var target = new Vector2(map[0].length - 2, 1);

        var frontier = new PriorityQueue<State>();
        frontier.add(new State(start, EAST, 0));

        var costs = Maps.<Position, Integer>newHashMap();
        costs.put(new Position(start, EAST), 0);

        while (!frontier.isEmpty()) {
            final var current = frontier.poll();

            var next = new State[]{
                    current.moveForward(), current.turnLeft(), current.turnRight()
            };

            for (State state : next) {
                if (state.cost() < costs.getOrDefault(state.asPosition(), Integer.MAX_VALUE)) {
                    costs.put(state.asPosition(), state.cost());

                    // Keep searching for the exit
                    if (Utils.get(map, state.position()) != '#' && Utils.get(map, state.position()) != 'E') {
                        frontier.add(state);
                    }
                }
            }
        }

        return costs.keySet().stream()
                .filter(p -> p.position().equals(target))
                .mapToInt(costs::get)
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    private int tilesVisitedInAllBestPaths(Input input) {
        var map = Utils.matrix(input.text());

        var start = new Vector2(1, map.length - 2);
        var target = new Vector2(map[0].length - 2, 1);

        var frontier = new PriorityQueue<State>();
        frontier.add(new State(start, EAST, 0));

        var costs = Maps.<Position, Integer>newHashMap();
        costs.put(new Position(start, EAST), 0);

        var positionsVisited = HashMultimap.<Position, Vector2>create();
        positionsVisited.put(new Position(start, EAST), start);

        while (!frontier.isEmpty()) {
            final var current = frontier.poll();

            var next = new State[]{
                    current.moveForward(), current.turnLeft(), current.turnRight()
            };

            for (State state : next) {
                int previousCost = costs.getOrDefault(state.asPosition(), Integer.MAX_VALUE);
                if (state.cost() <= previousCost) {
                    positionsVisited.put(state.asPosition(), state.position());
                    positionsVisited.putAll(state.asPosition(), positionsVisited.get(current.asPosition()));
                }

                if (state.cost() < previousCost) {
                    costs.put(state.asPosition(), state.cost());

                    // Keep searching for the exit
                    if (Utils.get(map, state.position()) != '#' && Utils.get(map, state.position()) != 'E') {
                        frontier.add(state);
                    }
                }
            }
        }

        var min = costs.keySet().stream()
                .filter(p -> p.position().equals(target))
                .min(comparingInt(costs::get))
                .orElseThrow();

        return positionsVisited.get(min).size();
    }
}
