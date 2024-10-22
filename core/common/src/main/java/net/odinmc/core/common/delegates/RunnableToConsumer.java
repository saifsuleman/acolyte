package net.odinmc.core.common.delegates;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public record RunnableToConsumer<T>(@NotNull Runnable delegate) implements Consumer<T> {
    @Override
    public void accept(final T t) {
        this.delegate.run();
    }
}
