package net.odinmc.core.common.scheduling;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface ContextualTaskBuilder {
    @NotNull
    Task consume(@NotNull Consumer<Task> consumer);

    @NotNull
    Task run(@NotNull Runnable runnable);
}
