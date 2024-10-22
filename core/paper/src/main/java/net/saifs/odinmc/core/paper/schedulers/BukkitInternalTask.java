package net.saifs.odinmc.core.paper.schedulers;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.scheduler.BukkitRunnable;
import net.odinmc.core.common.scheduling.InternalTask;
import net.odinmc.core.common.scheduling.Task;
import org.jetbrains.annotations.NotNull;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class BukkitInternalTask extends BukkitRunnable implements InternalTask {

    @NotNull
    Predicate<Task> backingTask;

    @NotNull
    AtomicBoolean cancelled = new AtomicBoolean(false);

    @NotNull
    AtomicInteger counter = new AtomicInteger(0);

    BukkitInternalTask(@NotNull final Predicate<Task> backingTask) {
        this.backingTask = backingTask;
    }

    @Override
    public int id() {
        return this.getTaskId();
    }
}
