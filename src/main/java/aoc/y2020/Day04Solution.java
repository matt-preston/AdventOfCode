package aoc.y2020;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Section;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 4, name = "Passport Processing")
public class Day04Solution {

    private static final String MOCK = """
            ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
            byr:1937 iyr:2017 cid:147 hgt:183cm
            
            iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
            hcl:#cfa07d byr:1929
            
            hcl:#ae17e1 iyr:2013
            eyr:2024
            ecl:brn pid:760753108 byr:1931
            hgt:179cm
            
            hcl:#cfa07d eyr:2025 pid:166559648
            iyr:2011 ecl:brn hgt:59in
            """;

    private static final String MOCK2 = """
            eyr:1972 cid:100
            hcl:#18171d ecl:amb hgt:170 pid:186cm iyr:2018 byr:1926
            
            iyr:2019
            hcl:#602927 eyr:1967 hgt:170cm
            ecl:grn pid:012533040 byr:1946
            
            hcl:dab227 iyr:2012
            ecl:brn hgt:182cm pid:021572410 eyr:2020 byr:1992 cid:277
            
            hgt:59cm ecl:zzz
            eyr:2038 hcl:74454a iyr:2023
            pid:3556412378 byr:2007
            
            pid:087499704 hgt:74in ecl:grn iyr:2012 eyr:2030 byr:1980
            hcl:#623a2f
            
            eyr:2029 ecl:blu cid:129 byr:1989
            iyr:2014 pid:896056539 hcl:#a97842 hgt:165cm
            
            hcl:#888785
            hgt:164cm byr:2001 iyr:2015 cid:88
            pid:545766238 ecl:hzl
            eyr:2022
            
            iyr:2010 hgt:158cm hcl:#b6652a ecl:blu byr:1944 eyr:2021 pid:093154719
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(2, part1(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(182, part1(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(4, part2(mockInput(MOCK2)));
    }

    @Test
    public void part2() {
        assertEquals(109, part2(input(this)));
    }

    private long part1(Input input) {
        return input.sections().stream()
                .map(this::passport)
                .filter(this::simpleValidation)
                .count();
    }

    private long part2(Input input) {
        return input.sections().stream()
                .map(this::passport)
                .filter(this::simpleValidation)
                .filter(this::complexValidation)
                .count();
    }

    private boolean simpleValidation(Map<String, String> passport) {
        return passport.containsKey("byr") &&
                passport.containsKey("iyr") &&
                passport.containsKey("eyr") &&
                passport.containsKey("hgt") &&
                passport.containsKey("hcl") &&
                passport.containsKey("ecl") &&
                passport.containsKey("pid");
    }

    private boolean complexValidation(Map<String, String> passport) {
        if (!between(passport.get("byr"), 1920, 2002)) {
            return false;
        }

        if (!between(passport.get("iyr"), 2010, 2020)) {
            return false;
        }

        if (!between(passport.get("eyr"), 2020, 2030)) {
            return false;
        }

        var height = passport.get("hgt");
        if (height.endsWith("cm")) {
            if (!between(height.substring(0, height.length() - 2), 150, 193)) {
                return false;
            }
        } else if (height.endsWith("in")) {
            if (!between(height.substring(0, height.length() - 2), 59, 76)) {
                return false;
            }
        } else {
            return false;
        }

        if (!passport.get("hcl").matches("#[0-9a-f]{6}")) {
            return false;
        }

        var validEyes = Set.of("amb", "blu", "brn", "gry", "grn", "hzl", "oth");
        if (!validEyes.contains(passport.get("ecl"))) {
            return false;
        }

        return passport.get("pid").matches("[0-9]{9}");
    }

    private boolean between(String value, int min, int max) {
        int i = Integer.parseInt(value);
        return i >= min && i <= max;
    }

    private Map<String, String> passport(Section section) {
        var parts = section.text().split("\\s+");
        return Arrays.stream(parts)
                .collect(Collectors.toMap(
                        s -> s.split(":")[0],
                        s -> s.split(":")[1]));
    }
}
