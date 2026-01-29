package com.chenchen.act_manager.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtil{

    public static <T> T find(Collection<T> cols, Predicate<T> p) {
        if (CollectionUtils.isEmpty(cols)) {
            return null;
        }
        for (T t : cols) {
            if (p.test(t)) {
                return t;
            }
        }
        return null;

    }

    public static <T> List<T> findList(List<T> list, Predicate<T> p) {
        List<T> subList = Lists.newArrayList();
        for (T t : list) {
            if (p.test(t)) {
                subList.add(t);
            }
        }
        return subList;
    }


    /**
     * Bigdecimal累加
     *
     * @param list
     * @param mapValue
     * @param filters
     * @param <T>
     * @return
     */
    public static <T> BigDecimal add(List<T> list, Function<T, BigDecimal> mapValue, Predicate<T>... filters) {
        Stream<T> stream = getFilterStream(list, filters);
        return stream
                .map(mapValue).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Integer累加
     *
     * @param list
     * @param mapValue
     * @param filters
     * @param <T>
     * @return
     */
    public static <T> Integer sum(List<T> list, Function<T, Integer> mapValue, Predicate<T>... filters) {
        Stream<T> stream = getFilterStream(list, filters);
        return stream
                .map(mapValue)
                .reduce(0, Integer::sum);
    }

    public static  <T, R> List<R> toList(List<T> list, Function<T, R> mapValue) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().map(mapValue).collect(Collectors.toList());
    }

    public static  <T, R> Set<R> toSet(List<T> list, Function<T, R> mapValue) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().map(mapValue).collect(Collectors.toSet());
    }

    private static <T> Stream<T> getFilterStream(List<T> list, Predicate<T>... filters) {
        Stream<T> stream = list.stream();
        if (filters.length > 0) {
            for (Predicate filter : filters) {
                stream = stream.filter(filter);
            }
        }
        return stream;
    }
}
