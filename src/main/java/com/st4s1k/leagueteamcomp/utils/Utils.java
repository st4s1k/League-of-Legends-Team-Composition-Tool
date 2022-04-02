package com.st4s1k.leagueteamcomp.utils;

import javafx.scene.Node;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@UtilityClass
public class Utils {

    public <T> boolean same(Collection<T> collection1, Collection<T> collection2) {
        return collection1.size() == collection2.size() && collection1.containsAll(collection2);
    }

    public <T> boolean notSame(Collection<T> collection1, Collection<T> collection2) {
        return !same(collection1, collection2);
    }

    public void disableInteraction(Node node) {
        node.setMouseTransparent(true);
        node.setFocusTraversable(false);
    }

    public void enableInteraction(Node node) {
        node.setMouseTransparent(false);
        node.setFocusTraversable(true);
    }

    public Consumer<Runnable> stop(Class<?> clazz) {
        return stopAction -> {
            String className = clazz.getSimpleName();
            log.info("Stopping {}...", className);
            stopAction.run();
            log.info("{} stopped.", className);
        };
    }

    public <T, R> void initializeFields(
        Object instance,
        Class<T> fieldType,
        Function<T, String> keyExtractor,
        BiConsumer<T, R> fieldStateInitializer,
        Function<String, R> fieldStateConverter,
        R defaultFieldState
    ) {
        initializeFields(instance, fieldType, fieldStateInitializer, fieldValue ->
            fieldStateConverter.apply(Resources.PREFERENCES.get(
                keyExtractor.apply(fieldValue),
                String.valueOf(defaultFieldState)
            ))
        );
    }

    public <T, R> void initializeFields(
        Object instance,
        Class<T> fieldType,
        BiConsumer<T, R> fieldStateInitializer,
        Function<T, R> defaultFieldStateProvider
    ) {
        getFieldTypeValues(instance, fieldType).forEach(fieldValue ->
            fieldStateInitializer.accept(fieldValue, defaultFieldStateProvider.apply(fieldValue)));
    }

    public <T, R> void initializeFieldsWithDefaultState(
        Object instance,
        Class<T> fieldType,
        BiConsumer<T, R> fieldStateInitializer,
        R defaultFieldState
    ) {
        getFieldTypeValues(instance, fieldType).forEach(fieldValue ->
            fieldStateInitializer.accept(fieldValue, defaultFieldState));
    }

    public <T> void saveFields(
        Object instance,
        Class<T> fieldType,
        Function<T, String> keyExtractor,
        Function<T, Object> valueExtractor
    ) {
        getFieldTypeValues(instance, fieldType).forEach(fieldValue -> Resources.PREFERENCES.put(
            keyExtractor.apply(fieldValue),
            String.valueOf(valueExtractor.apply(fieldValue))
        ));
    }

    public <T> List<T> getFieldTypeValues(Object instance, Class<T> fieldType) {
        return Arrays.stream(instance.getClass().getDeclaredFields())
            .filter(field -> field.getType().equals(fieldType))
            .map(Field::getName)
            .map(fieldName -> getFieldValue(instance, fieldName, fieldType))
            .toList();
    }

    @SneakyThrows
    public <T> T getFieldValue(Object instance, String fieldName, Class<T> fieldType) {
        Field field = instance.getClass().getDeclaredField(fieldName);
        boolean defaultAccessible = field.canAccess(instance);
        field.setAccessible(true);
        T fieldValue = fieldType.cast(field.get(instance));
        field.setAccessible(defaultAccessible);
        return fieldValue;
    }

    @SneakyThrows
    public <T> void setFieldValue(
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
