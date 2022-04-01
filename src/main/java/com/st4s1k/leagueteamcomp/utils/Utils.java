package com.st4s1k.leagueteamcomp.utils;

import javafx.scene.Node;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

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
}
