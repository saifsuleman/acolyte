package net.odinmc.core.common.scheduling;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InternalScheduledTask implements InternalTask {

    @NotNull
    private final Predicate<Task> backingTask;

    @NotNull
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    @NotNull
    private final AtomicInteger counter = new AtomicInteger(0);

    @Nullable
    private ScheduledFuture<?> task;

    public InternalScheduledTask(@NotNull final Predicate<Task> backingTask) {
        this.backingTask = backingTask;
    }

    @Override
    public void cancel() {
        if (this.task == null) {
            throw new IllegalStateException("Initiate the task using #scheduleAtFixedRate(long, long, TimeUnit)");
        }
        this.task.cancel(false);
    }

    @Override
    public int id() {
        throw new UnsupportedOperationException();
    }

    public void scheduleAtFixedRate(@NotNull final Duration initialDelay, @NotNull final Duration period) {
        if (this.task != null) {
            throw new IllegalStateException("You cannot schedule the same task twice!");
        }
        this.task = AsyncExecutor.INSTANCE.scheduleAtFixedRate(this, initialDelay.toMillis(), period.toMillis(), TimeUnit.MILLISECONDS);
    }

    public @NotNull Predicate<Task> backingTask() {
        return this.backingTask;
    }

    public @Nullable ScheduledFuture<?> task() {
        return this.task;
    }

    public @NotNull AtomicBoolean cancelled() {
        return this.cancelled;
    }

    public @NotNull AtomicInteger counter() {
        return this.counter;
    }
}
