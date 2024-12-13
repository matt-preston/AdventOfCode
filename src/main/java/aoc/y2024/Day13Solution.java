package aoc.y2024;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Section;
import utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 13, name = "Claw Contraption")
public class Day13Solution {

    private static final String MOCK = """
            Button A: X+94, Y+34
            Button B: X+22, Y+67
            Prize: X=8400, Y=5400
            
            Button A: X+26, Y+66
            Button B: X+67, Y+21
            Prize: X=12748, Y=12176
            
            Button A: X+17, Y+86
            Button B: X+84, Y+37
            Prize: X=7870, Y=6450
            
            Button A: X+69, Y+23
            Button B: X+27, Y+71
            Prize: X=18641, Y=10279
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(480, solve(mockInput(MOCK), 0));
    }

    @Test
    public void part1() {
        assertEquals(35729, solve(input(this), 0));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(875318608908L, solve(mockInput(MOCK), 10_000_000_000_000L));
    }

    @Test
    public void part2() {
        assertEquals(88584689879723L, solve(input(this), 10_000_000_000_000L));
    }

    private long solve(Input input, long prizeOffset) {
        return input.sections().stream()
                .mapToLong(section -> minimumTokens(section, prizeOffset))
                .sum();
    }

    private long minimumTokens(Section section, long prizeOffset) {
        var lines = section.lines().stream()
                .map(line -> line.replaceAll("\\D", " "))
                .toArray(String[]::new);

        var tmp = Utils.parseInts(lines[0]);
        var aX = tmp.get(0);
        var aY = tmp.get(1);

        tmp = Utils.parseInts(lines[1]);
        var bX = tmp.get(0);
        var bY = tmp.get(1);

        tmp = Utils.parseInts(lines[2]);
        var prizeX = tmp.get(0) + prizeOffset;
        var prizeY = tmp.get(1) + prizeOffset;

        // Simultaneous equations!
        //
        // Button A: X+94, Y+34
        // Button B: X+22, Y+67
        // Prize: X=8400, Y=5400
        //
        // 8400 = 94a + 22b
        // 5400 = 34a + 67b
        //
        // a = 80, b = 40
        var coefficients = new Array2DRowRealMatrix(new double[][]{{aX, bX}, {aY, bY}}, false);
        var solver = new LUDecomposition(coefficients).getSolver();

        var constants = new ArrayRealVector(new double[]{prizeX, prizeY}, false);
        var solution = solver.solve(constants);

        var solutionA = Math.round(solution.getEntry(0));
        var solutionB = Math.round(solution.getEntry(1));

        if ((solutionA * aX) + (solutionB * bX) == prizeX && (solutionA * aY) + (solutionB * bY) == prizeY) {
            return (solutionA * 3) + solutionB;
        } else {
            return 0; // no valid solution
        }
    }
}
