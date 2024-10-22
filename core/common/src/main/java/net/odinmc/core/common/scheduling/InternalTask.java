package net.odinmc.core.common.scheduling;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public interface InternalTask extends Task, Runnable {
    @NotNull
    Predicate<Task> backingTask();

    void cancel();

    @NotNull
    AtomicBoolean cancelled();

    default boolean closed() {
        return this.cancelled().get();
    }

    @NotNull
    AtomicInteger counter();

    @Override
    default void run() {
        if (this.closed()) {
            this.cancel();
            return;
        }
        try {
            if (this.backingTask().test(this)) {
                this.cancel();
                return;
            }
            this.counter().incrementAndGet();
        } catch (final Throwable e) {
            e.printStackTrace();
        }
        if (this.closed()) {
            this.cancel();
        }
    }

    @Override
    default boolean stop() {
        return !this.cancelled().getAndSet(true);
    }

    @Override
    default int timesRan() {
        return this.counter().get();
    }
}
