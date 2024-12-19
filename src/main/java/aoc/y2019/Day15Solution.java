package aoc.y2019;

import aoc.y2019.intcode.Computer;
import aoc.y2019.intcode.Memory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.locationtech.jts.geom.Coordinate;
import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Direction;
import utils.Vector2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;

@AdventOfCode(year = 2019, day = 15, name = "Oxygen System")
public class Day15Solution {

    @Test
    public void part1() throws IOException {
        var input = input(this);
        var computer = new Computer(Memory.init(input));
        var status = 1L;
        var droidPosition = new Vector2(0, 0);

        var maze = Maps.<Vector2, String>newHashMap();
        maze.put(droidPosition, ".");

        while(status != 2) {
            var next = randomMovement(droidPosition);

            computer.io().queueInput(next.command());
            computer.runUntilOutputAvailable();
            status = computer.io().takeOutput();

            if (status == 0) {
                maze.put(next.position(), "#"); // wall
            } else {
                maze.put(next.position(), "."); // successful move
                droidPosition = next.position();
            }
        }

        render(maze, droidPosition);

        assertEquals(270, minimumPath(maze, new Vector2(0, 0), droidPosition));
    }

    private int minimumPath(HashMap<Vector2, String> maze, Vector2 start, Vector2 target) {
        var frontier = Lists.<Vector2>newLinkedList();
        frontier.add(start);

        var distances = Maps.<Vector2, Integer>newHashMap();
        distances.put(start, 0);

        while(!frontier.isEmpty()) {
            var current = frontier.poll();

            for (Vector2 next : new Vector2[]{current.north(), current.south(), current.east(), current.west()}) {
                if (maze.getOrDefault(next, "#").equals(".")) {
                    if (!distances.containsKey(next)) {
                        distances.put(next, distances.get(current) + 1);
                        frontier.add(next);
                    }
                }
            }
        }

        return distances.getOrDefault(target, -1);
    }

    private void render(HashMap<Vector2, String> maze, Vector2 droidPosition) {
       var minX = maze.keySet().stream()
               .mapToInt(Vector2::x)
               .min()
               .orElseThrow();

        var maxX = maze.keySet().stream()
                .mapToInt(Vector2::x)
                .max()
                .orElseThrow();

        var minY = maze.keySet().stream()
                .mapToInt(Vector2::y)
                .min()
                .orElseThrow();

        var maxY = maze.keySet().stream()
                .mapToInt(Vector2::y)
                .max()
                .orElseThrow();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                var pos = new Vector2(x, y);
                if (pos.equals(droidPosition)) {
                    System.out.print("D");
                } else if (x == 0 && y == 0) {
                    System.out.print("S");
                } else {
                    System.out.print(maze.getOrDefault(pos, " "));
                }
            }
            System.out.println();
        }
        System.out.printf("%n%s%n%n", "-".repeat(80));

    }


    private static final Random RANDOM = new Random();

    record MovementCommand(int command, Vector2 position) {}

    private MovementCommand randomMovement(Vector2 position) {
        return switch(RANDOM.nextInt(4)) {
            case 0 -> new MovementCommand(1, position.north());
            case 1 -> new MovementCommand(2, position.south());
            case 2 -> new MovementCommand(3, position.west());
            case 3 -> new MovementCommand(4, position.east());
            default -> throw new IllegalStateException();
        };
    }



}
