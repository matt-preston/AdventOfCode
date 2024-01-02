package aoc.y2023;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkState;
import static java.util.regex.Pattern.compile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.parseNumbers;

@AdventOfCode(year = 2023, day = 5, name = "If You Give A Seed A Fertilizer")
public class Day05Solution {

    public static final String SEED_TO_SOIL = "seed-to-soil";
    public static final String SOIL_TO_FERTILIZER = "soil-to-fertilizer";
    public static final String FERTILIZER_TO_WATER = "fertilizer-to-water";
    public static final String WATER_TO_LIGHT = "water-to-light";
    public static final String LIGHT_TO_TEMPERATURE = "light-to-temperature";
    public static final String TEMPERATURE_TO_HUMIDITY = "temperature-to-humidity";
    public static final String HUMIDITY_TO_LOCATION = "humidity-to-location";

    public static final String MOCK = """
            seeds: 79 14 55 13
                    
            seed-to-soil map:
            50 98 2
            52 50 48
                    
            soil-to-fertilizer map:
            0 15 37
            37 52 2
            39 0 15
                    
            fertilizer-to-water map:
            49 53 8
            0 11 42
            42 0 7
            57 7 4
                    
            water-to-light map:
            88 18 7
            18 25 70
                    
            light-to-temperature map:
            45 77 23
            81 45 19
            68 64 13
                    
            temperature-to-humidity map:
            0 69 1
            1 0 69
                    
            humidity-to-location map:
            60 56 37
            56 93 4
            """;

    record Ranges(List<Range> ranges) {

        public long map(final long source) {
            for (final Range range : this.ranges) {
                if (range.srcRangeStart <= source && range.srcRangeStart + range.length > source) {
                    return range.destRangeStart + (source - range.srcRangeStart);
                }
            }
            return source;
        }
    }

    record Range(long destRangeStart, long srcRangeStart, long length) {

    }

    @Test
    public void part1WithMockData() {
        assertEquals(35, part1Solution(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(165788812, part1Solution(input(this)));
    }

    @Test
    public void part2WithMockData() throws Exception {
        assertEquals(46, part2Solution(mockInput(MOCK)));
    }

    @Test
    public void part2() throws Exception {
        assertEquals(1928058, part2Solution(input(this)));
    }

    private long part1Solution(final Input input) {
        final var seeds = seeds(input.text());
        final var maps = getMaps(input);

        long closest = Long.MAX_VALUE;
        for (final Long seed : seeds) {
            closest = Math.min(closest, location(seed, maps));
        }

        return closest;
    }

    private long part2Solution(final Input input) throws Exception {
        final var seeds = seeds(input.text());
        final var maps = getMaps(input);

        final var groups = Lists.partition(seeds, 2);

        final var executor = Executors.newCachedThreadPool();
        var futures = Lists.<Future<Long>>newArrayList();
        for (final List<Long> seedGroup : groups) {
            futures.add(executor.submit(() -> closest(maps, seedGroup.get(0), seedGroup.get(1))));
        }

        var closest = Long.MAX_VALUE;
        for (final Future<Long> future : futures) {
            closest = Math.min(closest, future.get());
        }

        executor.close();
        return closest;
    }

    private Map<String, Ranges> getMaps(final Input input) {
        return Map.of(
                SEED_TO_SOIL, map(input.text(), SEED_TO_SOIL),
                SOIL_TO_FERTILIZER, map(input.text(), SOIL_TO_FERTILIZER),
                FERTILIZER_TO_WATER, map(input.text(), FERTILIZER_TO_WATER),
                WATER_TO_LIGHT, map(input.text(), WATER_TO_LIGHT),
                LIGHT_TO_TEMPERATURE, map(input.text(), LIGHT_TO_TEMPERATURE),
                TEMPERATURE_TO_HUMIDITY, map(input.text(), TEMPERATURE_TO_HUMIDITY),
                HUMIDITY_TO_LOCATION, map(input.text(), HUMIDITY_TO_LOCATION)
        );
    }

    private long closest(final Map<String, Ranges> maps, long seedRangeStart, long seedRangeLength) {
        var closest = Long.MAX_VALUE;
        for (int i = 0; i < seedRangeLength; i++) {
            closest = Math.min(closest, location(seedRangeStart + i, maps));
        }
        return closest;
    }

    private long location(final Long seed, final Map<String, Ranges> maps) {
        final var soil = maps.get(SEED_TO_SOIL).map(seed);
        final var fertiliser = maps.get(SOIL_TO_FERTILIZER).map(soil);
        final var water = maps.get(FERTILIZER_TO_WATER).map(fertiliser);
        final var light = maps.get(WATER_TO_LIGHT).map(water);
        final var temperature = maps.get(LIGHT_TO_TEMPERATURE).map(light);
        final var humidity = maps.get(TEMPERATURE_TO_HUMIDITY).map(temperature);
        return maps.get(HUMIDITY_TO_LOCATION).map(humidity);
    }

    private List<Long> seeds(final String input) {
        final var matcher = compile("seeds: ([\\d ]+)").matcher(input);
        checkState(matcher.find());
        return parseNumbers(matcher.group(1));
    }

    private Ranges map(final String input, final String name) {
        final var matcher = compile(name + " map:\\n([\\d\\n ]+)").matcher(input);
        if (matcher.find()) {
            var result = Lists.<Range>newArrayList();

            final var lines = matcher.group(1).trim().split("\n");
            for (final String line : lines) {
                final var numbers = parseNumbers(line);
                checkState(numbers.size() == 3);
                result.add(new Range(numbers.get(0), numbers.get(1), numbers.get(2)));
            }
            return new Ranges(result);
        }
        throw new IllegalStateException();
    }
}
