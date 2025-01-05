package aoc.y2019;

import aoc.y2019.intcode.Computer;
import aoc.y2019.intcode.Memory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Direction;
import utils.Input;
import utils.Vector2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;

@AdventOfCode(year = 2019, day = 15, name = "Oxygen System")
public class Day15Solution {

    @Test
    public void part1() throws IOException {
        var map = explore(input(this));
        var target = findOxygenSystem(map);

        assertEquals(270, path(map, new Vector2(0, 0), target).size());
    }

    @Test
    public void part2() throws IOException {
        var map = explore(input(this));
        var start = findOxygenSystem(map);

        var frontier = Lists.<Vector2>newLinkedList();
        frontier.add(start);

        var distances = Maps.<Vector2, Integer>newHashMap();
        distances.put(start, 0);

        while (!frontier.isEmpty()) {
            var current = frontier.poll();

            for (Direction direction : Direction.values()) {
                var next = current.move(direction);
                if (!map.getOrDefault(next, "#").equals("#") && !distances.containsKey(next)) {
                    frontier.add(next);
                    distances.put(next, distances.get(current) + 1);
                }
            }
        }

        var max = distances.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElseThrow();

        assertEquals(364, max);
    }

    private Vector2 findOxygenSystem(Map<Vector2, String> map) {
        return map.entrySet().stream()
                .filter(e -> e.getValue().equals("O"))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
    }

    private Map<Vector2, String> explore(final Input input) {
        var computer = new Computer(Memory.init(input));
        var status = 1L;
        var droidPosition = new Vector2(0, 0);

        var maze = Maps.<Vector2, String>newHashMap();
        maze.put(droidPosition, ".");

        var target = unexplored(maze);
        while (target != null) {
            // move to the next unexplored position
            for (var direction : path(maze, droidPosition, target)) {
                var command = switch (direction) {
                    case NORTH -> 1;
                    case SOUTH -> 2;
                    case WEST -> 3;
                    case EAST -> 4;
                };

                computer.io().queueInput(command);
                computer.runUntilOutputAvailable();
                status = computer.io().takeOutput();

                // Update the map as we explore
                if (status == 0) {
                    maze.put(droidPosition.move(direction), "#");
                } else {
                    droidPosition = droidPosition.move(direction);
                    maze.putIfAbsent(droidPosition, status == 1 ? "." : "O");
                }
            }

            target = unexplored(maze);
        }

        return maze;
    }

    private Vector2 unexplored(Map<Vector2, String> maze) {
        for (Vector2 position : maze.keySet()) {
            if (!maze.get(position).equals("#")) {
                for (Direction direction : Direction.values()) {
                    var next = position.move(direction);
                    if (!maze.containsKey(next)) {
                        return next;
                    }
                }
            }
        }
        return null;
    }

    private List<Direction> path(Map<Vector2, String> maze, Vector2 start, Vector2 target) {
        var frontier = Lists.<Vector2>newLinkedList();
        frontier.add(start);

        var directions = Maps.<Vector2, Direction>newHashMap();

        var parents = Maps.<Vector2, Vector2>newHashMap();

        loop:
        while (!frontier.isEmpty()) {
            var current = frontier.poll();

            for (Direction direction : Direction.values()) {
                var next = current.move(direction);

                if (!maze.getOrDefault(next, "#").equals("#") || next.equals(target)) {
                    if (!directions.containsKey(next)) {
                        directions.put(next, direction);
                        parents.put(next, current);

                        if (next.equals(target)) {
                            break loop;
                        }

                        frontier.add(next);
                    }
                }
            }
        }

        var node = target;
        var path = Lists.<Direction>newArrayList();
        path.add(directions.get(target));

        while (!(node = parents.get(node)).equals(start)) {
            path.add(directions.get(node));
        }

        return path.reversed();
    }
}
