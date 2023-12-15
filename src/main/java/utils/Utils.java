package utils;

import java.util.Arrays;
import java.util.List;

public class Utils {

  public static List<Long> parseNumbers(final String s) {
    return parseNumbers(s, "\\s+");
  }

  public static List<Long> parseNumbers(final String s, final String sep) {
    return Arrays.stream(s.trim().split(sep)).map(Long::parseLong).toList();
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
}
