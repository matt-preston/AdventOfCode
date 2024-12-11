package aoc.y2024;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 11, name = "Plutonian Pebbles")
public class Day11Solution {

    private static final String MOCK = "125 17";

    @Test
    public void part1WithMockData() {
        assertEquals(55312, stones(mockInput(MOCK), 25));
    }

    @Test
    public void part1() {
        assertEquals(182081, stones(input(this), 25));
    }

    @Test
    public void part2() {
        var prev = 0;
        for (int i = 0; i <= 30; i++) {
            var result = stones(input(this), i);
            System.out.printf("%d => %d (diff=%d))%n", i, result, result - prev);
            prev = result;
        }

        System.out.println(predict(10));
    }

    private int predict(int times) {
        var a = 1;
        var b = 1.2;
        var c = 8.2;

        return (int) ((a * (int) Math.pow(times, 2)) + (b * times) + c);
    }

    private int stones(Input input, int times) {
        List<Long> stones = Utils.parseLongs(input.text());

        for (int i = 0; i < times; i++) {
            stones = rearrange(stones);
        }

        return stones.size();
    }

    private List<Long> rearrange(List<Long> stones) {
        var result = Lists.<Long>newLinkedList();

        for (Long stone : stones) {
            if (stone == 0) {
                result.add(1L);
            } else if (((int) (Math.log10(stone)) + 1) % 2 == 0) {  // even digits
                int len = (int) (Math.log10(stone) + 1);

                var first = (long) (stone / Math.pow(10, len / 2));
                var second = (long) (stone - (first * Math.pow(10, len / 2)));

                result.add(first);
                result.add(second);
            } else {
                result.add(stone * 2024L);
            }
        }

        return result;
    }


}
