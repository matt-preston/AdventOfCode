package aoc.y2025;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.Arrays;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2025, day = 10, name = "Factory")
public class Day10Solution {

    private static final String MOCK = """
            [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
            [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
            [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(7, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(509, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(33, solve2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(20083, solve2(input(this)));
    }

    private int solve(Input input) {
        return input.lines().stream()
                .mapToInt(this::minButtonPresses)
                .sum();
    }

    private int solve2(Input input) {
        Loader.loadNativeLibraries();

        return input.lines().stream()
                .mapToInt(this::minButtonPresses2)
                .sum();
    }

    private int minButtonPresses(String input) {
        var p = Pattern.compile("\\[(.*)] (.*) \\{.*}");
        var matcher = p.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalStateException("invalid input");
        }

        var targetBits = matcher.group(1).chars()
                .map(c -> c == '#' ? 1 : 0)
                .toArray();

        var buttonDefinitions = Arrays.stream(matcher.group(2).split(" "))
                .map(s -> s.replaceAll("\\(", ""))
                .map(s -> s.replaceAll("\\)", ""))
                .map(Utils::parseInts)
                .toList();

        long target = 0;
        for (int i = 0; i < targetBits.length; i++) {
            if (targetBits[i] == 1) target |= 1L << i;
        }

        int numButtons = buttonDefinitions.size();
        long[] buttonMasks = new long[numButtons];

        for (int b = 0; b < numButtons; b++) {
            long mask = 0;
            for (int light : buttonDefinitions.get(b)) {
                mask |= 1L << light;
            }
            buttonMasks[b] = mask;
        }

        int bestPresses = Integer.MAX_VALUE;
        long bestCombo = -1;

        long totalCombos = 1L << numButtons;

        // TODO could search by minimum presses increasing, then break on first match
        for (long combo = 0; combo < totalCombos; combo++) {

            long state = 0;

            // apply pressed buttons
            for (int j = 0; j < numButtons; j++) {
                if (((combo >> j) & 1L) == 1L) {
                    state ^= buttonMasks[j];
                }
            }

            if (state == target) {
                int presses = Long.bitCount(combo);
                if (presses < bestPresses) {
                    bestPresses = presses;
                    bestCombo = combo;
                }
            }
        }

        if (bestCombo > 0) {
            return bestPresses;
        }

        return 0;
    }

    private int minButtonPresses2(String input) {
        var p = Pattern.compile("\\[.*] (.*) \\{(.*)}");
        var matcher = p.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalStateException("invalid input");
        }

        var buttonDefs = Arrays.stream(matcher.group(1).split(" "))
                .map(s -> s.replaceAll("\\(", ""))
                .map(s -> s.replaceAll("\\)", ""))
                .map(Utils::parseInts)
                .toList();

        var targets = Utils.parseInts(matcher.group(2));

        var numButtons = buttonDefs.size();

        var solver = MPSolver.createSolver("CBC_MIXED_INTEGER_PROGRAMMING");

        // Compute a safe finite upper bound for each button variable.
        var upperBound = targets.stream()
                .mapToInt(i -> i)
                .sum();

        // Create integer variables x_j (0 .. upperBound)
        var x = new MPVariable[numButtons];
        for (int j = 0; j < numButtons; j++) {
            x[j] = solver.makeIntVar(0.0, upperBound, "x" + j);
        }

        // Create constraints: for each counter i, sum_j A[i][j] * x_j == targets[i]
        for (int i = 0; i < targets.size(); i++) {
            var c = solver.makeConstraint(targets.get(i), targets.get(i), "counter_" + i);
            for (int j = 0; j < numButtons; j++) {
                // coefficient is 1 if button j affects counter i, else 0
                var affects = false;
                for (int idx : buttonDefs.get(j))
                    if (idx == i) {
                        affects = true;
                        break;
                    }
                if (affects) c.setCoefficient(x[j], 1.0);
            }
        }

        // Objective: minimize sum_j x_j
        var objective = solver.objective();
        for (int j = 0; j < numButtons; j++) {
            objective.setCoefficient(x[j], 1.0);
        }
        objective.setMinimization();

        // Solve
        var resultStatus = solver.solve();

        if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            return (int) Math.round(solver.objective().value());
        }

        return 0;
    }
}
