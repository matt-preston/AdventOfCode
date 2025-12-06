package aoc.y2020;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.padStart;
import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 14, name = "Docking Data")
public class Day14Solution {

    private static final String MOCK = """
            mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
            mem[8] = 11
            mem[7] = 101
            mem[8] = 0
            """;

    private static final String MOCK2 = """
            mask = 000000000000000000000000000000X1001X
            mem[42] = 100
            mask = 00000000000000000000000000000000X0XX
            mem[26] = 1
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(165, solveGeneric(mockInput(MOCK), this::part1Decoder));
    }

    @Test
    public void part1() {
        assertEquals(13105044880745L, solveGeneric(input(this), this::part1Decoder));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(208, solveGeneric(mockInput(MOCK2), this::part2Decoder));
    }

    @Test
    public void part2() {
        assertEquals(3505392154485L, solveGeneric(input(this), this::part2Decoder));
    }

    interface Decoder {
        void updateMemory(Map<String, Long> memory, String mask, String address, String value);
    }

    private long solveGeneric(Input input, Decoder decoder) {
        var pattern = Pattern.compile("mem\\[(\\d+)] = (\\d+)");

        var memory = Maps.<String, Long>newHashMap();
        var mask = "X";

        for (String line : input.lines()) {
            if (line.startsWith("mask = ")) {
                mask = line.split("=")[1].trim();
            } else {
                var matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    var address = matcher.group(1);
                    var value = matcher.group(2);

                    decoder.updateMemory(memory, mask, address, value);
                }
            }
        }

        return memory.values().stream().mapToLong(l -> l).sum();
    }

    private void part1Decoder(Map<String, Long> memory, String mask, String address, String value) {
        var v = Long.parseLong(value);

        long mask1 = Long.parseLong(mask.replaceAll("X", "0"), 2);
        long mask2 = Long.parseLong(mask.replaceAll("X", "1"), 2);

        v = v | mask1;
        v = v & mask2;

        memory.put(address, v);
    }

    private void part2Decoder(Map<String, Long> memory, String mask, String address, String value) {
        var fullAddress = padStart(Integer.toString(parseInt(address), 2), 36, '0');
        var longValue = Long.parseLong(value);

        var result = new StringBuilder(fullAddress);
        for (int i = 0; i < mask.length(); i++) {
            if (mask.charAt(i) == 'X') {
                result.setCharAt(i, 'X');
            } else if (mask.charAt(i) == '1') {
                result.setCharAt(i, '1');
            }
        }

        expandAddresses(result.toString(), 0, "", addr -> memory.put(addr, longValue));
    }

    private void expandAddresses(String address, int index, String result, Consumer<String> onComplete) {
        if (index < address.length()) {
            var next = address.charAt(index);
            if (next == 'X') {
                expandAddresses(address, index + 1, result + "0", onComplete);
                expandAddresses(address, index + 1, result + "1", onComplete);
            } else {
                expandAddresses(address, index + 1, result + next, onComplete);
            }
        } else {
            onComplete.accept(result);
        }
    }
}
