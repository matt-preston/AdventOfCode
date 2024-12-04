package utils;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import static com.google.common.base.Preconditions.checkState;

public class CreateSolutionClass {

    public static void main(String[] args) throws IOException {
        var year = Integer.parseInt(args[0]);
        var day = Integer.parseInt(args[1]);

        checkState(year > 2014 && year <= Calendar.getInstance().get(Calendar.YEAR), "[" + year + "] is not a valid year");
        checkState(day > 0 && day < 26, "[" + day + "] is not a valid day");

        var path = String.format("src/main/java/aoc/y%d/Day%02dSolution.java", year, day);
        var file = new File(path);

        System.out.println(file.getAbsolutePath());

        if(!file.getParentFile().exists()) {
            checkState(file.getParentFile().mkdirs());
        }

        if (file.exists()) {
            throw new IllegalStateException("Solution class already exists");
        }

        var template = """
                package aoc.y%d;
                
                import utils.AdventOfCode;
                import org.junit.jupiter.api.Test;
                import utils.Input;
                
                import static org.junit.jupiter.api.Assertions.assertEquals;
                import static utils.Input.input;
                import static utils.Input.mockInput;
                
                @AdventOfCode(year = %d, day = %d, name = "")
                public class Day%02dSolution {
                    
                    private static final String MOCK_1 = ""\"
                                TODO
                                ""\";
                
                        private static final String MOCK_2 = ""\"
                                TODO
                                ""\";
                    
                    @Test
                    public void part1WithMockData() {
                        assertEquals(0, todo(mockInput(MOCK_1)));
                    }
            
                    @Test
                    public void part1() {
                        assertEquals(0, todo(input(this)));
                    }
            
                    @Test
                    public void part2WithMockData() {
                        assertEquals(0, todo(mockInput(MOCK_2)));
                    }
            
                    @Test
                    public void part2() {
                        assertEquals(0, todo(input(this)));
                    }
                    
                    private int todo(Input input) {
                        return 0;
                    }
                    
                }
                """;

        var code = String.format(template, year, year, day, day);
        System.out.println(code);

        Files.asCharSink(file, StandardCharsets.UTF_8).write(code);
    }
}
