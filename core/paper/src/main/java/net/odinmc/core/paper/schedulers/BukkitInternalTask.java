package net.odinmc.core.paper.schedulers;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.odinmc.core.common.scheduling.InternalTask;
import net.odinmc.core.common.scheduling.Task;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class BukkitInternalTask extends BukkitRunnable implements InternalTask {

    @NotNull
    private final Predicate<Task> backingTask;

    @NotNull
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    @NotNull
    private final AtomicInteger counter = new AtomicInteger(0);

    BukkitInternalTask(@NotNull final Predicate<Task> backingTask) {
        this.backingTask = backingTask;
    }

    @Override
    public int id() {
        return this.getTaskId();
    }

    public @NotNull Predicate<Task> backingTask() {
        return this.backingTask;
    }

    public @NotNull AtomicBoolean cancelled() {
        return this.cancelled;
    }

    public @NotNull AtomicInteger counter() {
        return this.counter;
    }
}
