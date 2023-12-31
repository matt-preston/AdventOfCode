package utils;

import java.io.IOException;
import java.util.List;

import com.google.common.io.CharSource;

public interface Input {

    static Input mockInput(final String text) {
        return () -> text;
    }

    static Input input(final Object obj) {
        final var annotations = obj.getClass().getDeclaredAnnotationsByType(AdventOfCode.class);
        if (annotations.length == 0) {
            throw new IllegalArgumentException("@AdventOfCode annotation missing");
        } else if (annotations.length > 1) {
            throw new IllegalArgumentException("Too many @AdventOfCode annotations");
        }

        return input(annotations[0].year(), annotations[0].day());
    }

    static Input input(final int year, int day) {
        try {
            return new RemoteInput(year, day);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String text();

    default List<String> lines() {
        try {
            return CharSource.wrap(text()).readLines();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default String[] linesArray() {
        return lines().toArray(String[]::new);
    }
}
