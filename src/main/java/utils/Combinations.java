package utils;

import java.util.List;
import java.util.function.BiConsumer;

public class Combinations {

    public static <T> void forEachPair(List<T> list, BiConsumer<T, T> consumer) {
        for (int i = 0; i < list.size(); i++) {
            var c1 = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                var c2 = list.get(j);
                consumer.accept(c1, c2);
            }
        }
    }

}
