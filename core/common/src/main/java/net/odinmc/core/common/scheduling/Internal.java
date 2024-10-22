package net.odinmc.core.common.scheduling;

import net.odinmc.core.common.terminable.Terminable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class Internal {

    private static final AtomicReference<Logger> LOGGER = new AtomicReference<>();

    private static final AtomicReference<Thread> MAIN_THREAD = new AtomicReference<>();

    private static final long MILLISECONDS_PER_SECOND = 1000L;

    private static final AtomicReference<SchedulerProvider> SCHEDULER_PROVIDER = new AtomicReference<>();

    private static final long TICKS_PER_SECOND = 20L;

    private static final long MILLISECONDS_PER_TICK = Internal.MILLISECONDS_PER_SECOND / Internal.TICKS_PER_SECOND;

    private Internal() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @NotNull
    public static Terminable init(@NotNull final SchedulerProvider schedulerProvider, @NotNull final Logger logger) {
        Internal.MAIN_THREAD.set(Thread.currentThread());
        Internal.SCHEDULER_PROVIDER.set(schedulerProvider);
        Internal.LOGGER.set(logger);
        return AsyncExecutor.INSTANCE::cancelRepeatingTasks;
    }

    public static long ticksFrom(@NotNull final Duration duration) {
        return duration.toMillis() / Internal.MILLISECONDS_PER_TICK;
    }

    @NotNull
    static Scheduler async() {
        return Internal.schedulerProvider().async();
    }

    @NotNull
    static Duration durationFrom(final long ticks) {
        return Internal.durationFrom(ticks * Internal.MILLISECONDS_PER_TICK, TimeUnit.MILLISECONDS);
    }

    @NotNull
    static Duration durationFrom(final long duration, @NotNull final TimeUnit unit) {
        return Duration.of(duration, unit.toChronoUnit());
    }

    @NotNull
    static Scheduler get(@NotNull final ThreadContext context) {
        return switch (context) {
            case SYNC -> Internal.schedulerProvider().sync();
            case ASYNC -> Internal.schedulerProvider().async();
        };
    }

    @NotNull
    static Logger logger() {
        return Objects.requireNonNull(Internal.LOGGER.get(), "initiate the task first!");
    }

    @NotNull
    static Thread mainThread() {
        return Objects.requireNonNull(Internal.MAIN_THREAD.get(), "initiate task first!");
    }

    @NotNull
    static Scheduler sync() {
        return Internal.schedulerProvider().sync();
    }

    @NotNull
    private static SchedulerProvider schedulerProvider() {
        return Objects.requireNonNull(Internal.SCHEDULER_PROVIDER.get(), "initiate the task first!");
    }
}
