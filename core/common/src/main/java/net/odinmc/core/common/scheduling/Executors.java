package net.odinmc.core.common.scheduling;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

final
class Executors {

    private Executors() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static void async(@NotNull final Runnable runnable) {
        if (ThreadContext.forCurrentThread() == ThreadContext.ASYNC) {
            new UncheckedRunnable(runnable).run();
        } else {
            Schedulers.async().run(runnable);
        }
    }

    static void asyncDelayed(@NotNull final Runnable runnable, @NotNull final Duration delay) {
        if (delay.isNegative() || delay.isZero()) {
            Executors.async(runnable);
        } else {
            Schedulers.async().runLater(new UncheckedRunnable(runnable), delay);
        }
    }

    static void sync(@NotNull final Runnable runnable) {
        if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
            new UncheckedRunnable(runnable).run();
        } else {
            Schedulers.sync().run(runnable);
        }
    }

    static void syncDelayed(@NotNull final Runnable runnable, @NotNull final Duration delay) {
        if (delay.isNegative() || delay.isZero()) {
            Executors.sync(runnable);
        } else {
            Schedulers.sync().runLater(new UncheckedRunnable(runnable), delay);
        }
    }
}
