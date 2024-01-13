package aoc.y2019;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Vector3;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.combinations;
import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2019, day = 12, name = "The N-Body Problem")
public class Day12Solution {

    private static final List<String> NAMES = List.of("Io", "Europa", "Ganymede", "Callisto");

    private static final String SAMPLE1 = """
            <x=-1, y=0, z=2>
            <x=2, y=-10, z=-7>
            <x=4, y=-8, z=8>
            <x=3, y=5, z=-1>
            """;

    private static final String SAMPLE2 = """
            <x=-8, y=-10, z=0>
            <x=5, y=5, z=10>
            <x=2, y=-7, z=3>
            <x=9, y=-8, z=-3>
            """;

    record Moon(Vector3 position, Vector3 velocity) {
        public Moon copy() {
            return new Moon(position.copy(), velocity.copy());
        }
    }

    @Test
    public void part1WithSampleData() {
        assertEquals(179, totalEnergyAfterSteps(10, mockInput(SAMPLE1)));
    }

    @Test
    public void part1WithMoreSampleData() {
        assertEquals(1940, totalEnergyAfterSteps(100, mockInput(SAMPLE2)));
    }

    @Test
    public void part1() {
        assertEquals(12773, totalEnergyAfterSteps(1000, input(this)));
    }

    @Test
    public void part2WithSampleData() {
        assertEquals(2772, howManyStepsToCycleState(mockInput(SAMPLE1)));
    }

    @Test
    public void part2WithMoreSampleData() {
        assertEquals(4_686_774_924L, howManyStepsToCycleState(mockInput(SAMPLE2)));
    }

    @Test
    public void part2() {
        assertEquals(306_798_770_391_636L, howManyStepsToCycleState(input(this)));
    }

    private long howManyStepsToCycleState(Input input) {
        var tmp = moons(input);
        var original = Maps.<String, Moon>newHashMap();
        for (String name : tmp.keySet()) {
            original.put(name, tmp.get(name).copy());
        }

        var cycles = Sets.<Integer>newHashSet();
        cycles.add(simulateUntilPositionsLoop(moons(input), original, Vector3::x));
        cycles.add(simulateUntilPositionsLoop(moons(input), original, Vector3::y));
        cycles.add(simulateUntilPositionsLoop(moons(input), original, Vector3::z));

        return cycles.stream()
                .mapToLong(Integer::intValue)
                .reduce(1, ArithmeticUtils::lcm);
    }

    private int simulateUntilPositionsLoop(Map<String, Moon> moons, Map<String, Moon> originalState, ToDoubleFunction<Vector3> axis) {
        var moonNamePairs = combinations(moons.keySet(), 2);
        int step = 1;
        while (true) {
            simulate(moons, moonNamePairs);
            var found = true;
            for (String name : moons.keySet()) {
                final var original = originalState.get(name);
                final var current = moons.get(name);
                if(axis.applyAsDouble(original.position) != axis.applyAsDouble(current.position) || axis.applyAsDouble(current.velocity) != 0) {
                    found = false;
                }
            }

            if(found) {
                return step;
            }

            step++;
        }
    }

    private int energy(Vector3 position) {
        return (int) (Math.abs(position.x()) + Math.abs(position.y()) + Math.abs(position.z()));
    }

    private int totalEnergyAfterSteps(int steps, Input input) {
        var moons = moons(input);
        var moonNamePairs = combinations(moons.keySet(), 2);

        for (int i = 0; i < steps; i++) {
            simulate(moons, moonNamePairs);
        }

        return moons.values().stream()
                .mapToInt(moon -> energy(moon.position) * energy(moon.velocity))
                .sum();
    }

    private void simulate(Map<String, Moon> moons, Set<Set<String>> moonNamePairs) {
        // update velocities by applying gravity
        for (Set<String> pair : moonNamePairs) {
            final var iterator = pair.iterator();
            final var moon1 = moons.get(iterator.next());
            final var moon2 = moons.get(iterator.next());

            final var velocityOffset = new Vector3(
                    velocity(moon1, moon2, Vector3::x),
                    velocity(moon1, moon2, Vector3::y),
                    velocity(moon1, moon2, Vector3::z)
            );

            moon1.velocity.plusEquals(velocityOffset);
            moon2.velocity.minusEquals(velocityOffset);
        }

        // apply velocity to positions
        for (Moon moon : moons.values()) {
            moon.position.plusEquals(moon.velocity);
        }
    }

    private int velocity(Moon moon1, Moon moon2, ToDoubleFunction<Vector3> axisValueFunction) {
        final var value1 = axisValueFunction.applyAsDouble(moon1.position);
        final var value2 = axisValueFunction.applyAsDouble(moon2.position);
        return Double.compare(value1, value2) * -1;
    }

    private Map<String, Moon> moons(Input input) {
        final var pattern = Pattern.compile("<x=(.+), y=(.+), z=(.+)>");
        final var moons = Maps.<String, Moon>newHashMap();
        final var names = Lists.newLinkedList(NAMES);

        for (String line : input.lines()) {
            final var matcher = pattern.matcher(line);
            checkState(matcher.matches());
            moons.put(names.poll(), new Moon(
                    new Vector3(parseInt(matcher.group(1)), parseInt(matcher.group(2)), parseInt(matcher.group(3))),
                    new Vector3(0, 0, 0)));
        }
        return moons;
    }


}
