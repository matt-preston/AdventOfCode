package aoc.y2019;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2019, day = 14, name = "Space Stoichiometry")
public class Day14Solution {

    public static final String SAMPLE_1 = """
            157 ORE => 5 NZVS
            165 ORE => 6 DCFZ
            44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL
            12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ
            179 ORE => 7 PSHF
            177 ORE => 5 HKGWZ
            7 DCFZ, 7 PSHF => 2 XJWVT
            165 ORE => 2 GPVTF
            3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT
            """;
    public static final String SAMPLE_2 = """
            2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG
            17 NVRVD, 3 JNWZP => 8 VPVL
            53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL
            22 VJHF, 37 MNCFX => 5 FWMGM
            139 ORE => 4 NVRVD
            144 ORE => 7 JNWZP
            5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC
            5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV
            145 ORE => 6 MNCFX
            1 NVRVD => 8 CXFTF
            1 VJHF, 6 MNCFX => 4 RFSQX
            176 ORE => 6 VJHF
            """;
    public static final String SAMPLE_3 = """
            171 ORE => 8 CNZTR
            7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL
            114 ORE => 4 BHXH
            14 VRPVC => 6 BMBT
            6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL
            6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT
            15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW
            13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW
            5 BMBT => 4 WPTQ
            189 ORE => 9 KTJDG
            1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP
            12 VRPVC, 27 CNZTR => 2 XDBXC
            15 KTJDG, 12 BHXH => 5 XCVML
            3 BHXH, 2 VRPVC => 7 MZWV
            121 ORE => 7 VRPVC
            7 XCVML => 6 RJRHP
            5 BHXH, 4 VRPVC => 5 LTCX
            """;

    @Test
    public void part1WithVerySimpleData() {
        assertEquals(31, oreRequired(mockInput("""
                10 ORE => 10 A
                1 ORE => 1 B
                7 A, 1 B => 1 C
                7 A, 1 C => 1 D
                7 A, 1 D => 1 E
                7 A, 1 E => 1 FUEL
                """)));
    }

    @Test
    public void part1WithSimpleData() {
        assertEquals(165, oreRequired(mockInput("""
                9 ORE => 2 A
                8 ORE => 3 B
                7 ORE => 5 C
                3 A, 4 B => 1 AB
                5 B, 7 C => 1 BC
                4 C, 1 A => 1 CA
                2 AB, 3 BC, 4 CA => 1 FUEL
                """)));
    }

    @Test
    public void part1WithSampleData1() {
        assertEquals(13312, oreRequired(mockInput(SAMPLE_1)));
    }

    @Test
    public void part1WithSampleData2() {
        assertEquals(180697, oreRequired(mockInput(SAMPLE_2)));
    }

    @Test
    public void part1WithSampleData3() {
        assertEquals(2210736, oreRequired(mockInput(SAMPLE_3)));
    }

    @Test
    public void part1() {
        assertEquals(870051, oreRequired(input(this)));
    }

    @Test
    public void part2WithSampleData1() {
        assertEquals(82892753, amountOfFuel(mockInput(SAMPLE_1)));
    }

    @Test
    public void part2WithSampleData2() {
        assertEquals(5586022, amountOfFuel(mockInput(SAMPLE_2)));
    }

    @Test
    public void part2WithSampleData3() {
        assertEquals(460664, amountOfFuel(mockInput(SAMPLE_3)));
    }

    @Test
    public void part2() {
        assertEquals(1863741, amountOfFuel(input(this)));
    }

    record Chemical(String type, int quantity) {
    }

    record Reaction(int outputQuantity, List<Chemical> input) {
    }

    private long oreRequired(Input input) {
        var reactions = reactions(input);
        return produceFuel(reactions, inventory(reactions), "FUEL", 1);
    }

    private long produceFuel(Map<String, Reaction> reactions, Map<String, Long> inventory, String chemical, long amount) {
        var ore = 0L;
        var reaction = reactions.get(chemical);

        var multiplier = (long) Math.ceil(amount / (double) reaction.outputQuantity());

        for (Chemical input : reaction.input()) {
            if (input.type().equals("ORE")) {
                ore += multiplier * input.quantity();
            } else {

                if (inventory.get(input.type()) < multiplier * input.quantity()) {
                    ore += produceFuel(reactions, inventory, input.type(), (multiplier * input.quantity() - inventory.get(input.type())));
                }

                inventory.compute(input.type(), (key, balance) -> balance - (multiplier * input.quantity()));
            }
        }

        inventory.compute(chemical, (key, balance) -> balance + (multiplier * reaction.outputQuantity()));

        return ore;
    }

    private long amountOfFuel(Input input) {
        var reactions = reactions(input);
        var bank = inventory(reactions);

        final var maxOre = 1_000_000_000_000L;

        var upperBound = 1L;
        while (produceFuel(reactions, bank, "FUEL", upperBound) < maxOre) {
            upperBound *= 10;
        }

        var lowerBound = upperBound / 10;

        while (lowerBound != upperBound) {
            var guess = lowerBound + (long) Math.floor((upperBound - lowerBound) / 2d);
            if (guess == lowerBound) {
                return lowerBound;
            }

            if (produceFuel(reactions, bank, "FUEL", guess) <= maxOre) {
                lowerBound = guess;
            } else {
                upperBound = guess - 1;
            }
        }

        return lowerBound;
    }

    private Map<String, Reaction> reactions(Input input) {
        Map<String, Reaction> reactions = Maps.newHashMap();

        for (String line : input.lines()) {

            final var split = line.split("=>", 2);
            var right = parse(split[1]);
            var left = Arrays.stream(split[0].trim().split(","))
                    .map(this::parse)
                    .toList();

            reactions.put(right.type(), new Reaction(right.quantity(), left));
        }

        return reactions;
    }

    private Map<String, Long> inventory(Map<String, Reaction> reactions) {
        Map<String, Long> result = Maps.newHashMap();
        for (String key : reactions.keySet()) {
            result.put(key, 0L);
        }
        return result;
    }

    private Chemical parse(String input) {
        final var parts = input.trim().split("\\s", 2);
        return new Chemical(parts[1], Integer.parseInt(parts[0]));
    }
}
