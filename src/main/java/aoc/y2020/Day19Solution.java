package aoc.y2020;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 19, name = "Monster Messages")
public class Day19Solution {

    private static final String MOCK = """
            0: 4 1 5
            1: 2 3 | 3 2
            2: 4 4 | 5 5
            3: 4 5 | 5 4
            4: "a"
            5: "b"
            
            ababbb
            bababa
            abbbab
            aaabbb
            aaaabbb
            """;

    private static final String MOCK2 = """
            42: 9 14 | 10 1
            9: 14 27 | 1 26
            10: 23 14 | 28 1
            1: "a"
            11: 42 31
            5: 1 14 | 15 1
            19: 14 1 | 14 14
            12: 24 14 | 19 1
            16: 15 1 | 14 14
            31: 14 17 | 1 13
            6: 14 14 | 1 14
            2: 1 24 | 14 4
            0: 8 11
            13: 14 3 | 1 12
            15: 1 | 14
            17: 14 2 | 1 7
            23: 25 1 | 22 14
            28: 16 1
            4: 1 1
            20: 14 14 | 1 15
            3: 5 14 | 16 1
            27: 1 6 | 14 18
            14: "b"
            21: 14 1 | 1 14
            25: 1 1 | 1 14
            22: 14 14
            8: 42
            26: 14 22 | 1 20
            18: 15 15
            7: 14 5 | 1 21
            24: 14 1
            
            abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa
            bbabbbbaabaabba
            babbbbaabbbbbabbbbbbaabaaabaaa
            aaabbbbbbaaaabaababaabababbabaaabbababababaaa
            bbbbbbbaaaabbbbaaabbabaaa
            bbbababbbbaaaaaaaabbababaaababaabab
            ababaaaaaabaaab
            ababaaaaabbbaba
            baabbaaaabbaaaababbaababb
            abbbbabbbbaaaababbbbbbaaaababb
            aaaaabbaabaaaaababaa
            aaaabbaaaabbaaa
            aaaabbaabbaaaaaaabbbabbbaaabbaabaaa
            babaaabbbaaabaababbaabababaaab
            aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(2, solve(mockInput(MOCK), false));
    }

    @Test
    public void part1() {
        assertEquals(171, solve(input(this), false));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(12, solve(mockInput(MOCK2), true));
    }

    @Test
    public void part2() {
        assertEquals(0, solve(input(this), true));
    }

    private int solve(Input input, boolean part2) {
        var rules = Maps.<String, String>newHashMap();
        for (String line : input.section(0).lines()) {
            var parts = line.split(":");
            rules.put(parts[0], parts[1].trim());
        }

        if (part2) {
            rules.put("8", "42 | 42 8");
            rules.put("11", "42 31 | 42 11 31");
        }

        var count = 0;

        for (String line : input.section(1).lines()) {
            var result = match(line, 0, rules, "0");
            if (result == line.length()) {
                count++;
            }
        }

        return count;
    }

    private int match(String input, int offset, Map<String, String> rules, String ruleId) {
        if (offset < input.length()) {
            var rule = rules.get(ruleId);

            if (rule.contains("a")) {
                if (input.charAt(offset) == 'a') {
                    return offset + 1;
                }
                return -1;
            } else if (rule.contains("b")) {
                if (input.charAt(offset) == 'b') {
                    return offset + 1;
                }
                return -1;
            } else if (rule.contains("|")) {

                var or = rule.split("\\|");

                int o1 = matchesSequence(input, offset, rules, or[1].trim());
                int o2 = matchesSequence(input, offset, rules, or[0].trim());

                return Math.max(o1, o2);
            } else {
                return matchesSequence(input, offset, rules, rule);
            }
        }

        return -1;
    }

    private int matchesSequence(String input, int offset, Map<String, String> rules, String sequence) {
        var nextOffset = offset;
        for (String nextRuleId : sequence.split("\\s+")) {
            nextOffset = match(input, nextOffset, rules, nextRuleId);
            if (nextOffset < 0) {
                return -1;
            }
        }
        return nextOffset;
    }
}
