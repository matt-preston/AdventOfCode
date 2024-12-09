package aoc.y2024;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 9, name = "Disk Fragmenter")
public class Day09Solution {

    private static final String MOCK = "2333133121414131402";

    @Test
    public void part1WithMockData() {
        assertEquals(1928, checksum(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(6320029754031L, checksum(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(2858, checksum2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(6347435485773L, checksum2(input(this)));
    }

    private long checksum(Input input) {
        var blocks = rearrange(blocks(input.text()));

        var sum = 0L;
        char[] charArray = blocks.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] != '.') {
                sum += ((long) i * (charArray[i] - 0xFF));
            }
        }
        return sum;
    }

    private long checksum2(Input input) {
        var blocks = rearrange(blocks2(input.text()));

        var sum = 0L;
        for (Entry<Integer, Block> entry : blocks.entrySet()) {
            var block = entry.getValue();
            for (int i = 0; i < block.length(); i++) {
                sum += (long) (entry.getKey() + i) * block.id();;
            }
        }
        return sum;
    }

    private String rearrange(String input) {
        var numFreeBlocks = input.chars().filter(ch -> ch == '.').count();

        while (input.indexOf('.') < input.length() - numFreeBlocks) {
            for (int i = input.length() - 1; i >= 0; i--) {
                if (input.charAt(i) != '.') {
                    var target = input.indexOf('.');

                    var tmp = input.toCharArray();
                    tmp[target] = tmp[i];
                    tmp[i] = '.';
                    input = new String(tmp);

                    break;
                }
            }
        }

        return input;
    }

    private Map<Integer, Block> rearrange(Map<Integer, Block> blocks) {
        var usedBlocks = blocks.entrySet().stream()
                .filter(e -> e.getValue().id() != null)
                .sorted(comparingByKey(Comparator.reverseOrder()))
                .toList();

        var freeBlocks = blocks.entrySet().stream()
                .filter(e -> e.getValue().id() == null)
                .collect(toMap(Entry::getKey, Entry::getValue, (b1, _) -> b1, TreeMap::new));

        var result = Maps.<Integer, Block>newHashMap();

        for (Entry<Integer, Block> used : usedBlocks) {
            var free = freeBlocks.entrySet().stream()
                    .filter(e -> e.getKey() < used.getKey())
                    .filter(e -> e.getValue().length() >= used.getValue().length())
                    .findFirst()
                    .orElse(null);

            if (free != null) {
                // Copy these as the Entry will change as we modify the underlying Map
                var targetIndex = free.getKey();
                var targetBlock = free.getValue();

                result.put(targetIndex, used.getValue());
                freeBlocks.remove(targetIndex);
                freeBlocks.put(used.getKey(), new Block(null, used.getValue().length())); // create free space where the file used to be

                // pad with free space
                if (targetBlock.length() > used.getValue().length()) {
                    int index = targetIndex + used.getValue().length();
                    freeBlocks.put(index, new Block(null, targetBlock.length() - used.getValue().length()));
                }
            } else {
                result.put(used.getKey(), used.getValue()); // keep in same position
            }
        }

        return result;
    }

    private String blocks(String text) {
        var id = 0xFF;
        var freeSpace = false;
        var result = new StringBuilder();

        for (char c : text.toCharArray()) {
            var times = Integer.parseInt("" + c);
            if (freeSpace) {
                result.append(Strings.repeat(".", times));
            } else {
                result.append(Strings.repeat("" + (char) (id++), times));
            }
            freeSpace = !freeSpace;
        }

        return result.toString();
    }

    record Block(Integer id, int length) {
    }

    private Map<Integer, Block> blocks2(String text) {
        var id = 0;
        var offset = 0;
        var freeSpace = false;
        var result = Maps.<Integer, Block>newTreeMap();

        char[] charArray = text.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            var c = charArray[i];
            var length = Integer.parseInt("" + c);
            if (freeSpace) {
                result.put(offset, new Block(null, length));
            } else {
                result.put(offset, new Block(id++, length));
            }
            offset += length;
            freeSpace = !freeSpace;
        }

        return result;
    }
}
