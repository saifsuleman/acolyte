package net.saifs.odinmc.core.paper.schedulers;

import java.time.Duration;
import java.util.function.Predicate;
import net.odinmc.core.common.delegates.RunnableToSupplier;
import net.odinmc.core.common.scheduling.*;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

final class BukkitSyncScheduler implements Scheduler {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(BukkitSyncScheduler.class);

    BukkitSyncScheduler() {}

    @NotNull
    @Override
    public ThreadContext context() {
        return ThreadContext.SYNC;
    }

    @NotNull
    @Override
    public Promise<?> run(@NotNull final Runnable runnable) {
        final var promise = Promise.empty();
        final var plugin = BukkitTasks.plugin();
        final var task = new BukkitInternalTask(t -> {
            new PromiseSupply<>(promise, new RunnableToSupplier<>(runnable)).run();
            return false;
        });
        if (plugin.isEnabled()) {
            task.runTask(plugin);
        } else {
            BukkitSyncScheduler.log.error("Plugin attempted to register task while disabled!");
            BukkitSyncScheduler.log.error("We are going to run the task in the current thread which is {}!", Thread.currentThread());
            task.run();
        }
        return promise;
    }

    @NotNull
    @Override
    public Promise<?> runLater(@NotNull final Runnable runnable, @NotNull final Duration delay) {
        final var promise = Promise.empty();
        final var plugin = BukkitTasks.plugin();
        final var task = new BukkitInternalTask(t -> {
            new PromiseSupply<>(promise, new RunnableToSupplier<>(runnable)).run();
            return false;
        });
        if (plugin.isEnabled()) {
            task.runTaskLater(plugin, Internal.ticksFrom(delay));
        } else {
            BukkitSyncScheduler.log.error("Plugin attempted to register task while disabled!");
            BukkitSyncScheduler.log.error("We are going to run the task in the current thread which is {}!", Thread.currentThread());
            task.run();
        }
        return promise;
    }

    @NotNull
    @Override
    public Task runRepeatingCloseIf(@NotNull final Predicate<Task> taskPredicate, @NotNull final Duration delay, @NotNull final Duration interval) {
        final var plugin = BukkitTasks.plugin();
        final var task = new BukkitInternalTask(taskPredicate);
        if (plugin.isEnabled()) {
            task.runTaskTimer(plugin, Internal.ticksFrom(delay), Internal.ticksFrom(interval));
        } else {
            BukkitSyncScheduler.log.error("Plugin attempted to register task while disabled!");
            BukkitSyncScheduler.log.error("The task won't be run because this is a repeating task!");
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
        BukkitSyncScheduler.log.error(
            "Sync scheduler does not support #scheduleRepeating(Consumer<Task>, long, long, TimeUnit), using async scheduler to schedule repeating instead!"
        );
        return Schedulers.async().scheduleRepeatingCloseIf(taskPredicate, delay, interval);
    }
}
