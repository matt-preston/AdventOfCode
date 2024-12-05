package aoc.y2023;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkState;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.parseLongs;

@AdventOfCode(year = 2023, day = 5, name = "If You Give A Seed A Fertilizer")
public class Day05Solution {

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

    record Range(long srcStart, long destStart, long length) implements Comparable<Range> {
        boolean contains(long value) {
            return srcStart <= value && srcStart + length > value;
        }

        long map(long value) {
            return destStart + (value - srcStart);
        }

        @Override
        public int compareTo(Range o) {
            return Long.compare(srcStart, o.srcStart);
        }

        Range reverse() {
            return new Range(destStart, srcStart, length);
        }
    }

    record Mapping(String name, Collection<Range> ranges) {
        long map(final long value) {
            for (Range range : ranges) {
                if (range.contains(value)) {
                    return range.map(value);
                }
            }
            return value;
        }

        Mapping reverse() {
            return new Mapping(name + "(rev)", ranges.stream()
                    .map(Range::reverse)
                    .collect(toCollection(TreeSet::new)));
        }
    }

    record Pipeline(List<Mapping> mappings) {
        long map(final long value) {
            var result = value;
            for (Mapping mapping : mappings) {
                result = mapping.map(result);
            }
            return result;
        }

        Pipeline reverse() {
            return new Pipeline(mappings.stream()
                    .map(Mapping::reverse)
                    .toList()
                    .reversed());
        }
    }

    @Test
    public void part1WithMockData() {
        assertEquals(35, lowestLocation(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(165788812, lowestLocation(input(this)));
    }

    @Test
    public void part2WithMockData() throws Exception {
        assertEquals(46, lowestLocationForSeedRanges(mockInput(MOCK)));
    }

    @Test
    public void part2() throws Exception {
        assertEquals(1928058, lowestLocationForSeedRanges(input(this)));
    }

    private long lowestLocation(final Input input) {
        final var seeds = seeds(input.text());
        final var pipeline = build(input);

        long closest = Long.MAX_VALUE;
        for (final Long seed : seeds) {
            closest = Math.min(closest, pipeline.map(seed));
        }

        return closest;
    }

    private long lowestLocationForSeedRanges(final Input input) {
        final var seedRanges = Lists.partition(seeds(input.text()), 2);
        final var pipeline = build(input).reverse();

        for (long location = 0; location < 5_000_000; location++) {
            final var seed = pipeline.map(location);

            // see if this seed is in one of the ranges
            for (List<Long> seedRange : seedRanges) {
                long start = seedRange.get(0);
                long length = seedRange.get(1);

                if (start <= seed && start + length > seed) {
                    return location;
                }
            }
        }

        throw new IllegalStateException("Couldn't find a seed in one of the expected ranges");
    }

    private Pipeline build(Input input) {
        List<Mapping> mappings = ImmutableList.of(
                mapping(input.text(), "seed-to-soil"),
                mapping(input.text(), "soil-to-fertilizer"),
                mapping(input.text(), "fertilizer-to-water"),
                mapping(input.text(), "water-to-light"),
                mapping(input.text(), "light-to-temperature"),
                mapping(input.text(), "temperature-to-humidity"),
                mapping(input.text(), "humidity-to-location")
        );

        return new Pipeline(mappings);
    }

    private Mapping mapping(final String input, final String name) {
        final var matcher = compile(name + " map:\\n([\\d\\n ]+)").matcher(input);
        if (matcher.find()) {
            var result = Sets.<Range>newTreeSet();

            final var lines = matcher.group(1).trim().split("\n");
            for (final String line : lines) {
                final var numbers = Utils.parseLongs(line);
                checkState(numbers.size() == 3);
                result.add(new Range(numbers.get(1), numbers.get(0), numbers.get(2)));
            }
            return new Mapping(name, result);
        }
        throw new IllegalStateException();
    }

    private List<Long> seeds(final String input) {
        final var matcher = compile("seeds: ([\\d ]+)").matcher(input);
        checkState(matcher.find());
        return Utils.parseLongs(matcher.group(1));
    }
}
