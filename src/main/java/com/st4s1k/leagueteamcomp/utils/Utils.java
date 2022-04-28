package com.st4s1k.leagueteamcomp.utils;

import javafx.scene.Node;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.st4s1k.leagueteamcomp.utils.Resources.PREFERENCES;

@Slf4j
public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> boolean same(Collection<T> collection1, Collection<T> collection2) {
        return collection1.size() == collection2.size() && collection1.containsAll(collection2);
    }

    public static <T> boolean notSame(Collection<T> collection1, Collection<T> collection2) {
        return !same(collection1, collection2);
    }

    public static void disableInteraction(Node node) {
        node.setMouseTransparent(true);
        node.setFocusTraversable(false);
    }

    public static void disableInteraction(Node firstNode, Node... nodes) {
        disableInteraction(firstNode);
        for (Node node : nodes) {
            disableInteraction(node);
        }
    }

    public static void enableInteraction(Node node) {
        node.setMouseTransparent(false);
        node.setFocusTraversable(true);
    }

    public static void enableInteraction(Node firstNode, Node... nodes) {
        enableInteraction(firstNode);
        for (Node node : nodes) {
            enableInteraction(node);
        }
    }

    public static <T, R> void initializeSavedFieldsProperty(
        Object instance,
        Class<T> fieldType,
        Function<T, String> keyExtractor,
        Function<String, R> fieldPropertyConverter,
        BiConsumer<T, R> fieldPropertySetter
    ) {
        getFieldTypeValues(instance, fieldType).forEach(fieldValue ->
            getFieldProperty(fieldValue, keyExtractor)
                .map(fieldPropertyConverter)
                .ifPresent(fieldProperty -> fieldPropertySetter.accept(fieldValue, fieldProperty)));
    }

    private static <T> Optional<String> getFieldProperty(T fieldValue, Function<T, String> keyExtractor) {
        return Optional.ofNullable(PREFERENCES.get(keyExtractor.apply(fieldValue), null));
    }

    public static <T, R> void initializeAllFieldsWithDefaultProperty(
        Object instance,
        Class<T> fieldType,
        BiConsumer<T, R> fieldPropertySetter,
        R defaultFieldProperty
    ) {
        getFieldTypeValues(instance, fieldType).forEach(fieldValue ->
            fieldPropertySetter.accept(fieldValue, defaultFieldProperty));
    }

    public static <T> void saveFields(
        Object instance,
        Class<T> fieldType,
        Function<T, String> keyExtractor,
        Function<T, Object> valueExtractor
    ) {
        getFieldTypeValues(instance, fieldType).forEach(fieldValue -> PREFERENCES.put(
            keyExtractor.apply(fieldValue),
            String.valueOf(valueExtractor.apply(fieldValue))
        ));
    }

    public static <T> List<T> getFieldTypeValues(Object instance, Class<T> fieldType) {
        return Arrays.stream(instance.getClass().getDeclaredFields())
            .filter(field -> field.getType().equals(fieldType))
            .map(Field::getName)
            .map(fieldName -> getFieldValue(instance, fieldName, fieldType))
            .toList();
    }

    @SneakyThrows
    public static <T> T getFieldValue(Object instance, String fieldName, Class<T> fieldType) {
        Field field = instance.getClass().getDeclaredField(fieldName);
        boolean defaultAccessible = field.canAccess(instance);
        field.setAccessible(true);
        T fieldValue = fieldType.cast(field.get(instance));
        field.setAccessible(defaultAccessible);
        return fieldValue;
    }

    @SneakyThrows
    public static <T> void setFieldValue(
        Object instance,
        String fieldName,
        T fieldValue
    ) {
        Field field = instance.getClass().getDeclaredField(fieldName);
        boolean defaultAccessibleValue = field.canAccess(instance);
        field.setAccessible(true);
        field.set(instance, fieldValue);
        field.setAccessible(defaultAccessibleValue);
    }
}
