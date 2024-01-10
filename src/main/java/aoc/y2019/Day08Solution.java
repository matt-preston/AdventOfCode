package aoc.y2019;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AdventOfCode(year = 2019, day = 8, name = "Space Image Format")
public class Day08Solution {

    @Test
    public void part1WithSampleData() {
        assertEquals(1, checkDecodedImage(Input.mockInput("123456789012"), 3, 2));
    }

    @Test
    public void part1() {
        assertEquals(1320, checkDecodedImage(Input.input(this), 25, 6));
    }

    @Test
    public void part2() {
        decodeImage(Input.input(this), 25, 6);
    }

    private void decodeImage(Input input, int width, int height) {
        final var charArray = input.text().toCharArray();
        var layerLength = width * height;
        var numLayers = charArray.length / layerLength;

        var output = new char[layerLength];
        Arrays.fill(output, '2');

        for (int layer = 0; layer < numLayers; layer++) {
            for(int i = 0; i < layerLength; i++) {
                if (output[i] == '2') {
                    output[i] = charArray[(layer * layerLength) + i];
                }
            }
        }

        for (int line = 0; line < height; line++) {
            System.out.println(new String(output, line * width, width)
                    .replaceAll("1", "#")
                    .replaceAll("0", " "));
        }
    }

    private int checkDecodedImage(Input input, int width, int height) {
        var layerLength = width * height;
        final var charArray = input.text().toCharArray();
        var numLayers = charArray.length / layerLength;

        int minZeros = Integer.MAX_VALUE;
        int layerWithMinZeros = 0;

        for (int layer = 0; layer < numLayers; layer++) {

            int numZeros = count('0', charArray, layer * layerLength, layerLength);
            if (numZeros < minZeros) {
                minZeros = numZeros;
                layerWithMinZeros = layer;
            }
        }

        return count('1', charArray, layerWithMinZeros * layerLength, layerLength) *
                count('2', charArray, layerWithMinZeros * layerLength, layerLength);
    }

    private int count(char c, char[] charArray, int from, int length) {
        var count = 0;
        for (int i = from; i < from + length; i++) {
            if (charArray[i] == c) {
                count++;
            }
        }
        return count;
    }

}
