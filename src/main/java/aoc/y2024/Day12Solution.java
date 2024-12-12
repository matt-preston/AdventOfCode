package aoc.y2024;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 12, name = "Garden Groups")
public class Day12Solution {

    private static final String MOCK = """
            RRRRIICCFF
            RRRRIICCCF
            VVRRRCCFFF
            VVRCCCJFFF
            VVVVCJJCFE
            VVIVCCJJEE
            VVIIICJJEE
            MIIIIIJJEE
            MIIISIJEEE
            MMMISSJEEE
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(1930, cost(mockInput(MOCK), Region::perimeter));
    }

    @Test
    public void part1() {
        assertEquals(1483212, cost(input(this), Region::perimeter));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(1206, cost(mockInput(MOCK), Region::sides));
    }

    @Test
    public void part2() {
        assertEquals(897062, cost(input(this), Region::sides));
    }

    record Region(String label, Set<Vector2> plots) {

        int area() {
            return plots.size();
        }

        int perimeter() {
            var numAdjacentEdges = 0;

            if (plots.size() > 1) {
                for (Set<Vector2> pair : Sets.combinations(plots, 2)) {
                    Iterator<Vector2> iterator = pair.iterator();
                    var p1 = iterator.next();
                    var p2 = iterator.next();

                    if (Math.abs(p1.x() - p2.x()) == 1 && p1.y() == p2.y()) {
                        numAdjacentEdges += 2;
                    } else if (Math.abs(p1.y() - p2.y()) == 1 && p1.x() == p2.x()) {
                        numAdjacentEdges += 2;
                    }
                }
            }

            return (plots.size() * 4) - numAdjacentEdges;
        }

        int sides() {
            // number of sides is == to the number of corners
            return plots.stream().mapToInt(this::corners).sum();
        }

        boolean contains(Vector2 plot) {
            return plots().contains(plot);
        }


        /*
          Interior corner patterns:

          ##  ##  #.  .#
          #.  .#  ##  ##

          Exterior corner patterns:

          ..  ..  .#  #.
          .#  #.  ..  ..

         */
        private int corners(Vector2 plot) {
            var count = 0;

            // Interior patterns
            if (!contains(plot.north()) && !contains(plot.west())) {
                count++;
            }

            if (!contains(plot.north()) && !contains(plot.east())) {
                count++;
            }

            if (!contains(plot.west()) && !contains(plot.south())) {
                count++;
            }

            if (!contains(plot.east()) && !contains(plot.south())) {
                count++;
            }

            // Exterior patterns
            if (contains(plot.south()) && contains(plot.east()) && !contains(plot.south().east())) {
                count++;
            }

            if (contains(plot.south()) && contains(plot.west()) && !contains(plot.south().west())) {
                count++;
            }

            if (contains(plot.north()) && contains(plot.east()) && !contains(plot.north().east())) {
                count++;
            }

            if (contains(plot.north()) && contains(plot.west()) && !contains(plot.north().west())) {
                count++;
            }

            return count;
        }
    }

    private int cost(Input input, Function<Region, Integer> f) {
        char[][] matrix = Utils.matrix(input.text());
        var regions = Lists.<Region>newArrayList();
        var seen = Sets.<Vector2>newHashSet();

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                var position = new Vector2(x, y);
                if (!seen.contains(position)) {
                    var region = region(matrix, position);

                    regions.add(region);
                    seen.addAll(region.plots());
                }
            }
        }

        var cost = 0;
        for (Region region : regions) {
            cost += region.area() * f.apply(region);
        }
        return cost;
    }

    private Region region(char[][] matrix, Vector2 start) {
        var frontier = Sets.<Vector2>newLinkedHashSet();
        frontier.add(start);

        var set = Sets.<Vector2>newHashSet();

        while (!frontier.isEmpty()) {
            final var current = frontier.removeFirst();
            set.add(current);

            for (Vector2 next : new Vector2[]{current.north(), current.south(), current.east(), current.west()}) {
                if (Utils.contains(matrix, next) && !set.contains(next) && Utils.get(matrix, next) == Utils.get(matrix, start)) {
                    frontier.add(next);
                }
            }
        }

        return new Region("" + Utils.get(matrix, start), set);
    }
}
