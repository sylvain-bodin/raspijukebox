package com.opoix.raspijukebox.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.net.httpserver.HttpExchange;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by copoix on 1/2/16.
 */
public class ResourceUtils {
    private static ResourceUtils instance = new ResourceUtils();

    public static ResourceUtils getInstance() {
        return instance;
    }

    private ResourceUtils() {
    }

    public Map<String, List<String>> parse(final String query) {

        Stream<String> params = Arrays.asList(query.split("&")).stream();
        BinaryOperator<List<String>> mergeLists = (l1, l2) -> Lists.newLinkedList(Iterables.concat(l1, l2));
        BiFunction<String[], Integer, String> getAt = (array, index) -> index >= array.length ? null : array[index];
        return params.map(p -> p.split("=")).collect(Collectors.toMap(s -> decode(index(s, 0)), s -> Arrays.asList(decode(index(s, 1))), mergeLists));
    }


    private static <T> T index(final T[] array, final int index) {
        return index >= array.length ? null : array[index];
    }

    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Impossible: UTF-8 is a required encoding", e);
        }
    }

    public HttpParams getParams(HttpExchange httpExchange) {
        return new HttpParams(parse(httpExchange.getRequestURI().getQuery()));
    }
}
