package com.st4s1k.leagueteamcomp.actions.base;

import javafx.application.Platform;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static lombok.AccessLevel.PACKAGE;

@SuppressWarnings("RedundantThrows")
public abstract class LTCActionListener extends LTCRunnable {

    @Setter(PACKAGE)
    private LTCAction action;

    public void before() throws Throwable {
    }

    public long whileRunning() throws Throwable {
        return -1;
    }

    public void onDone() throws Throwable {
    }

    @Override
    public void run() {
        action.getCompletableFutureProperty()
            .addListener((observable, oldValue, completableFuture) ->
                CompletableFuture.runAsync(() -> {
                    if (!canRun()) {
                        return;
                    }
                    try {
                        setRunning(true);
                        while (!completableFuture.isDone()) {
                            long timeout = whileRunning();
                            if (timeout > 0) {
                                MILLISECONDS.sleep(timeout);
                            } else if (timeout < 0) {
                                break;
                            }
                        }
                    } catch (Throwable t) {
                        Platform.runLater(() -> {
                            throw new RuntimeException(t);
                        });
                    } finally {
                        Platform.runLater(() -> {
                            try {
                                onDone();
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            } finally {
                                setRunning(false);
                            }
                        });
                    }
                }));
    }
}
