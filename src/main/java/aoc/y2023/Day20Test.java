package aoc.y2023;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 20)
public class Day20Test {

  enum Pulse {
    LOW, HIGH;

    public boolean high() {
      return this == HIGH;
    }

    public boolean low() {
      return !high();
    }
  }

  record Action(Module source, Module target, Pulse pulse) {


    @Override
    public String toString() {
      return (source == null ? "button" : source)  + " -" + pulse + "-> " + target;
    }
  }

  abstract class Module {
    private String name;
    private String displayName;
    protected Set<Module> connectedTo = Sets.newLinkedHashSet();
    protected Set<Module> connectedFrom = Sets.newLinkedHashSet();

    public Module(final String name, final String displayName) {
      this.name = name;
      this.displayName = displayName;
    }

    public String name() {
      return name;
    }

    public String displayName() {
      return displayName;
    }

    public void addConnection(final Module module) {
      connectedTo.add(module);
      module.connectedFrom(this);
    }

    abstract List<Action> trigger(Action action);

    protected void connectedFrom(Module module) {
      connectedFrom.add(module);
    }

    public void pruneConnectedTo(final Module module) {
      connectedTo.removeIf(p -> p != module);
    }

    public void pruneConnectedFrom(final Module module) {
      connectedFrom.removeIf(p -> p != module);
    }
  }

  class TestModule extends Module {

    public TestModule(final String name) {
      super(name, name);
    }

    @Override
    List<Action> trigger(final Action action) {
      return List.of();
    }
  }

  class BroadcasterModule extends Module {

    public BroadcasterModule(final String name) {
      super(name, name);
    }

    @Override
    List<Action> trigger(final Action action) {
      var actions = Lists.<Action>newArrayList();
      for (final Module target : connectedTo) {
        actions.add(new Action(this, target, action.pulse()));
      }
      return actions;
    }
  }


  class FlipFlopModule extends Module {
    private boolean on = false;

    public FlipFlopModule(final String name, String displayName) {
      super(name, displayName);
    }

    @Override
    List<Action> trigger(final Action action) {
      if (action.pulse.high()) {
        return List.of();
      }

      on = !on;
      var pulse = on ? Pulse.HIGH : Pulse.LOW;
      return connectedTo.stream()
          .map(m -> new Action(this, m, pulse))
          .collect(Collectors.toList());
    }
  }

  class ConjunctionModule extends Module {
    public Map<String, Pulse> state = Maps.newHashMap();

    public ConjunctionModule(final String name, String displayName) {
      super(name, displayName);
    }

    @Override
    protected void connectedFrom(final Module module) {
      super.connectedFrom(module);
      state.put(module.name, Pulse.LOW); // defaults to LOW
    }

    @Override
    public void pruneConnectedFrom(final Module module) {
      super.pruneConnectedFrom(module);
      final var iterator = state.keySet().iterator();
      while(iterator.hasNext()) {
        if (!iterator.next().equals(module.name)) {
          iterator.remove();
        }
      }
    }

    @Override
    List<Action> trigger(final Action action) {
      checkNotNull(state.put(action.source().name, action.pulse()));

      final var values = ImmutableSet.copyOf(state.values());

      var pulse = values.size() == 1 && values.contains(Pulse.HIGH) ?
          Pulse.LOW :
          Pulse.HIGH;

      return connectedTo.stream()
          .map(m -> new Action(this, m, pulse))
          .collect(Collectors.toList());
    }
  }

  @Test
  public void part1WithMockData() {
    assertEquals(32_000_000, simulate(build(mockInput("""
        broadcaster -> a, b, c
        %a -> b
        %b -> c
        %c -> inv
        &inv -> a
        """)), 1_000));

    assertEquals(11_687_500, simulate(build(mockInput("""
        broadcaster -> a
        %a -> inv, con
        &inv -> b
        %b -> con
        &con -> output
        """)), 1_000));
  }

  @Test
  public void part1() {
    assertEquals(898_557_000, simulate(build(input(this)), 1_000));
  }

