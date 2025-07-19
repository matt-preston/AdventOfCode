package utils;

import java.util.Arrays;
import java.util.List;

public interface Input extends Section {

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

    default List<Section> sections() {
        return Arrays.stream(text().split("\n\n"))
                .map(s -> (Section) s::stripTrailing)
                .toList();
    }

    default Section section(int index) {
        return sections().get(index);
    }
}
