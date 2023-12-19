package aoc.y2023;

import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day=19)
public class Day19Test {

  static class Node {
    record Edge(Node child, List<Condition> conditions) {}

    String name;
    Set<Edge> children = Sets.newHashSet();

    public Node(final String name) {
      this.name = name;
    }

    public void addChild(Node node, List<Condition> conditions) {
      children.add(new Edge(node, conditions));
    }

    @Override
    public String toString() {
      return name;
    }
  }

  record Part(int x, int m, int a, int s) {
    public int ratings() {
      return x + m + a + s;
    }
  }

  record Workflow(String name, List<Rule> rules) {
  }

  record Condition(char c, char op, int value) {
    public Condition invert() {
      return switch (op) {
        case '>' -> new Condition(c,'<', value + 1);
        case '<' -> new Condition(c,'>', value - 1);
        default -> throw new IllegalStateException("unknown operator");
      };
    }

    public boolean process(final Part part) {
      return switch (c) {
        case 'x' -> check(part.x);
        case 'm' -> check(part.m);
        case 'a' -> check(part.a);
        case 's' -> check(part.s);
        default -> throw new IllegalStateException("Unknown part value");
      };
    }

    private boolean check(int partValue) {
      return switch (op) {
        case '<' -> partValue < value;
        case '>' -> partValue > value;
        default -> throw new IllegalStateException("Unknown operator");
      };
    }

    public void clearBits(final BitSet bits) {
      if (op == '<') {
        IntStream.range(value, 4001).forEach(bits::clear);
      } else {
        IntStream.range(0, value + 1).forEach(bits::clear);
      }
    }
  }

  record Rule(Condition condition, String nextWorkflow) {
    public String process(final Part part) {
      if (condition == null) {
        return nextWorkflow;
      }

      return condition.process(part) ? nextWorkflow : null;
    }
  }

  @Test
  public void part1WithMockData() {
    assertEquals(19114, sumOfAcceptedParts(mockInput("""
        px{a<2006:qkq,m>2090:A,rfg}
        pv{a>1716:R,A}
        lnx{m>1548:A,A}
        rfg{s<537:gd,x>2440:R,A}
        qs{s>3448:A,lnx}
        qkq{x<1416:A,crn}
        crn{x>2662:A,R}
        in{s<1351:px,qqz}
        qqz{s>2770:qs,m<1801:hdj,R}
        gd{a>3333:R,R}
        hdj{m>838:A,pv}
                
        {x=787,m=2655,a=1222,s=2876}
        {x=1679,m=44,a=2067,s=496}
        {x=2036,m=264,a=79,s=2244}
        {x=2461,m=1339,a=466,s=291}
        {x=2127,m=1623,a=2188,s=1013}
        """)));
  }

  @Test
  public void part1() {
    assertEquals(495298, sumOfAcceptedParts(input(this)));
  }

  @Test
  public void part2WithMockData() {
    assertEquals(167_409_079_868_000L, distinctCombinations(mockInput("""
        px{a<2006:qkq,m>2090:A,rfg}
        pv{a>1716:R,A}
        lnx{m>1548:A,A}
        rfg{s<537:gd,x>2440:R,A}
        qs{s>3448:A,lnx}
        qkq{x<1416:A,crn}
        crn{x>2662:A,R}
        in{s<1351:px,qqz}
        qqz{s>2770:qs,m<1801:hdj,R}
        gd{a>3333:R,R}
        hdj{m>838:A,pv}
                
        {x=787,m=2655,a=1222,s=2876}
        {x=1679,m=44,a=2067,s=496}
        {x=2036,m=264,a=79,s=2244}
        {x=2461,m=1339,a=466,s=291}
        {x=2127,m=1623,a=2188,s=1013}
        """)));
  }

  @Test
  public void part2() {
    assertEquals(132_186_256_794_011L, distinctCombinations(input(this)));
  }

