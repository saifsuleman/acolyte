package net.odinmc.core.common.delegates;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record RunnableToSupplier<T>(@NotNull Runnable delegate) implements Supplier<@Nullable T> {
    @Nullable
    @Override
    public T get() {
        this.delegate.run();
        return null;
    }
}
