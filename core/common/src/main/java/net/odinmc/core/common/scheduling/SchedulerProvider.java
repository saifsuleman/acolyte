package net.odinmc.core.common.scheduling;

import org.jetbrains.annotations.NotNull;

public interface SchedulerProvider {
    @NotNull
    static SchedulerProvider of(@NotNull final Scheduler async, @NotNull final Scheduler sync) {
        return new Impl(async, sync);
    }

    @NotNull
    Scheduler async();

    @NotNull
    Scheduler sync();

    record Impl(@NotNull Scheduler async, @NotNull Scheduler sync) implements SchedulerProvider {}
}