  public long distinctCombinations(Input input) {
    // build nodes
    var nodes = Maps.<String, Node>newHashMap();
    nodes.put("A", new Node("A"));
    nodes.put("R", new Node("R"));
    for (final Workflow workflow : workflows(input).values()) {
      nodes.put(workflow.name(), new Node(workflow.name()));
    }

    // build relationships
    for (final Workflow workflow : workflows(input).values()) {
      var node = nodes.get(workflow.name());

      var negated = Lists.<Condition>newArrayList();
      for (final Rule rule : workflow.rules()) {
        final var childNode = nodes.get(rule.nextWorkflow());

        var conditions = Lists.newLinkedList(negated);
        if (rule.condition() != null) {
          conditions.add(rule.condition());
          negated.add(rule.condition().invert());
        }
        node.addChild(childNode, conditions);
      }
    }

    // Find all paths to A
    final var start = nodes.get("in");

    var frontier = new LinkedList<Node>();
    frontier.add(start);
    var cameFrom = HashMultimap.<Node, Node.Edge>create();
    cameFrom.put(start, null);

    while(!frontier.isEmpty()) {
      var current = frontier.poll();

      for (final Node.Edge edge : current.children) {
        final var next = edge.child();
        final Node.Edge reverse = new Node.Edge(current, edge.conditions());

        if(!cameFrom.containsEntry(next, reverse)) {
          frontier.add(next);
          cameFrom.put(next, reverse);
        }
      }
    }

    var combinations = 0L;

    // calculate combinations
    for (final Node.Edge edge : cameFrom.get(nodes.get("A"))) {
      var path = Lists.<List<Condition>>newArrayList();
      path.add(edge.conditions());

      var next  = edge;
      while (next.child != start) {
        next = cameFrom.get(next.child).stream().findFirst().get();
        path.add(next.conditions());
      }

      Collections.reverse(path);

      var x = createBitset();
      var m = createBitset();
      var a = createBitset();
      var s = createBitset();

      for (final List<Condition> conditions : path) {
        for (final Condition condition : conditions) {
          switch(condition.c) {
            case 'x' -> condition.clearBits(x);
            case 'm' -> condition.clearBits(m);
            case 'a' -> condition.clearBits(a);
            case 's' -> condition.clearBits(s);
            default -> throw new IllegalStateException("unknown part value");
          }
        }
      }

      combinations += (countSet(x) * countSet(m) * countSet(a) * countSet(s));
    }

    return combinations;
  }

  private long countSet(BitSet bitset) {
    int count = 0;
    for (int i = 1; i <= 4000; i++) {
      if (bitset.get(i)) {
        count++;
      }
    }
    return count;
  }

  private BitSet createBitset() {
    var result = new BitSet();
    result.set(0, 4001, true);
    return result;
  }

  public long sumOfAcceptedParts(Input input) {
    final var workflows = workflows(input);

    var sum = 0L;
    for (final Part part : parts(input)) {

      var next = "in";
      while(!("A".equals(next) || "R".equals(next))) {
        for (final Rule rule : workflows.get(next).rules()) {
          next = rule.process(part);
          if (next != null) {
            break;
          }
        }
      }

      if ("A".equals(next)) {
        sum += part.ratings();
      }
    }

    return sum;
  }


  private Map<String, Workflow> workflows(Input input) {
    final var pattern = Pattern.compile("(\\w+)\\{([^}]+)}");

    var workflows = Maps.<String, Workflow>newHashMap();
    for (final String line : input.text().split("\n\n")[0].trim().split("\n")) {
      final var matcher = pattern.matcher(line);
      Preconditions.checkState(matcher.matches());

      var rules = Lists.<Rule>newArrayList();
      for (final String ruleString : matcher.group(2).split(",")) {
        final var split = ruleString.split(":");
        if (split.length == 2) {
          var condition = new Condition(split[0].charAt(0), split[0].charAt(1), Integer.parseInt(split[0].substring(2)));
          rules.add(new Rule(condition, split[1]));
        } else {
          rules.add(new Rule(null, split[0]));
        }
      }

      final var workflowName = matcher.group(1);
      workflows.put(workflowName, new Workflow(workflowName, rules));
    }

    return workflows;
  }

  private List<Part> parts(Input input) {
    final var pattern = Pattern.compile("\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}");

    var parts = Lists.<Part>newArrayList();
    for (final String line : input.text().split("\n\n")[1].trim().split("\n")) {
      final var matcher = pattern.matcher(line);
      Preconditions.checkState(matcher.matches());
      parts.add(new Part(parseInt(matcher.group(1)), parseInt(matcher.group(2)), parseInt(matcher.group(3)), parseInt(matcher.group(4))));
    }

    return parts;
  }
}
