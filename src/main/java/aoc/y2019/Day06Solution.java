package aoc.y2019;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2019, day = 6, name = "Universal Orbit Map")
public class Day06Solution {

    private static final String SANTA = "SAN";

    @Test
    public void part1WithSampleData() {
        assertEquals(42, numberOfOrbits(mockInput("""
                COM)B
                B)C
                C)D
                D)E
                E)F
                B)G
                G)H
                D)I
                E)J
                J)K
                K)L
                """)));
    }

    @Test
    public void part1() {
        assertEquals(322508, numberOfOrbits(input(this)));
    }

    @Test
    public void part2WithSampleData() {
        assertEquals(4, numberOfOrbitalTransfersToSanta(mockInput("""
                COM)B
                B)C
                C)D
                D)E
                E)F
                B)G
                G)H
                D)I
                E)J
                J)K
                K)L
                K)YOU
                I)SAN
                """)));
    }

    @Test
    public void part2() {
        assertEquals(496, numberOfOrbitalTransfersToSanta(input(this)));
    }

    private int numberOfOrbits(Input input) {
        final var costs = numberOfOrbits(orbitGraph(input), "COM");
        return costs.values().stream().mapToInt(i -> i).sum();
    }

    private int numberOfOrbitalTransfersToSanta(Input input) {
        final var from = "YOU";
        final var graph = orbitGraph(input);

        var commonParent = from;
        while(!orbitedBySanta(graph, commonParent)) {
            final var edge = graph.incomingEdgesOf(commonParent);
            commonParent = Graphs.getOppositeVertex(graph, edge.iterator().next(), commonParent);
        }

        final var costs = numberOfOrbits(graph, commonParent);
        return costs.get(from) + costs.get(SANTA) - 2;
    }

    private Map<String, Integer> numberOfOrbits(Graph<String, DefaultEdge> graph, String start) {
        var costs = Maps.<String, Integer>newHashMap();
        var frontier = Lists.<String>newLinkedList();
        frontier.add(start);

        while (!frontier.isEmpty()) {
            final var current = frontier.poll();
            final var cost = costs.getOrDefault(current, 0);
            for (DefaultEdge defaultEdge : graph.outgoingEdgesOf(current)) {
                final var next = Graphs.getOppositeVertex(graph, defaultEdge, current);
                frontier.add(next);
                costs.put(next, cost + 1);
            }
        }

        return costs;
    }

    private boolean orbitedBySanta(Graph<String, DefaultEdge> graph, String object) {
        var frontier = Lists.<String>newLinkedList();
        frontier.add(object);

        while (!frontier.isEmpty()) {
            final var current = frontier.poll();
            if (current.equals(SANTA)) {
                return true;
            }

            for (DefaultEdge defaultEdge : graph.outgoingEdgesOf(current)) {
                frontier.add(Graphs.getOppositeVertex(graph, defaultEdge, current));
            }
        }

        return false;
    }

    private Graph<String, DefaultEdge> orbitGraph(Input input) {
        var graph = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        for (String line : input.lines()) {
            final var parts = line.split("\\)");
            Graphs.addEdgeWithVertices(graph, parts[0], parts[1]);
        }
        return graph;
    }
}
