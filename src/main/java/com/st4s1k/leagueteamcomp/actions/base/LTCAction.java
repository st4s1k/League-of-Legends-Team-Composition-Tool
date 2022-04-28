package com.st4s1k.leagueteamcomp.actions.base;

import dev.failsafe.function.CheckedConsumer;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class LTCAction extends LTCRunnable {

    @Getter
    private final Set<LTCActionListener> actionListeners = new HashSet<>();
    @Getter
    private final SimpleObjectProperty<CompletableFuture<Void>> completableFutureProperty = new SimpleObjectProperty<>();

    public LTCAction addListener(LTCActionListener actionListener) {
        actionListeners.add(actionListener);
        actionListener.setAction(this);
        actionListener.run();
        return this;
    }

    public void execute() {
        if (canRun()) {
            runBeforeActions();
            runActionAsync();
        }
    }

    private void runBeforeActions() {
        forEachChecked(actionListeners, listener -> {
            if (listener.canRun()) {
                listener.before();
            }
        });
    }

    private void runActionAsync() {
        completableFutureProperty.set(CompletableFuture.runAsync(() -> {
            try {
                setRunning(true);
                run();
            } catch (Throwable t) {
                Platform.runLater(() -> {
                    throw new RuntimeException(t);
                });
            } finally {
                setRunning(false);
            }
        }));
    }

    private <T> void forEachChecked(Collection<T> collection, CheckedConsumer<T> operation) {
        collection.forEach(item -> Platform.runLater(() -> {
            try {
                operation.accept(item);
            } catch (Throwable t) {
                Platform.runLater(() -> {
                    throw new RuntimeException(t);
                });
            }
        }));
    }
}
