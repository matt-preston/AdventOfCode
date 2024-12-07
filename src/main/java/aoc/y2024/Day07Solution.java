package aoc.y2024;

import com.google.common.collect.*;
import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;
import utils.Utils;

import java.util.*;

import static com.google.common.collect.Sets.cartesianProduct;
import static java.util.Collections.nCopies;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 7, name = "Bridge Repair")
public class Day07Solution {

    private static final String MOCK = """
            190: 10 19
            3267: 81 40 27
            83: 17 5
            156: 15 6
            7290: 6 8 6 15
            161011: 16 10 13
            192: 17 8 14
            21037: 9 7 18 13
            292: 11 6 16 20
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(3749, sumValidEquations(mockInput(MOCK), Set.of("+", "*")));
    }

    @Test
    public void part1() {
        assertEquals(3312271365652L, sumValidEquations(input(this), Set.of("+", "*")));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(11387, sumValidEquations(mockInput(MOCK), Set.of("+", "*", "||")));
    }

    @Test
    public void part2() {
        assertEquals(509463489296712L, sumValidEquations(input(this), Set.of("+", "*", "||")));
    }

    record Equation(long result, List<Long> values) {
    }

    private long sumValidEquations(Input input, Set<String> valid) {
        var sum = 0L;

        for (Equation equation : parse(input)) {
            for (List<String> operators : cartesianProduct(nCopies(equation.values().size() - 1, valid))) {
                var tmp = equation.values().getFirst();

                for (int i = 1; i < equation.values().size(); i++) {
                    var right = equation.values().get(i);
                    var op = operators.get(i - 1);

                    tmp = switch (op) {
                        case "+" -> tmp + right;
                        case "*" -> tmp * right;
                        case "||" -> Long.valueOf(tmp + Long.toString(right));
                        default -> throw new IllegalStateException();
                    };
                }

                if (tmp == equation.result) {
                    sum += equation.result;
                    break;
                }
            }
        }

        return sum;
    }

    private List<Equation> parse(Input input) {
        var result = Lists.<Equation>newArrayList();
        for (String line : input.lines()) {
            List<Long> longs = Utils.parseLongs(line);
            result.add(new Equation(longs.getFirst(), longs.subList(1, longs.size())));
        }
        return result;
    }
}
