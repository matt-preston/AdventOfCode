package aoc.y2023;

import org.jgrapht.Graph;
import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 25, name = "Snowverload")
public class Day25Solution {

    private static final String MOCK = """
            jqt: rhn xhk nvd
            rsh: frs pzl lsr
            xhk: hfx
            cmg: qnr nvd lhk bvb
            rhn: xhk bvb hfx
            bvb: xhk hfx
            pzl: lsr hfx nvd
            qnr: nvd
            ntq: jqt hfx bvb xhk
            nvd: lhk
            lsr: lhk
            rzs: qnr cmg lsr rsh
            frs: qnr lhk lsr
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(54, productOfMinCutPartitions(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(520380, productOfMinCutPartitions(input(this)));
    }

    private int productOfMinCutPartitions(final Input input) {
        var graph = parse(input);

        final var sw = new StoerWagnerMinimumCut<>(graph);

        var partition1 = sw.minCut().size();
        var partition2 = graph.vertexSet().size() - sw.minCut().size();

        return partition1 * partition2;
    }

    private Graph<String, DefaultEdge> parse(Input input) {
        final Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        for (String line : input.lines()) {
            final var parts = line.split("[:\\s]+");
            for (int i = 1; i < parts.length; i++) {
                g.addVertex(parts[0]);
                g.addVertex(parts[i]);
                g.addEdge(parts[0], parts[i]);
            }
        }
        return g;
    }
}
