package net.saifs.odinmc.core.paper.schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.odinmc.core.common.delegates.RunnableToSupplier;
import net.odinmc.core.common.scheduling.AsyncExecutor;
import net.odinmc.core.common.scheduling.Internal;
import net.odinmc.core.common.scheduling.InternalScheduledTask;
import net.odinmc.core.common.scheduling.Promise;
import net.odinmc.core.common.scheduling.PromiseSupply;
import net.odinmc.core.common.scheduling.Scheduler;
import net.odinmc.core.common.scheduling.Task;
import net.odinmc.core.common.scheduling.ThreadContext;
import org.jetbrains.annotations.NotNull;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class BukkitAsyncScheduler implements Scheduler {

    @NotNull
    @Override
    public ThreadContext context() {
        return ThreadContext.ASYNC;
    }

    @NotNull
    @Override
    public Promise<?> run(@NotNull final Runnable runnable) {
        final var promise = Promise.empty();
        AsyncExecutor.INSTANCE.execute(new PromiseSupply<>(promise, new RunnableToSupplier<>(runnable)));
        return promise;
    }

    @NotNull
    @Override
    public Promise<?> runLater(@NotNull final Runnable runnable, @NotNull final Duration delay) {
        final var promise = Promise.empty();
        AsyncExecutor.INSTANCE.schedule(new PromiseSupply<>(promise, new RunnableToSupplier<>(runnable)), delay.toMillis(), TimeUnit.MILLISECONDS);
        return promise;
    }

    @NotNull
    @Override
    public Task runRepeatingCloseIf(@NotNull final Predicate<Task> taskPredicate, @NotNull final Duration delay, @NotNull final Duration interval) {
        final var plugin = BukkitTasks.plugin();
        final var task = new BukkitInternalTask(taskPredicate);
        if (plugin.isEnabled()) {
            task.runTaskTimerAsynchronously(plugin, Internal.ticksFrom(delay), Internal.ticksFrom(interval));
        } else {
            BukkitAsyncScheduler.log.error("Plugin attempted to register task while disabled!");
            BukkitAsyncScheduler.log.error("The task won't be run because this is a repeating task!");
        }
        return task;
    }

    @NotNull
    @Override
    public Task scheduleRepeatingCloseIf(
        @NotNull final Predicate<Task> taskPredicate,
        @NotNull final Duration delay,
        @NotNull final Duration interval
    ) {
        final var task = new InternalScheduledTask(taskPredicate);
        task.scheduleAtFixedRate(delay, interval);
        return task;
    }
}