  @Test
  public void part2() {
    final var input = input(this);
    graphviz(build(input));

    // Based on the output from above, find the 4 cycles from
    // broadcaster that feed into the conjunction before RX.
    // Prune out those 4 sub graphs and find the cycle time for each

    var cycle1 = prune(input, "xk", "rz");
    final var cycle1Times = simulateUntilRXLow(cycle1);

    var cycle2 = prune(input, "rj", "kv");
    final var cycle2Times = simulateUntilRXLow(cycle2);

    var cycle3 = prune(input, "gf", "jg");
    final var cycle3Times = simulateUntilRXLow(cycle3);

    var cycle4 = prune(input, "cn", "mr");
    final var cycle4Times = simulateUntilRXLow(cycle4);

    assertEquals(238420328103151L, cycle1Times * cycle2Times * cycle3Times * cycle4Times);
  }

  private void graphviz(Map<String, Module> modules) {
    for (final Module value : modules.values()) {
      System.out.println(value.name + " [label=\"" + value.displayName() + "\"]");
      System.out.println(value.name + " -> { " + value.connectedTo.stream()
          .map(Module::name)
          .collect(Collectors.joining(",")) + " }");
    }
  }

  private Map<String, Module> prune(Input inout, String start, String end) {
    final var modules = build(inout);

    final var broadcaster = modules.get("broadcaster");
    broadcaster.pruneConnectedTo(modules.get(start));

    final var qb = modules.get("qb");
    qb.pruneConnectedFrom(modules.get(end));

    return modules;
  }

  private Map<String, Module> build(Input input) {
    var modules = Maps.<String, Module>newHashMap();

    // build modules
    for (final String line : input.lines()) {
      final var name = line.split("->")[0].trim();
      if(name.equals("broadcaster")) {
        modules.put(name, new BroadcasterModule(name));
      } else if (name.startsWith("%")) {
        modules.put(name.substring(1), new FlipFlopModule(name.substring(1), name));
      } else if (name.startsWith("&")) {
        modules.put(name.substring(1), new ConjunctionModule(name.substring(1), name));
      } else {
        throw new IllegalStateException("bad name");
      }
    }

    // build edges
    for (final String line : input.lines()) {
      final var parts = line.split("->");
      var name = parts[0].trim();
      if (name.startsWith("%") || name.startsWith("&")) {
        name = name.substring(1);
      }

      final var from = modules.get(name);

      for (final String target : parts[1].trim().split(",")) {
        final var targetModule = modules.computeIfAbsent(target.trim(), TestModule::new);
        from.addConnection(targetModule);
      }
    }
    return modules;
  }

  private long simulate(final Map<String, Module> modules, int nTimes) {
    final var broadcaster = modules.get("broadcaster");

    long lowPulses = 0;
    long highPulses = 0;

    for (int i = 0; i < nTimes; i++) {
      var actions = Lists.<Action>newLinkedList();
      actions.add(new Action(null, broadcaster, Pulse.LOW));

      while(!actions.isEmpty()) {
        final var next = actions.poll();
//        System.out.println(next);
        if (next.pulse.high()) {
          highPulses++;
        } else {
          lowPulses++;
        }

        actions.addAll(next.target.trigger(next));
      }
    }

    return lowPulses * highPulses;
  }

  private long simulateUntilRXLow(final Map<String, Module> modules) {
    final var broadcaster = modules.get("broadcaster");

    long index = 0;
    while (true) {
      index++;
      if (index > 100_000) {
        throw new IllegalStateException("Too many iterations without getting LOW");
      }

      var actions = Lists.<Action>newLinkedList();
      actions.add(new Action(null, broadcaster, Pulse.LOW));

      while(!actions.isEmpty()) {
        final var next = actions.poll();

        if (next.target.name.equals("rx") && next.pulse().low()) {
          return index;
        }

        actions.addAll(next.target.trigger(next));
      }
    }
  }


}
