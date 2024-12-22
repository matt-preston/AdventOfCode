package aoc.y2024;

import com.google.common.collect.*;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 22, name = "Monkey Market")
public class Day22Solution {

    private static final String MOCK1 = """
            1
            10
            100
            2024
            """;

    private static final String MOCK2 = """
            1
            2
            3
            2024
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(37327623, sumOfSecretNumbers(mockInput(MOCK1)));
    }

    @Test
    public void part1() {
        assertEquals(21147129593L, sumOfSecretNumbers(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(23, sumOfBananas(mockInput(MOCK2)));
    }

    @Test
    public void part2() {
        assertEquals(2445, sumOfBananas(input(this)));
    }

    private long sumOfSecretNumbers(Input input) {
        return input.lines().stream()
                .mapToLong(Long::parseLong)
                .map(secretNumber -> secretNumber(secretNumber, 2000))
                .sum();
    }

    private int sumOfBananas(Input input) {
        var totals = Maps.<Integer, Integer>newHashMap();

        for (String line : input.lines()) {
            long secretNumber = Long.parseLong(line);
            var changes = priceChanges(secretNumber);
            for (Map.Entry<Integer, Integer> entry : changes.entrySet()) {
                totals.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        return totals.values().stream().max(Comparator.naturalOrder()).orElse(0);
    }

    private Map<Integer, Integer> priceChanges(long secretNumber) {
        var sequences = Maps.<Integer, Integer>newHashMap();

        // ring buffer
        var sequenceOffset = 0;
        var sequence = new int[4];

        var lsd = leastSignificantDigit(secretNumber);

        for (int i = 1; i <= 2000; i++) {
            var nextSecretNumber = secretNumber(secretNumber);
            var nextLsd = leastSignificantDigit(nextSecretNumber);

            sequence[sequenceOffset++ % sequence.length] = nextLsd - lsd;

            if (i > 3) {
                if (nextLsd != 0) { // no point counting zeros
                    var total = 0;
                    for (int j = 0; j < sequence.length; j++) {
                        var digit = sequence[(sequenceOffset + j) % sequence.length] + 10; // remap to unsigned byte
                        total |= (digit) << j * 8;  // pack into a single int
                    }
                    sequences.putIfAbsent(total, nextLsd);
                }
            }

            secretNumber = nextSecretNumber;
            lsd = nextLsd;
        }

        return sequences;
    }

    private long secretNumber(long secretNumber, int times) {
        if (times == 0) {
            return secretNumber;
        }
        return secretNumber(secretNumber(secretNumber), times - 1);
    }

    private long secretNumber(long secretNumber) {
        var step1 = (long) Math.floorMod(secretNumber ^ (secretNumber * 64), 16777216);
        var step2 = (long) Math.floorMod(step1 ^ (step1 / 32), 16777216);
        return  Math.floorMod(step2 ^ (step2 * 2048), 16777216);
    }

    private int leastSignificantDigit(long value) {
        return (int) (value - ((value / 10) * 10));
    }
}
