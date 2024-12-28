package aoc.y2024;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 24, name = "Crossed Wires")
public class Day24Solution {

    private static final String MOCK1 = """
            x00: 1
            x01: 1
            x02: 1
            y00: 0
            y01: 1
            y02: 0
            
            x00 AND y00 -> z00
            x01 XOR y01 -> z01
            x02 OR y02 -> z02
            """;

    private static final String MOCK2 = """
            x00: 1
            x01: 0
            x02: 1
            x03: 1
            x04: 0
            y00: 1
            y01: 1
            y02: 1
            y03: 1
            y04: 1
            
            ntg XOR fgs -> mjb
            y02 OR x01 -> tnw
            kwq OR kpj -> z05
            x00 OR x03 -> fst
            tgd XOR rvg -> z01
            vdt OR tnw -> bfw
            bfw AND frj -> z10
            ffh OR nrd -> bqk
            y00 AND y03 -> djm
            y03 OR y00 -> psh
            bqk OR frj -> z08
            tnw OR fst -> frj
            gnj AND tgd -> z11
            bfw XOR mjb -> z00
            x03 OR x00 -> vdt
            gnj AND wpb -> z02
            x04 AND y00 -> kjc
            djm OR pbm -> qhw
            nrd AND vdt -> hwm
            kjc AND fst -> rvg
            y04 OR y02 -> fgs
            y01 AND x02 -> pbm
            ntg OR kjc -> kwq
            psh XOR fgs -> tgd
            qhw XOR tgd -> z09
            pbm OR djm -> kpj
            x03 XOR y03 -> ffh
            x00 XOR y04 -> ntg
            bfw OR bqk -> z06
            nrd XOR fgs -> wpb
            frj XOR qhw -> z04
            bqk OR frj -> z07
            y03 OR x01 -> nrd
            hwm AND bqk -> z03
            tgd XOR rvg -> z12
            tnw OR pbm -> gnj
            """;

    @Test
    public void part1WithSimpleMockData() {
        assertEquals(4, simulate(mockInput(MOCK1)));
    }

    @Test
    public void part1WithMockData() {
        assertEquals(2024, simulate(mockInput(MOCK2)));
    }

    @Test
    public void part1() {
        assertEquals(52038112429798L, simulate(input(this)));
    }

    enum Logic {AND, OR, XOR}

    record Gate(int id, String left, String right, String output, Logic logic) {
    }

    @Test
    public void part2() throws Exception {
        var outputFile = new File("docs/2024/24/graph.dot");
        outputFile.getParentFile().mkdirs();

        try (BufferedWriter w = Files.newWriter(outputFile, Charset.defaultCharset())) {
            var gates = gates(input(this));

            w.write("digraph {\n");

            for (Gate gate : gates) {
                w.write(format("  %s [label=%s,shape=square,style=filled,fillcolor=lightgrey]%n", gate.id(), gate.logic()));
            }

            var wires = new ImmutableSet.Builder<String>()
                    .addAll(gates.stream().map(Gate::left).collect(Collectors.toSet()))
                    .addAll(gates.stream().map(Gate::right).collect(Collectors.toSet()))
                    .addAll(gates.stream().map(Gate::output).collect(Collectors.toSet()))
                    .build();

            for (String wire : wires) {
                w.write("  " + wire + "\n");
            }

            w.newLine();

            for (Gate gate : gates) {
                w.write(format("  %s -> %s%n", gate.left(), gate.id()));
                w.write(format("  %s -> %s%n", gate.right(), gate.id()));
                w.write(format("  %s -> %s%n", gate.id(), gate.output()));
                w.newLine();
            }

            w.write("}");
        }

        // After checking the graph visually, rewire:
        // y12 -> XOR -> XOR -> *z12*
        // y12 -> AND -> *OR*
        // swap: z12,kwb

        // y16 -> XOR -> XOR -> *z16*
        // y16 -> XOR -> AND -> *OR*
        // swap: z16,qkf

        // y24 -> XOR -> XOR -> *z24*
        // y24 -> AND -> OR -> XOR
        // swap: z24,tgr

        // y29 -> XOR -> *XOR* -> z29
        // y29 -> AND -> *OR*
        // swap: jqn,cph

        var result = Set.of("z12", "kwb", "z16", "qkf", "z24", "tgr", "jqn", "cph")
                .stream()
                .sorted()
                .collect(Collectors.joining(","));

        assertEquals("cph,jqn,kwb,qkf,tgr,z12,z16,z24", result);
    }

    private long simulate(Input input) {
        var wires = Maps.<String, Boolean>newHashMap();
        for (String line : input.section(0).lines()) {
            var bits = line.split(":\\s+");
            wires.put(bits[0], bits[1].equals("1"));
        }

        var gates = gates(input);

        while (!gates.isEmpty()) {
            var gate = gates.poll();

            if (wires.containsKey(gate.left()) && wires.containsKey(gate.right())) {
                var left = wires.get(gate.left());
                var right = wires.get(gate.right());

                var result = switch (gate.logic()) {
                    case AND -> left && right;
                    case OR -> left || right;
                    case XOR -> left ^ right;
                };

                wires.put(gate.output(), result);
            } else {
                gates.add(gate);
            }
        }

        var values = wires.entrySet().stream()
                .filter(e -> e.getKey().startsWith("z"))
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();

        var result = 0L;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i)) {
                result |= (1L << i);
            }
        }

        return result;
    }

    private static LinkedList<Gate> gates(Input input) {
        var result = Lists.<Gate>newLinkedList();
        for (String line : input.section(1).lines()) {
            var bits = line.split(" -> ");
            var inputBits = bits[0].split("\\s");
            result.add(new Gate(result.size(), inputBits[0], inputBits[2], bits[1], Enum.valueOf(Logic.class, inputBits[1])));
        }
        return result;
    }
}
