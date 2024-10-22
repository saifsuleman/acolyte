package net.odinmc.core.common.scheduling;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface ContextualPromiseBuilder {
    @NotNull
    <T> Promise<T> call(@NotNull Callable<T> callable);

    @NotNull
    Promise<?> run(@NotNull Runnable runnable);

    @NotNull
    <T> Promise<T> supply(@NotNull Supplier<T> supplier);
}
