package aoc.y2024;

import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.clique.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Set;

import static java.util.stream.Collectors.joining;
import static org.jgrapht.Graphs.addEdgeWithVertices;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 23, name = "LAN Party")
public class Day23Solution {

    private static final String MOCK = """
            kh-tc
            qp-kh
            de-cg
            ka-co
            yn-aq
            qp-ub
            cg-tb
            vc-aq
            tb-ka
            wh-tc
            yn-cg
            kh-ub
            ta-co
            de-co
            tc-td
            tb-wq
            wh-td
            ta-ka
            td-qp
            aq-cg
            wq-ub
            ub-vc
            de-ta
            wq-aq
            wq-vc
            wh-yn
            ka-de
            kh-ta
            co-tc
            wh-qp
            tb-vc
            td-yn
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(7, interconnectedComputers(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(998, interconnectedComputers(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals("co,de,ka,ta", maximalClique(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals("cc,ff,fh,fr,ny,oa,pl,rg,uj,wd,xn,xs,zw", maximalClique(input(this)));
    }

    private int interconnectedComputers(Input input) {
        final var graph = graph(input);

        var result = Sets.<Set<String>>newLinkedHashSet();

        for (String v1 : graph.vertexSet()) {
            for (DefaultEdge edgeV1 : graph.edgesOf(v1)) {
                var v2 = Graphs.getOppositeVertex(graph, edgeV1, v1);
                for (DefaultEdge edgeV2 : graph.edgesOf(v2)) {
                    String v3 = Graphs.getOppositeVertex(graph, edgeV2, v2);
                    if (Graphs.neighborSetOf(graph, v3).contains(v1)) {
                        if (v1.startsWith("t") || v2.startsWith("t") || v3.startsWith("t")) {
                            result.add(Set.of(v1, v2, v3));
                        }
                    }
                }
            }
        }

        return result.size();
    }

    private String maximalClique(Input input) {
        var finder = new BronKerboschCliqueFinder<>(graph(input));
        return Streams.stream(finder.maximumIterator())
                .findFirst()
                .map(strings -> strings.stream()
                        .sorted()
                        .collect(joining(",")))
                .orElse("");
    }

    private Graph<String, DefaultEdge> graph(Input input) {
        var graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        for (String line : input.lines()) {
            var bits = line.split("-");
            addEdgeWithVertices(graph, bits[0], bits[1]);
        }
        return graph;
    }
}
