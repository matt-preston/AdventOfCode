package aoc.y2020;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 17, name = "Conway Cubes")
public class Day17Solution {

    private static final String MOCK = """
            .#.
            ..#
            ###
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(112, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(295, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(848, solve2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(1972, solve2(input(this)));
    }

    private int solve(Input input) {

        var activeUniverse = Sets.<Coord<D3>>newHashSet();

        var lines = input.lines();
        for (int y = 0; y < lines.size(); y++) {
            var line = lines.get(y).toCharArray();
            for (int x = 0; x < line.length; x++) {
                if (line[x] == '#') {
                    activeUniverse.add(new D3(x, y, 0));
                }
            }
        }

        return simulate(activeUniverse);
    }

    private int solve2(Input input) {

        var activeUniverse = Sets.<Coord<D4>>newHashSet();

        var lines = input.lines();
        for (int y = 0; y < lines.size(); y++) {
            var line = lines.get(y).toCharArray();
            for (int x = 0; x < line.length; x++) {
                if (line[x] == '#') {
                    activeUniverse.add(new D4(x, y, 0, 0));
                }
            }
        }

        return simulate(activeUniverse);
    }

    private <T extends Coord<T>> int simulate(Set<Coord<T>> activeUniverse) {
        // simulate
        for (int i = 0; i < 6; i++) {
            var newUniverse = Sets.<Coord<T>>newHashSet();

            var inactiveCubes = Sets.<Coord<T>>newHashSet();

            for (Coord<T> activePosition : activeUniverse) {
                var activeNeighbours = activeNeighbours(activePosition, activeUniverse);
                if (activeNeighbours == 2 || activeNeighbours == 3) {
                    newUniverse.add(activePosition);
                }

                for (Coord<T> neighbour : activePosition.neighbours()) {
                    if (!activeUniverse.contains(neighbour)) {
                        inactiveCubes.add(neighbour);
                    }
                }
            }

            for (Coord<T> inactivePosition : inactiveCubes) {
                var activeNeighbours = activeNeighbours(inactivePosition, activeUniverse);
                if (activeNeighbours == 3) {
                    newUniverse.add(inactivePosition);
                }
            }

            activeUniverse = newUniverse;
        }
        return activeUniverse.size();
    }

    private <T extends Coord<T>> int activeNeighbours(Coord<T> position, Set<Coord<T>> activeUniverse) {
        return (int) position.neighbours().stream()
                .filter(activeUniverse::contains)
                .count();
    }

    interface Coord<T extends Coord<T>> {
        Set<T> neighbours();
    }

    record D3(int x, int y, int z) implements Coord<D3> {
        @Override
        public Set<D3> neighbours() {
            var result = Sets.<D3>newHashSet();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (!(dx == 0 && dy == 0 && dz == 0)) {
                            result.add(new D3(x + dx, y + dy, z + dz));
                        }
                    }
                }
            }
            return result;
        }
    }

    record D4(int x, int y, int z, int w) implements Coord<D4> {
        @Override
        public Set<D4> neighbours() {
            var result = Sets.<D4>newHashSet();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        for (int dw = -1; dw <= 1; dw++) {
                            if (!(dx == 0 && dy == 0 && dz == 0 && dw == 0)) {
                                result.add(new D4(x + dx, y + dy, z + dz, w + dw));
                            }
                        }
                    }
                }
            }
            return result;
        }
    }
}
