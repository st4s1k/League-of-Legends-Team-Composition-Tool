package com.st4s1k.leagueteamcomp.actions.base;

import dev.failsafe.function.CheckedRunnable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public abstract class LTCRunnable implements CheckedRunnable {

    @Getter
    @Setter(PROTECTED)
    private boolean allowParallelExecution = false;
    @Getter
    @Setter(PROTECTED)
    private boolean running = false;

    public boolean canRun() {
        return !running || allowParallelExecution;
    }
}
