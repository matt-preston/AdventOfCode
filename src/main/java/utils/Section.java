package utils;

import com.google.common.io.CharSource;

import java.io.IOException;
import java.util.List;

public interface Section {

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
