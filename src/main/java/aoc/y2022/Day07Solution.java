package aoc.y2022;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2022, day = 7, name = "No Space Left On Device")
public class Day07Solution {

    record Directory(Directory parent, Map<String, Directory> directories, Map<String, Long> files) {
        public Directory(Directory parent) {
            this(parent, new HashMap<>(), new HashMap<>());
        }

        long size() {
            long f = files.values().stream()
                    .mapToLong(Long::longValue)
                    .sum();

            return f + directories.values().stream()
                    .mapToLong(Directory::size)
                    .sum();
        }
    }

    private static final String MOCK = """
            $ cd /
            $ ls
            dir a
            14848514 b.txt
            8504156 c.dat
            dir d
            $ cd a
            $ ls
            dir e
            29116 f
            2557 g
            62596 h.lst
            $ cd e
            $ ls
            584 i
            $ cd ..
            $ cd ..
            $ cd d
            $ ls
            4060174 j
            8033020 d.log
            5626152 d.ext
            7214296 k
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(95437, solvePart1(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(1444896, solvePart1(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(24933642, solvePart2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(404395, solvePart2(input(this)));
    }

    private long solvePart1(Input input) {
        return collectSizes(filesystem(input)).stream()
                .filter(l -> l <= 100000)
                .mapToLong(Long::longValue)
                .sum();
    }

    private long solvePart2(Input input) {
        var root = filesystem(input);
        var needed = 30000000 - (70000000 - root.size());

        return collectSizes(root).stream()
                .sorted()
                .filter(size -> size >= needed)
                .findFirst()
                .orElse(0L);
    }

    private Directory filesystem(Input input) {
        var root = new Directory(null);
        Directory context = root;
        var listing = false;

        for (String line : input.lines()) {
            var parts = line.split("\\s");

            if (listing) {
                if (line.startsWith("$ ")) {
                    listing = false;
                } else {
                    if (line.startsWith("dir ")) {
                        context.directories().put(parts[1], new Directory(context));
                    } else {
                        context.files().put(parts[1], Long.valueOf(parts[0]));
                    }
                }
            }

            if (!listing) {
                switch (line) {
                    case "$ cd /" -> context = root;
                    case "$ cd .." -> context = context.parent();
                    case String c when c.startsWith("$ cd ") -> context = context.directories().get(parts[2]);
                    default -> listing = true; // "$ ls"
                }
            }
        }

        return root;
    }

    private List<Long> collectSizes(Directory directory) {
        var result = Lists.newArrayList(directory.size());
        directory.directories().values().forEach(d -> result.addAll(collectSizes(d)));
        return result;
    }
}
