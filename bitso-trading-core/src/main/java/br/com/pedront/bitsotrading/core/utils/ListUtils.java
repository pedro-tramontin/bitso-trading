package br.com.pedront.bitsotrading.core.utils;

import java.util.List;
import java.util.Optional;

public class ListUtils {
    public static <T> Optional<T> getLastItem(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(list.get(list.size() - 1));
    }

    public static <T> Optional<T> getFirstItem(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }
}
