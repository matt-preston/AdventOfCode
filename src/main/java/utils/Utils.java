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

}
