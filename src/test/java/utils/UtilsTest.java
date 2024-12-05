package utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    public void testParseInts() {
         assertEquals(List.of(1, 2, 3, 4, 5, 6), Utils.parseInts("1,2|3 4:5;6"));
         assertEquals(List.of(0, -1, -2, 4, 5, 6), Utils.parseInts("0,-1|-2| 4:5;6"));
    }

}