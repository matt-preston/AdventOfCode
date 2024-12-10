package utils;

import java.util.Arrays;
import java.util.List;

public class Utils {

    public static List<Long> parseLongs(final String s) {
        return parseLongs(s, "[\\s,|:;]+");
    }

    public static List<Long> parseLongs(final String s, final String sep) {
        return Arrays.stream(s.trim().split(sep)).map(Long::parseLong).toList();
    }

    public static List<Integer> parseInts(final String s) {
        return parseInts(s, "[\\s,|:;]+");
    }

    public static List<Integer> parseInts(final String s, final String sep) {
        return Arrays.stream(s.trim().split(sep)).map(Integer::parseInt).toList();
    }

    public static Vector2 find(char[][] matrix, char target) {
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                if (matrix[y][x] == target) {
                    return new Vector2(x, y);
                }
            }
        }
        return null;
    }

    public static char[][] matrix(final Input input) {
        return matrix(input.text());
    }

    public static boolean contains(char[][] matrix, Vector2 v) {
        return v.y() >= 0 && v.y() < matrix.length && v.x() >= 0 && v.x() < matrix[0].length;
    }

    public static char get(char[][] matrix, Vector2 v) {
        return matrix[v.y()][v.x()];
    }

    public static char[][] matrix(final String string) {
        final var split = string.split("\n");
        var result = new char[split.length][split[0].length()];
        for (int i = 0; i < split.length; i++) {
            result[i] = split[i].toCharArray();
        }
        return result;
    }

    public static char[][] rotateCW(final char[][] matrix) {
        var result = new char[matrix[0].length][matrix.length];
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[0].length; y++) {
                result[y][result[0].length - x - 1] = matrix[x][y];
            }
        }
        return result;
    }

    public static char[][] copy(final char[][] matrix) {
        final var result = new char[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return result;
    }

    public static long predictNthInQuadratic(long n, long first, long second, long third) {
        return first + n * (second - first + (n - 1) * (third - second - second + first) / 2);
    }

    public static long triangleSeries(long nth) {
        if (nth < 1) {
            return 0;
        }
        return (nth * (nth + 1)) / 2;
    }
}
