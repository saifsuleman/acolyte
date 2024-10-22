package net.odinmc.core.common.delegates;

import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ConsumerToFunction<T, R>(@NotNull Consumer<T> delegate) implements Function<T, @Nullable R> {
    @Nullable
    @Override
    public R apply(final T t) {
        this.delegate.accept(t);
        return null;
    }
}
